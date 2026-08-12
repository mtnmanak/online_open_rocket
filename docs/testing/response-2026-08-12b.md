# Response — issue batch 2026-08-12b

**Shipped as v0.045** (2026-08-12). Source batch: `docs/testing/issues-2026-08-12b.md`.
No engine rebuild — `packages/engine` and `engine-java` untouched, so the differential test
is unaffected.

---

## Quick status

| Item | Status |
|---|---|
| JOB 1 (a) — contract points at the wrong host (live in production) | ✅ Fixed |
| JOB 1 (b) — band wraps to 2–3 rows between 768px and ~924px | ✅ Fixed |
| Component STL exporter — "build it" | ✅ **Built** — splitting, printer profiles, zip |
| DXF | ✅ Done last release, nothing to do |
| STEP / CNC | 📥 **Backlogged** per your call, recorded below |
| Darkstar image — auto-fit or trim | ✅ **Auto-fit built** (and why it beats trim) |
| GitHub-link exception in the footer | ✅ Confirmed, kept as-is |

**Tell the site:** Online OpenRocket is now **v0.045**.

---

## JOB 1 — both fixes

### (a) The contract host

`CONTRACT` now points at `https://www.mountainmanrockets.com/chrome/nav.v1.json`.

For the record on how this happened: I built against spec §5 as it read at the time, flagged
the host as an open question rather than deviating unilaterally, and you corrected the spec
(`4537e11`, "the reference still pointed at the pre-cutover pages.dev"). That's the loop
working — raising it beat quietly picking the other host.

I swept the whole app package for other `pages.dev` URLs: **none the app fetches or links
to.** The only remaining mentions are the tool's own deploy URL in docs (correct, left alone)
and the warning comment explaining why not to revert. Every host the app actually talks to is
`www.mountainmanrockets.com`, `github.com`, `youtube.com`, `thrustcurve.org`, `openrocket.info`
or `gnu.org`.

I also corrected the stale claim in `docs/working-notes.md` and closed open question 4 in
`response-2026-08-12a.md`, and rewrote the header comment in `useMmrNav.ts` — it still said
"the constants are the spec's, unchanged", which a future session would have read as licence
to restore the pages.dev literal. That failure is **silent by design** (MUST 1 means
`fetchNav` swallows everything; the only symptom is `data-mmr-source="fallback"` in DevTools),
so the comment mattered more than usual.

### (b) The wrapping

Applied as specified: `flex-wrap: nowrap` and the `overflow-x: auto` scroll treatment moved to
**base** selectors, with only the genuinely width-conditional rules left in
`@media (max-width: 767px)`. The `-4px 0 / 4px 0` focus-ring gutter is kept as a pair, and the
"don't raise the breakpoint — ~924px is a measurement of eight labels" ruling is recorded at
the rule so nobody re-derives it.

**Two of your selectors don't match our markup, so I translated them rather than pasting
them.** Your block came from the vanilla reference; our band is React (spec §5 explicitly
leaves the JSX to each tool). Applied verbatim, two rules would have been silent no-ops:

| Your rule | Ours | Why |
|---|---|---|
| `.mmr-band-nav li { flex: none }` | `.mmr-band-nav a { flex: none }` | We render bare `<a>`, no list. This one is load-bearing — `white-space: nowrap` forbids a line break but does nothing against flex *shrinking*. |
| `.mmr-band-search form { display: none }` | `.mmr-band-search { display: none }` | In our markup `.mmr-band-search` **is** the `<form>`, not a wrapper. As written the descendant selector matches nothing and **the 130px search input would have survived to 375px.** |

Both translations carry a comment naming the reference selector they correspond to, so the
next person diffing our CSS against your block sees why they differ. I did not restructure the
JSX to match the reference, and did not raise the breakpoint.

I also skipped `.mmr-band-brand { flex: none }` and `.mmr-band-search { flex: none }` — both
already carry `flex: 0 0 auto`, the identical computed value.

### Three findings I did NOT act on — they belong upstream

Each is real, but fixing it here alone would make this tool's band differ from the other
three, and the point of the contract is that they agree. All three apply to anything
vendoring `chrome.ref.js`.

1. **The focus-ring gutter is vertical-only, so it still clips horizontally.** A scroll
   container clips on all four sides (`overflow-x: auto` computes `overflow-y: auto` too), and
   the horizontal padding is zero — so the ring on the *first* link's left edge and the
   *last* link's right edge is still shaved, which is the exact defect the pair exists to fix.
   `margin: -4px; padding: 4px` (all four sides) fixes it and preserves the gaps and the
   29/34px heights.
