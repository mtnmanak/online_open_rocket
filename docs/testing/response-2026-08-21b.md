# Response 2026-08-21b — everything you green-lit, done in one sitting

Your batch (`issues-2026-08-21b.md`) answered the whole "Waiting on you" list, so this
session executed it. Short version: **v0.048 shipped** (tour + CG/CP callouts + mass-type
picker), **the phase-2 301 is live**, the screenshots moved to Dropbox, multi-config
import is scoped, the site repo is fixed, and the S1/S5/S4 mockups are on a design canvas
waiting for your eyes.

## The mockups you asked to see (S1, S5 — and S4's new direction)

**→ https://claude.ai/code/artifact/1b9fe671-771e-4d48-8bcf-f537315f63f1**

Four artboards, all in our own dark theme and Rajdhani/telemetry voice, with sticky
notes carrying the open questions:

- **S1 — rocket as hero (desktop):** the 2D canvas as the whole center column, the
  floating stats chip top-left (my pick: length, loaded mass, CG, CP, stability), the
  full stat grid one click away in a drawer (there's a toggle above the frame — flip
  `drawerOpen` to preview it open), and a 90° rotate control in the canvas toolbar.
  Your two calls from the proposal stand: chip contents, and drawer default (mocked
  closed).
- **S2 — callouts on today's layout:** what actually shipped in v0.048 (below), drawn
  as an under-stable example so you can see the color coding.
- **S4 — launch-centered phone:** reworked around your steer (see next section).
- **S5 — selective richness:** motor tinted + labeled in the mount, hover highlight
  with a name tag, and a before/after sketch of the 3D materials pass (directional —
  the real render will differ).

These are static mockups, not clickable prototypes — the point is layout and feel;
say the word on any of them and the real build starts.

## S4 — thought through backwards from the field, per your steer

Your framing (desktop = design-heavy, phone = launch-centered) changes what stage 1
should be. The likely field workflow: open the design you built at home → confirm
stability with the motor you're about to fly → check optimum delay against the delays in
your range box → check descent rate for the field → sim → read apogee. So the phone
mockup makes **Fly the home screen** below the phone breakpoint: stability verdict up
top, the four field numbers (apogee, optimum delay, descent rate, max velocity), the
motor swap row, the three launch conditions that actually change at the field (rod
length, angle, wind), a big thumb-zone LAUNCH, and an entry into batch compare ("which
of my motors flies this best today"). Design and Results stay one tap away in a bottom
bar — field tweaks like adding an altimeter mass still work, but design isn't what the
phone leads with.

Stage 1 stays cheap (this arrangement of existing panels + the vertical rocket);
bottom-sheet editing inside Fly is stage 2, after beta feedback shows what phone users
actually reach for. If the mockup direction looks right to you, stage 1 is buildable
before the forum post.

## Shipped this session as v0.048

1. **S3 — first-run tour.** Six anchored stops (component tree → 2D canvas → Motors &
   Launch → Launch button → Results → Guide + Feedback), one terse card each, in-house
   (no library — `FirstRunTour.tsx`, ~170 lines). Shows exactly once
   (`online-openrocket.tour.v1` flag; Skip counts as seen), auto-suppressed for anyone
   arriving on a share link or with a restored session, replayable from the Guide's new
   **⟲ Tour** button, and Preferences → Display gains the off switch you asked for.
   Steps live in one array — adding/deleting stops later is a one-line edit each.
2. **S2 — CG/CP leader-line callouts.** Dashed leaders from the markers to labeled dots
   in clear lanes above (CG) and below (CP) the airframe, margin text on the drawing
   ("✓ 1.52 cal — ok") color-coded exactly like the stability card, same
   glyphs/vocabulary as everywhere else. The schematic reserves lane sky so short/fat
   rockets don't clip it, exports (SVG/PNG) bake the status colors, and the
   nearly-overlapping-markers case — precisely the under-stable design you most need to
   read — is now legible because one leader goes up and one down.
3. **Mass-component Type picker.** A Type dropdown on mass components (altimeter,
   flight computer, deployment charge, tracker, payload, recovery hardware, battery,
   plain ballast) — one schema entry; the panel, component table and CSV picked it up
   automatically. Cosmetic by design (kernel treats it as label-only), round-trips into
   saved .ork files, sparse default preserved.

26 engine + 588 app tests green (26 of them new), typecheck clean, build clean,
version gate consistent.

## The 301 — done, verified, and the trail updated

You said all invitees confirmed, so **phase 2 fired today**: a Cloudflare Single
Redirect on the zone, exactly per the rename plan's Task 10 (expression
`http.host eq "openrocket.mountainmanrockets.com"`, dynamic 301 to
`concat("https://mmrsim.mountainmanrockets.com", http.request.uri.path)`, query
preserved). Verified by curl: root → 301 with the right Location; `/some/path?x=1&y=2`
→ 301 preserving path and query; the canonical host still 200s. Doc trail updated:
Task 10 marked executed in the plan, CLAUDE.md's deploy table, and the beta invite's
migration note (which now simply says the old address forwards automatically —
"open the old address and export" stopped being possible the moment the 301 landed,
and stopped being needed the moment your last invitee moved). The old-origin migration
code (`hostMigration.ts` / `MovedNotice`) stays in the app harmlessly — the retired
host never reaches it now; it can come out whenever we next feel like tidying.

