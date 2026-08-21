# Response 2026-08-20a — project status survey & proposed next batch

**This one is inverted from the usual pairing.** No issues file prompted it — it is a full
status survey (8-agent workflow + hand verification, 2026-08-20) written so you can pick
items from it into a new `issues-2026-08-XXa.md`. Sections 2–4 are written to be lifted
straight into an issues file; §3 is the consolidated everything-waiting-on-you list.

---

## 1. Where the project stands

- **v0.045 is live and verified** at https://openrocket.mountainmanrockets.com
  (`version.json` on the live site matches local, byte-checked). Local `main` ==
  `origin/main` at `3059490`, 0 ahead / 0 behind. All five recent CI deploys succeeded.
- **515 tests green on this machine** (492 app + 23 engine), re-run this session.
- **The written roadmap is essentially finished.** Phases 0–2 complete; Phase 3 complete
  except automatic design optimization and a selectable geodetic model. Since ~v0.031 the
  work has been beta-feedback iteration, not roadmap execution.
- **The testing queue is empty for the first time.** All 14 dated issue batches in
  `docs/testing/` have matching responses; the newest (2026-08-12b) shipped as HEAD.
  CLAUDE.md's standing rule ("fix the waiting issue list before new feature work") is
  satisfied — nothing is waiting.
- **The 8-day commit gap is the real signal.** The project's only prioritization input is
  your test batches, and the loop is dark: the central tracker
  (`mtnmanak/mountainmanrockets-feedback`) has exactly one issue ever, closed 2026-08-11.
  **The beta invite (`docs/beta-invite.md`) is finished copy that was never sent.**

---

## 2. Recommended next batch: the beta-launch bundle

Ship the invite. Three things outside the running app block it, plus one in-app fix that
belongs in the same batch because the invite aims testers straight at it.

### 2.1 The source repo is still PRIVATE, with Issues enabled

Verified this session: `gh repo view mtnmanak/online_open_rocket` →
`{"hasIssuesEnabled": true, "visibility": "PRIVATE"}`.

- The header's "source code (GPL)" link (`packages/app/src/App.tsx:898`) **404s for every
  tester** — a GPL §6 source-offer problem, not just a broken link. The user guide
  (`packages/app/src/data/userGuide.ts:81`) tells testers that link "is the front door."
- **The visibility flip is a one-sitting operation.** Issues filed on this repo can never
  be transferred to the central tracker, so
  `gh repo edit --visibility public --accept-visibility-change-consequences` and
  `gh repo edit --enable-issues=false` must run together
  (procedure: `docs/feedback-tracker.md:143-160`).
- **Defuse `deploy-pages.yml` in the same pass.** Its header
  (`.github/workflows/deploy-pages.yml:3-9`) is a runbook whose step 1 is "make the
  repository public" and step 3 stands up a **second live copy at a github.io address** —
  contradicting the canonical-URL rule, with no `cp version.json` step so the site's drift
  check would see nothing there. Going public hands a future session that instruction.

### 2.2 The five uncommitted docs need to push

`origin/main`'s README still says "Live app: https://online-open-rocket.pages.dev" — the
address CLAUDE.md says never to hand out. The 2026-08-12 handoff listed three uncommitted
files; the tree carries five (`M CLAUDE.md`, `M README.md`, `M docs/working-notes.md`,
`?? docs/beta-invite.md`, `?? docs/handoff-2026-08-12.md`). The two extras are the handoff
itself and the working-notes pointer to it — benign, but the push must land **before** the
repo goes public.

### 2.3 The invite's first claim is false — cut it or build it *(your call — §3.1)*

`docs/beta-invite.md:20` leads with "designs that open from a link." **No implementation
exists**: no share button, no URL/hash design loading anywhere in `packages/app/src` — the
only `searchParams` use in App.tsx is the feedback prefill (verified by grep this session).
It is the first claim a curious tester will try. Either cut the phrase or promote it to a
feature in this batch (a hash-encoded or fetch-from-URL `.ork` loader is a plausible S–M
item, but it is your copy and your scope call).

### 2.4 Phone-width header overflow — fix before testers arrive on phones *(treatment: §3.2)*

