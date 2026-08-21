# Response 2026-08-21a — RocketForge lessons (Part 1) + closing out your annotated survey (Part 2)

Two batches arrived under one filename today: your **annotated survey** (answers written
onto response-2026-08-20a's items) and, replacing it on disk mid-session, your
**RocketForge note**. Both are answered here — the RocketForge note first, since it's the
current ask. (The annotated survey was never committed and the file now holds the
RocketForge note, so Part 2 restates each of its items before answering — nothing is
lost.)

---

# Part 1 — the RocketForge note

Noted and agreed on the framing: friendly space, no competition, good ideas recognized
wherever they appear — and it's good to hear the OpenRocket folks are in the loop. I
looked at all six screenshots closely. You read their positioning right, and the
screenshots also quietly confirm your "depth" framing: their demo rocket is four
components and their properties panel is ~six fields where ours carries the full
OpenRocket component model. The two tools really don't occupy the same square.

## Done now, per your instruction

- **The invite now says it plainly** — a new first bullet under "Getting out of your
  way": *"Free, open source, and no account — ever. Nothing to sign up for, nothing to
  log in to: your designs live in your browser and in the `.ork` files you save, not on
  someone's server. The whole app is free software (GPL, full source public), it costs
  nothing, and it will stay that way."* Deliberately placed first: after your RocketForge
  session, it's also the sharpest differentiator on the list. (Your two other invite
  copy calls from the rename — the non-affiliation half-clause, and whether the
  migration note stays in the public post — are still open and still yours.)
- **Screenshots housekeeping**: the six PNGs sit untracked in `docs/RocketForge
  Screenshots/`. Recommendation: move them to the Dropbox reference folder
  (`online_open_rocket_reference`) rather than committing — same rule as the RASAero
  PDFs, and screenshots of someone else's product don't belong in our public GPL repo.
  Everything design-relevant from them is captured in this document. Say the word if
  you'd rather they be committed.

## What their design does well, and what I suggest we take — five proposals

I'm treating this as the brainstorm you asked for; each proposal ends with where I'd
want your input before building. None of this is implemented — your call on which to
green-light, and mockups come first for anything visual (I can put the contenders
side-by-side on a design canvas next session).

### S1. Make the rocket the hero of the screen (desktop)

Their single strongest move: the rocket fills the canvas and the canvas fills the
window. Ours is a smaller horizontal strip with the stat cards consuming the space below
it. But note what their vertical layout costs them on a desktop: a wide window is mostly
*empty margin* either side of a tall thin rocket. Our horizontal orientation is actually
the right one for wide screens — rockets are long and thin, and horizontal uses the width
they waste. So: don't copy the orientation; copy the *dominance*.

- Let the 2D canvas flex to fill the available height; today's fixed-ish strip becomes
  the whole center column.
- Collapse the stat-card rows into **a compact floating stats chip** overlaid on canvas
  whitespace (they do this well: length / mass / CG / CP in a small dark card, top-left)
  with the full card grid one click away in a drawer. Our canvas is mostly empty sky —
  the numbers can live in it instead of below it.
- Add a **90° rotate toggle** on the 2D view (this is also the phone story — S4).

**Your input:** chip contents (my pick: length, loaded mass, CG, CP, stability cal —
the five you check constantly), and whether the drawer defaults open or closed.

### S2. CG/CP leader-line callouts on the drawing

They draw dashed leader lines from the CG/CP markers to labeled dots with the margin
("0.7 cal — Unstable") floating beside the rocket — legible from across the room. We
have the same markers but small, and the margin lives in a card below. Adopting the
callout style is cheap (our 2D view is SVG already), keeps our schematic aesthetic, and
makes the *one number every flyer checks* impossible to miss. Color-code the margin
(green ok / amber marginal / red unstable) exactly as our stability card already does.

### S3. First-run tour (this also closes the "guide nudge" item from Part 2)