## The rest of the list

- **RocketForge screenshots** — moved to
  `Dropbox\online_open_rocket_reference\RocketForge Screenshots\` (six PNGs), repo tree
  clean. Same rule as the RASAero PDFs.
- **Clocking** — stays WARN, per your call. Nothing changed; the flip remains one
  branch if you ever reverse it.
- **Multi-config .ork import** — scoped in **`docs/multi-config-import-scope.md`**.
  Headline: three stages — A: read all configs *correctly* and let the user pick one at
  import (M — this also fixes real silent wrongness today, where a kept motor can pair
  with another config's ignition override, and finally reads dual-deploy
  `<deploymentconfiguration>`); B: named configs as app state, motors axis (M–L);
  C: per-config deployment/separation + stage activeness (L — gated on the next engine
  rebuild, rides with the geodetic selector). My recommendation: green-light Stage A
  by itself; hold B/C for beta feedback.
- **Beta-send clarification** — recorded in the working notes: the invite went to
  selected individuals, not publicly; the public forum post is still pending and still
  gated on your two invite copy calls (non-affiliation half-clause; whether the
  migration note stays in the public version — note the 301 just made that note mostly
  moot: the old address is now invisible to anyone who follows it).

## Site repo — fixed, committed, pushed

Per "just fix it": the band findings landed as `chrome.ref.js` **1.3.0** (focus-ring
gutter; the 768–924 px dead zone got the conservative overflow-only edge-fade, option 1,
because option 2 still couldn't fit the labels at 800 px and surrenders the visible
search input; phone gap stated in px), MUST 6 now carries the GitHub `_blank` exception
explicitly, the last two old-name code comments read MMRocket Sim, and the tools-page
screenshot was retaken from the live app (same 1200×770 asset, same path). All site
checks green. One downstream item surfaced for you: **bp_calculator (and
motor_dashboard) vendor ref 1.2.0 and now trail 1.3.0** — `check-tools.mjs` is already
reporting the first of these, which is the re-vendor signal doing its job; a future
session in those repos picks it up.

# Waiting on you (new list — much shorter)

1. **S1 / S5 / S4-direction verdicts** after you look at the canvas — S1 chip contents
   + drawer default; S5 go/no-go; S4 "Fly-first phone" yes/no (stage 1 buildable
   pre-forum-post if yes).
2. **The two invite copy calls** (unchanged, still gating the forum post): the
   non-affiliation half-clause, and whether the public post keeps the migration note.
3. **Multi-config Stage A** — green-light or hold.
4. Standing queue, unchanged: geodetic selector (rides next engine rebuild), supersonic
   default-ON (still blocked on the ARCAS anchor), design optimization (post-beta).
