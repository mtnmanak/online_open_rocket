# Working notes — collaboration style & project state

## ⚡ START HERE → read `docs/handoff-2026-08-11.md` first

The current, self-contained session handoff: v0.041 + v0.042 + v0.043 (the
2026-08-11 marathon — both issue batches AND the tracker adjudication). THE
TRACKER QUESTION IS SETTLED — one public tracker `mountainmanrockets-feedback`,
adjudicated in E:\git\mountainmanrockets\docs\issue-tracking-consolidation.md
(rev.3); our kit files were ADOPTED, the setup runs CENTRALLY (never from
here), and this repo's assigned scope shipped as **v0.043**: header 🐞
Feedback menu (bug/feature forms w/ tool+version prefill, browse, mailto —
new-tab/mailto rulings honored), guide Feedback section (both mirrors), and
the repo's first README.md. SESSION CLOSED CLEAN 2026-08-11: tree clean,
pushed, v0.043 live-verified, no background work running. Eric's own live
test of the 3D 📷 export (8K Darkstar w/ correct data header) is committed
at docs/2D_3D_Models/Wildman_Darkstar_3_-3d.png — every v0.042 smoke item
CONFIRMED. The per-version blocks below remain as history.

## ⚡ v0.043 (2026-08-11): 🐞 Feedback links + README (adjudication scope)

Eric's central adjudication landed mid-wrap-up (SETUP.md gained a ⛔ banner;
full decision in the site repo's issue-tracking-consolidation.md rev.3 —
READ IT before any tracker work). This repo's only scope: (1) header 🐞
Feedback menu (Icon.tsx gained 'bug'; FEEDBACK_REPO const in App.tsx;
GitHub links window.open _blank noopener per standing ruling, mailto via
location.href, bug form prefills tool + version, browse link goes to
/issues?label:tool:online-openrocket); (2) guide section 'feedback' before
limitations (both mirrors, md TOC renumbered); (3) README.md authored
(repo had NONE — adjudication correction) w/ the tracker line. NOT DONE
DELIBERATELY: LICENSE (adjudication §9.5 leaves it as Eric's open item —
raise, don't add). 247+23 tests green; DOM-verified on built dist (menu
items, bug URL params, guide TOC). NOTE for next session: if the 🐞 links
404, the CENTRAL tracker setup (rename/templates/labels) hadn't finished —
not ours to fix. Next version v0.044.

## ⚡ v0.042 (2026-08-11): issue batch b — printable component STL, glTF, image picker

issues-2026-08-11b → response-2026-08-11b.md. NO engine rebuild. Eric
CONFIRMED batch-a items (CSV export, both shape bugs); all approved
follow-ups shipped same-day. (1) 🖨 COMPONENT STL (the flagship — "no other
sim has it"): src/tree/solidMesh.ts = watertight-solid builders
(revolveProfile: closed (x,r)-loop about +X, signed-area winding
normalization, pole collapse; extrudePolygon: ShapeUtils caps, concave OK)
+ componentSolid() per type: hollow nose/transition w/ shoulders+caps at
real wall (profile from shapeProfile = kernel-exact), single fin w/ TAB
merged (tabHeight/tabLength/finTabFront), true-bore centering ring
(parentInnerRadius + mountOuterRadius from ctx — PropertyPanel derives from
parent tube + sibling innertube), bulkhead/tubes/coupler/engineblock/lug/
tubefin. isWatertight (edge-paired, direction-strict) + solidVolume + 
analytic anchors (cone πR²L/3 etc) in 38 tests. BUILT VIA WORKFLOW
(5 agents: 3 parallel implementers + 2 adversarial verifiers; ~380k tokens):
verifier CAUGHT 2 real defects pre-release — fore-shoulder cap DROPPED
(11% volume err on capped reducers) + closed-freeform-outline+tab broke
watertightness (dup first/last point retraced root edge). Both fixed+pinned.
(2) services/stlExport.ts: binary STL, mm (×1000 lives ONLY here), header
never starts 'solid', winding-derived normals; piecesToStl = whole-rocket
display shell (Save/Export, labeled reference). (3) services/gltfExport.ts:
rocketToGlb via three GLTFExporter {binary:true}, per-piece colors as
materials — Save/Export ".glb — 3D model with colors". (4) IMAGE PICKER:
ImageExportMenu.tsx (PNG/JPG × HD/4K/8K) replaces fixed-PNG buttons on 2D
(svgToImage) AND 3D — 3D snapshot RE-RENDERS at export width (r3f state ref;
gl.setPixelRatio(1)+setSize(w,h,false)→render→capture→restore) = genuine 8K.
(5) GUIDE: shape-parameter TABLE (Eric asked; both mirrors) + new formats
rows. (6) RECOMMENDATIONS in response doc: STEP/CNC = DXF for flat parts
next (cheap, covers most CNC) then STEP via opencascade.js WASM lazy chunk
as own phase (Eric validates in Fusion/SolidWorks); ISSUE TRACKER = go
further than other-session's proposal: ONE repo for site+tools — rename
mountainmanrockets-site-feedback→mountainmanrockets-feedback (GitHub
redirects), dropdown+Action auto-labeling; READY-TO-PASTE KIT in
docs/feedback-repo-kit/ (SETUP/README/2 issue forms/config/labeler wf);
in-app 🐞 links wired next session once Eric sends the URL. 270 tests
(247 app + 23 engine). Live-verified on built dist: nose STL byte-exact
260.35mm (221.23+39.12 shoulder ✓ mm scale), GLB magic/len valid, JPG@4K
3840px real JPEG, whole-STL 184KB. 3D 📷 STILL needs Eric's click (same
environmental WebGL wedge as v0.041 session — R3F canvas never inits under
this CDP browser; live v0.040 identical).
**v0.042 LIVE (2026-08-11, ~90 s after push; cache-bypass verified: served
bundle index-ADHolj8P.js byte-identical to local (2,823,250 B), version.json
0.042, feature strings present).** POST-RELEASE: Eric clicked 3D 📷 on the
live site himself — genuine 8K Darkstar export w/ correct header
(docs/2D_3D_Models/Wildman_Darkstar_3_-3d.png) — the last smoke item is
CONFIRMED. He also reorganized docs/2D_3D_Models/ (RockSim examples →
Rocksim/ subfolder). issues-2026-08-11c.md = a copy of response-b with an
issues header (no new issues; likely his annotation template — check for
annotations next session). Next version v0.043.

