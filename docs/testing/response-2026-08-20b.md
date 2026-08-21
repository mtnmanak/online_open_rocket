# Response 2026-08-20b — issue batch `issues-2026-08-20a.md`

Ships as **v0.046**. Every item in the batch is addressed below in the batch's own
numbering. Two items stay queued by your decision (supersonic default-ON, design
optimization). Built via staged workflows (5 + 2 + 2 + 1 implementation lanes, an
8-dimension adversarial verification pass — 22 findings, 3 confirmed blockers — and a
2-lane fix pass); test suite grew 492 → 555 app + 26 engine, including end-to-end tests
that drive the real TeaVM kernel.

---

## §1 — Beta invite reviewed and revised

`docs/beta-invite.md` is updated for the public-beta push:

- **"Designs that open from a link" is now TRUE** — the feature was built (§2.3), so the
  claim stays, and a matching bullet was added under "Getting out of your way".
- A **"Getting oriented"** paragraph points testers at the header's Guide button (quick
  start + feature reference + physics docs) before the feedback paragraph.
- The maintainer footnote no longer names the pages.dev origin; canonical subdomain only.
- Version line reads v0.046. Every factual bullet was re-verified against the app source
  by an independent verifier before ship.

## §2 — The beta-launch bundle

### §2.1 Repo public, Issues off — READY TO EXECUTE (one sitting, this session)

- `deploy-pages.yml` **deleted** — its header was a runbook instructing a future session
  to stand up a second live copy on github.io; Cloudflare deploys on push, it had no
  purpose.
- README rewritten as the public landing page: a prominent **Feedback** section routes to
  the central tracker per `docs/feedback-tracker.md` (plain `?template=` form links —
  dropdowns can't be prefilled — the label-filtered browse queue, the in-app 🐞 button,
  and the email fallback), and states this repo's Issues tab is off on purpose.
- **A copyright problem was found and fixed before the flip** (it would have shipped in
  the public repo): docs/ carried five third-party copyrighted PDFs (RASAero II Users
  Manual, two RASAero comparison reports, Rogers/Cooper 2011, the Ironbark thesis,
  ~27 MB) plus 47 MB of RockSim reference exports. Per your "Purge from history" pick:
  all moved to `online_open_rocket_reference` in Dropbox (syncs to the desktop), and git
  history rewritten with git-filter-repo so the public repo never distributed them. **The
  desktop must re-sync before its next session** — instructions in
  `docs/handoff-2026-08-20.md`.