Measured at a real 375px viewport: `documentElement.scrollWidth` = **946px**. The invite
asks testers to "use it once on your phone at a launch" and points them at the 🐞 Feedback
button — whose right edge lands at x≈744px, with Guide at x≈645px. **The two buttons the
invite names are exactly the ones off-screen on a phone.**

Correcting the 08-12 handoff's diagnosis: it is **not** a `.file-btn` rule and not in
`styles.css` at all — it is the inline `style={{display:'flex',alignItems:'baseline',gap:8}}`
at `packages/app/src/App.tsx:755`, which computes to the initial `flex-wrap: nowrap`.
Injecting `flex-wrap: wrap` at runtime took the page 946 → 360px. But it is not a
one-liner:

- `.file-menu` is `position:absolute; right:0; min-width:240px`
  (`styles.css:222-235`) — once the header wraps, the Feedback dropdown renders at
  `left:-55px` on a 412px viewport. The dropdowns must be re-anchored in the same change.
- `.workspace-tabs` (`styles.css:141-166`) is a second unwrapped flex row that becomes the
  #1 overflow source at 320px the moment the header is fixed.
- The same inline style is repeated verbatim at `App.tsx:1116` and `:1203` — factoring it
  into a shared class changes two other rows too.
- The stylesheet has no app-shell breakpoint below 900px; expect one new narrow-width
  media block. Provably unchanged above ~980px.

Size: S–M, 2 files, ~5 rules.

**Why the bundle beats everything in §4:** items there are real defects, but they are
improvements to a tool with one user. Sending the invite turns the feedback signal back
on, and testers will rank §4 better than any survey can — whether the `.ork` data loss
bites before anyone misses a landing-drift number is worth learning from data.

---

## 3. Waiting on you — the consolidated list

Everything currently gated on a decision of yours, in one place (several carried over from
response-2026-08-12b so your next issues file can adjudicate them all at once):

1. **"Designs that open from a link"** — cut the phrase from the invite, or scope it as a
   feature in the beta batch (§2.3).
2. **Header overflow treatment** — wrap to multiple rows (with dropdowns re-anchored), or
   the site band's own pattern (`overflow-x:auto` + `min-width:0`, horizontal scroll,
   `styles.css:392-418`), which sidesteps the dropdown problem entirely (§2.4).
3. **Go public** — your call and your sitting; Issues off in the same breath (§2.1).
4. **Three nav-band findings that belong UPSTREAM in the site repo's `chrome.ref.js`**
   (carried from response-2026-08-12b): focus-ring gutter vertical-only; scrollbar hidden
   at 768–924px with no other overflow affordance; `gap:.6em` resolving per-tool. Do not
   fix here — they need a site-repo decision.
5. **Clocking: warn or refuse?** A component with a rail button or launch lug currently
   *warns* that a round joint holds no clocking. One branch to flip (carried from
   response-2026-08-12b).
6. **One click on the 3D 📷 with "Fit rocket to frame" on** — still not click-verified
   (R3F canvas never initialises under the CDP browser; 25 unit tests cover the maths).
7. **Footer GitHub-link exception vs chrome-spec MUST 6** — the footer's github.com links
   use `target="_blank"` per the feedback-tracker ruling, a scoped letter-violation of
   MUST 6 (`_top`). Confirm the exception or it gets reverted to `_top`
   (response-2026-08-12a §"GitHub-link exception").
8. **Supersonic aero default-ON flip** — the locked decision was default-OFF during beta,
   flip once the full anchor suite passes. The prerequisites you named (per-run model
   recording, one-click Classic mode, guide framing) all exist; the anchor suite does
   **not** fully pass (ARCAS M1.49 CD fails at +0.033 vs ±0.02 —
   `validation/scorecard-phase4-2026-08-04.md`). Presumably stays OFF; flagged so it isn't
   lost (working-notes.md:663).
9. **LICENSE file** — the repo has none. Tracker adjudication §9.5 leaves it as your open
   item; standing instruction is raise-don't-add. It becomes material the moment the repo
   is public and the app is distributed (GPL-3.0-or-later by inheritance). Raising it: a
   public GPL repo without a LICENSE file is the kind of thing a tester files an issue
   about.
