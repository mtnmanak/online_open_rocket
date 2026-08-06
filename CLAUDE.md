# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A browser-based re-creation of **OpenRocket** (the Java/Swing model-rocketry design and
flight-simulation desktop app). The full technical plan, strategy comparison, and phased
roadmap live in `docs/online-openrocket-plan.md` — read it before making architectural
decisions.

Reference Java source (OpenRocket release 24.12, GPLv3+) is available locally at
`G:\Documents\Dropbox\Open_Rocket_Source_Code\openrocket-release-24.12`. Its
`core/` module (`info.openrocket.core`) is the physics/model reference; its `swing/`
module is being replaced by the web UI and should not be ported.

## Deploying

This tool is **its own Cloudflare Pages project**. It is not copied into the
mountainmanrockets.com site any more — publishing this repo publishes the tool, and
the site is not rebuilt or touched.

| | |
|---|---|
| Pages project | `online-open-rocket` |
| Live URL | https://online-open-rocket.pages.dev |
| After DNS cutover | https://openrocket.mountainmanrockets.com |
| Old WordPress path | `/online_open_rocket/` — 301s to the above, do not reuse |

**`git push` to `main` is the deploy.** `.github/workflows/deploy.yml` runs
`npm ci && npm run build` (tsc + vite only — the TeaVM engine kernel is the committed
artifact `packages/engine/vendor/orkengine.mjs`, so CI needs no Java), copies
`version.json` into dist the way `scripts/package-dist.mjs` does, and publishes
`packages/app/dist` with wrangler. The workflow needs the `CLOUDFLARE_API_TOKEN`
repo secret (account API token with "Cloudflare Pages — Edit").

Manual override from this machine:

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
  `C:\Users\Eric\.online-openrocket\jdk-17.0.19+10` — set `JAVA_HOME` to it for Gradle
  (difftest.mjs finds it automatically). Re-fetch via the Adoptium API if missing.
- TeaVM is pinned ≥ 0.15: **0.10's JS backend silently inverts NaN comparisons**
  (see docs/phase0-findings.md). Never downgrade; differential tests are the guard.
- engine-java/build.gradle MUST keep `optimization = NONE` and `fastGlobalAnalysis = true`:
  TeaVM's default devirtualizer inlines wrong virtual-method impls (fin instances 3→1,
  masses zeroed) and its precise analyzer prunes reachable methods. Details in
  docs/phase0-findings.md "P1.2 addendum". Any change requires a full differential pass.
- Carved files are NEVER edited; targeted changes live in `engine-java/patches/`
  (documented in `engine-java/patches/LEDGER.md`) and are applied by carve.mjs.

## Repo location

The repo lives at `G:\git\online_open_rocket` — deliberately OUTSIDE Dropbox. It was moved
out on 2026-07-02 because Dropbox's file-provider driver intermittently denied git's
object writes (`Permission denied` on `.git/objects/...`, sometimes sticking to specific
object paths). Do not move it back under a synced folder. GitHub (origin) is the sync
channel. The reference OpenRocket source still lives in Dropbox (read-only use) —
`engine-java/scripts/carve.mjs` points there.

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
- `docs/` — plan and Phase 0 findings.
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
