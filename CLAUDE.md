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
