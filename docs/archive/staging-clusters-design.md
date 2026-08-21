# Staging & Clustering — design and build plan (Phase 3, part 2)

*2026-07-03. Research complete (desktop core, our bridge, our app — details
below). This is the discuss-first document: build order, effort estimates,
and the decisions that are Eric's. Nothing here is implemented yet.*

## The headline finding

**The physics kernel we carved already does all of it.** Staging, separation
triggers, upper-stage ignition, separate booster flight branches (with tumble
aerodynamics for chuteless boosters), and clustered motors are all live code
in `info.openrocket.core` — compiled into our TeaVM artifact today, just
unreachable. Every gap is in our thin layers:

| Layer | Gap |
|---|---|
| Java bridge (`OrkEngine.java`) | Creates exactly ONE `AxialStage`; tree JSON has no stage nodes; flight output reads branch 0 only (a booster's flight would be silently dropped); cluster config never set |
| Engine TS types | `RocketTree` is a flat component list; `FlightResult` is single-branch |
| App tree model | `'stage'` is a sentinel string, not a node; every walker assumes one implicit stage |
| App UI | One motor + one active mount in state (the engine API is already per-mount) |
| `.ork` I/O | Import keeps only the FIRST stage of a desktop file; export hard-codes one "Sustainer" stage and `<clusterconfiguration>single</clusterconfiguration>` |
| Reporting | One flight = one landing; no concept of a booster coming down separately |

No new physics gets written. This is plumbing + UI + test coverage — exactly
the kind of work the differential test protects.

## How the desktop does it (what we inherit for free)

**Serial staging.** A rocket holds multiple `AxialStage`s (stage 0 = sustainer
at the top, numbers increase downward). Each lower stage carries a separation
config: trigger (**ejection charge** [default], **current-stage ignition /
burnout**, **upper-stage ignition**, launch, altitude-ascending/descending,
apogee, never) plus a delay. At separation the simulator *forks*: the booster
becomes its own simulation branch with its own data series and events — it
tumbles (validated tumble-CD model) or flies its own recovery if it has a
device — while the ascending stack continues lighter. Mass/aero bookkeeping is
automatic (both branches filter by "active stages").

**Upper-stage ignition.** A motor's ignition event defaults to AUTOMATIC:
launch-stage motors light at launch; upper-stage motors light on the
**ejection charge of the stage below** (burnout → ejection charge → upper
ignition, with an optional extra ignition delay). All the triggers are enum
values we simply pass through.

**Clusters.** An inner tube carries a cluster pattern (single, double, 3/4-row,
3/4/5/6-ring, 3–6-star, 9-grid, 9-star) plus scale and rotation. ONE motor
definition serves the whole cluster — thrust is multiplied by tube count, and
mass/inertia fall out of the per-tube geometry. So our existing one-motor-per-
mount UI maps perfectly: pick one motor, the cluster fires N of them.

**Flight output.** `FlightData` holds multiple named branches ("Sustainer",
"Booster 1"…), each with its own series, events, apogee, and ground hit. The
top-level summary is branch 0 (sustainer) — which keeps our existing report
semantics valid.

## Recommended build order

### Release A — Clusters (small, self-contained; ~1 session)

The quick win. One motor choice drives N motors; no new flight branches, no
report changes.

- **Bridge:** `innertube` factory reads `cluster` / `clusterScale` /
  `clusterRotation`, calls the kernel setters (pattern looked up by its XML
  name — same strings the .ork format uses). TeaVM rebuild + a cluster golden
  scenario (3-ring cluster flight; thrust must be exactly 3× at every sample)
  + full differential pass.
- **App:** three new schema fields on inner tubes (pattern dropdown, scale,
  rotation); 2D/3D draw the N tubes at their real cluster positions; `.ork`
  export writes the real values (rotation in degrees at the boundary, like
  the desktop) and import reads them (today desktop cluster files silently
  collapse to a single tube).
- **Everything downstream is automatic:** launch mass, thrust:weight, batch
  simulation, CSV — they all read simulated series, which the kernel already
  computes cluster-aware.

### Release B — Serial staging, engine through .ork (~1–2 sessions)

- **Tree format:** `RocketTree.components` may now contain `stage` nodes
  (each holding today's component chain). Backward compatible: a tree whose
  top level has no stage nodes is wrapped in one implicit stage — old
  sessions and saved designs keep working untouched.
- **Bridge:** build one `AxialStage` per stage node; per-stage separation
  config (trigger + delay); per-mount ignition config (AUTOMATIC default,
  optional override + delay); **multi-branch flight output** — `branches[]`
  with name, events, series, per-branch apogee/ground-hit, alongside the
  existing branch-0 summary (back-compat).
- **Differential:** new 2-stage golden scenario exercising separation, upper
  ignition, booster tumble AND booster-with-chute branches. Determinism
  review (branch fork order is deterministic in the kernel; our
  LinkedHashMap patch already covers the known hazard).
- **`.ork`:** import ALL stages (drop the "imported the first" limitation),
  separation elements, per-mount motors; export multiple `<stage>` blocks +
  `<separationevent>/<separationdelay>` + per-mount `<motormount>` blocks.
  Desktop round-trip via the mass/CG-parity harness, per house rule.

### Release C — Staging UI + per-branch reporting (~1–2 sessions)

- **Tree editor:** stage rows ("Add stage" appends a booster below), stage
  rename, per-stage add menus; 2D/3D stack stages axially (the render
  cursor already accumulates — stages just extend the chain).
- **Motor panel becomes per-mount:** each motor mount (across stages) lists
  its assigned motor with its own picker/delay/auto-delay; sustainer ignition
  stays AUTOMATIC unless overridden. Session state grows a motor map.
- **Launch report:** per-branch sections — sustainer keeps today's full
  report; each booster branch gets its own deployment table, descent rates,
  landing verdict (same SAFETY thresholds, device-named per Eric's rules).
  Saved-run/CSV grow booster landing columns.
- **Batch simulate:** default = vary the SUSTAINER motor, boosters held
  fixed (question 4 below).

Parallel staging (side boosters) and pods exist in the kernel too but are
deliberately out of scope for A–C (question 5).

## Eric's decisions (2026-07-03)

1. **Order: clusters first**, then staging. Parallel/strap-on boosters and
   pods deferred until clusters and staging both work.
2. **Sustainer ignition: electronics-timed is THE standard in high power.**
   Nobody ignites an HPR sustainer off the booster's ejection charge — that
   pattern belongs to low/mid power (where it's common). UI defaults must
   therefore be POWER-CLASS AWARE: low/mid power → AUTOMATIC (ejection
   charge); high power → timed ignition (booster burnout + user delay,
   modeling the onboard timer/altimeter).
3. **Booster recovery: also power-class dependent.** Low/mid boosters often
   tumble-recover (fine, no warning). HIGH-POWER BOOSTERS MUST HAVE ACTIVE
   RECOVERY — a chuteless HPR booster gets a loud HIGH warning.
4. **The low/mid vs high power line: the G80 motor.** Anything smaller is
   low/mid; anything larger is high power. This matches the certification
   rule, so it's computable from motor data: **high power ⇔ average thrust
   > 80 N or total impulse > 160 Ns** (the G80 itself sits exactly at the
   boundary and counts as low/mid).
5. **Batch simulation: NOT on staged rockets** (combinatorics explode).
   Batch on clustered rockets IS wanted — one motor swept for the cluster,
   thrust/mass ×N automatic.
6. **Mixed motors in one cluster must stay SYMMETRIC** — thrust vectors must
   balance or the rocket cants off axis (e.g. a 6-motor ring flown as 3+3 of
   two types in alternating/opposite tubes). Modeling: one motor per
   clustered mount stays the rule (desktop parity); a mixed symmetric array
   is expressed as TWO clustered mounts (e.g. two 3-star patterns rotated
   60°), which needs the per-mount motor assignment UI that arrives with
   Release C. Until then: single motor type per cluster.

## Research references (file-level details)

Three audit reports back this doc (kernel mechanics, bridge gaps, app gaps).
Key anchors: kernel separation fork `BasicEventSimulationEngine` STAGE_SEPARATION
case; ignition chain `IgnitionEvent.AUTOMATIC`; cluster thrust
`MotorClusterState.getThrust` (thrust × count); our single-stage creation
`engine-java/src/api/java/api/OrkEngine.buildRocket`; branch-0-only output
`OrkEngine.flightDataToJson`; app sentinel `'stage'` in `tree/treeModel.ts`;
first-stage-only import `services/orkFile.ts:66-69`.
