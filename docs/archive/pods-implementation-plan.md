# Pods / Parallel Stages — Implementation Plan

> Produced by the multi-agent pods-design pass (2026-07-05), grounded in the OpenRocket 24.12 reference source, our engine bridge, and the app tree/render/file layers. The critique below found two P0 bugs in the Phase-4 bridge design — read it before implementing Phase 4.

All five reports check out against the actual source. Here is the synthesized plan.

# Implementation Plan — Parallel Stages / Strap-On Boosters / Pods

## 1. Goal & scope

**What a user will be able to do when done:** attach an off-centerline **Pod Set** (non-separating external pod: camera pod, side electronics, permanent strap-on) or **Booster** (`ParallelStage` — a separable strap-on that drops and flies its own trajectory) onto any body component; set its ring **count**, **radial distance + method**, **angle**, and **axial position**; give it a body-tube sub-chain with its own fins/motor mount; see it drawn off-axis in 2D and 3D; simulate it (boosters spawn their own flight branch on separation, pods just add mass/inertia/drag); and round-trip it through `.ork`.

**Already exists — do NOT rebuild:**
- **Serial staging** (`type:'stage'` top-level nodes, `applySeparationConfig`, per-stage flight branches, separation config UI fields on `stage`) — reuse verbatim for booster separation.
- **Clusters** (`cluster.ts`, `clusterOffsets`, the `TreeSchematic`/`Rocket3D` per-instance draw loops) — the cross-section ring math and the "draw child N times at offsets" pattern already exist.
- **The kernel is complete.** `ParallelStage`, `PodSet`, `ComponentAssembly`, `RadiusMethod`, `AngleMethod`, `RingInstanceable`, `MassCalculation` off-axis transforms, `BarrowmanCalculator` pod handling, and the branch-based separation engine are all carved and compiled. **No carved-source edits, no kernel patches.** The only kernel-adjacent work is the editable bridge (`ComponentFactory.java` / `OrkEngine.java`).

**Out of scope (park):** true off-axis aero lift/CP/interference (Barrowman is axisymmetric — desktop only scales drag ×count; our differential test will still match desktop because both share this approximation). "Split boosters" button. `MIRROR_XY`/`COAXIAL`/`SURFACE` angle/radius methods beyond the two the desktop exposes (`FREE`/`RELATIVE`, `RELATIVE`/`FIXED`).

---

## 2. Data model

### Two new component types

Add to the `ComponentType` union (`packages/engine/src/orkEngine.ts:99`):

```ts
  | 'podset' | 'parallelstage'
```

They differ only in physics/behavior; their geometry controls are identical:

