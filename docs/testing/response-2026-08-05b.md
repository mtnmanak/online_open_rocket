# Response — issues-2026-08-05b.md (→ v0.034)

Everything decidable was built same-day. Item-by-item, following your file.

---

## 3. Customizable highlighted tiles — **BUILT (the tiles row, as you meant)**

The highlighted boxes under the tab row are now customizable: a **⚙ button**
at the end of the tiles row opens a picker with **14 metrics** — apogee, max
velocity, max Mach, max accel, apogee-at, landing rate, drogue descent,
flight time, pad weight, **recovery weight**, **thrust:weight**,
**guide-departure velocity**, static margin, optimal delay. Checked = shown,
in a fixed sensible order; your picks persist with your preferences. Your
stated set (apogee, max velocity, recovery weight, T:W, guide departure) is
five clicks away.

Two fixes that fell out of it:
- **"Descent hits" is gone** — you were right that it was a weird label. It
  was the ground-hit velocity; the tile is now **"Landing rate"** (final
  descent under the last deployed device — the main, in a dual-deploy). The
  drogue rate is its own separate tile if you want it.
- Opening a **stored run** from the history now shows the tiles too (they
  used to appear only after a fresh launch).

## 4 (+6). Stability thresholds — **CONFIRMED, no further change**

Locked in as shipped: red ⚠ under 1.0 cal, green ✓ 1.0–3.0, yellow △ caution
above 3.0, one rule on every surface. (The wind-aware idea from the response
doc stays on the shelf unless you want it.)

## 9. Tagline + Kbf default — **DONE**

- Header now reads: *"Design, simulate, fly — OpenRocket-derived physics,
  validated to Mach 4.6 against NASA wind-tunnel data."* (also the PWA's
  meta description).
- **Rogers Kbf is now ON by default.** Your flight-data observation is
  exactly the evidence that matters. Details of the migration: anyone who
  explicitly turned it OFF stays off; everyone else (including fresh
  installs) gets it on. The Preferences hint now says it plainly: on by
  default, turn off for exact desktop parity. Every saved run records
  which way it flew (the "+kbf" tag in the CSV), so your old and new runs
  stay distinguishable. Note the differential-parity guarantee is
  unaffected — the harness sets flags explicitly.
- Expect small shifts: stability reads a bit higher (more aft CP) on
  everything you re-fly. That's the model working, not a regression.

## 11. Partial overrides in RockSim export — **DONE**

When only one of mass/CG is overridden, the exporter now writes the
**calculated** value for the other (threaded from the engine's per-component
info at export time). RockSim couples both under one flag, so the missing
half used to export as 0 — which pinned the CG to the component's front in
real RockSim. Regression-tested.

## 13. Aft view zoom — **DONE**

Wheel-zoom about the cursor, drag-to-pan once zoomed, and the same + / − / ⤢
buttons as the side view. Works in both the Design-tab Aft view and the
Motors-tab cluster inset.

## 14. Spill holes — **BUILT (it was easy AND productive)**

Every parachute now has a **Spill hole ⌀** field. Physics: the standard
effective-area reduction — Cd_eff = Cd · (1 − (hole⌀/canopy⌀)²) — applied at
the engine boundary; this is the same treatment RockSim uses, so imported
RockSim chutes now keep their spill holes (and export them back). A 100 mm
hole in a 450 mm chute costs ~5% of the drag; a 150 mm hole in a 360 mm
chute costs ~17% — real enough to matter on marginal landings. .ork files
carry it as one of our extension tags (desktop warns-and-ignores).

Does it "make a difference"? For descent rate, only what the formula says —
usually single-digit percent. The real-world reasons for spill holes
(stability of the descent, reduced oscillation) aren't modeled by any
descent-rate calculator, ours included; the guide note says so.

## RockSim gap audit — **recommend yes; here's the shape**