## ⚡ v0.041 (2026-08-11): issue batch a — true shapes in 2D/3D, component export, cert drawings

issues-2026-08-11a → response-2026-08-11a.md. NO engine rebuild. ENV FIRST
(Eric's items 1-2): repo moved G:→E: — local==origin clean, BUT the npm
workspace junction to packages/engine came through the copy as an EMPTY DIR
(3 suites failed to resolve @online-openrocket/engine; `npm install` fixed —
laptop clone may need the same), and the DEAD-PATH orphan
patches/rocketcomponent/InstanceMap.java rode along (carve.mjs hard-fails on
orphans — deleted). Session store is now localStorage (not sessionStorage) —
the browser-test backup protocol updated accordingly. (1) SHAPE RENDERING
(the 2 bugs): 2D drew a FIXED Bézier nose + straight-polygon transition
(never read shape); 3D nose read shape but simplified (power hard-coded 0.5,
no param), 3D transition = plain cylinder cone. Fix = src/tree/shapeProfile.ts,
line-for-line port of kernel Transition.Shape.getRadius incl. secant ogive,
per-shape param clamp, and CLIPPED transitions (ellipsoid/power/haack —
kernel's default state; binary-search clip solve). TreeSchematic profilePath()
+ Rocket3D lathePoints() both sample it. shapeParameter FIELD added to
PropertyPanel (shown only for ogive/power/parabolic/haack, max 1/3 for haack,
blank placeholder "default: X"). orkFile transition export now writes
<shapeclipped> = isClippable (was always false — desktop mismatch). QUEUED
KERNEL GAPS (next rebuild): bridge ignores shapeParameter on TRANSITIONS
(nose ok, 3-line fix), and imported shapeclipped=false can't be honored.
(2) COMPONENT DATA EXPORT: services/componentTable.ts — one row per component,
engine-computed mass/CG/position + schema-field union in USER units
(diameter-pref aware, option labels); CSV + XLSX in Save/Export menu.
(3) 2D/3D IMAGE EXPORT (RockSim cert-packet parity, modernized): services/
schematicExport.ts — dataHeaderLines (name/dims/span/mass/CG/CP/margin),
schematicSvg (bakes --surface-1/--text-primary/--accent to light values,
identity zoom, white bg, PHYSICAL mm size = prints 100% scale — one file
replaces RockSim's model+100% pair), svgToPng 3840px, snapshotWithHeader
(3D canvas + header; Rocket3D gained preserveDrawingBuffer + onCreated ref).
⬇SVG/⬇PNG on 2D view, 📷PNG on 3D view (Design tab only via exportData
prop). Format matrix in response doc: wrl/iv/oogl/rib/pov/x3d/xbm/xpm/pnm =
dead, skip; OFFERED glTF/STL/resolution-picker/PDF — awaiting Eric's pick.
(4) ISSUE TRACKING: recommended separate public issues-only repo
(online-openrocket-issues); in-app prefilled-GitHub-link + mailto next
session once Eric creates it. 221 tests (198 app + 23 engine; new:
shapeProfile 16, Rocket3D shapes 4, componentTable 5). Browser-verified on
built dist: nose ogive→conical→haack distinct in 2D+3D w/ CP tracking,
param field show/hide + placeholder, transition ogive-vs-conical in 2D, SVG
content inspected (header/mm-size/CG-CP/no vars), 2D PNG 363KB, CSV 13 rows.
NOT click-verified: 3D 📷 (GPU wedged mid-session — LIVE v0.040 3D showed
the same 300×150 stall, so environmental; Eric to click once). GOTCHA: the
rAF→setTimeout monkeypatch trick BREAKS R3F mounting — don't use it for the
3D view; also repeated 2D↔3D toggles under CDP leak WebGL contexts until
Context Lost. Deploy = git push (Actions → Cloudflare Pages).
**v0.041 LIVE (2026-08-11, ~80 s after push; cache-bypass verified: served
bundle index-CwadgWxB.js byte-identical to local (2,772,207 B), version.json
0.041, feature strings present).** Next version v0.042.

## ⚡ v0.040 (2026-08-05): issue batch e — fin auto-align, shroud convert, sub-minimum, EX folders

issues-2026-08-05e → response-2026-08-05e.md. Both QUEUED DISCUSSIONS resolved
by the batch itself (Eric gave direction in the issue file). NO engine rebuild.
(1) 🧭 AUTO-ALIGN FIN SETS button (PropertyPanel, fin set w/ ≥2 sibling sets)
→ tree/finAlign.ts autoAlignFinSets: per-parent overlap groups, grid-search
each later set's rotation for max-min circular clearance vs earlier sets
(generalizes import-interleave; found live that v0.039's add-second-set
default π/exCount is a NO-OP when the new set's own pitch divides it — 6-tube
set added to 3-fin stayed colliding at 60°; button fixes to 30°). (2) TUBE-FIN
COLLISION CAPS now on explicit values: PropertyPanel cross-limits — OD ≤
touching radius (slider+NumField max+SI commit clamp), count ≤ π/asin(r/(R+r));
FIXED tubefins.ts n=2 ÷0 (sin π/2=1 → Infinity; now kernel rule n<3 = body R).
(3) blank tube-fin OD shows `auto: <val>` placeholder (unit/diameter-pref
aware). (4) SHROUD IMPORT PROMPT: tree/shroudConvert.ts detects 1-fin freeform
named /shroud|camera|fairing/i on EVERY import (.rkt/.ork/.CDX1), modal offers
convert→native fairing (same id; L/H from points bbox, W=thickness, mass=
override else shoelace-area×thickness×density; shape defaults halfround);
verified on the real Ultra Neon (114×41×36 mm, 88 g est). Declining is NOT
remembered — reopen re-asks (flagged to Eric). (5) SUB-MINIMUM: bodytube
`caseAirframe` bool (shown only when motorMount on) → mountDiaMm uses OD not
bore (the browser fit filter was the ONLY blocker — sim never gated fit);
AftView now draws motors in bodytube mounts (was innertube-only); .ork
extension tag <caseairframe>; no RockSim equivalent (documented). (6) EX
MOTORS: already existed since 2026-07-03 (e2c298a) — Eric couldn't find it.
Renamed picker button "Browse motors / import EX (.eng, .rse)…", multi-file
input, 📁 Import EX folder (webkitdirectory one-shot scan — PWA can't watch
folders), import notice, guide section "EX motors & sub-minimum builds" (both
mirrors). DRIVE-BY FIXES: import clears diameter-class chips too (persisted
38mm chip hid a fresh 54mm EX import); GENERIC_ROCKET_NAMES gains the
importer fallbacks 'imported rocksim/rasaero rocket' (filename fallback never
fired for name-less .rkt — Ultra Neon showed the generic). 196 tests
(173 app + 23 engine; new: tubefins/finAlign/shroudConvert + caseairframe
round-trip). Browser-verified on built dist end-to-end (placeholder 40.64 on
the 6-tube identity, slider cap, count cap 7 @30mm, align 60°→30°+banner,
sub-min chip 38→41mm, shroud prompt+convert on real Ultra Neon, .eng import
through the real input). deploy/online-openrocket-v0.040.zip READY.
Cleanup verified: Eric's Wild Child session/filters/EX list restored.
**v0.040 LIVE (2026-08-05, cache-bypass verified: served bundle
index-8ZgGK_NG.js at full byte size = the final local build incl. both
drive-by fixes; version.json 0.040; badge renders; Eric's live session
already shows the filename-fallback name on his re-imported Ultra Neon).**
Next version v0.041.

## ⚡ START HERE → read `docs/handoff-2026-08-05.md` first

The current, self-contained session handoff: v0.033–v0.039 (the 2026-08-05
marathon — five issue batches, seven releases), the live-verify state, and
**the QUEUED NEXT-SESSION DISCUSSIONS Eric asked for: (1) camera-shroud
import/conversion of hand-rolled 1-fin-freeform shrouds, (2) fin-rotation
functionality follow-up.** Discuss before building — his standing style.
The per-version blocks below remain as history.

## ⚡ v0.039 (2026-08-05): fin rotation + auto-interleave + pair-mode combos (batch d)

issues-2026-08-05d → response-2026-08-05d.md. FIN ROTATION: 'rotation' on
all 4 fin-set types; ENGINE REBUILD (bridge ComponentFactory wires
setBaseRotation for FinSet AND TubeFinSet — kernel modeled it all along);
differential 258 ×3 stable, engine vitest green. 3D+aft draw true angles
(2D side stays stylized — check interleave in AFT view). Round-trips .ork
<rotation> (was hard-coded 0.0!) + RockSim RadialAngle. AUTO-INTERLEAVE:
RockSim stores NO angle (Ultra Neon: both sets at 0) — import rotates
overlapping same-angle sets by half the other's pitch + note; verified on
the real Ultra Neon (tube fins → 30°); editor add-second-set defaults
between existing fins. NOTE: Eric's Ultra Neon contains a hand-rolled
1-fin-freeform "Camera Shroud" — suggested swapping for the native
component. PAIR-MODE COMBOS: splitClusterPairsTree (6-ring → 3×'double',
scale ×2, φ+90/±30 — exact), batch checkbox "mixed 4+2 / 2+2+2" flying
candidate MULTISETS minus all-same (cubic growth — warned), combo pass
generalized to K groups, configs tagged/sorted/tabbed. 181 tests (158+23).
AWAITING ERIC: live-fire Darkstar + Ultra Neon; RockSim audit (optional);
shroud calibration flight.

## ⚡ v0.038 (2026-08-05): Darkstar cluster import + batch mount picker + ⏏ unload

Chat batch. KEY BUG: RockSim writes the SAME tube with drifting rounding
between cluster copies (Darkstar: OD 79.38 vs 79.375) — exact-key grouping
split the ring; now 1%-tolerance grouping (position 1 mm). Verified against
Eric's real file (G:\Documents\Dropbox\Rocksim Designs\Scratch Builds\
PELTZER - 12in Darkstar.rkt): 6×75 ring → one 6-ring scale 1.2 around the
central 98, both motors auto-load. Batch dialog gained a MOUNT PICKER
(other mounts keep their assigned motors per flight) — this was why "3+3
seemed missing": batch was locked to the primary mount (Darkstar primary =
central 98). ARCHITECTURE: batch now builds its OWN engine handles and
never mutates the shared design handle (stale-motor class gone; restore
logic deleted). Vitals ⏏ unloads all motors (all tabs, one click).
179 tests (156+23). End-to-end verified on the real Darkstar in Chrome.

## ⚡ v0.037 (2026-08-05): combination batching + mount-level max motor length

Eric's chat go-ahead (no issues file — spec was in chat): combination batch
is OPT-IN ("mixed pairs" checkbox, 4/6-motor clusters only, default =
same motor everywhere, count shown first). Core = treeModel.
splitClusterTree(): 4-ring → 2×'double' (scale ×√2, ±45°), 6-ring →
2×'3-ring' (scale ×√3, 0/60°) — exact positions, geometry-tested; combo
pass = separate engine handle from the split tree, unordered pairs, spec
cache reuse, per-combo auto model. Exports grouped per Eric: motorConfig
field + CSV column, singles-then-mixed CSV sort, XLSX tabs All/Single/
Mixed (xlsx.ts → sheetsToXlsx multi-sheet). KERNEL PROOF
(comboFlight.test.ts): split-same ≡ original 4-ring within 1% apogee.
Max motor length: PRIMARY on the mount tube (schema maxMotorLength +
.ork <maxmotorlength> extension) — root cause of Eric's "doesn't
persist" was session-only storage wiped on .ork open; Motors-tab field
is now a per-stage override (placeholder "design: X", clear = fall
back). 178 tests (155+23). Smoke: mixed-pairs checkbox appears only on
4-ring, count line 161→+12,880 warns before committing.

## ⚡ v0.036 (2026-08-05): issue batch c — one model picker, batch aero, XLSX, RockSim pods

Batch c (issues-2026-08-05c.md → response-2026-08-05c.md). Aero terminology
FIXED per Eric: ONE Preferences pulldown (EB desktop parity / Rogers Kbf
DEFAULT / Auto = Kbf + our supersonic past M0.9 / Supersonic ours) — derives
from and writes the existing aeroModel+rogersKbf keys, NO migration; Kbf
checkbox gone; "RASAero-class" scrubbed from all UI (it's OUR model — no
RASAero code/equations exist; guide keeps the historical reference). Batch
dialog: own aero select DEFAULT AUTO, per-motor supersonic refly, restores
the shared handle flags (handleFlags prop replaced aeroModelLabel). XLSX:
services/xlsx.ts = minimal OOXML zip over fflate (~2 KB; typed cells/frozen
bold header/autofilter) + runsToTable() sharing the CSV catalog; buttons in
SimHistory + Batch. RockSim gaps CLOSED: ExternalPod↔podset/parallelstage
both ways (desktop PodHandler semantics; Detachable↔booster; N instances
split), fin CantAngle round-trip (radians; desktop only writes it),
RingTail stays ignored (desktop parity). 172 tests (149+23).
Browser-verified: pulldown kbf-default, batch auto-default, XLSX buttons.
AWAITING ERIC: combination-batch go (his symmetry cap 4/6-motor agreed;
"offer to split a 4-ring into 2+2" design in response doc); optional full
RockSim audit; shroud calibration flight.

## ⚡ v0.035 (2026-08-05): HOTFIX — aft-view pan crash

Eric hit it on the live site within the hour: pan the zoomed aft view →
"Cannot read properties of null (reading 'x')". AftView's onPointerMove read
pan.current inside the setZoom UPDATER (runs after the handler; pointerup
nulls the ref in between). Fix = capture to a local first — the pattern
TreeSchematic already used. LESSON for any new pointer/zoom code: never read
a mutable ref inside a state updater. Verified by scripting the exact race
on the built dist (React batches move+up, so the deferred updater sees the
nulled ref — reproduces old crash, passes fixed). v0.035 zip ready; Eric's
batch-b follow-up answers still pending (gap audit, combo batch, shroud
calibration).

## ⚡ v0.034 (2026-08-05): issue batch b — camera shrouds, spill holes, custom tiles

Eric's follow-up file (issues-2026-08-05b.md) same-day; status in
**response-2026-08-05b.md**. DECISIONS LOCKED: Rogers Kbf DEFAULT ON (his
flight data; explicit stored false preserved, "off = desktop parity"),
tagline = "Design, simulate, fly — OpenRocket-derived physics, validated to
Mach 4.6 against NASA wind-tunnel data.", stability thresholds confirmed.
THE BIG BUILD: **camera shroud 'fairing' component** — app-level type
lowered in treeModel.engineTree() to a 1-fin freeform strake (Barrowman
low-AR = Jones slender-strake ⇒ real kernel CP shift) + Hoerner frontal-area
protuberance CD as overrideCD (streamlined .25/halfround .55/box 1.05) +
as-built mass as overrideMass, same node id; NO kernel/TeaVM change (engine
pkg tsc rebuild only for the ComponentType). THICK_FIN kernel warning
suppressed for shrouds in App's build memo. No wind-tunnel anchor — Eric to
fly before/after for calibration. Also: spill holes (engineTree
cd·(1−(dh/D)²), RockSim SpillHoleDia round-trip); FlightStats → run-driven
14-metric catalog + ⚙ picker (prefs.resultTiles; "Descent hits"→"Landing
rate" — it was groundHitVelocity); aft-view zoom/pan; RockSim
partial-override export writes computed other value (componentInfo map
threaded into exportRkt); 2D glyphs (chute/mass/CR/cord) + bulkhead hatch +
two-way selection sync (dragMoved guard so drags don't select). 168 tests
(145 app + 23 engine). Browser-verified: Kbf flipped Wild Child 4.13→4.49
cal; shroud add 4.49→4.78 & 825→855 g, solid accent-outlined render.
**deploy/online-openrocket-v0.034.zip ready.** WAITING ON ERIC: RockSim gap
audit go/no-go; mixed-motor combination batch go/no-go (~1 session, design
in response doc); shroud calibration flight.

## ⚡ v0.033 (2026-08-05): issue batch 2026-08-05a — 16 of 19 items, one session

Eric's 19-item beta list (docs/testing/issues-2026-08-05a.md) fixed same-day;
item-by-item status in **response-2026-08-05a.md** (READ IT — it carries the
open discussion items). Root causes worth remembering: preset dragCoefficient
was silently dropped on apply (Fruity Iris Ultra flew Cd 0.8 instead of 2.2 —
descent 1.66× fast; presets.ts one-liner); RASAero's GetSimulations NRE was
our 5-element Simulation block vs the 24 fields its loader reads blindly
(+ empty SustainerEngine = second NRE; engines must be OMITTED when absent);
RockSim masscomponent export passed the 10 g `mass` param over the override;
the 2D coupler asymmetry was PAINT ORDER (opaque next-tube covered the aft
overhang — inner/dashed shapes now render in an overlay pass); tube fins
imported fine but were never DRAWN. New surface: AftView.tsx (2D/3D/Aft
toggle + auto Motors-tab inset when cluster/pod present), plugged-delay UI
(JSON.stringify(Infinity)=null corrupted stored plugged runs — "Infinity"
string replacer in simStore+session), cluster reconstruction on RockSim
import (pattern+scale+rotation fitted to CLUSTER_POINTS), cut/copy/paste,
tiered stability (under=red, 1–3=green, over=YELLOW caution, one rule on all
surfaces — thresholds PROVISIONAL pending Eric), per-type inner-component
colors+tags, header Undo, unit-pref CSV detail columns (flight-day lead 14
stay fixed ft/mph/Gs/g deliberately). 161 tests green (138 app + 23 engine);
NO kernel change (differential untouched, 258 lines). Browser-verified on
the built dist incl. aft view single+3-ring and the coupler overhang.
**deploy/online-openrocket-v0.033.zip ready for Eric's upload; version.json
bumped.** WAITING ON ERIC (response doc §"Waiting on you"): stability
thresholds, tagline wording, results-page customization, camera-shroud
feature go/no-go, inner-component visual upgrade pick.
Gotcha: monkeypatching rAF in the live page then `delete
window.requestAnimationFrame` REMOVES rAF entirely (it's an own window
property) — reload the page to restore; also CDP screenshots time out once
then succeed (retry, don't debug).

## ⚡ FULL AUDIT (2026-08-04, Eric: "pause and audit/debug all work to-date") → v0.031

Five parallel review agents swept engine bridge/patches, app services,
components/UI, tree/state/deploy, and the validation harness; every confirmed
finding was fixed same-session. **THE BIG ONE: the InstanceMap LinkedHashMap
determinism patch (v0.013 era, LEDGER "Determinism fixes") had been DEAD since
commit d1cc156** — it sat at `patches/rocketcomponent/` but carve.mjs resolves
patches at the FULL manifest-relative path (`patches/info/openrocket/core/...`),
where an older undocumented CHM→HashMap classlib swap lived instead. The
shipped kernel iterated in identity-hash order the whole time (differential
passed on tolerances + TeaVM's deterministic object ids). Restored at the
right path; carve.mjs now FAILS on orphaned patch files. Full fix list
(v0.031 changelog + commit): plugged-motor `<delay>none</delay>` import
(was 0 s → ejection at burnout; now Infinity + "-P" labels + note),
tube-fin thickness import, overridesubcomponents mass/CG/CD round-trip AND
kernel application (new ComponentFactory support + engine test), RockSim
middle-position export (desktop BasePartDTO parity) + power-nose
ShapeParameter fallback, RASAero non-bottom fin offset, preset CSV
lineMaterial, App triple-normalizeTree phantom-id bug (fresh users got no
default motor; legacy maxMotorLength migration no-oped), last-stage delete
guard, undo-coalescing-across-undo fix, parallel-booster G80 warning keying
(branch name, not serial stage — safety hole), rename-wipes-results (reset
effect now keys on a physics projection of the tree, names/colors excluded),
railbutton schematic size, freeform-fin vHalf from points y-max, CSV "Aero
model" column, guide seed-claim correction, OrkEngine escape() control chars,
setMotorById handle leak, airfoilSection validated at build, lat/long
degrees documented at the boundary. VALIDATION ANCHOR REVISION 137→135
gates: ARCAS M1.19 CD rows were D-4013 base-corrected data double-counted
under the wrong base convention — removed; scores now classic 7/135,
supersonic 64/135 (was 8/137 / 65/137; the delta is the spurious pass);
README scoreboard marks phase scorecards historical, current state in
scorecard-audit-2026-08-04.md. Deliberately NOT fixed (recorded): Pages
workflow ships no version.json (dormant), package-dist validates source not
artifact when invoked directly, pendingRelaunch latch (unreachable), batch
sim never auto-upgrades aero (deliberate), latitude ° not a UnitChip,
SimResults motor-diameter hard mm, fin transonic C¹ kink at M1.5 (same
approximation class as upstream). Suite grew 146→154 tests (8 new
regressions); differential 258 lines 5× stable post-restore.

## ⚡ START HERE → read `docs/handoff-2026-08-04.md` first

That file is the current, self-contained session handoff (v0.024–v0.029:
the workspace-tabs reorg, the COMPLETE RASAero #1 supersonic build with its
validation harness, Auto aero mode + alert, the version.json deploy channel,
and the three-pass design refresh — plus the session's hard-won gotchas).
**One-line state:** v0.030 LIVE and verified (2026-08-04, cache-bypass:
bundle index-raArmRY-.js 200 at full size, version.json serving 0.030, app
badge v0.030, new wide layout rendering with Eric's Cherokee L1 Smart session
intact). 124 app+engine vitest green; Eric beta-testing (his next issues file
gets fixed FIRST). Next version v0.031. The RASAero-build sections below
remain as history.

**v0.030 (2026-08-04, one session):** Eric's request — desktop app felt cramped
on big monitors. Thesis: chrome stays dense, the STAGE grows. 1200px workspace
cap removed (fluid, clamp padding); TreeSchematic viewBox now adopts container
width via ResizeObserver + height from the rocket's own aspect (clamped
200–480px, `maxHeight` prop = 300 on Motors tab; the old fixed 640×210 +
motors-svg 240px CSS cap are gone); visible + / − / ⤢ Fit zoom cluster
(wheel/pan existed, undiscoverable); ≥1600px: rails 320/380px, charts-grid
2-up (3-up ≥2400px), chart height scales 160→240px, 3D view 42vh, and a
working-panel TYPE-SCALE bump (labels 12.5, inputs/tree 14, tabs 16, vitals 18,
tile values 28). GOTCHA burned once already: the wide-screen media block MUST
stay LAST in styles.css — equal-specificity overrides win by source order, and
a mid-file block silently lost to later base rules (.stat-row). Verified live
in Chrome at ~2100px CSS width (design/motors/results tabs, zoom buttons, B6-4
flight for charts). Below 1600px the dense layout is byte-identical.

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

**State:** Phase 0a/0b research COMPLETE (2026-08-03, 14 agents, all verified
solid). Deliverables: **docs/research/rasaero-supersonic-spec-2026-08-03.md**
(target equation spec — NOTE: no RASAero equations exist in any source; both
"Rogers & Cooper" PDFs are actually Users Manuals with methods-by-name only,
so the spec is a full open-literature reconstruction scored via anchors) and
**docs/research/validation-anchors-2026-08-03.md** (6 datasets; confirmed
ceilings: ARCAS M4.63, Basic Finner M4.47 full set, HB-2 M10 well-populated /
M16.5 single-run, flights M5.5 Aftershock II; NTRS primary reports retrieved
incl. original ARCAS TN D-4013/D-4014). Key corrections vs handoff §5: the M4
CP anchors track RASAero (63.5 %L), the TUNNEL says 60.7 %L — harness should
prefer the tunnel; Short transonic peak is at M1.15 not M1.1. Kernel finding:
body CNα/CP freeze is total (SCC never reads Mach), but nose wave drag + fin
Busemann CNα are already RASAero-consistent below ~M4 — the build is mostly
EXTENSION not replacement. **Phase-0 HARNESS BUILT (2026-08-04):**
getDragSweep now emits cp/cna (bridge change, engine rebuilt, difftest 248
3× + vitest 20 green); validation/ has fixtures (arcas-short/long,
basic-finner, hb2 w/ documented approximations), anchors.json (137 gate
points), score.mjs, README, and the frozen classic baseline
(**8/137 = 5.8%** — baseline-classic-2026-08-04.md). KEY BASELINE INSIGHT:
kernel CP isn't just frozen — combined CP travels forward ~2× TOO FAR
(frozen body CP + Busemann fin falloff): ARCAS model 27 %L vs tunnel 57 %L
at M4.63. Supersonic CD ~2× high. Harness gaps listed in validation/README
(Re-matching, flight fixtures, Cajun span, power-on series).
**PHASE 1 SHIPPED (2026-08-04): 8/137 → 52/137 gate points.** Opt-in
`supersonicAero` flag (kernel patches, LEDGER "feature #1 Phase 1"):
(1) supersonic fin CNα was HALF of 2D linear theory (K1=2/β single-surface
Busemann used as the whole slope — THE root cause of the CP collapse); flag-on
scales by 2·(1−1/(2·AR·β)), analytic (no M4.9 grid clamp); (2) NACA-1307 exact
body-fin interference K_W(B)+fa·K_B(W) at all Mach (fa = afterbody carryover
factor; supersedes rogersKbf when on); (3) nose CNα Mach growth g=0.10 cone /
0.07 ogive (Taylor–Maccoll-bracketed surrogate; first-ever SCC patch).
RESULT: ARCAS supersonic CP 9/9 gated BOTH configs (matches the TUNNEL incl.
above M3.5 where RASAero diverges); Finner CP 17/23, CNα 16/23 (fails =
transonic band → Phase 2, + shot-scatter marginals); CD series all red (Phase
2); HB-2 flare body deferred. Finner scores at α=2° (free-flight fit
condition, _aoaNote in anchors.json). Differential 252 lines 3×; 145 tests
green; prototyped in scratchpad JS before porting (fast iteration pattern —
reuse it). Flag is API-only (OrkRocket.setSupersonicAero) — NOT yet exposed in
the app UI (default-off during beta per Eric).
**PHASE 2 SHIPPED (2026-08-04): 52/137 → 68/137.** Same flag; LEDGER Phase-2
block: (1) sharp AIRFOIL fins lose the spurious blunt-LE cylinder-drag plateau
(root cause of flat supersonic CD AND the early fake transonic rise) → thin-
airfoil wave K·4(t/c)²/β; (2) boattail supersonic wave (strip −2θ/β, blended
0.8→1.5 past the near-sonic divergence); (3) nose wave decay past table end
(analytic branch / Fleeman shape); (4) base cap 1.2/M² above M≈4.8; (5) fin-
body junction interference ×1.8 on fin friction (calibrated to D-4013 fins-
on/off increment); (6) sweep machAlt option = tunnel-Re matching (harness
uses RASAero's own ARCAS Mach-Alt table); fixtures now polished finish.
RESULT: ARCAS-Short supersonic CD 7/7 GREEN (M1.19–4.65!), Long 5/6, subsonic
green, CP still 9/9. Differential 256 lines 3× (new ssaerocd goldens); 145
tests green. DOCUMENTED limitations (do not fudge-tune): transonic peak band
M0.95–1.2 underpredicts up to ~0.3 (tunnel fin transonic drag ≈4× subsonic;
RASAero misses same anchors by 0.10–0.22) — "transonic refinement" backlog;
Finner Cx0 low pending wedge blunt-TE fin base drag (Phase 3 airfoils); HB-2
flare/bluntness (hypersonic phase). **PHASE 3 SHIPPED (2026-08-04): fin airfoil sections (#4), 68 → 65/137 —
an HONEST decrease.** Input-gated (no flag; absent = bit-identical): FinSet
patch (FIRST rocketcomponent patch) adds airfoilSection (hexagonal/naca/
doublewedge/biconvex/hexbluntbase/singlewedge) + LE/TE diamond lengths +
finLeRadius; FinSetCalc.sectionPressureCD implements per-shape linearized
thickness wave τ²-family + fin base drag for blunt-base sections + LE
bluntness (swept-cylinder fit; NACA gets implicit 1.1019τ²c radius).
Differential 258 lines 3× (finsection goldens); 146 tests. Finner fixture
now uses its TRUE singlewedge section — which UNMASKED a systematic Finner
Cx0 deficit (−0.04..−0.13, decaying with M) the biconvex placeholder had
been accidentally covering. NOT fudged; flagged: suspected free-flight
base-drag environment (base pressure behind finned body < clean-cylinder
Hoerner 0.25/M law) — refinement candidates: McCoy/BRL base-pressure
correlation, NACA RM A53D02 digitization. ARCAS + all CP series unchanged
green. UI for sections NOT yet exposed (Phase 5 with the pref toggle;
schema/PropertyPanel/.ork+.CDX1 round-trip still to do there).
**PHASE 4 SHIPPED (2026-08-04): hypersonic corrections, 65/137 unchanged
but physics moved right.** Same flag: VD-II friction fit above M4 (fade
3.5–4.5); cone wave coefficient 2.1→Cp_max(M) fade M4–8 (stagnationCpMax
helper, Rayleigh pitot). HB-2 CA0 high-M excess cut ~45% (+0.25→+0.14);
ARCAS M4.65 tightened to −0.003. DELIBERATE NON-GOALS documented in LEDGER:
spherical-cap nose bluntness + flare-effectiveness decay (HB-2-specific,
no hobby relevance, single-dataset calibration risk). Differential 258 3×;
146 tests. Kernel model work for the RASAero build is now COMPLETE through
hypersonic. Next: **Phase 5 — app surface + v0.025 release**: Preferences →
Aerodynamics "Supersonic aerodynamics (beta)" toggle (default OFF per Eric,
wire rocket.setSupersonicAero in App buildResult + record model in SimRun/
launch report), fin airfoil-section UI (schema fields + PropertyPanel +
2D?), .ork round-trip for airfoilSection/diamonds/finLeRadius (+ .CDX1
mapping), DragPanel Mach range to 25 w/ regime shading + CP-vs-Mach plot,
coefficient CSV export (#6), guide both mirrors, changelog, package, browser
smoke-test. v0.024 LIVE; next version v0.025.
**PHASE 5 SHIPPED (2026-08-04): v0.025 packaged — the RASAero #1 build is
COMPLETE end-to-end.** App surface: Preferences → Aerodynamics "Supersonic
aerodynamics (beta)" toggle (default OFF; supersedes Kbf when on; wired into
buildResult so staticInfo/dragSweep/simulate all agree); SimRun.aeroModel
recorded + shown in the launch report ("Aero model" row); fin airfoil-section
UI (schema AIRFOIL_FIELDS on all 3 finset types); .ork round-trip
(<airfoilsection>/<airfoillediamond>/<airfoiltediamond>/<finleradius> —
desktop warns-and-ignores); DragPanel: CP-vs-Mach chart (%L), Mach 10/25
options (model-gated), model-aware honesty labels, CSV = full coefficient
table (feature #6 done); guide BOTH mirrors (new How-It-Works section);
changelog v0.025. Browser-verified on the built dist: toggle flips stability
4.13→4.41 cal live, airfoil select + fields render, all 3 drag charts +
Mach 25, Aero-model row renders (via stored-run path — live flight blocked
only by window occlusion/rAF, known gotcha). 146 tests green.
**deploy/online-openrocket-v0.025.zip READY for Eric's upload.** REMAINING
backlog (refinement, not blocking): transonic peak band, Finner free-flight
base drag (McCoy/RM A53D02), blunt/flare bodies, MESOS/Aftershock
end-to-end flight fixtures, .CDX1 airfoil mapping, default-ON flip after
Eric's beta testing. **v0.025 LIVE (2026-08-04, cache-bypass verified:
bundle index-DyrG7f5k.js matches local, all feature strings present).**
Eric now beta-testing the supersonic model; standing rule applies — his
next dated issues file gets fixed before new feature work.
**v0.026 BUILT (2026-08-04, Eric's design decision): three-way aero pref
(Classic default / Auto / Supersonic) + supersonic-flight alert.** Auto:
fly classic, if maxMach > 0.9 (transonic onset) re-fly the WHOLE flight
supersonic (App.autoSupersonic state; displayed statics follow, "M+ aero"
vitals chip; resets on design/motors/launch/mode change); run recorded as
'auto-supersonic'. Classic-mode supersonic flight → warning banner in
Results + saved-report comment + one-click "Switch to Auto & re-fly"
(pendingRelaunch effect). Prefs migration: supersonicAero:true →
aeroModel 'supersonic'. Batch flies the handle's current model
(aeroModelLabel prop). Guide both mirrors + changelog. BROWSER-VERIFIED
end-to-end incl. the full auto-flip and one-click-switch loops (trick for
occluded-window testing: monkeypatch requestAnimationFrame to
setTimeout(cb,33) in the page — sims run headlessly; 0ms spins the CPU,
use 33ms; lightweight rocket via session-JSON overrideMass edits + reload
beats fighting NumField DOM commits). 146 tests green.
**v0.026 LIVE (2026-08-04, cache-bypass verified: bundle index-DY2bfBqi.js
matches local, all v0.026 feature strings present in the served JS).**
**UI DESIGN REVIEW (2026-08-04, frontend-design skill, recommendations
Eric approved in order):** pass 1 = accent/icons/quiet-chrome/polish;
pass 2 = vitals-strip signature + schematic "stage" backdrop + empty
states; pass 3 (optional) = bundled display face (MUST be self-hosted +
PWA-precached, no CDN). Deliberate non-changes: workspace structure,
density, chart palette/discipline, dark default, unit chips, report copy.
**v0.027 BUILT (pass 1): exhaust-orange accent (--accent per theme +
--launch for filled buttons; series colors now data-only), Icon.tsx
inline-SVG stroke set replaces ALL emoji (wordmark = orange rocket glyph
+ tracked-caps ONLINE OPENROCKET), session-restore demoted to fading
.session-note, neutral tree badges, muted chart legends, site-nav skinned
to WordPress voice, checkbox width fix, in-app links accent-colored.
Browser-verified both themes; 146 tests; zip ready.**
**NEW RELEASE STEP (Eric, 2026-08-04): /version.json at repo root ships
at the app root every release (package-dist.mjs copies it post-build —
never SW-precached — and FAILS if its version lags APP_VERSION). Update
version+released+note (user-facing) each release; his Online Tools page
polls it to prompt refreshes.** **v0.028 BUILT (pass 2): vitals strip = SIGNATURE instrument bar
(.vitals-item label/value stations, hairline dividers, orange Launch
terminus), .rocket-stage dusk-sky gradient behind the 2D/3D viewports
(Design + Motors), empty-state cards (.empty-state) + .panel-dormant for
collapsed Drag/Saved panels. Self-review catch: v0.027's global accent
link rule out-specified .site-nav a (all-orange nav) — fixed with
.viz-root .site-nav a. Browser-verified both themes; 146 tests; zip
ready (Eric can skip uploading 0.027 — 0.028 supersedes).** **v0.029 BUILT (pass 3, FINAL): Rajdhani display face (OFL, @fontsource
latin 600+700, ~56 KB self-hosted, woff/woff2 added to workbox
globPatterns → precached/offline). --font-display token drives wordmark,
panel/chart headings, tabs, vitals labels+values, stat tiles, LAUNCH
button (tracked caps). Body stays system-ui. THE DESIGN REFRESH IS
COMPLETE (0.027 accent+icons → 0.028 telemetry bar+stage+empty states →
0.029 type). Eric uploads 0.029 only (supersedes 0.027/0.028 zips).**
**v0.029 LIVE (2026-08-04, cache-bypass verified: bundle index-_iVJtr_m.js
matches local; Rajdhani/vitals/rocket-stage CSS present; woff2 fonts serve
200 with correct byte size; version.json LIVE at the app root serving the
0.029 payload — the Online Tools auto-refresh channel is operational).**
Next version v0.030.
GOTCHA (hit THREE times this session — stop using it): PowerShell
`(Get-Content -Raw) -replace ... | Set-Content` MANGLES UTF-8 (em-dashes →
mojibake) and `git show | Set-Content` collapses newlines. For any file
with non-ASCII content, edit with the Edit/Write tools only; restore files
with `git checkout <commit> -- <path>`, never via PowerShell pipes.

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

- Resume work: `cd E:\git\online_open_rocket` → `claude` (CLAUDE.md auto-loads;
  repo moved from `G:\git` 2026-08-11 — `git fetch` first, Eric works from two
  machines now).
- Run the app: `cd packages\app && npx vite preview --port 4180` (built dist), or
  `npm run dev` from the repo root for live-reload during development.
- Batch issue lists: markdown file preferred, e.g. `docs/testing/` + repro `.ork`
  files beside it (see below).