| | `podset` (`PodSet extends ComponentAssembly`) | `parallelstage` (`ParallelStage extends AxialStage`) |
|---|---|---|
| Separates | No — flies rigidly with parent | Yes — own stage number, separation config, flight branch |
| Extra fields | none | `separationEvent`, `separationDelay`, `separationAltitude` (reuse `stage`'s) |
| DISPLAY_NAME | `'Pod set'` | `'Booster (parallel stage)'` |

### Fields (UI units → stored SI, per the standing boundary rule)

| Field key | UI unit | Stored | Kernel target | Default |
|---|---|---|---|---|
| `instanceCount` | count (`smin:1`) | int | `setInstanceCount(int)` (auto-spaces `2π/count`) | `2` |
| `radialDistance` | mm | m | `setRadius(method, m)` | `0.03` |
| `radiusMethod` | select `relative`/`free` | enum | `setRadiusMethod(RadiusMethod)` | `relative` |
| `angleOffset` | deg | rad | `setAngleOffset(rad)` | `0` |
| `angleMethod` | select `relative`/`fixed` | enum | `setAngleMethod(AngleMethod)` | `relative` |
| `position` (existing `ComponentPosition`) | mm + method | m | `setAxialMethod`/`setAxialOffset` (reuse `create():386-390`) | `{method:'bottom', offset:0}` |
| `separationEvent`/`separationDelay`/`separationAltitude` (parallelstage only) | select/s/m | — | `applySeparationConfig` (reuse) | `ejection` |

> **`radiusMethod` is load-bearing and must be persisted.** `RELATIVE` (default) means `radiusOffset` is a *gap* — resolved radius = `radiusOffset + parentOuterRadius + assemblyBoundingRadius` (0 = surfaces touch). `FREE` means raw distance from parent centerline. Drop the method and geometry shifts silently on reload. `assemblyBoundingRadius` = max outer radius of the assembly's own body-tube/transition children.

`defaultParams` (SI):
```ts
case 'podset':        return { instanceCount: 2, radialDistance: 0.03, radiusMethod: 'relative',
                               angleOffset: 0, angleMethod: 'relative', position: { method: 'bottom', offset: 0 }, children: [] };
case 'parallelstage': return { ...same..., separationEvent: 'ejection', separationDelay: 0, children: [] };
```

### Containment (`schema.ts:41`)

```ts
  bodytube:      [...BODY_CHILDREN, 'podset', 'parallelstage'],
  nosecone:      [...INTERNAL, 'podset', 'parallelstage'],   // optional
  transition:    [...INTERNAL, 'podset', 'parallelstage'],   // optional
  podset:        STAGE_CHILDREN,   // ['nosecone','bodytube','transition']
  parallelstage: STAGE_CHILDREN,
```
Kernel `isCompatible` restricts both to `BodyComponent` children — `STAGE_CHILDREN` already matches exactly. Fins/mounts/recovery go inside the pod's **body tube**, not directly under the assembly. Add both to `POSITIONABLE` (`schema.ts:293`) since they carry an axial `position`.

### Invariant reconciliation — **no conflict**

`tree.components === stage nodes` is a **top-level-only** invariant (enforced by `normalizeTree`, `buildRocket`'s "every top-level node is a stage" check, `asStageNodes`). Both new types are **always nested** under a body component, exactly as the kernel models them (`PodSet`/`ParallelStage` are children of a `BodyComponent`, never children of `Rocket`). Do **not** reuse `type:'stage'` for boosters. Consequence: `normalizeTree`'s three top-level branches need **zero change**; `stages()`, `addStage`, `stageIndexOf` stay correct. The generic recursers (`reseedIds`, `findNode`, `findParent`, `updateNode`/`removeNode`/`addChild`/`moveNode`/`duplicateNode`/`cloneSubtree`, `motorMounts`) already walk `children` — pod subtrees get ids, clone, and expose motor mounts for free. **`treeModel.ts` needs no edits.**

---

## 3. Phased build

Ordering principle: **all pure-TS work that the app can render/persist with a `podset`/`parallelstage` node lands first behind the kernel gap** (a pod built through the current bridge would throw `"Unknown component type"`), so Phase 1 must *not* send these nodes to `buildRocket` yet — it's schema + tree + rendering only, testable via the tree editor and unit tests without touching the engine. The **single heavy/risky step (Phase 4, TeaVM rebuild)** is isolated and comes after everything that can be validated without it.

> **Sequencing note on the bridge gate:** until Phase 4, `OrkRocket.buildTree` will throw if a `podset`/`parallelstage` node reaches the engine. Phases 1–3 are validated through the tree model, schema unit tests, and the renderers (which read the app tree, not the engine). To keep the app runnable during 1–3, have `buildTree`/its caller **strip or skip** unbuilt assembly nodes (temporary guard, removed in Phase 4). Call this out in the Phase 1 PR.

---

### Phase 1 — Tree schema & data model (pure TS, **no rebuild**)

**Files:** `packages/engine/src/orkEngine.ts` (union), `packages/app/src/tree/schema.ts` (DISPLAY_NAME, FIELDS, CONTAINMENT, POSITIONABLE, defaultParams), `packages/app/src/tree/position.ts`.

**Changes:**
- Extend `ComponentType` (forces exhaustive TS errors in the three total `Record<ComponentType,…>` tables — lean on this as the safety net; fill DISPLAY_NAME, FIELDS, defaultParams).
- Add FIELDS entries: `instanceCount`, `radialDistance` (`lenMM`), `radiusMethod` (select), `angleOffset` (deg), `angleMethod` (select); `parallelstage` additionally gets the two/three separation fields the `stage` type already declares (`schema.ts:151-154`).
- Add CONTAINMENT + POSITIONABLE entries as in §2.
- **`position.ts` — the one real gap.** `resolveAbsolutePositions` sequentially stacks *top-level* stage chains nose-to-tail (`:74-86`) but `fixChildren` (`:55`) only converts `absolute`→`top` and does **not** stack a nested chain. A pod's internal nose→tube→tube chain is a mini-rocket that must stack along the **pod's own local axis**. Factor the top-level `map` body into a reusable `layoutChain(children, x0)` and invoke it for pod children rooted at the pod's axial start.

**Temporary guard:** strip `podset`/`parallelstage` from the tree before `buildTree` (removed Phase 4).

**Tests:** schema unit tests (containment allows/denies correctly; defaultParams round-trip); a `position.ts` test asserting a pod's internal chain stacks in its local frame; `normalizeTree` test proving a nested pod does not perturb the top-level stage invariant.

---

### Phase 2 — Off-axis 2D & 3D rendering (pure TS, **no rebuild**)

**Files:** `packages/app/src/tree/cluster.ts` (add `ringInstanceOffsets`), `packages/app/src/components/TreeSchematic.tsx`, `packages/app/src/components/Rocket3D.tsx`.

The single biggest change in both renderers is identical: **the root axial-chain builder is currently inlined and root-only; refactor it to accept an origin**, then call it once per pod instance.

**`cluster.ts`** — add alongside `clusterOffsets`, reusing the same rotation convention:
```ts
export function ringInstanceOffsets(count, radialDistance, angleOffset = 0): {y,z}[] {
  // a = angleOffset + 2π·i/count;  {y: r·cos a, z: r·sin a}
}
```
Draw-only (physics reads the kernel's own instancing).

**2D (`TreeSchematic.tsx`):**
- Factor the inline root nose/body/transition loop (`313-347`) into `renderChain(nodes, xStart, yCenterPx, opacity?)` — `ctx.cy` becomes a passed-in `yc`.
- Add a `podset`/`parallelstage` case in `renderChildren`: compute `radius` (mirror `RadiusMethod.RELATIVE`), `count`, per-instance `angle[i]`; project onto screen-vertical `yc = cy − radius·cos(angle[i])·scale` (reuse the cluster projection convention — ignores depth `off.z`); call `renderChain(child.children, axialStart, yc)` per instance.
- Extend the `totalLen`/`maxR` measure pass (`73-89`) to walk assemblies (radial extent `radius + boundingRadius`, and aft-extending pods) so pods don't clip the viewBox.
- Accept the projection collapse (pod at 90° overlaps body — same limitation clusters have today). **Recommended follow-up (park):** a small cross-section end-view inset drawn from `{y,z}` — the only unambiguous way to show angular arrangement.

**3D (`Rocket3D.tsx`) — easier and fully correct (no projection loss):**
- Factor the root chain (`148-195`) into `addChain(nodes, xStart, transform: THREE.Matrix4)`.
- Add a pod case in `addChildren`: per instance build `makeRotationX(angle[i]) · makeTranslation(0, radius, 0)` (plus the assembly's axial offset) — the **exact primitive `addFins` already uses** (`translate` then `makeRotationX`), lifted from one fin to a whole sub-chain; recurse `addChain(child.children, axialStart, instanceMatrix)`.
- Pod pieces flow through the same `maxR` (camera framing) and the same `useEffect` geometry-disposal cleanup (keys stay unique via the `k++` counter). **Bonus:** because `buildPieces` is shared with the OBJ exporter, pods get OBJ export for free.

**Tests:** `ringInstanceOffsets` unit test (2 → 180° apart, 3 → 120°, angleOffset phase-shift); a render smoke test that a 2-pod tree produces 2× the child pieces at the expected `y` offsets. Manual: `/run` the app, eyeball a 2-booster rocket in 2D + 3D.

---

### Phase 3 — Assembly config UI panel (pure TS, **no rebuild**)

**Files:** the property-panel component (consumes `FIELDS`) + add-component menu.

- Add "Pod Set" and "Booster" to the add-component menu, enabled only when a body component is selected.
- The property panel renders the new FIELDS automatically (it's data-driven). Verify unit chips: `radialDistance` mm↔m, `angleOffset` deg↔rad, `instanceCount` integer spinner min 1. Mirror desktop defaults (count 2, RELATIVE, offset 0 = flush, angle 0, axial BOTTOM → two tangent boosters 180° apart, aft-aligned, zero further input).
- `parallelstage` shows the separation sub-panel (reuse `stage`'s separation fields).

**Tests:** panel renders all fields for both types; editing a field updates the node; deg/rad + mm/m conversions correct at the boundary.

---

### Phase 4 — Bridge + TeaVM ENGINE REBUILD ⚠️ **HEAVY / RISKY — the one gated step**

This is the only phase that regenerates the kernel JS. It makes previously-dead `PodSet`/`ParallelStage` code **reachable**, so the full ritual is **mandatory** and the 229-line differential test **must stay green**.

**Files (all editable — none carved):**
- **`engine-java/src/api/java/api/ComponentFactory.java`** — add `case "podset":` → `new PodSet()` and `case "parallelstage":` → `new ParallelStage()` to the `create()` switch (currently hits `default:` → throws). Add a shared helper `applyRadial(RocketComponent c, node)`, gated by `instanceof RingInstanceable`/`RadiusPositionable`, reading `instanceCount` → `setInstanceCount`, `radiusMethod`+`radialDistance` → `setRadius(method, m)` (**one call sets both** — read method first, default RELATIVE), `angleOffset` (rad) → `setAngleOffset`, `angleMethod` → `setAngleMethod`. `attachChildren` already recurses generically via `addChild`, so children just work.
- **`engine-java/src/api/java/api/OrkEngine.java`** — **no stage-loop change**: route both types through the node tree so they nest under a bodytube/stage via `attachChildren` (confirmed `AxialStage.addChild` accepts a `ParallelStage`). For `parallelstage`, invoke the existing `applySeparationConfig` (currently stage-only, `:201`) at creation time.

**Remove the Phase-1 temporary `buildTree` guard.**

**The rebuild ritual (in order):**
1. `node engine-java/scripts/carve.mjs` (idempotent)
2. `JAVA_HOME` → portable JDK (`C:\Users\Eric\.online-openrocket\jdk-17.0.19+10`); `engine-java/gradlew.bat generateJavaScript`
3. `npm run build` (build-engine then app)
4. `node engine-java/scripts/difftest.mjs` — **JVM vs TeaVM bit-identical must stay green.**

**Preserve the two mandated build knobs** (`optimization = NONE`, `fastGlobalAnalysis = true`) — do not touch. Any change requires a full differential pass.

**Add a golden fixture** (recommended, and effectively required): the current differential harness likely does not exercise `ComponentAssemblyCalc` / off-axis `MassCalculation`. Add a pod + parallel-stage case to `GoldenMain`/golden fixtures so `difftest.mjs` actually covers the newly-reachable assembly and off-axis mass/inertia code. Without this, the rebuild could pass while the new code paths go untested.

**Verify off-axis physics is live:** a symmetric ring keeps CG on-axis but adds transverse/roll inertia via parallel-axis (`RigidBody.add`); a 1-instance or asymmetric pod shifts CG laterally; drag scales ×count. Assert these against desktop in the fixture.

**Tests:** golden fixture green; an integration test that a 2-booster tree builds without throwing and returns a sim result; a test that a separating booster produces an **additional `FlightDataBranch`**.

---

### Phase 5 — `.ork` import/export round-trip (pure TS, **no rebuild**)

**File:** `packages/app/src/services/orkFile.ts` (+ update `orkFile.test.ts`).

The kernel savers/loaders already encode both; the app currently drops the tags via `ignored.add(el.tagName)`.

**Element names & nesting:**
- `PodSet` → `<podset>` (nests in a body component's `<subcomponents>`).
- `ParallelStage` → `<parallelstage>` (loader also accepts legacy `<boosterset>`); nests as a sibling of body components inside a `<stage>`.
- Centerline `AxialStage` stays `<stage>` (unchanged).

**Params (watch the unit boundary):**
- `<instancecount>` — integer.
- `<radiusoffset method="relative|free|surface|coaxial">` — **metres** (SI, no conversion).
- `<angleoffset method="relative|fixed">` — **DEGREES** on disk → convert to/from radians on import/export (same pattern as existing `cant`/`clusterRotation`).
- `<axialoffset method>` + legacy `<position type>` — reuse existing `readPosition`/`position`.
- **Do NOT emit `<color>`/`<linestyle>`** (savers skip these for any `ComponentAssembly`) and **no `<radialdirection>`** (explicitly suppressed for these types).
- `<parallelstage>` also carries the separation block — reuse the booster-`<stage>` sep read/write already in `orkFile.ts` **verbatim**.

**Import:** add `podset`/`parallelstage`/`boosterset` cases in `convertElement`; `convertChildren` recurses generically so inner nose/body/fins import automatically; confirm `readMotor` fires for pod-mounted inner tubes (per-innertube — it does). Land under `stage.children`, never at `tree.components` top level.

**Export:** add cases to `emitNode` (instancecount, radiusoffset+method, angleoffset in **degrees**, axialoffset+position, parallelstage separation via existing `sep()` helper), then `emitChildren`.

**Tests:** round-trip a pod and a booster `.ork` (import → export → re-import, node-equal); import a real desktop `.ork` with a `<parallelstage>` and assert geometry + separation survive; replace the current `orkFile.test.ts:134,148` assertions that *expect* podset to be ignored.

---

## 4. Reuse map

| Need | Lift from |
|---|---|
| Cross-section ring `{y,z}` offsets | `cluster.ts` `clusterOffsets` / `ring()` → new `ringInstanceOffsets` |
| Per-instance rotate-about-X of a whole sub-chain | `addFins` loop in `Rocket3D.tsx` (`116-123`) — same `translate`+`makeRotationX` primitive |
| 2D "project radial onto screen-vertical, ignore depth" | clustered-inner-tube branch in `TreeSchematic.renderChildren` |
| Draw/build a chain at an arbitrary origin | factor root loops (`TreeSchematic 313-347`, `Rocket3D 148-195`) → `renderChain`/`addChain` |
| Serial-stage separation config (UI, bridge, `.ork`) | `stage` FIELDS (`schema.ts:151-154`), `applySeparationConfig` (`OrkEngine.java:201`), booster-`<stage>` sep in `orkFile.ts` — all reusable verbatim for `parallelstage` |
| Branch-based flight sim / N-branch results | existing serial-stage `FlightDataBranch` machinery — `ParallelStage` IS an `AxialStage`, so dropped boosters spawn branches automatically |
| Generic tree ops (ids, clone, motor-mount discovery, nesting) | `treeModel.ts` recursers — **no edits needed** |
| deg↔rad / mm↔m boundary conversion | `cant`, `clusterRotation` handling (established pattern) |
| Radial gap → physical radius | mirror `RadiusMethod.RELATIVE`: `offset + parentOuterRadius + boundingRadius`; compute once, share 2D/3D/`.ork` |

---

## 5. Risks & open questions

- **TeaVM rebuild is the only high-risk step (Phase 4).** DCE has pruned the assembly constructors/setters; making them reachable regenerates a large surface. Mitigate by adding the golden fixture *first* so the differential test actually covers off-axis mass/inertia, and by never touching `optimization`/`fastGlobalAnalysis`. If the differential goes red, the failure is in newly-reachable `ComponentAssemblyCalc`/`MassCalculation` paths — diff the JVM vs JS golden output for the pod fixture.
- **Do parallel boosters need separate flight branches? — Yes, and it's free.** `ParallelStage extends AxialStage` with its own stage number + `StageSeparationConfiguration`; the branch-based `BasicEventSimulationEngine` spawns a new `SimulationStatus`/`FlightDataBranch` on `STAGE_SEPARATION` exactly like a serial stage. **`PodSet` never separates and never branches.** → **Results UI must handle N branches** (core + each dropped booster). Confirm the current results view isn't hard-coded to one trajectory. **Note:** batch-sim is currently disabled on staged rockets (combinatorics) — a `parallelstage` makes a rocket "staged," so it inherits that restriction. Flag to Eric.
- **`stageIndexOf` returns the enclosing top-level axial stage**, not a parallel-stage-aware index. Fine for geometry/first cut; staging UI + branch-coloring may later need a `parallelstage`-aware index. Not a blocker.
- **Aero is approximate for off-axis bodies** — Barrowman scales drag ×count but models no true off-axis lift/CP/interference. The differential test will *still match desktop* (shared approximation), so it won't catch aero error here — don't expect it to. **Keep the geometry warnings wired** (`PODSET_FORWARD`, `PODSET_OVERLAP`, `AIRFRAME_GAP`, `DIAMETER_DISCONTINUITY`, `SEPARATION_ORDER`, `EARLY_SEPARATION`) — they're OpenRocket's signal that the approximation is being stretched; surface them in `warningTexts`.
- **AFTER is forbidden for these types** — `ComponentAssembly.setAxialMethod` downgrades an `AFTER` request to `TOP`; `PodSet.getAxialOffset` throws `BugException` if it ever finds itself `isAfter()`. UI must offer only Top/Middle/Bottom (+ absolute) for assembly axial method, never After.
- **2D projection ambiguity** (pod at 90° overlaps body). Accepted for v1 (clusters have it today). Cross-section end-view inset is the honest fix — parked follow-up.
- **Eric decisions:** (a) expose only `FREE`/`RELATIVE` radius + `RELATIVE`/`FIXED` angle (desktop parity), or the full enum? Recommend desktop parity. (b) Add the end-view inset now or park it? (c) "Split boosters" button — park. (d) Confirm the batch-sim-disabled-on-staged interaction is acceptable.

---

## 6. First-increment definition of done

**Ship Phase 1 alone: the `podset` + `parallelstage` tree schema & data model, no engine rebuild, no rendering.**

Done when:
1. `ComponentType` includes both; all three total `Record<ComponentType,…>` tables compile (TS exhaustiveness satisfied) with DISPLAY_NAME, FIELDS, defaultParams filled.
2. CONTAINMENT + POSITIONABLE updated; `allowedChildren('bodytube')` includes both, `allowedChildren('podset')` returns `STAGE_CHILDREN`.
3. `position.ts` `layoutChain` refactor lands; a pod's internal nose→tube chain stacks correctly in its local frame (unit test).
4. A user can add a Pod Set / Booster under a body tube in the tree editor, give it a body-tube sub-chain, and the tree serializes/clones/reseeds ids correctly (generic `treeModel` recursers — assert via existing tree tests).
5. `normalizeTree` proven unchanged in behavior: a nested pod does not appear at top level and does not perturb the stage invariant (regression test).
6. Temporary guard: `buildTree` strips unbuilt assembly nodes so the app still runs and simulates the core rocket (removed in Phase 4). Documented in the PR.

This lands the schema foundation and the tricky nested-chain layout with full test coverage, **carries zero rebuild risk**, and unblocks Phases 2–3 (rendering + UI) which are also rebuild-free — so the risky Phase 4 rebuild happens only once, against a fully-built-out app that can immediately exercise the new engine paths.

---

# Critique & Corrections (verified against source — MUST-FIX items flagged)

I've verified the plan against the actual kernel, bridge, and app sources. The plan is largely sound and its architectural reads are accurate, but I found two P0 correctness bugs in the Phase-4 bridge design that would throw or silently corrupt geometry, plus several smaller corrections.

# Review — Parallel Stages / Pods implementation plan

## Verified correct (grounding the plan's core claims)
- **Kernel is carved and wired.** `PodSet`, `ParallelStage`, `ComponentAssembly` (patched), `RingInstanceable`, `RadiusMethod`/`AngleMethod`/`RadiusPositionable`, `ComponentAssemblyCalc` all present under `engine-java/src/carved/...`. No carved-source edit needed. ✅
- **BodyTube accepts both.** `BodyTube.isCompatible` (BodyTube.java:373-385) explicitly whitelists `ParallelStage` and `PodSet` — so `attachChildren`'s generic `parent.addChild(child)` works and `addChild(component)` defaults `trackStage=true` (RocketComponent.java:1890-1962), registering a `ParallelStage` as a real stage. The "no stage-loop change" claim holds. ✅
- **treeModel.ts needs no edits** — every recurser walks `children` generically; `stages()`/`normalizeTree`/`stageIndexOf` key off `type==='stage'`, which nested pods never are. Top-level stage invariant is untouched. ✅
- **Branch plumbing is free** — `simulateJson`/`flightDataToJson` already emit `branches[]` when `getBranchCount()>1` (OrkEngine.java:596-609). ✅
- **`.ork` unit boundary confirmed** — existing `emitNode` writes `<radiusoffset>` in metres and `<angleoffset>` in **degrees** (orkFile.ts:751 emits `180.0`). ✅
- **`ringInstanceOffsets` convention matches kernel** — `getInstanceOffsets` uses `y=r·cos(angle), z=r·sin(angle)` (PodSet.java:88-90), and the 2D renderer projects `off.y` while ignoring `z` (TreeSchematic.tsx:279), exactly as planned. ✅
- **AFTER really is forbidden** — `ComponentAssembly.setAxialMethod` downgrades AFTER→TOP (patch line 148-154) and `PodSet.getAxialOffset` throws `BugException` on `isAfter()` (PodSet.java:132-134). UI must offer only Top/Middle/Bottom/absolute. ✅

---

## P0 — correctness bugs in the Phase-4 bridge (plan is wrong as written)

**1. Applying `position` inside `create()` will throw NPE for assemblies.**
The plan says reuse "`create():386-390`" for axial position. But `create()` sets `c.setAxialMethod(...)` on the freshly-built component **before it is attached** (attachChildren does `create(kid)` → *then* `parent.addChild`). For both new types `setAxialMethod` requires a parent:
- `ParallelStage.setAxialMethod` throws `NullPointerException("a Stage requires a parent...")` when `parent==null` (ParallelStage.java:188-190).
- `PodSet.setAxialMethod` → `super` = patched `ComponentAssembly.setAxialMethod`, which throws the same NPE (ComponentAssembly.java:145-147).

So the generic common-params block at ComponentFactory.java:386-390 will crash on the first pod/booster. **Correction:** in `create()`, skip the `position` block for `ComponentAssembly` instances, and apply axial method/offset (and the radial/angle/instance config) in `attachChildren` **after** `parent.addChild(child)`. This is a structural change to attachChildren, not a "reuse 386-390."

**2. `setRadius(method, m)` uses radius-from-centerline semantics, not the offset the plan/`.ork` store.**
The plan's §2 table maps `radialDistance` (m) → `setRadius(method, m)`, while calling it a "gap." Those are different quantities. `setRadius(requestMethod, requestRadius)` treats the arg as the **radius from parent center** and stores `radiusOffset_m = method.getAsOffset(...)` — for RELATIVE that's `radius − parentOuterRadius − boundingRadius` (RadiusMethod.java:67-78). The `.ork` `<radiusoffset>` and the kernel field are the **offset** (the gap), not the radius. Feeding the stored offset into `setRadius(RELATIVE, x)` double-subtracts the parent radius → geometry shifts.
**Correction:** store the app field as `radiusOffset_m` (gap semantics, matches `.ork` verbatim → clean round-trip), and in the bridge call **`setRadiusMethod(method)` then `setRadiusOffset(value)`** — *not* `setRadius(method, value)`. `setRadiusOffset` stores directly for RELATIVE/FREE (`clampToZero()==false`), so it's safe even pre-attach. Rename the field `radiusOffset` to avoid the "distance from centerline" misconception.

---

## P1 — behavior/design corrections

**3. `PodSet.setAngleMethod` is an empty no-op** (PodSet.java:216-218). Exposing `angleMethod` as an editable field for `podset` does nothing — pods are always `RELATIVE`. Only `ParallelStage.setAngleMethod` actually works (ParallelStage.java:257-267). Don't present `angleMethod` as functional for pods (drop it from podset FIELDS or mark read-only); keep it for parallelstage.

**4. `applySeparationConfig` cannot be "reused verbatim" for parallelstage.** It's a `private static` in `OrkEngine` (OrkEngine.java:201) and the pod/booster is built in a different class (`ComponentFactory`). The separation logic **plus** `separationEventOf` must be duplicated into `ComponentFactory`, applied after attach. (No fcid needed — it uses `stage.getSeparationConfigurations().setDefault(sep)`, so this is doable there.)

**5. Unstaged `buildRocket` path attaches children *after* `createFlightConfiguration`** (OrkEngine.java:183-193 vs the staged path at :173). A `parallelstage` nested via the unstaged path may not be active in the already-created config. In practice the app always emits top-level stage nodes (hits the staged path, attach-before-config — fine), but the legacy/test path is a latent bug. Flag for the Phase-4 integration test; safest fix is to attach children before `createFlightConfiguration` in both paths.

---

## P2 — sequencing and smaller items

**6. The `position.ts` `layoutChain` refactor is mis-scoped as "the one real gap" in Phase 1.** `resolveAbsolutePositions` only rewrites `absolute` positions, which **only come from `.ork` import** (Phase 5). The kernel auto-stacks a pod's internal nose→tube chain via the default AFTER method (ComponentAssembly.updateChildSequence), so a hand-built pod needs no app-side chain stacking to simulate or draw correctly. Move this work to land with Phase 5, or keep it in Phase 1 but stop calling it a Phase-1 blocker — the DoD item #3 overstates its necessity.

**7. Default `radialDistance` is self-contradictory:** §2 sets `0.03`, Phase 3 says "offset 0 = flush." Desktop default is offset `0` (RELATIVE, tangent boosters). Pick `0`.

**8. `ParallelStage.setAngleOffset` applies `MathUtil.reducePi`** (ParallelStage.java:225) — angle round-trips *geometrically* but the numeric value can change to (−π, π]. A round-trip test asserting node-equality on `angleOffset` may fail for inputs >180°; assert modulo-2π or on geometry.

**9. Phase-1 `buildTree` strip guard needs to also drop motor mounts *inside* pods.** If a user puts a motor in a pod's inner tube during Phases 1-3, its `id` won't exist in the stripped engine and `setMotorById`/`componentInfo` will throw. Strip the whole subtree and filter it out of `motorMounts()` results, or guard those calls.

**10. "No rebuild" conflates two builds.** Phases 1-3 still require the **engine npm package** (`tsc`) rebuild because `ComponentType` lives in `packages/engine/src/orkEngine.ts:99`. Only the **TeaVM kernel** regeneration is gated to Phase 4. Worth stating explicitly so no one thinks Phase 1 skips `npm run build`.

**11. Interim save loses pods.** `emitNode` has a silent default for unknown types, so between Phase 1 and Phase 5 a design with pods will **save without them** (and import already `ignored.add`s them — orkFile.ts:333, asserted at orkFile.test.ts:148). Acceptable as a temporary state but should be called out in the Phase 1 PR so users aren't surprised by data loss on save/reload.

---

## Net assessment
The strategy (schema/tree/render/UI all rebuild-free first, single gated TeaVM rebuild last, golden fixture before rebuild) is correct and the reuse map is accurate. **Items 1 and 2 are must-fix before Phase 4 will run at all** — they're concrete kernel-contract mismatches, not style. Items 3-5 are real but contained. Everything else is refinement. Recommend the plan explicitly rewrite the Phase-4 bridge section to (a) defer all assembly positioning to post-attach in `attachChildren`, and (b) use `setRadiusMethod`+`setRadiusOffset` with offset semantics.