Worth doing once, systematically. Proposal: same protocol as the
attribute-audit — one research pass over the RockSim format docs + the
desktop's RockSim reader, producing `docs/testing/rocksim-gap-audit.md` with
every RockSim feature vs. our support (import / export / simulate), each
tagged supported / degraded / dropped, with a fix-cost estimate. From memory
the known gaps are now short: ring tails, external pods (RockSim-style),
per-fin cant on import, subsonic-only Mach handling, and their custom
turbulence model (which we'd not want). Say go and it's a half-session; the
audit doc then becomes the backlog.

## 18. Camera shrouds — **BUILT (v1, full physics)**

The component you asked for exists: **Add → Camera shroud / fairing** on any
body tube. Length, width, height (off the surface), three shapes
(streamlined / half-round / box), **as-built mass** (printed parts weigh what
they weigh), plus your exterior requirements: **solid outline** in every
view, display **color**, and **surface finish**.

The physics — and this is the part nobody else has:
- **CP shift is real, not bolted on.** The shroud's side profile is lowered
  into the simulation as a one-panel slender strake, flown through the
  kernel's own low-aspect-ratio fin lift. At shroud aspect ratios Barrowman's
  fin equation reduces to the classic **Jones slender-body model — which IS
  the established strake model** — so the CP shift comes out of the same
  validated machinery as everything else, at the shroud's true position and
  size. Your intuition is confirmed by the numbers: on a 4-in rocket a
  typical 80×25×20 mm shroud moves CP measurably; on a 7.5-in bird it's
  noise.
- **Drag** uses Hoerner's protuberance coefficients on the frontal area
  (streamlined 0.25 / half-round 0.55 / box 1.05, boundary-layer
  interference included), fed to the kernel as a component-CD override.
- **Honesty note** (also in the guide): there's no wind-tunnel anchor for
  this model yet — the strake lift is textbook physics and the drag numbers
  are Hoerner's measured values, but the combination is our own. Treat it as
  a good engineering estimate and keep margin on small airframes. If you can
  fly a before/after with the same motor and log apogee, that's our first
  calibration point.

File behavior, per your agreement: `.ork` round-trips it as our extension
element (desktop warns-and-skips); RockSim export degrades gracefully to a
**mass object** so CG survives; RASAero export (aero-only format) drops it.
Radial mounting angle is not modeled (like launch lugs) — it draws on the
top surface. That's the one v2 candidate if you need shrouds clocked
relative to fins or rail buttons.

## 21. Glyphs, patterns, selection sync — **ALL THREE BUILT**

- **Miniature glyphs** (your pick): parachutes draw a canopy with shroud
  lines, mass components a weight block with a handle, centering rings their
  ring cross-section, shock cords a zigzag — whenever the box has room
  (the colored text tag stays for tight fits and the other types).
- **Fill pattern**: bulkheads get the engineering diagonal hatch.
- **Two-way selection**: click any component in the 2D drawing — hull, fins,
  shroud, or any dashed inner box — and it selects in the tree; select in
  the tree and it highlights in the drawing with an accent outline. Click
  and drag stay compatible (a real drag never changes the selection).

## Batch + clusters (your question) — **a motor in EVERY tube**

The batch simulator loads each candidate motor into the cluster mount, and
the kernel's cluster configuration fires **one motor per tube** — thrust,
mass and CG all ×N at the true tube positions. The "Motors (cluster)" column
in the CSV records the count. So a 4-motor cluster batch compares candidates
as 4× flights, which is the honest comparison.

## Mixed-motor combination batching (deeper question) — **feasible; here's the honest cost**

Your 2+2 example is exactly the case our motor model already supports —
today you'd build it as **two 2-tube cluster mounts** (the "mixed arrays =
two clustered mounts" rule from the staging work), assign H100s to one and
H210s to the other, and fly it. What's missing is only the BATCH layer.

Feasibility: **good.** The sim core needs nothing new — a combination run is
just `setMotorById(mountA, m1); setMotorById(mountB, m2); simulate()`. The
work is UI + combinatorics policy:
- Full cross-product explodes fast (20 candidates × 20 = 400 sims ≈ 30–60 s
  — actually tolerable; 3 mounts × 20³ = 8,000 — not).
- The sane v1: pick a candidate SET per mount (with your existing filters),
  cap the cross-product (~500 sims), enforce your symmetry rule (identical
  motors within each cluster mount — the engine already models it that way),
  and reuse the existing batch table/CSV with one motor column per mount.
- Estimate: about a session. Ignition timing per combination (both at
  liftoff vs. airstart) adds another half-session if you want it batched too.

Not built now per your instruction — say go when you want it.

---

## Test state

App 145 + engine 23 = **168 tests green** (7 new this batch: fairing
lowering ×2, spill-hole transform ×3, extension round-trip, RockSim partial
override). Engine package re-typechecked for the new component type — the
TeaVM kernel itself is untouched (the shroud lowers to existing kernel
primitives), so the differential stands at 258 lines unchanged.
v0.034 zip: `deploy/online-openrocket-v0.034.zip`.

## Waiting on you

1. **RockSim gap audit** — go / no-go (half-session).
2. **Combination batch** — go / no-go (~a session, v1 scope above).
3. **Results customization** — the tile picker shipped; if you also want
   pinned rows in the detailed report, say so.
4. Fly a shroud before/after when you get a chance — first calibration
   point for the new model.