- The flip itself (`gh repo edit --visibility public` + `--enable-issues=false` together,
  per `docs/feedback-tracker.md`'s one-sitting rule) runs at the end of this session;
  result recorded in the handoff.

### §2.2 Uncommitted docs — pushed

All five (plus this batch's work) go in the v0.046 commit. origin/main's README no
longer hands out the pages.dev origin anywhere.

### §2.3 "Open from a link" — BUILT (your "add the link")

- **Copy share link** in the Save/Export menu packs the whole design — geometry, motors,
  **launch conditions** — deflate-compressed into the URL fragment (`#d=1.…`). The
  fragment never leaves the browser (fragments aren't sent to servers): no account, no
  upload, the link *is* the file. A typical design is a ~2.5 KB URL.
- Opening such a link imports through the same path as Open…, with motor matching and
  import notes. If you have a non-default design open, it **asks before replacing**;
  decline keeps your work. Corrupt links fail soft with a note. The fragment is cleared
  after handling so reloads don't re-trigger. **Hostile links are defused**: the
  adversarial pass built a 49 KB link that inflated to 122 MB of valid XML (a 664 KB
  one reached 2.6 GB of process memory) — decode now hard-caps inflated output at 4 MB
  and refuses over-long fragments before decoding at all.
- Click-verified end-to-end in a real browser (copy → decode → open → conditions
  applied), plus 7 codec unit tests.

### §2.4 Phone-width header — FIXED (treatment: wrap; the delegated call)

You said "fix it" without picking wrap vs horizontal-scroll; I chose **wrap** — every
control stays visible, no hidden-behind-a-swipe affordance problem (the exact criticism
the band findings make of hidden scroll). Below 980 px the button row wraps; the h1 +
version badge move as one unit; **the dropdown trap was real and solved** — both menus
re-anchor to the row itself below 980 px (pure CSS containing-block swap), measured fully
on-screen at 320/375/412 px. `.workspace-tabs` wraps too (it was the next overflow source).
At ≥ 980 px the layout is pixel-identical to before (A/B measured). Correction to the
08-12 handoff: the culprit was an inline style in App.tsx (`flex-wrap: nowrap` computed),
not a `.file-btn` CSS rule.

Measured after: `scrollWidth == clientWidth` at every width 320→1400; before: 946 px at a
375 px viewport with Guide/Feedback off-screen.

## §3 — The consolidated decisions list

1. **Share link** — built (§2.3).
2. **Header treatment** — wrap, delegated call, rationale above (§2.4).
3. **Go public** — executed this session (§2.1); you send the invite.
4. **Upstream band findings blurb** — written: **`docs/chrome-ref-band-findings.md`**,
   self-contained for a session in the site repo (names files by role, carries the
   measurements, the exact CSS asks, the re-translation rules for each tool's markup,
   and — see #7 — a request that the spec bless the GitHub `_blank` exception).
5. **Clocking: warn or refuse — the explanation you asked for.** When the splitter cuts
   a part whose children include a rail button or launch lug, the segments join with a
   **round** glued spigot, which holds no rotational alignment ("clocking") — so a
   button on one segment and a lug on another are no longer guaranteed to line up after
   glue-up. Current behavior (**warn**): the export proceeds and the print-pack README
   says "draw an alignment line down the outside before you glue." The rationale in the
   code (`printPack.ts`): the buttons/lugs are *not part of the exported mesh* — they're
   separate parts fitted after assembly — so the segments are geometrically correct;
   what's lost is a reference line, and a pencil line drawn before cutting recovers it
   in ten seconds. The **refuse** branch would return `kind:'refuse'` for such parts
   (whole-part STL still offered, split withheld). One `if` in `printOffer()`.
   **My recommendation: keep the warn.** Rail buttons are near-universal on exactly the
   rockets big enough to need splitting — refusing makes the flagship feature unusable
   for its primary audience, to prevent a mistake the printed instruction already
   prevents, and mis-clocking is recoverable (sand + re-glue). Flip it later with one
   branch if beta feedback disagrees.
6. **3D 📷 auto-fit** — you click-verified it; closed.
7. **Footer MUST 6 (GitHub `_blank`)** — you delegated ("just fix it"). Ruling: **the
   code is already right and stays** — `_blank` for github.com only, because `_top`
   navigates the whole tab off a half-finished design, and MUST 6's *purpose* (escaping
   an embedding frame) is satisfied by `_blank` exactly as completely. The fix that was
   actually missing was recording the decision: it is now settled here, and the
   chrome-ref blurb asks the spec to add the exception explicitly so the other three
   tools adopt it deliberately.
8. **Supersonic default-ON** — queued per your decision (and for the record, the anchor
   gate you set isn't met anyway: ARCAS M1.49 CD still fails by +0.033 vs ±0.02).
9. **LICENSE** — decided and done: canonical GPL-3.0 full text added at repo root
   (byte-checked against gnu.org). The repo is GPL-3.0-or-later by inheritance and
   `package.json` already declared it; a public GPL repo without the license text was
   the gap. No approval needed — this records an obligation, it doesn't create one.
   CLAUDE.md now also notes that *serving* the app is GPL distribution and the in-app
   repo link is the corresponding-source offer (why §2.1's public flip matters legally).
10. **Design optimization** — queued per your decision.

## §4 — The backlog items you said "fix now"

### §4.1 `.ork` round-trip — FIXED (what was blocking it: nothing, it just needed doing)

- **Launch conditions travel both ways.** Save writes a desktop-shaped `<simulation>`
  block (every element name/unit derived from the desktop 24.12 saver/loader source —
  rod angle in degrees on disk, wind direction in radians, turbulence as intensity
  ratio, extended-ISA in kelvin/pascal; an independent verifier re-derived the format
  separately and diffed). Open applies the file's conditions — wind, rod, site
  altitude/latitude, temperature/pressure — with ISA handled as explicit "standard".
- **Multi-configuration files are no longer silently truncated**: the first
  configuration is kept (deterministically, the desktop's declaration order) and an
  import note names the kept config and how many weren't imported.
- **Mass-component identity round-trips**: altimeter / tracker / flight computer /
  battery / etc. survive open→save instead of flattening to generic mass.
- **`shapeclipped` round-trips** and is honored end-to-end (see §4.2).

### §4.2 Engine rebuild — DONE (what was blocking it: no JDK on the laptop; fetched)

Temurin 17.0.20+8 installed via the Adoptium API; carve clean (0 copied, 259 verified,
13 patched); `optimization=NONE` / `fastGlobalAnalysis=true` verified before building;
**differential test PASSED** ("differential ok: 258 lines, 148 bit-identical, 110 within
tolerance" — the harness's own pre-existing tolerance regime). Artifact +3.6 KB. The
four queued fixes, now live:

a. **The kernel's simulation warnings reach you.** 29 warning types surfaced with
   plain-language labels (no recovery device, high-speed deployment, separation order,
   large AOA…), a warnings block in the flight report, a banner when a HIGH-priority
   warning fires, a run-table column, and persistence with each saved run.
b. **Transition shape parameter honored** — a secant-ogive reducer now simulates the
   shape it draws.
c. **Transition clipping honored** — imported `shapeclipped=false` reaches the kernel
   AND both renderers (2D schematic + 3D), keeping drawn == simulated geometry (profile
   radii verified against hand-derived numbers from `Transition.java`).
d. **The full flight recording is bridged** — every series the kernel computes (~58
   beyond the friendly dozen), behind a `series: 'summary' | 'full'` engine option.
   Default flights request summary (the dozen plus the five position/roll series the
   report reads — measured +4.5% vs the old artifact, after the first cut's
   unconditional dump measured +44% and was caught by the adversarial pass); the
   flight-data CSV re-flies the shown flight with `full` (deterministic engine — the
   re-fly is byte-exact, proven in-browser). Payoffs shipped with it: **landing distance from pad + compass bearing** in the report (the e2e test
   flies 4 m/s wind → 270 m drift at bearing ≈270°, and caught a wind-convention error
   in my first derivation — the kernel's wind direction is meteorological), **max roll
   rate** (r/s, matching the desktop's unit; row hidden below integrator noise), and
   **⬇ Flight data .csv** — the per-timestep series export, with booster branches as
   their own labeled column groups.

Toolchain fixed for both machines while in there: `difftest.mjs` and `build-engine.mjs`
no longer pin one machine's JDK dir (newest `jdk-17*` under `~/.online-openrocket`, and
the phantom `setup-jdk.ps1` reference is gone).

### §4.3 User guide — CAUGHT UP, and the fiction is dead

- The "documentation workflow" the file header claimed **never existed**. Now it does:
  `scripts/build-user-guide.mjs` generates `userGuide.ts` from `docs/user-guide.md`
  (single source of truth) as part of the app build — drift is now impossible. The
  generator is dependency-free, deterministic (byte-identical re-runs), and refuses
  loudly on markdown it doesn't handle rather than emitting wrong HTML.
- Content updated: v0.045's part splitting (derived from the code, not release notes),
  snapshot auto-fit, the .stl row, plus this release's share links, launch-conditions
  round-trip, warnings block, landing drift, roll rate, and flight-data CSV.
- Found while converging the two copies: the drift was **bidirectional** — the hand-kept
  ts had a Daylight-mode passage and a modeling tip the markdown lacked; both ported
  before making md canonical, so nothing was lost.

### §4.4 Silent quota failures — FIXED

- `simStore.persist` no longer returns the array that *failed* to store — the table
  shows stored truth, and a dismissible note above Saved simulations says run history
  isn't being saved (storage full) with the CSV-export suggestion.
- The session autosave exposes failure on the transition edge (not 2.5×/s): a
  persistent header banner — "Autosave can't write… save your design to a file" — that
  clears itself on recovery. Both paths unit-tested with a throwing localStorage,
  browser-verified in both themes.
- Bonus correctness: `clearRuns` now uses `removeItem` (freeing space can't fail on
  quota), so clearing your history works even on a full origin.

## §5 — Housekeeping

1. **`npm install`** — done; the workspace links the drive move broke are proper
   symlinks again (the hand-made junction is gone).
2. **CLAUDE.md standardized on two machines** — a "Two machines" table (repo, user,
   JDK, reference source per machine), the never-under-a-synced-folder rule kept with
   its history, and every stray single-machine path scrubbed. `carve.mjs` and
   `fetch-component-presets.mjs` now probe both machines' reference paths and accept
   `OPENROCKET_SRC`. **Repo housekeeping**: stale docs (5 old handoffs, deployment.md
   after folding its one unique fact into CLAUDE.md, the adopted feedback-repo-kit)
   moved to `docs/archive/` — **tracked, not gitignored**: gitignoring would break the
   two-machine git sync, and the public repo is tidier with an archive than with a
   hole. The bulk third-party files left the repo entirely (§2.1).
3. **Architecture list** — engine-java/, validation/ (carrying the never-widen-a-
   tolerance rule), scripts/ added; docs/ described honestly.
4. **deploy-pages.yml** — deleted (§2.1).

## Waiting on you

1. **Send the invite** (`docs/beta-invite.md`) — everything it claims is now true and
   live.
2. **Desktop re-sync** after the history rewrite — see `docs/handoff-2026-08-20.md`
   (two commands; your local work is all pushed, nothing to lose).
3. The **chrome-ref blurb** (`docs/chrome-ref-band-findings.md`) whenever you next open
   a session in the site repo.
4. Queued, untouched: supersonic default-ON (anchor gate not met), design optimization
   (discuss first).

## Verification

**Adversarial pass**: 8 read-only verifier dimensions over the full uncommitted change
set (share-link hostile inputs, independent .ork format re-derivation, engine perf
old-vs-new artifact, header/CSS sweep across themes, quota edges, docs/copy audit,
history-purge completeness, flight-data correctness, ship-gate build). **22 findings, 3
confirmed blockers — all fixed before ship**:

1. **Deflate-bomb DoS in share links** (measured: 49 KB link → 122 MB inflation) →
   4 MB inflate cap + 1 MB fragment pre-check, bomb regression test added.
2. **+44% single-sim regression** from unconditionally serializing 58 extra series →
   the summary/full option above; re-measured at **+4.5%** default vs the old artifact
   (median 197.7 → 206.6 ms; full mode 284 ms, opt-in). Same rebuild also excluded the
   nondeterministic `tc` wall-clock series (same-seed runs are byte-identical again)
   and hardened the wire format against `Infinity` samples.
3. **The history purge would have missed all 19 RockSim blobs** (~46 MB) at their
   pre-v0.043 flat paths (`docs/2D_3D_Models/Rocksim_*` before the subfolder
   reorganization) → purge invocation extended with the path-glob; the verifier
   re-ran the corrected purge on a scratch clone and confirmed zero surviving PDF or
   RockSim objects. The five PDFs only ever lived at their current paths.

Confirmed minors, all fixed: multilevel-wind files now note that only the average wind
was imported; the multi-config note no longer claims a config was "kept" from a
motorless file; the runs-quota banner clears itself on recovery and gets honest copy
when nothing is stored; every CSV export carries a UTF-8 BOM (Excel garbled the Greek
series headers without it); a long rocket name no longer forces sideways scroll at
phone widths (`min-width: 0` on the design-layout items); the header's Open… control is
keyboard-reachable; the invite's nose-cone example got its 4:1 qualifier back;
deploy.yml now **fails the deploy if version.json ≠ APP_VERSION** (the drift class the
site's check-tools caught externally is now caught in CI).

Accepted/noted: a PWA installed from the pages.dev origin will bounce to the subdomain
on next launch (intended — reinstall from the canonical address); `docs/materials/`
RockSim-derived component-dimension CSVs stay in the repo (factual catalog data, same
practice as OpenRocket's own component database; flagged for awareness, not a defect).

**Final counts**: 555 app + 26 engine tests, 0 failed/skipped (was 492 + 23 at
v0.045). Differential test passed on both rebuilds this session ("differential ok: 258
lines, 148 bit-identical, 110 within tolerance" — the harness's pre-existing tolerance
regime). Production build clean: 21 precache entries (4.28 MB), canonical-host
redirect and regenerated guide confirmed in dist, `package-dist` version gate green at
0.046. Two engine rebuilds ran carve-clean (0 copied, 259 verified, 13 patched — no
carved file touched). Deploy + live verification and the public flip are recorded in
`docs/handoff-2026-08-20.md`.
