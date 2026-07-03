# Working notes — collaboration style & project state

For the project owner (Eric) and any future Claude session. Technical rules live in
`CLAUDE.md`; this file carries the *conversational* context that doesn't fit there.
Last updated: 2026-07-03, end of the fifth pass (issue batch "b" — the in-depth
test list, all items addressed). **Current state: batch issues-2026-07-03b.md is
fixed and answered in response-2026-07-03b.md — Eric should verify visually.
Next batch = new dated file per protocol (never append to a resolved batch's
file). Phase 3 decision (deployment vs staging/clusters) is still Eric's call
and still pending.**

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
- **He is the visual QA.** Claude cannot see the rendered UI; Eric reloads
  http://localhost:4180 and reports. Never claim UI "works" — claim it builds, tests
  pass, and ask him to verify.
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
- **Pending decision (Eric's):** Phase 3 opens with either **deployment**
  (standalone hosting + WordPress embed — a founding goal) or **staging/clusters**
  (the flagship complex feature). Do not start either without his call. He wants
  one more in-depth test pass (new issue list) before Phase 3 begins.
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
  `node packages/app/scripts/fetch-component-presets.mjs` (3,449 parts,
  openrocket/openrocket-database); materials generated once from the desktop's
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
