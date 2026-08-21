# Scope: full multi-config `.ork` import

*2026-08-21, per Eric's "yes, scope it" in `docs/testing/issues-2026-08-21b.md`. This is a
scope, not a build — nothing here is implemented. Facts below were verified against
current code and the OpenRocket 24.12 reference source in a dedicated research pass.*

## What "multi-config" means in a real `.ork`

A desktop flight configuration is an id that up to three component types key overrides
off: **motor + ignition per mount**, **deployment per recovery device**, **separation per
stage** — plus per-config **stage activeness** (`<stage number active="false"/>`, what
makes "sustainer only" vs "full stack" different flights) and a per-simulation
`<configid>` binding. The desktop's file convention is *defaults bare, overrides in
per-config blocks* (mount and recovery savers skip default-equal configs; the stage saver
does not — its `isDefault` skip is commented out in 24.12).

## Where we are today (the honest-note behavior, v0.046)

- Import keeps the **first `<motor>` in document order** and annotates "File has N flight
  configurations — kept X" (`orkFile.ts:487-517`). Three first-element grabs don't check
  `configid` at all — motor `:98`, ignition `:102`, separation `:468` — so on a
  multi-config file the kept motor can silently pair with *another config's* ignition
  override. `<deploymentconfiguration>` is invisible in **both** directions
  (`readDeployment` reads only bare tags; export emits only bare tags). Stage activeness
  is never read; export always writes `active="true"`.
- The app has **no configuration object**: motors live in `mountMotors` React state,
  deployment/separation live as *properties on tree nodes* (schema-driven PropertyPanel),
  launch conditions in their own state. Export mints a fresh `configId = uuid()` per save.
- The engine bridge is **one config per rocket handle, hard-wired** (`OrkEngine.java`
  RocketCtx holds a single `fcid`; no stage-activeness or config API is `@JSExport`ed —
  the compiled kernel contains the full machinery, unexposed).
- No multi-config fixture exists anywhere in the repo; the only test data is the inline
  `TWO_CONFIGS` string, and four tests pin the current drop-note wording by exact text
  (`orkFile.test.ts:656-718`) — the feature *replaces* that describe block.

## Staged scope

### Stage A — read all configs correctly, let the user pick one at import (M)

Import parses every config with the desktop's default-with-fallback semantics (the three
different saver conventions above), presents a picker when a file has more than one
("Which configuration do you want? Club field C6 / Demo day D12 / …", default = the
file's `default="true"`), and applies the **chosen** config's motor+ignition+
separation+deployment — instead of whatever elements came first. Export stays
single-config. This fixes the silent cross-config mismatches, makes
`<deploymentconfiguration>` readable (dual-deploy altitude is one of the commonest
reasons people keep two configs), and delivers most of the user-visible value for a
fraction of the cost. Touches: `orkFile.ts` import paths, one modal (the `shareOffer`
modal is the pattern), `applyImported`, a real multi-config fixture, and the rewritten
honesty tests. No engine change; stage activeness still ignored (note it in the import
note when a config deactivates stages).

### Stage B — named configurations as app state, motors axis (M–L)

`configs[]` + `activeConfigId` in app state; per-config motors/ignition (the
`mountMotors` slice becomes per-config); a config picker in the vitals strip and Motors
tab; stable config ids through save (replacing the per-save `uuid()`); session-autosave
migration (third shim, the established pattern); export emits N `<motorconfiguration>`
blocks + per-config motors/simulations — at which point share links carry configs for
free (they encode the `.ork` itself). Config switch rebuilds the engine handle (the
BatchSimulate precedent; a switch pays one `buildTree` + `staticInfo`, acceptable).
Two collisions to settle: `SimRun.motorConfig` already means *cluster combination* (new
field + CSV header decision), and undo history only tracks the tree (config edits either
join it or are documented as un-undoable). Deployment/separation stay single-valued
(bare defaults), documented as such.

### Stage C — full parity: per-config deployment/separation + stage activeness (L, gated)

Needs the engine bridge to grow config-aware APIs (`OrkEngine.java` + `orkEngine.ts` +
committed-artifact rebuild + full differential pass) — the expensive ritual. Rides the
**next engine rebuild** alongside the already-queued geodetic selector, not before. Also
the point where PropertyPanel's direct-write of `deployEvent` on tree nodes must become
config-aware (shadowing node props), which is the hairiest UI change in the whole
feature.

## Recommendation

Stage A is the item worth green-lighting on its own — it is the *import* fidelity Eric's
issue actually names, it kills real silent-wrongness, and it doesn't commit the app to a
configuration model before beta feedback shows how people actually use configs. Stages
B/C are a direction, not a promise; revisit after the public beta (B) and the next
engine rebuild (C).