2. **Hiding the scrollbar removes the only overflow affordance at 768–924px.** Below 768px
   swipe covers it; at those desktop widths nothing does. Measured at 800px: the eight labels
   need ~700px and get ~527px, so "Online Tools" and "Contact" are silently gone — no
   scrollbar, no fade. Before the fix they wrapped: ugly, but visible. Cheapest fixes are a
   right-edge fade that only paints on overflow, or allowing the compact magnifier at these
   widths (§3 already permits forcing it at all widths, and it frees ~130px).
3. **`gap: .6em` resolves differently per tool.** Against our 11.5px band font that's 6.9px,
   replacing 12px — a ~40% phone tightening arriving inside a desktop fix, and the only `em`
   in an otherwise all-px block. Applied as given, and it measures fine, but if the four tools
   have different band font-sizes then one "shared" rule yields four different gaps.

---

## The component STL exporter — splitting, built

You said "build it", so here it is.

### What you get

Set your printer once in **Preferences → 3D printing** (eight presets — H2D, X1C/P1S/A1,
A1 mini, MK4S, XL, Ender 3, K1 Max, Neptune 4 Plus — plus Custom X/Y/Z, and the joint
clearance). Then the existing 🖨 button becomes printer-aware:

- **Part fits, or no printer set** → exactly what it does today. Same button, same single
  STL, same filename. This is the compatibility guarantee: ignore the feature and nothing
  changes.
- **Printer set, part fits** → a quiet confirmation line.
- **Printer set, part doesn't fit** → the button reads **"🖨 STL for printing — N pieces"**
  with an amber line telling you how much too long it is and what the joint will be, and it
  exports **one zip**: the numbered segment STLs plus a `README.txt`.

The README carries what a slicer can't tell you: print each piece base down, tip up, no
supports; **same material and same printer for every segment** (PLA shrinks ~0.3%, ASA/ABS
0.6–0.8%, and on a 72 mm bore 0.7% is 0.5 mm — several times the joint clearance; it only
cancels if both halves shrink identically); the clearance actually used, and that 30-minute
epoxy suits it while CA will seize; dry-fit first; and the segment lengths so you can check
nothing was lost.

### How it works, and why it was cheap

Exactly the argument from last time: every revolved part comes from one closed 2-D `(x, r)`
profile, so a split is **polygon clipping plus a generated spigot, then re-revolving** — no
CSG, no mesh booleans, no new dependency. Each segment inherits `revolveProfile()`'s
watertightness proof by construction.

The spigot is a **running minimum of the bore**, not a plain offset — that detail matters. A
nose bore *widens* aft, so a straight offset copy would be fatter at its tip than the socket
mouth and could not enter at all. The running minimum flattens it to a cylinder at the mouth
radius; on a reducing transition it *is* the bore, giving the full-length tapered register.
Either way the plug converges in the insertion direction, which is the insertability
condition.

Solid parts (`filled`, or wall ≥ radius) have no bore to put a spigot in, so they fall back
to an on-axis stub-and-socket rather than refusing.

### Two things I corrected against my own earlier write-up

1. **"3 segments on an MK4S" was wrong, and it was my error.** That came from the design
   doc's 25 mm spigot estimate; the implemented rule is `max(0.25 × diameter, 12 mm)` capped
   at 30, which is 19.05 mm on a 3" part. The 3" nose is **2 segments on both** an H2D and an
   MK4S. A case that genuinely needs more: the 5" Goblin nose (650 mm, Ø130.8) is 3 on an H2D
   and 4 on an MK4S.
2. **The usable-height margin.** The first implementation inset the margin at *both* ends of
   every axis — including Z — purely to reproduce the segment counts I had asserted. X and Y
   are right that way (you inset from both bed edges), but a part sits **on** the bed at z=0,
   so Z only loses headroom at the top. Corrected to one-sided, which restores the honest
   number: your 3" nose is **64 mm** too tall for an H2D, not 72.

### What it refuses, and what it only warns about

- A cut landing in a shoulder can't work (the profile doubles back there and the result
  wouldn't be manifold), so cuts are nudged to a legal position; only a part with no legal
  placement at all is refused.
- **Refusing refuses the *split*, not the export** — you still get the whole-part STL, under
  a line saying why it couldn't be split. Nothing that won't assemble is ever emitted.
- A component with a rail button or launch lug **warns** rather than refuses: a round joint
  holds no clocking, so the note tells you to draw an alignment line down the outside before
  gluing. Those are separate components and aren't in the mesh, so the segments themselves are
  correct — refusing would block a good export over a hole you drill after assembly. Say the
  word if you'd rather it refuse; it's one branch.

