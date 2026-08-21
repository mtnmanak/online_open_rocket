# Response 2026-08-21c — the build batch: everything you green-lit is live as v0.049

Short version: **v0.049 shipped and live-verified** — S1's hero canvas, the 3D
callouts you sent the screenshot for, S5's richness pass, the launch-centered Fly
screen, the tour fixes, multi-config import Stage A with a picker, and the labeled
Unload button. 26 engine + 613 app tests green (25 new this release), and the site's
tools page shows v0.049 with a fresh card.

## S1 — rocket as hero (desktop): built as mocked

The canvas is the whole center column now; the floating chip carries the five picks
you approved (length, loaded mass, CG, CP, stability); the full stat grid is behind
**▤ All stats**, default **closed** per your call, restyled as a compact instrument
strip so the rocket stays visible when it's open. The motor-mount pill rides the
canvas bottom-right, and the **⟳ 90°** toggle from the mockup is real — nose-up
viewing mode (drag/zoom pause while rotated, labels stay horizontal).

## S2 in 3D — your screenshot, adapted

Built from `rocket_forge_3d_screenshot.png` (now moved to the Dropbox reference
folder with its siblings): a floating gadget beside the hull — CG and CP spheres at
their **true axial stations** joined by a thin line, labels above/below, and the
color-coded margin ("1.52 cal", green/amber/red by the same card rule) beside the
line. Billboarded, so it reads from any orbit angle, and it never swallows a drag.
One deliberate divergence from their look: our CG sphere stays neutral-white (not
blue) to match the on-axis markers and the caption legend we already ship.

## S5 — built, both halves

2D: the loaded motor is tinted and labeled right in the mount ("C6-5" style — the
part you called out), and hovering any component highlights it with a light accent
wash plus a name tag. 3D: soft studio lighting and a slightly translucent shell —
inner tubes and the loaded motor now render inside, so the internals read through
the wall exactly like the mockup's right panel.

## S4 — built

Phones open on **Fly**: your rocket vertical with the CG/CP callouts, the stability
verdict, the four field numbers (apogee, optimum delay, descent, max velocity — they
show dashes until the first flight, never fake zeros), the motor swap row, rod
length / rod angle / wind, a thumb-zone LAUNCH, and the batch-compare shortcut. The
workspace tabs dock to the bottom of the screen, and in Fly the header slims to the
wordmark so the field screen wastes nothing. Desktop is untouched — the Fly tab
doesn't even show there. (Stage 2 — bottom-sheet editing inside Fly — still waits
for beta feedback, per the plan you approved.)

## S3 tour — both notes taken

- **Replay moved out of the Guide**: it's a header button now (**⟲ Tour**, next to
  Guide), which is also where the tour's last step points.
- **It takes the stage now**: the page dims to a spotlight cut around each step's
  target (accent ring + darkened everything-else), and the card sits on the
  note-yellow surface with an accent border — the one background no app panel uses,
  so it can't be mistaken for part of the UI. Same terse copy, same six stops.

## Multi-config .ork — Stage A is in

Every flight configuration is parsed with the desktop's own default-and-override
semantics — motor, ignition, **deployment** (dual-deploy altitude finally survives),
and separation — and the file's `default="true"` configuration is what opens, not
whichever motor came first in document order (which could silently pair a motor with
another configuration's ignition override — that bug is dead). A file with several
configurations now asks which one you want, by name, before anything is applied;
reopen the file any time to pick another. Stage-deactivating configurations get an
honest note (activeness itself is Stage C, gated on the next engine rebuild).
**Stage B** (named configurations as switchable app state) is scoped and ready in
`docs/multi-config-import-scope.md` — per its recommendation I'd let beta feedback
shape it; say the word if you want it sooner.

## The unload button

**⏏ Unload** — a real labeled button in the vitals strip next to the motor name,
same one-click behavior, same tooltip. The bare glyph is gone.

## Also in this release

The site band's chrome.ref **1.3.0** rulings are re-translated into this app's own
band copy (4-sided focus-ring gutter, the overflow edge fade at mid widths, the 8px
phone gap) — that closes the follow-up from yesterday's site-repo session. Still
outstanding in OTHER repos: bp_calculator and motor_dashboard re-vendor (the site's
check-tools flags the first automatically).

# Waiting on you

1. **Look at v0.049 live** — especially the hero Design page, the 3D view, and Fly
   on your phone. Everything above is tuneable; the 3D lighting warmth and the
   chip/drawer contents are one-line changes if you want them different.
2. **The two invite copy calls** (unchanged, still gating the forum post): the
   non-affiliation half-clause, and whether the public post keeps the migration
   note (the 301 makes it near-moot).
3. **Multi-config Stage B** — now or after beta feedback (my recommendation: after).
4. Standing queue, unchanged: geodetic selector + Stage C (next engine rebuild),
   supersonic default-ON (ARCAS anchor), design optimization (post-beta).
