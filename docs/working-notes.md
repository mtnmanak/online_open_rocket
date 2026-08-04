# Working notes — collaboration style & project state

## ⚡ RASAero #1 build — decisions locked (2026-08-03, do not re-litigate)

Eric approved starting the flagship supersonic/hypersonic aero build. Decisions:

1. **Scope: full Mach 0.01–25** with EXTENDED validation anchors (beyond the
   ARCAS 4.63 ceiling): AGARD HB-2 wind-tunnel data (~M1.5–10, body aero),
   Army-Navy Basic Finner (~M0.5–4.5+, finned CP/CD), NASA/NTRS sounding-rocket
   reports (M2–6), and >M4 amateur flight telemetry (Traveler IV, CSXT GoFast,
   Daugirdas, ~M5.5) for end-to-end apogee checks. Goal: out-validate RASAero
   (whose own published validation also stops at ARCAS 4.63 / MESOS M4.18 —
   their "to Mach 25" is extrapolation too). Only >M10 labeled as extrapolation.
2. **Exposure: default-OFF during beta** (Preferences → Aerodynamics toggle,
   same pattern as rogersKbf), then **flip to default-ON once the full anchor
   suite passes** — with: (a) every launch report/saved run recording which
   aero model produced it, (b) a one-click "Classic OpenRocket (desktop
   parity)" mode, (c) loud guide + changelog framing. NOTE: the differential
   test constraint (flag-off bit-identical) is independent of the UI default —
   the harness sets flags explicitly.