10. **Design optimization** (roadmap's last headline) — your standing note says discuss
    first. Recommendation when you want it: an in-browser loop over dimension sweeps
    reusing the batch-sim machinery, not the plan's server-side idea. Better scoped after
    beta feedback exists. The geodetic-model selector is genuinely small
    (`OrkEngine.java:752` hardcodes SPHERICAL) and could ride along with any engine
    rebuild (§4.2).

---

## 4. Ranked backlog behind the bundle

None of these block the beta. Ordered by likely user impact once testers exist.

### 4.1 `.ork` round-trip data loss (M — no engine rebuild, no decision needed)

The sharpest **undocumented** defect found — it appears in no working-note and no response
doc. `packages/app/src/services/orkFile.ts` (1212 lines):

- **Never reads the `<simulations>` block and writes it as two empty tags**
  (`orkFile.ts:1096-1097`). Opening a desktop design discards wind speed/turbulence,
  launch rod length/angle, site altitude/latitude/longitude, temperature, pressure, and
  geodetic model — and a design saved here opens in desktop OpenRocket with defaults.
  Launch conditions live only in localStorage (`services/session.ts`), never in the file
  the user saves and shares.
- **Multi-config files silently keep whichever motor comes first** — the importer takes
  the first `<motor>` per mount and ignores `configid` entirely (`orkFile.ts:91`, single
  minted configid at `:500`/`:1053`). Unlike unknown components this produces **no import
  note**, though the `notes[]`/`ignored[]` mechanism sits unused at `:467-468`. A desktop
  file carrying an H128 setup and an I300 setup loses one without a word.
- Minor: every mass object exports as generic `masscomponent` (`orkFile.ts:1004`), so
  altimeters and trackers round-trip as indistinguishable dead weight.

Cheap 80%: read/write launch conditions + emit a "this file had N flight configurations,
kept the first" import note. Full multi-config support is a separate, larger schema change
worth quoting on its own. Every beta tester's first move is opening a real `.ork`; this is
the defect class most likely to come back as confusing reports that cost a session each to
diagnose.

### 4.2 One engine rebuild, four queued kernel fixes (L — needs a JDK on this machine)

Four small changes stuck behind the same expensive ritual (v0.042–v0.045 all shipped with
no engine rebuild). The Java bridge and the committed `orkengine.mjs` are currently in
sync (both last changed at `196f5c7`) — the cleanest possible moment.

a. **The kernel's simulation WarningSet is computed and discarded.** Every flight
   populates NO_RECOVERY_DEVICE, SEPARATION_ORDER, EARLY_SEPARATION, RECOVERY_LAUNCH_ROD,
   HighSpeedDeployment, LargeAOA + two more, and `flightDataToJson`
   (`engine-java/src/api/java/api/OrkEngine.java:831-864`) never calls
   `getWarningSet()`. The static path DOES surface warnings, which hides this. Breaks an
   explicit MUST in `docs/pods-implementation-plan.md:220`.
b. **Transition shape parameter ignored** — ComponentFactory's nosecone case calls
   `setShapeParameter` (`ComponentFactory.java:68-70`); the transition case never does, so
   a secant-ogive reducer draws correctly and simulates wrong.
c. **Transition clipping ignored** — same case never calls `setClipped`; imported
   `shapeclipped=false` is dropped. (b) and (c) queued since v0.041.
d. **Only 12 of 59 FlightDataType series are bridged**
   (`OrkEngine.java:885-899`) — no TYPE_POSITION_XY (the app cannot say where the rocket
   lands or how far the walk is, the direct payoff of the wind model already exposed) and
   no TYPE_ROLL_RATE despite fin cant being editable. Bridging the series makes a
   per-timestep CSV export nearly free.

Ritual: re-fetch Temurin 17 (Adoptium API), carve, `gradlew generateJavaScript`,
build-engine, then BOTH difftest and engine vitest (difftest cannot catch a bug identical
in JVM and JS). Plus app-side UI for the warnings and a drift readout — worth showing you
the presentation before building.

### 4.3 User guide is a version behind, and the "documentation workflow" is fiction (S)

- The invite headlines v0.045's part splitting; the guide explains it nowhere — zero
  occurrences of "build volume", "split", "spigot", "segments" in either mirror. Both
  `docs/user-guide.md` and `packages/app/src/data/userGuide.ts` were last touched in
  `6e34f3a` (v0.044). `GuideDialog.tsx:20` stamps "User Guide v0.045" over v0.044 content.
- `userGuide.ts:3-8` claims "Generated by the documentation workflow… re-run the
  user-guide workflow." **No such workflow or script exists** (`.github/workflows/` holds
  only deploy.yml and deploy-pages.yml). The app mirror is a hand transcription that
  drifts silently every release — either build the small generator or delete the claim and
  put the guide on the release checklist.
