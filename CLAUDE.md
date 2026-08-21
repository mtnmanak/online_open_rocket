# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> ## 🎫 Feedback / issue tracking — settled, do NOT rebuild it
>
> Bug reports and feature requests for this tool go to **one central public tracker**,
> `mtnmanak/mountainmanrockets-feedback`, routed by label from a dropdown on the issue form.
> It is implemented and live as of 12 Aug 2026. This tool's dropdown option is
> **MMRocket Sim** and its label is `tool:mmrocket-sim` (both renamed with the product in
> 2026-08; the dropdown string is byte-matched by the tracker's labeling workflow).
>
> **Read `docs/feedback-tracker.md` before writing anything feedback-shaped** — a button, a
> link, help copy, a report page. It has the URLs, the label taxonomy, the exact prefill
> rules (dropdowns **cannot** be prefilled — that one wastes an afternoon), and the standing
> UI rulings.
>
> **Do not create a feedback repo, labels, issue templates, or a labeling workflow in this
> repo.** Several older docs here still recommend exactly that; they are stale and
> `docs/feedback-tracker.md` overrides them.

## What this is

**MMRocket Sim** — a browser-based re-creation of **OpenRocket** (the Java/Swing
model-rocketry design and flight-simulation desktop app). The full technical plan,
strategy comparison, and phased roadmap live in `docs/online-openrocket-plan.md` — read
it before making architectural decisions (it predates the rename and still says "Online
OpenRocket"; that is history, leave it).

**Naming rule (2026-08 rename).** The product was *Online OpenRocket* through v0.046 and
is **MMRocket Sim** from v0.047. Only the product identifier changed: every OpenRocket
*attribution* stays as written — the 24.12 kernel, `.ork` compatibility, the project
links, the GPL lineage. Never write copy implying this app **is** OpenRocket or is
published by the OpenRocket project; that implication is why it was renamed. History
keeps the old name (past CHANGELOG entries, `docs/handoff-*.md`, `docs/testing/*`,
`docs/archive/**`, `docs/working-notes.md` history). Also unchanged by the rename: the
`online-openrocket.*` localStorage keys, the `@online-openrocket/*` package names, the
`online-open-rocket` Cloudflare Pages project, and local repo folder paths.

Reference Java source (OpenRocket release 24.12, GPLv3+) is available locally on both
machines — desktop `G:\Documents\Dropbox\Open_Rocket_Source_Code\openrocket-release-24.12`,
laptop `C:\Users\peltz\Dropbox\Open_Rocket_Source_Code\openrocket-release-24.12` (see
"Two machines" below; the scripts that consume it probe both paths). Its
`core/` module (`info.openrocket.core`) is the physics/model reference; its `swing/`
module is being replaced by the web UI and should not be ported.

## Deploying

This tool is **its own Cloudflare Pages project**. It is not copied into the
mountainmanrockets.com site any more — publishing this repo publishes the tool, and
the site is not rebuilt or touched.

| | |
|---|---|
| Pages project | `online-open-rocket` |
| Live URL (canonical) | https://mmrsim.mountainmanrockets.com — cutover 2026-08 (old `openrocket.*` subdomain serves a moved notice until the phase-2 301) |
| Old WordPress path | `/online_open_rocket/` — 301s to the old `openrocket.*` subdomain, do not reuse |

**`mmrsim.mountainmanrockets.com` is the ONLY address to hand out** — anywhere: docs,
invites, links, help copy. It is the address on the beta invite (`docs/beta-invite.md`)
and the one testers will bookmark. The retired `openrocket.*` subdomain still serves the
app behind a moved notice so already-invited testers aren't stranded; never hand it out as
the app's address — the beta invite's migration note is the one sanctioned mention.

**`git push` to `main` is the deploy.** `.github/workflows/deploy.yml` runs
`npm ci && npm run build` (the user-guide generator, then tsc + vite — the TeaVM
engine kernel is the committed artifact `packages/engine/vendor/orkengine.mjs`, so
CI still needs no Java), copies
`version.json` into dist the way `scripts/package-dist.mjs` does, and publishes
`packages/app/dist` with wrangler. The workflow needs the `CLOUDFLARE_API_TOKEN`
repo secret (account API token with "Cloudflare Pages — Edit").

Manual override from either machine:

```bash
npm run build                      # -> packages/app/dist
cp version.json packages/app/dist/
npx wrangler pages deploy packages/app/dist --project-name=online-open-rocket --branch main
```

Bump `version.json` when you cut a release — `scripts/package-dist.mjs` copies it into
`dist`. The main site prints that version on its Online Tools page from
`src/data/tools.mjs` in the `mountainmanrockets` repo, and `node scripts/check-tools.mjs`
there reports when the two have drifted. That check caught this repo sitting 22 releases
ahead of what had actually been published, which is the failure mode it replaces.

Serving the app to browsers is GPL **distribution**: the app is a derivative of
OpenRocket (GPL-3.0-or-later, full text in `LICENSE`), and the in-app link to this
public GitHub repository (`mtnmanak/mmrocket-sim`) is what satisfies the
corresponding-source offer — if the repo is ever renamed again, that link moves with it
in the same sitting.

## Commands

- `npm run dev` — start the Vite dev server (app package)
- `npm run build` — build engine then app
- `npm test` — run engine tests (vitest)
- Single test: `npm run test -w @online-openrocket/engine -- src/index.test.ts`

Engine (Java kernel → JS via TeaVM, in `engine-java/`):
- `node engine-java/scripts/carve.mjs` — copy manifest-listed OpenRocket sources in
  (idempotent; FAILS if a carved copy was edited — carved files are never edited)
- `engine-java/gradlew.bat generateJavaScript` (run inside `engine-java/`) — TeaVM build
- `node engine-java/scripts/difftest.mjs` — JVM vs TeaVM-JS bit-identical differential test
- No system JDK. A portable Temurin JDK 17 lives at
  `C:\Users\Eric\.online-openrocket\jdk-17.0.19+10` (desktop) /
  `C:\Users\peltz\.online-openrocket\jdk-17.0.20+8` (laptop) — set `JAVA_HOME` to it
  for Gradle (difftest.mjs finds it automatically). If it is missing on either
  machine, re-fetch Temurin 17 via the Adoptium API (that is how the laptop's was
  installed).
- TeaVM is pinned ≥ 0.15: **0.10's JS backend silently inverts NaN comparisons**
  (see docs/phase0-findings.md). Never downgrade; differential tests are the guard.
- engine-java/build.gradle MUST keep `optimization = NONE` and `fastGlobalAnalysis = true`:
  TeaVM's default devirtualizer inlines wrong virtual-method impls (fin instances 3→1,
  masses zeroed) and its precise analyzer prunes reachable methods. Details in
  docs/phase0-findings.md "P1.2 addendum". Any change requires a full differential pass.
- Carved files are NEVER edited; targeted changes live in `engine-java/patches/`
  (documented in `engine-java/patches/LEDGER.md`) and are applied by carve.mjs.

## Two machines

Eric alternates between a desktop and a laptop. GitHub (origin) is the sync channel,
so **start every session with `git fetch` and confirm local == origin/main** before
working.

| | Desktop | Laptop |
|---|---|---|
| Repo | `E:\git\online_open_rocket` | `C:\git\online_open_rocket` |
| Windows user | `Eric` | `peltz` |
| Portable JDK 17 | `C:\Users\Eric\.online-openrocket\jdk-17.0.19+10` | `C:\Users\peltz\.online-openrocket\jdk-17.0.20+8` |
| OpenRocket reference source | `G:\Documents\Dropbox\Open_Rocket_Source_Code\openrocket-release-24.12` | `C:\Users\peltz\Dropbox\Open_Rocket_Source_Code\openrocket-release-24.12` |

On both machines the repo lives deliberately OUTSIDE any synced folder. It was moved
out of Dropbox on 2026-07-02 because Dropbox's file-provider driver intermittently
denied git's object writes (`Permission denied` on `.git/objects/...`, sometimes
sticking to specific object paths). Do not move it back under a synced folder.
Dropbox is fine for read-only reference material: the OpenRocket source above, and
the bulk third-party reference files (RASAero PDFs, RockSim export samples) that were
pulled out of the repo before it went public — those live in
`online_open_rocket_reference` in Dropbox and must not be re-committed.
`engine-java/scripts/carve.mjs` and `packages/app/scripts/fetch-component-presets.mjs`
probe both machines' reference-source paths and accept an `OPENROCKET_SRC` override
(the openrocket-release-24.12 root).

