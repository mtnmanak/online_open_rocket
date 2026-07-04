# Working notes — collaboration style & project state

For the project owner (Eric) and any future Claude session. Technical rules live in
`CLAUDE.md`; this file carries the *conversational* context that doesn't fit there.
Last updated: 2026-07-03. **Phase 3 is underway — Eric decided: deployment
FIRST, then staging/clustering.** Deployment shipped as v0.006 (PWA/offline +
manual-webhost package + dormant GitHub Pages workflow — see the Phase 3
entry below and docs/deployment.md). Eric will manually upload the package to
his own web host now; GitHub Pages comes later when he makes the repo public
(it's private today). **Next up: staging/clustering — research is DONE and
the design doc awaits Eric's review: docs/staging-clusters-design.md.**
Headline: the carved kernel already implements everything (separation
branching, tumble CD, cluster thrust×N); all gaps are in our bridge/app
layers. Recommended order: Release A clusters (~1 session), B staging
engine+.ork (~1-2), C staging UI+per-branch reports (~1-2). Five questions
for Eric are at the end of the design doc (defaults for separation/ignition
triggers, batch-sim scope on staged rockets, parallel-stage deferral…). Do
not start implementing until he's reacted to the doc.

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
