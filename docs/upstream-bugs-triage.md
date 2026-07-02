# Upstream OpenRocket bug triage (32 open `bug` issues, 2026-07-02)

Source: https://github.com/openrocket/openrocket/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug

Relevance filter: we reuse `info.openrocket.core`'s physics kernel (TeaVM track) and
**rewrite the entire UI** — so each upstream bug lands in one of three buckets.

## A. Inherited via kernel reuse — fixable by us (≈13)

These live in code we transpile. A byte-identical carve reproduces them bug-for-bug.

| # | Bug | Kernel area | Notes |
|---|---|---|---|
| [#2892](https://github.com/openrocket/openrocket/issues/2892) | Ground hit velocity wrong | `BasicLandingStepper` / final-step handling | Popular (7 comments). Likely last-step interpolation. |
| [#2595](https://github.com/openrocket/openrocket/issues/2595) | Booster-only sims: impossible warnings in wrong sims | `simulation` events/warnings | Upstream *release blocker*, 11 comments. |
| [#2489](https://github.com/openrocket/openrocket/issues/2489) | Less weathercocking than RockSim/RASAero | aero model | Research-grade; needs benchmark data. |
| [#2427](https://github.com/openrocket/openrocket/issues/2427) | Incorrect stability for booster stage | Barrowman + staging | |
| [#2437](https://github.com/openrocket/openrocket/issues/2437) | Disabling sustainer on single-stage: no effect | config/sim | |
| [#2092](https://github.com/openrocket/openrocket/issues/2092) | Strange deployment with motors in pods | event logic | |
| [#2031](https://github.com/openrocket/openrocket/issues/2031) | Automatic streamer Cd "nuts" | drag model | |
| [#1060](https://github.com/openrocket/openrocket/issues/1060) | Side-force coefficient plot constant zero | `aerodynamics/barrowman` | **Confirmed in source:** every component calc does `setCside(0)`; value is never computed. Unimplemented, not miscomputed. |
| [#870](https://github.com/openrocket/openrocket/issues/870) | Centering ring ignores offset/clustered inner tubes | component geometry | |
| [#2454](https://github.com/openrocket/openrocket/issues/2454) | Some default wall thicknesses are 0 | component defaults (data) | Upstream "good first issue" — pure data fix, safe anytime. |
| [#3156](https://github.com/openrocket/openrocket/issues/3156) | Deleting all motor configs → IndexOutOfBounds | document model | |
| [#3037](https://github.com/openrocket/openrocket/issues/3037) | Opening file causes exception | file loader | Inherited only if in the `.ork` path we reuse. |
| [#2470](https://github.com/openrocket/openrocket/issues/2470) | RASAero import includes motor mass in airframe | RASAero importer | Only relevant if/when we support RASAero import (Phase 3). |

## B. Disappear by construction — Swing/JOGL/desktop/installers (≈15)

Not carried over because we rewrite the UI/3D/packaging: #3175 (license text/fonts in
desktop bundle), #3035 (Snapcraft), #2622/#2446/#2433/#2306/#2296/#2160/#1336/#1080/#2273
(Photo Studio & JOGL 3D), #2542 (Swing fin editor rendering), #2043 (macOS installer),
#1359 (Swing numeric fields accept pasted line breaks), #41 (fin template label overlap).

Lesson-not-bug: our web UI must still get the *equivalents* right (input sanitization,
3D clipping, fin-template export layout) — tracked as UI acceptance criteria, not patches.

## C. Must-NOT-recreate — behavioral traps for the new UI (≈4)

Fresh code, so these are free fixes if we simply don't repeat the design:
- [#3148](https://github.com/openrocket/openrocket/issues/3148) — default launch conditions
  not applied to new sims ("Reset to default" broken).
- [#3127](https://github.com/openrocket/openrocket/issues/3127) — plot configuration is
  global instead of per-simulation.
- [#2374](https://github.com/openrocket/openrocket/issues/2374) — parts-library text filter
  matches invisible fields.
- [#2475](https://github.com/openrocket/openrocket/issues/2475) — warnings shown in wrong
  units after load (unit conversion belongs at the UI boundary — our invariants already
  mandate SI internally).

## Strategy: when and how we fix Category A

1. **During carve/validation (Phase 1): fix nothing in the kernel.** Differential tests
   (JVM ↔ TeaVM-JS bit-parity) are only meaningful against byte-identical sources. Bugs
   and all.
2. **After parity is established: quirks ledger.** Each confirmed engine bug gets an entry
   here + a failing test + a patch maintained as a small, documented diff against upstream.
3. **Compatibility toggle.** Ship patches behind an engine flag:
   `compatMode: 'openrocket-24.12' | 'corrected'` — users can reproduce desktop results
   exactly OR get corrected physics. (Precedent: bug-for-bug compatibility modes in
   reimplementations.)
4. **Upstream every fix.** Both projects are GPLv3 — we PR fixes back to
   openrocket/openrocket. Shrinks our fork delta and benefits the community. Category A
   fixes are valuable upstream contributions precisely because the desktop app shares the
   same core.
5. **Category C is enforced now**: the items above become Phase 1/2 UI acceptance criteria.

## Priority order for actual fixes (post-parity)

1. **#2454** — data-only, trivially safe (upstream "good first issue").
2. **#2892** — high user impact, likely localized in the landing stepper.
3. **#1060** — implement side-force computation (well-specified in Barrowman literature).
4. **#3156** — crash-class bug, likely a simple guard.
5. **#2595 / #2427 / #2437 / #2092** — staging/event logic cluster; tackle together when
   Phase 3 staging work touches that code anyway.
6. **#2031 / #2489 / #870** — need physics research/benchmark data; schedule with care.