Their 7-step anchored-tooltip tour with Skip/"Next (1 of 7)" is the elegant version of
the first-run nudge we've had queued. Proposal: 6 stops — component tree → 2D canvas →
motors & launch tab → LAUNCH button → results/saved-runs → Guide + Feedback buttons.
Shown once (localStorage flag, so it never nags), replayable from the Guide menu the way
theirs has a "Tour" button. Small in-house component — anchored tooltips against
existing refs, no library. This is the highest value-per-effort item of the five for the
public beta: first-time visitors from the forum post get oriented in 30 seconds.

**Your input:** the six stops above, or a different set? Tone of the copy (I'd keep it
terse — one sentence per stop)?

### S4. Phone: vertical rocket, sheet-based panels (staged)

Your "if we do nothing else" item. Agreed, and their mobile insight is real: don't
compress the desktop layout — change the layout. Portrait screens and upright rockets
are made for each other. Staged plan:

1. **Stage 1 (small):** the 2D view gets a rotate-to-vertical mode, defaulting vertical
   below a phone breakpoint, horizontal on desktop (with the manual toggle from S1).
   Labels/dimensions counter-rotate to stay readable.
2. **Stage 2 (the real redesign, M–L):** below the breakpoint, the canvas goes
   full-screen with a bottom bar — Parts / Properties / Motors / Fly — each opening a
   bottom sheet, header reduced to the wordmark + version. This is their pop-menu
   pattern, and it's the right shape for us too.