3. **Fin airfoils (#4: 8 cross-sections + LE radius) are IN scope** as a phase
   of this build (working assumption Eric didn't object to; wave-drag equations
   extracted in Phase 0 regardless).
4. **Ultracode authorized** for the research passes ("ultracode do number 1").

**Phase plan:** 0a equation spec (local PDFs: RASAero Manual, Rogers&Cooper,
ARCAS, MESOS + kernel map + open literature) / 0b validation-data hunt +
automated scoring harness (extend getDragSweep to emit CP/CNα; ARCAS + HB-2 +
Finner fixtures) → 1 supersonic body CP (unfreeze SymmetricComponentCalc's
M>1 freeze — the big gap; fin CNα already has a real Busemann-style model
above M1.5) → 2 drag fidelity (transonic rise, per-shape wave drag, boattail,
supersonic base + large-nozzle power-on) → 3 fin airfoils → 4 hypersonic
(modified Newtonian blend) → 5 app surface (Mach-25 drag panel w/ regime
shading, CP-vs-Mach plot, coefficient CSV export #6, guide). ~4–6 sessions.

**State:** Phase-0 research workflow launched 2026-08-03 (run wf_25b19352-aac,
6 extractors + 4 dataset hunters + adversarial verify). Assemble results into
docs/research/ spec + anchor docs when it lands. v0.024 (workspace-tabs UI
reorg) is LIVE and verified; next version v0.025.

For the project owner (Eric) and any future Claude session. Technical rules live in
`CLAUDE.md`; this file carries the *conversational* context that doesn't fit there.
Last updated: 2026-07-05, end of the tenth working session (v0.014–v0.021:
issue batch a, per-stage motor length, the user guide, CG/CP-in-3D + mount
sizes, and the COMPLETE pods/parallel-stage feature).

## ⚡ START HERE → read `docs/handoff-2026-07-05.md` first

That file is the current, self-contained session handoff (state, what shipped
v0.013–v0.021, the pods architecture + the hard-won RingInstanceable bug, the
engine-rebuild ritual, gotchas, and the RASAero work that's next). The
per-version detail below remains as history.

**One-line state:** v0.021 is pushed AND LIVE (verified in Chrome incl. pods
building/simulating on the deployed engine); 137 tests green; differential 237
lines 5× stable; next version v0.022; next work = the RASAero gap features.

---

## ⚡ SESSION HANDOFF (2026-07-04, ninth session) — historical

**State (as of v0.018):** the LIVE SITE
https://www.mountainmanrockets.com/online_open_rocket/ once ran v0.018.
Eric uploads deploy zips manually;
for each release run `npm run package` and hand him the
deploy/online-openrocket-v<ver>.zip (remember: package-dist.mjs zips only —
never Compress-Archive; check assets/ in Chrome after his upload — the WAF
scrambles non-browser probes). Issue batch 2026-07-04a is DONE (v0.014);
watch for the next dated issue list and fix it before new feature work
(standing rule).

**This session (2026-07-05) shipped v0.013–v0.018:** debug/polish pass
(0.013), issue batch a (0.014: add-to-parent, site menu, motor silhouette),
per-stage max motor length (0.015), built-in user guide (0.016, docs/
user-guide.md + in-app GuideDialog), CG/CP visible in 3D (0.017), motor
mount sizes surfaced (0.018).

**v0.013 was a full-codebase debug/refactor/polish pass** (Eric: "take the
time now to debug, refactor and polish"). Three parallel review agents swept
services/components/tree; ~25 confirmed fixes + 14 new regression tests
(suite now 127: engine 17, app 110). Highlights worth knowing when reading
code: freshId now RESEEDS from the tree inside normalizeTree (duplicate-id
bug after session restore — the big one); undo coalesces edits within 800 ms
(one gesture = one Ctrl+Z step); App's engine build memo returns
{rocket,info}|{error} instead of setState-in-render; 'absolute' axial
positions are rewritten to parent-relative 'top' at normalizeTree (UI edits
in parent frame; engine reads rocket frame — they used to disagree);
RockSim masscomponent KnownMass was exported twice (imports read the first,
0 — real data corruption, now single-emission via common() opts); RASAero
export now throws instead of silently dropping unsupported fin sets, and
converts trapezoid-shaped freeform fins exactly (Eric's freeform workflow);
orkFile reads legacy <position type> (≤15.03 files) and round-trips line
material/elliptical cant/shoulder thickness; Rocket3D disposes swapped
geometries. Shared helpers now live in services/xmlUtil.ts (escapeXml/
xmlText/xmlNum), services/csvUtil.ts (csvCell), treeModel.asStageNodes().
No engine/kernel changes — differential untouched (still 229 lines).

**Deliberately skipped (judgment calls, revisit if Eric asks):** CSV
formula-injection hardening (his own local data; a leading-quote prefix
would pollute his flight-day CSVs); engine rebuild on rocket-name
keystrokes (cosmetic perf); FT/MPH constants in simStore/simReport left
inline (readability beats indirection there); parseEng mid-curve zero-thrust
split (malformed-RASP edge case).

**Issue batch 2026-07-04a arrived and was fixed same-session as v0.014**
(issues-2026-07-04a.md + response-2026-07-04a.md): add-to-parent/stage
buttons in the tree (ComponentTree targets list), the mountainmanrockets.com
site menu (SITE_MENU in App.tsx, target="_top" to escape the iframe — keep
that), and to-scale motor silhouettes in the 2D view (App passes motorDims
{length,diameter} per mount id → TreeSchematic `motors` prop, drawn flush
to the mount's aft end, per cluster offset). Eric may ask to skin the menu
to match his WordPress theme, or to add the motor to the 3D view — both
offered in the response doc.

**v0.015 (same session): max motor length went PER-STAGE** (Eric's follow-up
issue) — App.maxMotorLen is Record<stage id, m|null>, session key
maxMotorLengthByStage (legacy scalar seeds all stages), Motors panel groups
mounts under stage headers with one limit input each.

**v0.016 (same session): built-in USER GUIDE.** A ❓ Guide header button
opens GuideDialog (TOC sidebar + content pane) rendering GUIDE_SECTIONS from
src/data/userGuide.ts (11 sections: welcome, quick-start, designing,
visualizing, motors, launch-conditions, simulating, staging/clusters, files,
physics, limitations/references). Same content in docs/user-guide.md.
Authored by a multi-agent Workflow (ultracode) — 4 parallel readers +
physics draft grounded in the OpenRocket reference source + critique +
finalize. GOTCHAS for regeneration: the physics *inventory* agent (structured
schema) errored on the retry cap, which zeroed the adversarial Verify phase —
the physics *draft* agent still produced source-grounded content, and I
manually spot-checked base drag (0.12+0.13M²), ISA constants, and the WGS84
Somigliana gravity coefficients against the real source (all exact). Also the
finalize agent double-ESCAPED some sections' html (&lt;p&gt;); the assembler
(scratchpad/assemble-guide.mjs, reads the workflow journal.jsonl) detects
entity-encoded html and decodes one level, and strips the redundant in-app
Contents list. GuideDialog uses dangerouslySetInnerHTML (content is our own
trusted static markup, never user input). Verified live: dialog opens, 11
sections, real HTML render (no tag leak), physics formulas in <pre>.

**v0.017: fixed CG/CP markers invisible in the 3D view** (user report) — the
spheres sit on the rocket axis inside the opaque shell; now render on top
(meshStandardMaterial depthTest:false + renderOrder), larger, CG a visible
neutral instead of near-black. Rocket3D.tsx. Browser-verified via screenshot
(window was visible this time). NOTE: this production @react-three/fiber v8
build does NOT expose canvas.__r3f, so the 3D scene graph can't be introspected
headlessly — verify the 3D view by screenshot, not DOM.

**v0.018: motor mount sizes surfaced** (user request) — the Rocket panel and
Motors panel now show each mount's nominal size (classLabel(diameterClass(
mountDiaMm)) — 24/29/38/54/75-76 mm), per stage with cluster count, so you
don't have to open the mount tube in the tree. App.tsx mountSizes useMemo +
.mount-sizes / .mount-size-chip / .mount-size-inline CSS. Browser-verified
single + 2-stage (29/38 mm) via DOM.

**PODS / PARALLEL STAGES — work STARTED (Eric un-deferred it 2026-07-05).**
Full source-grounded plan + critique in **docs/pods-implementation-plan.md**
(multi-agent pods-design workflow). The critique found TWO P0 bugs in the
FUTURE Phase-4 bridge design — READ IT before writing ComponentFactory:
(1) applying axial position inside create() NPEs for assemblies (setAxialMethod
needs a parent) — apply AFTER attach; (2) use setRadiusMethod+setRadiusOffset
(offset/gap semantics), NOT setRadius(method,val) (which is radius-from-centerline
and double-subtracts). Also: PodSet.setAngleMethod is a no-op (pods always
relative); ParallelStage IS an AxialStage so boosters get flight branches free;
AFTER axial method is forbidden for assemblies.
**v0.019 shipped Phase 1 (app-side foundation, NO engine rebuild):** added
'podset'|'parallelstage' to ComponentType (orkEngine.ts); schema.ts
DISPLAY_NAME/FIELDS/CONTAINMENT/POSITIONABLE/defaultParams (fields:
instanceCount, radiusOffset [gap], radiusMethod, angleOffset, + angleMethod &
separation on parallelstage; radiusOffset name matches kernel/.ork for 1:1
round-trip); treeForEngine() in treeModel.ts TEMPORARILY strips assembly
subtrees before the engine (so core rocket still builds/sims; also keeps
pod-internal mounts out of motorMounts) — App.tsx derives engineTree and builds
mounts+rocket from it; 2D schematic skips assemblies. 5 new tests (treeModel.test).
Browser-verified: add-menu offers both types, a booster-bearing rocket still
sims (stability 1.19), pod mount excluded, no errors.
**REMAINING pods phases (all gated for Eric's review; Phase 4 is the heavy one):**
P2 off-axis 2D/3D rendering (reuse cluster offsets + fin per-instance rotation;
refactor renderChain/addChain); P3 config UI panel; **P4 bridge + TeaVM ENGINE
REBUILD ⚠ (ComponentFactory podset/parallelstage cases w/ the two P0 fixes; add
a pod golden fixture BEFORE rebuild; full difftest ritual must stay green) —
DO NOT run autonomously, needs Eric's go-ahead**; P5 .ork round-trip
(<podset>/<parallelstage>, radiusoffset m, angleoffset DEGREES, reuse sep).
Open questions for Eric in the plan doc §5 (radius/angle method exposure;
2D end-view inset; batch-sim-disabled-on-staged now also hits parallelstage).

**v0.020 shipped pods Phase 2 (off-axis 2D/3D rendering, NO engine rebuild).**
New src/tree/assembly.ts (shared geometry: ringInstanceOffsets [y=r·cosθ,
z=r·sinθ per kernel], resolveAssemblyRadius [RELATIVE = gap: offset+parentR+
boundingR; FREE = from centerline], assemblyChainLength/BoundingRadius,
isAssembly). Rocket3D.buildPieces refactored: place() bakes an instance
Matrix4 into geometry (position/rotation-free pieces → OBJ export gets pods
FREE), addChain(nodes,xform) recursion, pod case in addChildren (rotX(angle)·
translate(podStart,podRadius,0)); fins on pods work (addFins takes xform).
TreeSchematic refactored: renderChain(nodes,xStart,baseY) + baseY threaded
through renderChildren/shoulderRect/renderTab/nosePath; pod case projects
off.y onto baseY (ignores depth z — accepted projection collapse at 90°, same
as clusters); vHalf measure walks pods so they don't clip. position.ts
axialLength handles assemblies. 3 new buildPieces tests (Rocket3D.test.ts).
Browser-verified 2D (boosters ringed above/below body w/ their own nose+body+
fins, core still sims); 3D geometry proven by unit test (2D/3D share the
helpers) — live 3D screenshot blocked by the R3F occlusion rAF-pause.
treeForEngine still strips pods from the engine (they render but DON'T
simulate — that's Phase 4).

**REMAINING pods phases:** P3 config UI panel (expose add-menu is already on;
the property panel already renders assembly FIELDS via data-driven schema —
verify unit chips + maybe a nicer layout); **P4 bridge + TeaVM ENGINE REBUILD
⚠ (gated, needs Eric)**; P5 .ork round-trip. Then the RASAero work
(docs/research/rasaero-gap-analysis) — Eric's stated order: pods first.

**PODS FEATURE COMPLETE (v0.021) — Phases 4+5 done, engine REBUILT.** Pods/
parallel boosters now BUILD in the kernel, SIMULATE (booster = own flight
branch), and round-trip through .ork. Differential 229→**237 lines**, 5×
stable. Bridge changes (engine-java/src/api): ComponentFactory podset/
parallelstage cases + applyAssembly (post-attach: setInstanceCount/
setRadiusMethod+setRadiusOffset[gap]/setAngleOffset/[parallelstage]setAngleMethod
+separation; the position block in create() is guarded !(c instanceof
ComponentAssembly)); OrkEngine.applySeparationConfig made package-private and
reused. GoldenMain.podScenarios() (8 golden lines: pod.geometry/info/comp,
mass.pod.structure, mass.pod1.offaxis, para.info/branches/summary).
**HARD-WON BUG (the workflow critique got it WRONG): FinSet, SymmetricComponent
(nose/body/transition) and TubeFinSet ALSO implement RingInstanceable** — so
guarding the applyAssembly hook on `instanceof RingInstanceable` clobbered
every fin/tube instanceCount with the pod default (2), collapsing 3 fins→2,
breaking mass/CG/clusters/staging. The differential PASSED (JVM==JS both
wrong via the tree API) but the engine vitest caught it (tree-API-vs-direct
divergence). FIX: guard on `instanceof ComponentAssembly` (only PodSet/
ParallelStage). LESSON: after an engine rebuild, run BOTH difftest AND the
engine vitest — difftest can't catch a bug present identically in JVM+JS.
App cleanup: treeForEngine strip REMOVED; buildTree/mounts use the real tree;
new hasParallelStage() ORs into isStaged so batch-sim is disabled when a
separating booster is present (podset alone keeps batch — single branch).
Phase 5 orkFile.ts: <podset>/<parallelstage>/<boosterset> import + export
(instancecount, radiusoffset[m], angleoffset[DEG], separation reuse; no
color/linestyle/radialdirection). Full plan+critique in
docs/pods-implementation-plan.md; the exact Phase-4/5 spec is in
scratchpad/pods-phase45-spec.md (not committed). 137 tests green (engine 17,
app 120). NOT browser-flight-verified (rAF pause on occluded window) — the
multi-branch flight is proven by the engine "serial two-stage branch" test +
the para.branches golden scenario.

**Live site still on v0.012** — Eric deploys manually; v0.013–v0.021 all
pushed but NOT yet uploaded. Next up per Eric: the RASAero gap work
(docs/research/rasaero-gap-analysis) now that pods are complete.

**Next version is v0.022** (0.NNN +1 per pushed build).

**Phase 3 remaining (in likely order):**
1. Parallel/strap-on boosters + pods — kernel supports them
   (ParallelStage/PodSet compiled in); gaps are bridge tree-schema, app UI,
   and off-axis 2D/3D rendering. Expect ~2-3 sessions.
2. Design optimization — DISCUSS FIRST (Eric's style): propose in-browser
   (batch-sim-style loop over dimension sweeps) over the plan's server-side
   idea; the engine is fast enough.
3. Geodesy options (flat/spherical/WGS84 exposure in launch conditions) — small.

**Session gotchas worth remembering (hard-won this session):**
- Eric's webhost: the WAF returns 403/scrambled-looking results to
  non-browser user agents (PowerShell probes) — debug the live site through
  the real Chrome, not Invoke-WebRequest. The host's zip-extract once
  silently SKIPPED the assets/ folder — after any upload, check assets/
  exists before debugging anything else. Deploy zips MUST come from
  scripts/package-dist.mjs (bsdtar) — PowerShell Compress-Archive writes
  backslash entries that break Linux-side extraction.
- Browser smoke-testing protocol (localhost:4180 preview): ALWAYS snapshot
  `online-openrocket.session.v1` before mutating (sessionStorage survives
  reloads; window.* does not), restore + reload after, and delete any test
  runs added to `online-openrocket.sim-runs.v1`. File-open flows are
  drivable headlessly via DataTransfer + dispatchEvent('change') with bytes
  fetched from a file temporarily copied into dist/ (the SW's SPA fallback
  eats direct navigations to non-precached files — fetch() bypasses it).
  Sims run inside requestAnimationFrame: a hidden/occluded Chrome window
  means "Simulating…" hangs forever — that's window visibility, not a bug.
  The extension's javascript_tool sometimes returns [BLOCKED] for responses
  that look like credentials — return fewer/simpler fields.
- Git on this box: multi-line commit messages via here-strings fail
  silently in some compound commands — write the message to a scratchpad
  file and `git commit -F <file>` (established pattern). Push directly is
  approved (`git push origin main` as its own command; compound
  commit+push sometimes trips the permission classifier).
- The engine rebuild ritual (bridge/harness changes): set JAVA_HOME to the
  portable JDK, `gradlew.bat generateJavaScript` in engine-java/, then
  `node engine-java/scripts/build-engine.mjs`, then difftest 5× consecutive
  (currently 229 golden lines). Kernel patches go through engine-java/
  patches/ + LEDGER.md — never edit carved files (LEDGER gained the %g→%s
  BasicEventSimulationEngine log patch this session).

**Test totals at handoff:** 113 (engine 17, app 96), all green; differential
229 lines 5× stable; build clean; deploy/online-openrocket-v0.012.zip is
what's live. Deployment shipped as v0.006 (PWA/offline +
manual-webhost package + dormant GitHub Pages workflow — see the Phase 3
entry below and docs/deployment.md). Eric will manually upload the package to
his own web host now; GitHub Pages comes later when he makes the repo public
(it's private today). **DEPLOYED LIVE 2026-07-03:
https://www.mountainmanrockets.com/online_open_rocket/ (v0.007, SW +
offline verified; iframe snippet delivered for his WordPress pages; see
docs/deployment.md for the extraction war story).**
**Staging/clustering COMPLETE (serial): A clusters v0.007, B engine v0.008,
C full staging UI v0.009.** v0.009 highlights: tree.components is now ALWAYS
stage nodes (normalizeTree wraps legacy trees/sessions at every load
boundary — invariant enforced app-wide); per-mount motor map
(App.MountMotor: label/spec/meta/ignition) replaced the single-motor state,
legacy sessions migrate; ignition defaults are power-class aware (assignMotor:
HP sustainer in staged rocket → burnout+1 s, else automatic; isHighPower in
motorDb = >80 N avg or >160 Ns); multi-stage .ork both directions incl.
separation + per-mount ignition (importOrk returns motors map keyed by mount
node id; exportOrk takes a motors map — legacy single-motor arg still
accepted); launch report has per-branch booster sections + chuteless-HPR
warning; batch sim disabled on staged rockets (Eric's rule); auto-delay
offered only on stage-0 mounts. Engine goldens/differential unchanged from
v0.008 (229 lines). Browser-verified: legacy migration, staged UI (two motor
cards w/ ignition selects), batch gating. Kernel/engine staging behavior
covered by the 17 engine tests + 2 goldens; branch REPORT logic by 4 new
simReport unit tests. **Remaining parked: parallel/strap-on boosters + pods
(deferred by Eric until serial staging + clusters prove out), mixed
symmetric clusters need a second clustered mount (works now via two mounts —
document for Eric), 2D/3D don't yet draw separation planes or per-stage
colors.** Plan amended at Eric's direction (2026-07-04): the Phase 3 file
item is now IMPORTS AND exports — RockSim (.rkt) and RASAero II design files
must load as well as save (OBJ/SVG stay export-only). **RockSim .rkt
import/export SHIPPED v0.010** (services/rocksimFile.ts; format knowledge
from the desktop's file/rocksim package — mm units, diameters-not-radii
÷2000, Stage3Parts=sustainer top-down slots, <Ring> UsageCode fan-out,
Xb sign flip for rear-referenced positions, PointList reversed vs ours).
Beyond-desktop feature: EngineSet/EngineCode motors are imported and
auto-loaded from the motor DB (desktop drops them; stale MountSerialNo
links fall back to the stage's first mount — real files have them).
Fixtures = the desktop's own test .rkt files (4 copied into
__fixtures__/). Gotcha: happy-dom's DOMParser rejects CDATA sections —
importRkt inline-escapes them before parsing. SVG fin templates SHIPPED
v0.011 (services/finTemplate.ts — 1:1 physical-mm SVG per fin set with TTW
tab + 50 mm calibration ruler; button in PropertyPanel). **RASAero II .CDX1
both ways + OBJ export SHIPPED v0.012 — the Phase 3 file item is COMPLETE.**
rasaeroFile.ts (inches ÷39.37, flat part list → stages, Booster elements =
lower stages, fins nested in tubes w/ bottom-referenced Location, nose-shape
STRING map w/ shape params [Von Karman→haack 0, LV-Haack→haack 0.33,
Parabolic→power 0.5], conical-only transitions, fins-on-boattail → freeform
conversion, 2-slot Recovery → chutes). RASAero is aero-only: imports warn
about 2 mm default walls and LIST the file's motors (no mounts exist to
attach them); export requires 3-8 fins/conical transitions and writes launch
weight/CG into the mandatory Simulation block. objExport.ts reuses
Rocket3D.buildPieces (now exported) via three's OBJExporter — meters,
external shell only. Desktop fixtures in __fixtures__ (3 .CDX1). Phase 3
still open: parallel boosters/pods, design optimization, geodesy options.

## How Eric works / prefers to collaborate

- **Discuss before changing.** When Eric says he wants to talk something through,
  do NOT start implementing — analyze, present options with honest effort estimates,
  and wait for a decision. He asks sharp questions and decides quickly once informed.
- **Issue reporting is batched.** Eric tests thoroughly, then delivers one aggregated
  list. Exception: anything that *blocks further testing* is reported immediately and
  alone. Agreed report shape: what he did → what he expected → what happened
  (verbatim error text), a repro `.ork` where relevant, tagged blocking/annoying/cosmetic.
- **Sequential decisions.** He prefers finish-then-decide over speculative planning
  (e.g., "I'll decide about Phase 3 after testing"). Don't push him to pre-commit.
- **He is the visual QA — but Claude can now smoke-test too.** As of the sixth
  pass, Claude drives Eric's Chrome via browser tools: build, serve the dist
  (`npx vite preview --port 4180` from packages/app), navigate, click Launch,
  screenshot, read localStorage. Use it to verify changes live before claiming
  anything (it caught two real bugs batch-b missed: null Launch CP, finish
  select misreporting). Eric stays the final judge on look-and-feel. His live
  session state is readable from localStorage key `online-openrocket.session.v1`
  — invaluable for reproducing exactly what he's testing.
- **Real use case:** he designs almost exclusively with **freeform fins** and uses
  **rounded or airfoil (pointed) cross-sections — never square**. Anything degrading
  those paths hits his primary workflow. This is why cross-sections + freeform were
  pulled forward out of Phase 3.
- His preferred fin-design workflow: click points roughly in a visual editor, then
  refine exact values in a coordinate table. Both stay in sync (implemented).
- **Issue-batch protocol (refined 2026-07-03):** one NEW dated file per test pass
  in docs/testing/ (issues-<date>.md); Claude answers with a matching
  response-<date>.md giving item-by-item status. Repro .ork files sit beside them.
  Same-day second batch gets a letter suffix: issues-<date>b.md (then c, …),
  with the response doc named to match.
- **Committing/pushing:** Eric authorized pushing to origin/main as the normal
  end-of-chunk step ("go ahead and push it", then routine). Commit at each
  coherent milestone; push after. (If the permission system blocks the push,
  Eric approves via `! git push origin main` or by adding
  `"Bash(git push origin *)"` to .claude/settings.local.json permissions.allow.)
- **Versioning (Eric's rule, 2026-07-03):** beta builds are `0.NNN`, NNN +1 per
  released (pushed) build, until the first production release. Single source of
  truth: `packages/app/src/version.ts` (APP_VERSION + CHANGELOG — rendered in
  the app via the header version badge → What's-new dialog). Release checklist:
  bump APP_VERSION, prepend a CHANGELOG entry, commit, push. npm package
  versions stay independent (semver, internal only).
- **Units:** he values the in-place unit click-through ("very valuable") — any new
  UI that displays a unit must use UnitChip. Diameter (not radius) is his default
  input mode; dark theme is the default at his request.
- **Motors matter most after physics** (his words). The motor-domain rules he
  taught us (adapter-down fit, 75≡76 mm, OOP still flying, sort by burn time /
  total impulse, max-motor-length as flag-not-block) are implemented in
  services/motorDb.ts — don't regress them.
- **Staging/cluster domain rules (Eric, 2026-07-03 — encode, don't regress):**
  - **The G80 line divides low/mid power from high power.** Computable:
    high power ⇔ avg thrust > 80 N or total impulse > 160 Ns (G80 itself =
    low/mid). Several behaviors branch on this class:
  - **Sustainer ignition:** electronics-timed is THE standard in HPR — nobody
    lights an HPR sustainer off the booster's ejection charge (that's common
    only in low/mid power). Defaults must be power-class aware.
  - **Booster recovery:** low/mid boosters may tumble-recover (no warning);
    HPR boosters MUST have active recovery (loud warning if chuteless).
  - **Mixed motors in a cluster must stay symmetric** (thrust vectors balance
    or the rocket cants) — e.g. 6-ring flown 3+3 in alternating tubes. Until
    per-mount motor assignment (staging Release C), one motor type per
    cluster; mixed arrays will be modeled as two clustered mounts.
  - **No batch simulation across staged rockets** (combinatorics); batch on
    clusters is wanted and works (candidate ×N).

## Decisions made (and why)

- **Engine strategy:** carve OpenRocket's real Java kernel, compile with TeaVM,
  differential-test JVM↔JS. Chosen over a TS rewrite after spikes proved bit-parity.
- **Fidelity discipline:** carved sources are never edited; changes go through
  `engine-java/patches/` + `LEDGER.md`. Desktop-compat is *proven* via the real
  OpenRocket loader (headless JAR harness in `engine-java/tools/GenerateOrk.java`),
  not assumed.
- **Repo moved out of Dropbox** (2026-07-02) to `G:\git\online_open_rocket` — Dropbox's
  driver corrupted git object writes. GitHub is the sync channel. Never move it back.
- **Deterministic simulations by choice:** wind turbulence is seeded (default 42), so
  identical inputs give identical results — intentionally different from desktop,
  which time-seeds.
- **Units at the boundary:** engine is pure SI/radians; UI speaks mm/°/g; converters
  live only at the edges (lesson from upstream bug #2475).

## Project state at sign-off

- **Done:** Phase 0 (spikes), Phase 1 (MVP: engine, API, UI, thrustcurve.org, .ork),
  Phase 2 (tree editor, 3D view, launch conditions, 12 plot series, full-tree .ork),
  plus fin cross-sections and freeform fins with the click/drag+table editor.
- **2026-07-03: first batch of Eric's test issues fixed** — see
  `docs/testing/issues-2026-07-03.md` and the item-by-item status in
  `docs/testing/response-2026-07-03.md`. Shipped: fin default position, clear-all,
  sliders everywhere, full design readouts (dry/loaded mass+CG, stability cal+%),
  nose shoulders + solid flag, surface finishes, mass/CG/Cd overrides, parachute
  deployment events. New engine bridge fields required a TeaVM rebuild
  (differential re-passed) and the .ork writer now has **CG parity** with the
  desktop loader too (found+fixed: hardcoded shapeparameter reshaped Haack noses
  on save — second instance of that war story, now regression-tested).
- **2026-07-03 (second pass): units/preferences shipped** at Eric's direction —
  desktop-mirrored unit groups (11 quantities, exact UnitGroup factors, metric/
  imperial default sets), radius-vs-diameter input preference (defaults to
  diameter), and light/dark/system theme, all in a persisted ⚙ Preferences
  dialog. Engine/.ork stay SI; conversions live in packages/app/src/prefs/.
- **Still open from the issue list (Eric-visible backlog):** component/material/
  parachute preset databases (browse/edit/CSV), snap-to-anchors +
  drag-in-preview, appearance colors, attribute audit vs desktop. In-place
  unit click-through shipped same day at Eric's request (UnitChip component —
  he called it "very valuable"; keep it working on anything new that displays
  units). Units polish candidates if he asks: fractional inches (in/64),
  stability unit selection.
- **2026-07-03 (third pass): motor selection rewritten** after a design discussion
  with Eric (his spec: manufacturer toggles first, then diameter classes with
  adapter-down logic, sortable by burn time / total impulse, max-motor-length
  flagging as warning-not-block, OOP toggle on, 75≡76 mm). Full thrustcurve.org
  summary DB is bundled (packages/app/src/data/motors.json, `npm run
  motors:refresh`); curves still fetched+cached on demand. Filter/sort logic in
  services/motorDb.ts (unit-tested). Dark theme now the default at his request.
- **Motor-domain knowledge from Eric worth keeping:** adapters let any smaller
  class load in a bigger mount, never the reverse; 75 mm and 76 mm are the same
  casing class (manufacturers round differently); OOP motors matter ("millions
  still in circulation"); his search attributes are designation, diameter,
  length, burn time, total impulse.
- **Recovery-domain knowledge from Eric (sixth pass, encoded in
  services/simReport.ts SAFETY):** ejection delays get DRILLED to the whole
  second the flyer wants — recommendation is round(optimum), NEVER snapped to
  the manufacturer's prescribed list (his example: prescribed 0/6/8/10/14,
  optimal 12.7 → recommend 13); the user can always type any delay, including
  on the main panel without reloading the motor. Dual deployment: accepted
  descent under drogue is 60–70 ft/s (cap 70), landing target ≤ 20 ft/s;
  safety warnings must NAME the device (drogue vs main) — a main opening
  under a healthy drogue at 60–70 ft/s is normal, never a warning.
- **Component-input conventions:** everywhere a tube-like dimension appears,
  offer OD + ID + wall thickness as three synced views (body tubes, and nose
  cone bases as of v0.002). He thinks in diameters and ft/s.
- **2026-07-03 (fourth pass): databases + task-list completion.** Materials DB
  (80 desktop materials incl. surface/line — chute/cord materials required new
  ComponentFactory bridging + TeaVM rebuild, differential re-passed), component
  preset DB (3,449 parts from openrocket/openrocket-database .orc files, script
  in packages/app/scripts/fetch-component-presets.mjs, CSV round-trip w/ custom
  presets in localStorage), snap-to-anchor positioning (tree/position.ts) with
  2D-preview dragging, per-component display color, and the attribute audit
  (docs/testing/attribute-audit-2026-07-03.md). Eric will now do another test
  pass and deliver a fresh issue list — fix that before anything else.
- **2026-07-03 (fifth pass): issue batch "b" fixed end-to-end** — see
  docs/testing/issues-2026-07-03b.md + response-2026-07-03b.md (item-by-item).
  Highlights: fin tabs through the whole stack (kernel bridge + UI + .ork
  desktop-format round-trip + 2D render + centering-ring snap; "Fit tab to
  motor tube" is Eric's real-build default); numeric inputs rebuilt (NumField —
  typed negatives, validation with red border, ≤3-decimal display, draft
  buffering); session autosave/restore; New-button confirm w/ save offer;
  imported generic "Rocket" names fall back to the filename; full launch
  report (~30 attributes incl. kernel optimumDelay from its ballistic probe);
  saved-runs table + 33-column CSV; batch motor simulation with acceptance
  criteria + per-motor optimal delay; "Auto (optimal)" delay option; EX motor
  import (.eng/.rse → manufacturer "EX", .rse per-sample masses used);
  component UX batch (wall⇄ID dual input, finish→all, per-component stats
  readout via new engine componentInfo, two add buttons, inherit-from-previous
  defaults, duplicate ⧉, override placeholders, color swatches, motor-mount
  auto-name, shoulder outlines); 2D wheel-zoom/pan/reset; RockSim parts CSVs
  merged into presets (report in docs/testing/). Answers: mass/CG overrides
  are independent; fin-set mass override = ALL fins combined (now labelled).
- **CRITICAL find (fifth pass): simulations were nondeterministic run-to-run**
  — upstream's InstanceMap is identity-hash ordered, so BarrowmanCalculator's
  per-step force summation order varied per JVM process; ULP noise
  chaos-amplified over a flight (flipped sample counts, made the differential
  flaky — it had passed all day on luck). Patched (HashMap→LinkedHashMap,
  patches/LEDGER.md "Determinism fixes"), turbulent golden scenario capped at
  8 s, difftest tolerances documented; 211 lines now pass stably (5×
  consecutive). Never revert that patch without re-reading the ledger entry.
- **2026-07-03 (sixth pass): live iteration with Eric, released v0.001–v0.004**
  (all pushed; version badge + What's-new changelog now in the app header,
  source of truth packages/app/src/version.ts — bump 0.NNN +1 per pushed
  build, prepend a changelog entry, until first prod release resets to 1.0.0).
  - v0.001: batch-b fixes + versioning itself.
  - v0.002: drill-to-fit delays (see recovery-domain notes above), main-panel
    delay editor, motor-browser "Custom (drilled)…", nose cone OD/ID/wall sync.
  - v0.003: dual-deployment-aware recovery reporting — engine events now carry
    source component names (bridge change + dual-deploy golden, differential
    213 lines stable); per-device deployment table with Eric's thresholds.
  - v0.004: preset DB completeness — **the desktop does NOT use github
    openrocket-database; it bundles its own richer set** in
    core/.../datafiles/components/internal/ (Fruity_Chutes_Enhanced 42,
    Spherachutes 46, Rocketman 107, FlisKits 160, Front Range, b2, legacy
    files). fetch-component-presets.mjs now merges both + rocksim on top:
    4,700 parts. Fruity Chutes 10 → 52.
  - Investigated Eric's "215 mph descent under a 15-inch chute": calculation
    verified correct by flying his exact Wild Child (pulled from session
    localStorage) — the number is the main's OPENING velocity after a
    near-ballistic descent under a 36×1 in streamer drogue; the same chute at
    apogee descends at 26 mph. Caveat kept for honesty: OpenRocket's streamer
    model rates extreme aspect ratios (36:1) near-zero drag; desktop-identical
    but may undercount reality.
  - Push permission: resolved — pushes to origin/main now go through as the
    routine end-of-chunk step (Eric approved via manual `! git push` once;
    subsequent pushes worked).
- **2026-07-03 (seventh pass): issue batch "c" fixed, released v0.005** — see
  issues-2026-07-03c.md + response-2026-07-03c.md. Highlights: max motor
  length promoted to a rocket-level property in the main Motor panel
  (session-persisted, seeded from the old browser filter; browser still
  flags-not-blocks per Eric's rule, batch sim now EXCLUDES over-length motors
  with a shown count); displayDesignation() in motorDb.ts strips Cesaroni's
  impulse prefix + HP- prefixes everywhere motors display (raw designation
  stays the .ork/API identity); motors.json regenerated with propInfo/caseInfo
  (906/846 of 1,129 have them); CSV rebuilt to Eric's 14 flight-day lead
  columns in ft/mph/Gs/g (incl. Pad Weight = launch mass, Recovery Weight =
  new burnoutMass from the mass series at burnout), SI detail after; .ork
  import matches motors against the bundled DB via findDbMotor() and
  auto-loads them (the "G80T isn't built-in" nag is dead — verified with
  Eric's own WM_Wild_Child.ork); saved-simulation rows click-to-open the
  launch report (SimRunDetails renders from a stored run; charts still need a
  fresh sim's series). Older stored runs predate the new SimRun fields —
  formatters treat them as optional. Smoke-testing gotcha learned: sims run
  inside requestAnimationFrame, which Chrome pauses in hidden/occluded tabs —
  "Simulating…" that never finishes during browser automation means the
  window isn't visible, not a hang.
- **Small follow-ups parked (not blockers):** rail-button presets skipped (no
  RailButton preset kind yet — desktop has 8 entries incl. Wildman); consider
  collapsing the 10 Apogee-partNo Fruity duplicates into the 42 desktop
  entries (same physical chutes, reseller part numbers — ask Eric); fin
  library CSVs (FinsDATA*.CSV) still unmerged (no fin preset kind; matters to
  Eric's freeform-fin workflow, good candidate when preset kinds grow);
  cross-parent component paste if duplicate ⧉ proves insufficient; second
  visualization window for the edited component if the numeric readout isn't
  enough.
- **Phase 3 decision (2026-07-03): deployment first, then staging/clusters
  (Eric's call).** Deployment shipped as **v0.006**:
  - **PWA/offline:** vite-plugin-pwa@0.21 (Vite 5 line), autoUpdate, precaches
    the full app shell (11 files incl. the 2.5 MB engine chunk —
    maximumFileSizeToCacheInBytes raised to 6 MB or workbox silently skips
    it). Icons GDI+-generated in packages/app/public (regenerate with a
    similar System.Drawing script if ever needed). SW registered in main.tsx
    via virtual:pwa-register (types via src/vite-env.d.ts).
  - **Manual-webhost flow (Eric's current choice):** `npm run package` →
    deploy/online-openrocket-v<version>.zip (gitignored). docs/deployment.md
    has upload steps, .htaccess cache tips, and the WordPress iframe snippet
    (he hasn't yet said which WordPress setup he has — iframe works
    everywhere custom HTML is allowed).
  - **GitHub Pages later:** .github/workflows/deploy-pages.yml is
    workflow_dispatch-ONLY because the repo is private (Pages is paid on
    private repos). When Eric makes it public: Settings→Pages→GitHub Actions,
    run the workflow, optionally uncomment the push trigger.
  - **GPL:** header now links to the GitHub repo as the source offer;
    docs/deployment.md notes the repo should go public at/soon after first
    real deployment.
  - Browser-automation gotcha reconfirmed: sims + SW installs stall in
    hidden/occluded Chrome windows (rAF paused, rendering frozen) — drive
    verification through DOM/JS, not screenshots, when the window is covered.
- **Backlog menus for Eric to pick from:** the attribute audit's top-10 gaps
  (docs/testing/attribute-audit-2026-07-03.md — fin tabs, motor overhang/ignition,
  fin fillets, body-tube-as-motor-mount, rail-button geometry, tube-fin thickness,
  packed dims), units polish (in/64 fractional inches, stability unit selection),
  .ork appearance persistence for the display colors.
- Verification state (end of 2026-07-03): all suites green (engine 8, app 36),
  differential 202 lines re-passed after two engine bridge rebuilds,
  desktop-loader mass+CG parity bit-exact. Everything pushed through `624d821`.
- Data bundles + refresh scripts (rerun when upstream updates): motors
  `npm run motors:refresh` (1,129 motors, thrustcurve.org API); presets
  `node packages/app/scripts/fetch-component-presets.mjs` (github
  openrocket-database PLUS the desktop's own datafiles/components/internal —
  the desktop bundles Fruity Chutes/Spherachutes/Rocketman/FlisKits etc. that
  github does NOT have; reads the Dropbox reference source, override with
  OPENROCKET_SRC) **then** `node packages/app/scripts/merge-rocksim-parts.mjs`
  (re-adds Eric's RockSim CSV extras; regeneration wipes them otherwise).
  4,700 parts total as of v0.004. Materials generated once from the desktop's
  Databases.java into src/data/materials.ts.

## Notable technical war stories (details in docs/phase0-findings.md)

Worth remembering because they shape how we work:
- TeaVM 0.10 silently inverted NaN comparisons; 0.15 devirtualization/analysis
  needed `optimization = NONE` + `fastGlobalAnalysis = true`. **Never change those
  without a full differential pass.**
- Rollup tree-shook the entire engine out of a production bundle once (blank page)
  because TeaVM's UMD exports are dynamic — the artifact is now a native ES module.
- Hardcoded `shapeparameter` silently reshaped Haack noses (0.5 g phantom mass) —
  caught by the desktop-loader mass-parity check. That check is gold; keep using it.
- Square-vs-airfoil fin cross-section is a ~2× fin-drag difference — Eric's designs
  were significantly under-predicted before the fix.

## Practical bits

- Resume work: `cd G:\git\online_open_rocket` → `claude` (CLAUDE.md auto-loads).
- Run the app: `cd packages\app && npx vite preview --port 4180` (built dist), or
  `npm run dev` from the repo root for live-reload during development.
- Batch issue lists: markdown file preferred, e.g. `docs/testing/` + repro `.ork`
  files beside it (see below).