- Nothing ever nudges a user to the guide (`showGuide` starts false, no first-run or
  version-change trigger — `App.tsx:216,825`); one sentence in the invite pointing at the
  Guide button is worth adding regardless.

### 4.4 Two silent-failure defects on the localStorage quota path (S)

- `simStore.persist` (`packages/app/src/services/simStore.ts:33-42`) computes `kept`,
  tries `setItem`, swallows a quota throw with a bare catch, then returns `kept` — the
  exact thing that did NOT get stored — and every caller (`addRun`, `addRuns`,
  `deleteRun`, `clearRuns`, `:45-59`) feeds it into the visible Saved-simulations table.
  Failure mode: a flight-day user with a big design and MAX_RUNS=500 of history watches
  runs appear in the table, reloads, and they are gone with no error ever shown.
- The debounced session autosave (`services/session.ts:57-64`) has the same bare catch on
  the same origin quota budget, so the two failures arrive together — while the app's
  stated contract is "your work saves itself."

One small pass: return `loadRuns()` on the catch path, surface a signal for both, plus
regression tests. ~An hour.

---

## 5. Housekeeping (no decision needed — next session should just do these)

1. **`npm install` at the repo root.** The workspace symlinks did not survive the drive
   move; `node_modules/@online-openrocket/engine` was hand-repaired with a directory
   junction (not durable) and the app link may still be empty. Tests currently pass.
2. **Fix CLAUDE.md's machine paths** (folded into the already-dirty file): repo is at
   `C:\git\online_open_rocket` on this laptop (CLAUDE.md says `E:\git` — the desktop);
   the JDK path names `C:\Users\Eric` (this profile is `peltz`; **no JDK exists here** —
   java not on PATH, JAVA_HOME empty, no `~/.online-openrocket`); the OpenRocket reference
   is at `C:\Users\peltz\Dropbox\Open_Rocket_Source_Code\openrocket-release-24.12`, not
   `G:\Documents\Dropbox\...`. Two scripts hardcode the dead `G:` default and abort here:
   `engine-java/scripts/carve.mjs:24` and
   `packages/app/scripts/fetch-component-presets.mjs:36`. CLAUDE.md needs to describe
   both machines.
3. **Add `engine-java/`, `validation/`, `scripts/` to CLAUDE.md's Architecture list.**
   `validation/` appears nowhere in CLAUDE.md despite carrying the standing "never widen a
   tolerance to make a phase pass" rule. The uncommitted README already gets this right —
   bring CLAUDE.md up to it.
4. **Neutralize the `deploy-pages.yml` runbook header** (§2.1) before the repo goes
   public.

---

## 6. Verification notes

- Survey: 8 agents (6 parallel readers — roadmap, working-notes backlog, testing-docs
  cross-check, repo health, header-overflow verification, beta readiness — plus a
  completeness critic and a synthesis pass), 112 evidence items, followed by hand
  spot-checks of the load-bearing claims (repo visibility, open-from-link grep, live
  version.json, tracker issue count, origin/main README).
- Testing-docs cross-check confirms every issues batch 2026-07-03 → 2026-08-12b has a
  matching response; no unanswered batch exists.
- 515 tests re-run green this session on this machine (492 app + 23 engine). An earlier
  failure on this laptop was the broken workspace junction from the drive move, since
  hand-repaired (§5.1).
- Header overflow measured live at 375px/412px viewports (946px scrollWidth; runtime
  `flex-wrap:wrap` A/B → 360px; `.file-menu` mis-anchor reproduced at 412px).
- The R3F/CDP wedge is unchanged: 3D features still cannot be click-verified from a
  session here (§3.6).