---

## The 3D snapshot — auto-fit

Built **auto-fit**, not trim. The reason: trimming crops pixels away *after* rendering, so an
"8K" export of a small on-screen rocket yields far less than 8K of actual rocket. Auto-fit
moves the camera *before* the hi-res render, so the full requested resolution lands on the
subject. Trim is the weaker half of the same idea.

A **"Fit rocket to frame"** checkbox appears in the image export menu, **default on**.
Unticked is byte-for-byte the old behaviour. It preserves your current viewing *direction* —
only distance and target move — so a three-quarter view stays a three-quarter view, filled.

One implementation note worth knowing: it renders through a **throwaway camera** and never
touches the live one. OrbitControls owns the on-screen camera and re-derives its state from it
every frame, and an 8K encode takes seconds — mutating and restoring would have shown you a
jumped view for that whole window. It also gets its own near/far planes, because fitting a
5 cm component pulls the camera closer than the default 0.1 m near plane and would otherwise
have exported a blank frame.

---

## STEP / CNC — backlogged

Recorded as agreed. For whoever picks it up, the order that matters:

1. **Fabrication geometry first** — hole patterns (bolt circle, eyebolt, vent, all-thread
   pass-throughs) on centering rings and bulkheads, then the ring shoulder/step, then chamfers.
   This pays off **immediately in the DXF** as inner contours, independently of STEP.
2. **Then STEP** — hand-written AP214, ~600 lines, zero dependency, sharing the same profile
   generators the STL and DXF already use so the three can't drift.
3. **Never opencascade.js** — 48.9 MB raw / 9.1 MB brotli against a 4.2 MB app, and its
   `.wasm` wouldn't be precached while its `.js` would, giving a STEP button that looks
   offline-ready and isn't.

The DXF exporter already emits multiple contours per part, so hole patterns are additive
there — no retrofit cost.

---

## Verification

- **515 tests** green (492 app + 23 engine), up from 349. Build clean, precache still 21
  entries. `packages/engine` and `engine-java` untouched — no differential exposure.
- **Adversarially reviewed twice.** The build pass found **14 defects**, the fix pass
  re-verified all of them with independently re-derived numbers, then found **2 more**. Among
  them: the auto-fit framed stubby rockets *smaller* than doing nothing (the feature was
  net-negative for short parts); the lean check certified a placement against the bed
  *diagonal* while ignoring the part's width; and a solid part's stub and socket were cut to
  exactly the same length, hydraulically trapping the epoxy the README tells you to use.
- **Responsive, measured in Chrome on the built `dist`** across 1400 → 375px: the band is
  **one row at every width** (38px above 768px, 28px below), the search form swaps to the
  magnifier at 767px, and the band itself never overflows (352px wide at a 375px viewport).
  The wrapping bug is gone.
- **The contract is fetched from `www.mountainmanrockets.com`** — confirmed from the live
  resource timings, with `data-mmr-source="live"` and `menuVersion 480f0531d406`.
- **PWA offline test**, service worker installed, origin server killed outright: the app came
  up **entirely from precache** — rocket drawn, component tree, vitals, Launch button, band
  with all eight links, footer strip. And the band's baked fallback is **byte-identical to
  the live contract** in the shipped bundle (same `menuVersion`, same eight labels and URLs),
  so an offline user sees exactly the online menu. The fetch-rejection path itself is unit
  tested.
- **Not click-verified: the 3D auto-fit snapshot.** The R3F canvas doesn't initialise under
  this CDP browser (a documented gotcha since v0.041), so the framing maths is covered by 25
  unit tests against hand-derived numbers instead — including a real `PerspectiveCamera`
  projection check that all eight bounding-box corners land inside NDC. **Worth one click
  from you**, same as the 3D 📷 export was.

### One pre-existing bug found, not in this batch

At narrow widths the **app's own header** overflows horizontally — its file buttons don't
wrap, so at a 375px viewport the page scrolls sideways to ~950px. This predates all of this
work (the band is clean; I measured it separately) and it isn't in this batch, so I left it
alone. Worth a line in a future one — it's the kind of thing that makes the tool feel broken
on a phone at the launch site.

---

## Waiting on you

1. **The three upstream band findings** above — they need a decision in the site repo, not
   here, since they affect all four tools.
2. **Clocking: warn or refuse?** A component with a rail button or launch lug currently warns
   that a round joint holds no clocking. Say the word and it refuses instead.
3. **One click on the 3D 📷 export** with "Fit rocket to frame" on, to confirm the framing.
4. The app-header overflow above, if you want it in the next batch.