## Collaboration context

Read docs/working-notes.md — it carries the project owner's working preferences,
pending decisions, and the reasoning behind key choices. Current standing state:
an issue list from user testing may be waiting in docs/testing/ — fix that before
new feature work.

## Architecture

npm-workspaces monorepo, licensed GPL-3.0-or-later (inherited from OpenRocket):

- `packages/engine` — `@online-openrocket/engine`. The simulation engine, kept strictly
  UI-free (mirrors OpenRocket's core/swing split). The app must consume the engine only
  through this package's public API so the same artifact can serve a standalone PWA and
  a WordPress embed.
- `packages/app` — `@online-openrocket/app`. Vite + React + TS front-end. `base: './'`
  in `vite.config.ts` keeps builds embeddable — don't change it to an absolute path.
- `engine-java/` — the Java kernel and its TeaVM→JS build: carved OpenRocket sources
  (copied in by `scripts/carve.mjs`, NEVER edited) plus `patches/` (the only place
  kernel changes live, ledgered in `patches/LEDGER.md`). Its committed build output is
  `packages/engine/vendor/orkengine.mjs`.
- `validation/` — the supersonic-aero validation harness: published-data anchors
  (`anchors.json`), fixtures, `score.mjs`, dated scorecards. Standing rule: **never
  widen a tolerance to make a phase pass** — tolerances come from the datasets' own
  stated accuracies (see `validation/README.md`).
- `scripts/` — repo-level build/packaging scripts (`package-dist.mjs`).
- `docs/` — the plan (`online-openrocket-plan.md`), Phase 0 findings, user guide,
  beta invite, working notes, research notes (`research/`), user-testing batches
  (`testing/`), the current session handoff (`handoff-*.md`), and `archive/` for
  superseded docs.
- `spikes/` — throwaway experiments (e.g. CheerpJ oracle). JARs there are fetched, not
  committed (gitignored).

## Engine invariants (from OpenRocket core — do not violate)

- Internal units are **pure SI** (m, kg, s, N); internal angles are **radians**.
  Degrees exist only at the `.ork` file-format and UI boundaries.
- Orientation is a **quaternion**, never Euler angles.
- Integration: **RK4** with adaptive time step; aerodynamics: **Extended Barrowman**;
  atmosphere: **ISA** (288.15 K, 101325 Pa, −6.5 K/km).
- After recovery deployment the sim switches 6DOF → 3DOF; parachute Cd defaults to 0.8.
- `.ork` files are zip-wrapped XML; OpenRocket parses them with its own `simplesax`
  (not JAXB).

## Key facts from the Java source (verified, 24.12)

- `core` targets Java 17; the physics kernel (~260 files: `aerodynamics`, `masscalc`,
  `models`, `motor`, `simulation` core, `rocketcomponent`, `unit`, `util`) has **no
  threading** and is free of heavy deps.
- Reflection-heavy deps are localized: Guice → `startup`/`formatting`/`plugin` + the
  simulation *extensions* subsystem; GraalVM JS → `scripting` only; JAXB → `file`
  (RockSim/RASAero) and `preset` only; classgraph → `plugin` only.
- License is **GPLv3-or-later** (LICENSE.TXT), with a §7 permission for bundling
  non-compilable data files. Apache-2.0 deps are compatible.