Stage 1 is worth doing before the forum post if you want one mobile win now; Stage 2
deserves beta feedback first (we'll learn which panels phone users actually reach for).

### S5. Visual richness of parts and internals — selectively

Theirs is prettier; ours is more technical. I'd protect our blueprint/schematic identity
(it matches the "serious sim" positioning you chose in the rename) and take three
targeted upgrades: tint + label the loaded motor inside the mount on the 2D view (their
orange "C10" block reads instantly), a hover/selection highlight with a subtle fill on
the hovered component, and a materials/lighting pass on the 3D view we already ship
(soft studio lighting, slight body translucency so internals read — cheapest place to
gain "wow" since the geometry is already there).

### What I'd deliberately NOT copy

The account/login model (their worst UX moment by your own experience — our
no-account/localStorage/share-link/.ork story is strictly better and the invite now says
so), the modal-only sim results (ours as a full tab with saved-runs history and CSV is
the power-user shape), and the vertical-on-desktop orientation (see S1).

## If you want, next session

Happy to take you up on the logged-in walkthrough of their tool together, and/or build
the S1/S2/S3 mockups on a design canvas so you can pick visually. Ordering suggestion if
you green-light everything: S3 (tour) and S2 (callouts) before the forum post, S1 with
the chip right behind them, S4 stage 1 opportunistically, S4 stage 2 + S5 after beta
feedback.

---

# Part 2 — closing out the annotated survey

Your annotations were written against `response-2026-08-20a.md`, which surveyed the repo
**the morning before v0.046 shipped** — and v0.046 (that same evening) plus the v0.047
rename closed most of what you flagged. A doc you may not have seen,
`docs/testing/response-2026-08-20b.md`, had already adjudicated several of the §3
decision items. Everything below was re-verified against current code this session
(4-agent evidence sweep, file:line cited), not taken from changelogs.

**One correction:** your §1 said the invite went out and people are using the app. The
08-21 handoff had claimed the invite was never sent — that record is corrected, and it
shaped the rename for the better: the old address keeps serving behind a moved banner
(no redirect yet) precisely so your invitees don't lose their browser-saved work.

## The short version

| Item (your annotation) | State |
|---|---|
| §1 invite review/revise | **Done in v0.047** (accuracy pass + rename), plus today's free/open-source/no-account bullet; 2 copy calls still yours |
| §2.1 repo public, Issues off | **Done 2026-08-20** (re-verified during the repo rename) |
| §2.2 push uncommitted docs | **Done** — everything ships with this batch's push |
| §2.3 share links | **Built in v0.046** — the invite's claim is now true |
| §2.4 phone header overflow | **Fixed in v0.046** (wrap ≤980px, dropdowns on-screen to 320px) |
| §3.4 nav-band blurb file | **Already exists**: `docs/chrome-ref-band-findings.md` — rename note added this session; hand it to a site-repo session as-is |
| §3.5 clocking | **Explained below**; standing recommendation KEEP WARN — one branch flips it on your word |
| §3.6 3D 📷 fit | **Closed by your click-test** — recorded |
| §3.7 footer GitHub `_blank` | **Settled 08-20, exception STANDS** — recorded in `feedback-tracker.md` standing rulings, `SiteBand.tsx:130-146`, and the upstream spec-ask in the chrome-ref file |
| §3.8 supersonic default-ON | **Stays OFF**, as you said — ARCAS M1.49 CD anchor still fails (+0.033 vs ±0.02) |
| §3.9 LICENSE | **Exists since the flip** — full GPLv3 at repo root, linked from README + the in-app source link |
| §3.10 design optimization | **Queued post-beta**, as you said |
| §4.1 .ork round-trip loss | **Shipped in v0.046** (all three sub-defects; evidence below) |
| §4.2 engine rebuild + 4 kernel fixes | **Shipped in v0.046** (all four; difftest passed; evidence below) |
| §4.3 guide behind + workflow fiction | **Mostly fixed earlier; last 3 gaps fixed in this batch** |
| §4.4 quota silent failures | **Shipped in v0.046**, with regression tests |
| §5.1 npm install | **Done 2026-08-20** — both workspace links are real symlinks (verified by readlink) |
| §5.2 machine paths + scrubs | **CLAUDE.md/scripts already fixed**; this batch archived 3 superseded docs + fixed dead `G:\git` paths in a research doc |
| §5.3 Architecture list | **Done** — engine-java/, validation/, scripts/ all listed |
| §5.4 deploy-pages.yml | **Gone entirely** — the file was deleted; CI holds only deploy.yml (Cloudflare); no github.io mechanism exists |

## §3.4 — the blurb file for the site-repo sessions

It already exists and is exactly what you asked for: **`docs/chrome-ref-band-findings.md`**.
Self-contained ("you don't need to have seen this repo"), all three findings with
measurements and concrete fixes — focus-ring gutter (`margin:-4px; padding:4px;`), the
768–924px hidden-scrollbar dead zone (two options + an anti-fix warning), the `.6em`
per-tool gap resolution — plus the re-translation rules and a fourth spec-level item:
asking `chrome-spec.md` to add the GitHub `_blank` exception to MUST 6 explicitly. This
session added a rename note under its title. Paste or reference that one file in the
other tools' sessions; nothing else travels.

All three band defects are still live in this repo's own band CSS (`styles.css:496-504,
615`) — deliberately unfixed here because they need the site-repo ruling first.

## §3.5 — clocking: the explanation you asked for

**What happens today:** only when an oversized part is offered as a *segmented* print
(the split-ZIP path) AND that part's direct children include a rail button or launch
lug, the offer's note appends: *"This part carries a rail button or launch lug: a round
joint holds no clocking, so draw an alignment line down the outside before you glue."*
(`printPack.ts:160-165`, fires via `CLOCKED_CHILDREN` at `:62`, test-pinned at
`printPack.test.ts:154-161`.)

**Why it warns instead of refusing** (the comment at `printPack.ts:51-61`): the
button/lug are separate components fitted after assembly — they are not part of the
exported mesh. The printed segments are geometrically correct; the only thing a round
joint loses is the *rotational reference* for where to mount them. Refusing would block
a perfectly good body-tube export over a hole you drill after gluing.

**What "refuse" would concretely be:** one added branch in `printOffer()`
(`printPack.ts:179-247`) returning the existing `kind:'refuse'` shape before the split
offer. Effect: the "N pieces" button reverts to whole-part single STL with the warning
note under it — it never blocks export entirely. No kernel involvement; app-side only.

**Standing adjudication** (`response-2026-08-20b.md` §3.5, recorded in
`working-notes.md`): **keep the WARN** — rail buttons are near-universal on exactly the
rockets big enough to need splitting, so refusing would make the flagship splitting
feature unusable for its main audience, and a mis-clocked mount is recoverable (sand,
re-glue). Say the word and the branch flips in one edit; otherwise it stands.

## §3.7 — footer GitHub links: nothing left to fix

Settled 2026-08-20 after you delegated it: github.com links (footer band + feedback
buttons) open in a new tab, everything else in the band is `target="_top"` per
chrome-spec MUST 6. Your own standing ruling ("so the user is not completely taken away
from the site") took precedence, scoped to github.com only — and `_blank` still
satisfies MUST 6's purpose (escaping an embedding frame). Recorded in three places so it
stops resurfacing: `docs/feedback-tracker.md` "Standing UI rulings", the adjudication
comment at `SiteBand.tsx:130-146` (test-pinned in `SiteBand.test.tsx:118-140`), and the
upstream ask in `chrome-ref-band-findings.md`. The one remaining action lives in the
**site repo**: add the exception to `chrome-spec.md` MUST 6.

## §4.1 / §4.2 / §4.4 — your "can you just fix this?" items were fixed in v0.046

You annotated the survey from the morning *before* v0.046 shipped. Verified in current
code, not changelog:

- **§4.1 .ork round-trip**: launch conditions now travel both ways
  (`orkFile.ts:533-534` read, `:1229-1287` write, round-trip tests at
  `orkFile.test.ts:482-636`); multi-config files now emit an import note — "File has N
  flight configurations — kept X" (`orkFile.ts:493-515`, tests `:683-712`); mass
  components preserve their `masscomponenttype` so altimeters/trackers survive
  open→save (`orkFile.ts:385-393`, `:1134-1137`, test `:717-731`).
- **§4.2 kernel**: all four fixes are in the Java source AND the committed artifact was
  rebuilt with them at the v0.046 commit (`git log -- packages/engine/vendor/orkengine.mjs`
  → `1eb1e98`): warnings surfaced (`OrkEngine.java:842-857`), transition shape parameter
  (`ComponentFactory.java:97-100`), transition clipping (`:105-108`), and the series
  bridge now has summary + full modes carrying POSITION_XY, ROLL_RATE, and every series
  the kernel computes (`OrkEngine.java:974-1028`). Differential test passed at that
  rebuild (258 lines: 148 bit-identical, 110 within tolerance).
- **§4.4 quota**: `simStore.persist` now returns the stored truth and raises a flag on
  quota errors (`simStore.ts:48-65`), session autosave raises `saveFailing`
  (`session.ts:78-93`), both feed visible banners (`App.tsx:1082-1086`, `:1823-1829`),
  with dedicated quota-stub regression tests in both service test files.

**What §4.1/§4.2 genuinely leave open** (real items, queued below): full multi-config
*import* (the app keeps-and-annotates the first config; importing all of them is a
schema change), an in-app picker so mass components you *create* can be typed as
altimeter/tracker (today the type only survives round-trips), and the geodetic model
still hardcoded SPHERICAL (`OrkEngine.java:763`) — a small rider for the next engine
rebuild.

## §4.3 — the guide: last gaps closed in this batch

Already fixed before this batch: the generator is real (`scripts/build-user-guide.mjs`
runs inside `npm run build`; the "documentation workflow is fiction" complaint died in
v0.046), the guide's version stamp is dynamic (`GuideDialog.tsx` renders
`APP_VERSION`), and the guide already covered v0.045 part-splitting and every headline
v0.046 feature — plus the invite now points testers at the Guide button.

Fixed in this batch (the three residual gaps the sweep found): the guide now documents
the storage-quota warnings; explains that autosaves are bound to the web address you
saved them at (which covers both the rename migration and the iframe-embed case) with
the export-to-move instruction; and the stale Save/Export parenthetical now matches the
nine-entry menu (with Undo and Feedback added to the header-button list). Regenerated;
ships with this batch's push.

The **first-run nudge** idea graduates into Part 1's S3 (the tour) — a better shape for
the same need.

## §5.2 — housekeeping judgment calls

- **Archived** into `docs/archive/` (with its README updated): `handoff-2026-08-12.md`
  (superseded by the 08-20/08-21 handoffs), `staging-clusters-design.md` and
  `pods-implementation-plan.md` (both plans fully shipped long since). Kept at top level
  on purpose: `phase0-findings.md` (cited normatively by CLAUDE.md and
  `engine-java/build.gradle`), `upstream-bugs-triage.md` (a live rule via
  `carve-manifest.txt`), `chrome-ref-band-findings.md` (undelivered handoff — archive it
  only after the site repo acts on it).
- **No gitignore on the archive** — deliberate. It is public GPL history; ignoring it
  would neither remove it from git nor serve anyone. Old-machine references inside dated
  history docs also stay as written; the one *live* doc carrying dead `G:\git` absolute
  paths (`research/rasaero-supersonic-spec-2026-08-03.md`) was switched to repo-relative
  paths.
- **"Dev pages" scrub — verified clean**: `deploy-pages.yml` no longer exists at all; CI
  is exactly one workflow (Cloudflare deploy with the version gate); every remaining
  `pages.dev` reference in live code is functional (the canonical-host redirect and its
  regression tests). github.io appears only in archived/history docs.

---

# Waiting on you (the whole list, both parts)

1. **RocketForge proposals S1–S5** — which get green-lit, and in what order? (My
   suggestion: S3 tour + S2 callouts before the forum post.) Mockups on a design canvas
   on request.
2. **Two invite copy calls** before the forum post: the non-affiliation half-clause, and
   whether the pre-rename migration note stays in the public version.
3. **RocketForge screenshots**: move to Dropbox reference (recommended) or commit?
4. **Phase-2 301** (old subdomain → mmrsim): say when — ~2 weeks or once your invitees
   confirm they've moved (mechanics: rename plan Task 10).
5. **Clocking**: stands at WARN — one branch to flip if you disagree.
6. **Full multi-config .ork import** (M) — want it scoped?
7. **Mass-component type picker** (S) — altimeter/tracker identity for components
   created in-app.
8. **Geodetic model selector** (S) — rides the next engine rebuild.
9. **Supersonic default-ON** — still blocked on the ARCAS anchor; unchanged.
10. **Design optimization** — post-beta, unchanged.
11. **Site repo, next session there**: act on `chrome-ref-band-findings.md`; retake the
    tools-page screenshot; two internal code comments there still carry the old name.
12. **Desktop, next session there**: `git remote set-url origin
    https://github.com/mtnmanak/mmrocket-sim.git`.

# What this batch changed

- Beta invite: the free / open-source / no-account bullet (Part 1).
- Guide: quota-warning + address-bound-autosave sentences, Save/Export parenthetical +
  header-button list fix; `userGuide.ts` regenerated.
- `chrome-ref-band-findings.md`: rename note added under the title.
- Archived 3 superseded docs; archive README updated; live links fixed.
- `rasaero-supersonic-spec` research doc: dead absolute paths → repo-relative.
- README: Licensing section now names the `LICENSE` file.
- Regression tests: exported `.ork` root asserts `creator="MMRocket Sim"`; STL binary
  header asserts the stamp (the two assertions the rename's final review asked for).
- This response + your RocketForge note committed together; everything above deploys
  with this batch's push.
