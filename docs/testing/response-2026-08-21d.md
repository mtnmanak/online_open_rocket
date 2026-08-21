# Response 2026-08-21d — the polish batch + Stage B, live as v0.050

*(Addendum, same day: two chat reports followed and shipped as **v0.051** — the stats
chip is genuinely floating: drag anywhere, fold to a stability pill, position
remembered — and **v0.052** — the hero drawing surface now spans the full canvas; the
"black box the rocket slid under" was the drawing's invisible clip edge. Both
live-verified. Session record: `docs/handoff-2026-08-21f.md`.)*

Everything in your batch is addressed: the vertical/drawer collision is fixed, all
three 3D notes are in, both invite copy calls are closed the way you called them, and
**Stage B is built** — flight configurations are now first-class, switchable state.

## S1 — the vertical + drawer collision: fixed

The drawing area now measures the open drawer and shrinks to the sky above it, in both
orientations — a vertical rocket sits fully visible above the stats instead of hiding
under them. (Root cause: vertical mode is deliberately read-only — no zoom/pan — so
there was no way to escape the overlap. Now there's nothing to escape.)

## S2 in 3D — all three notes

- **Axis balls**: down to 0.6× — they no longer overwhelm a small rocket. The floating
  gadget keeps its size (it lives in empty space).
- **Lost-camera recovery**: **Reset / Side / Aft** buttons in the canvas corner jump
  the camera to known-good views, and zoom is now distance-limited in both directions,
  so the camera can neither bury itself inside the hull nor fly off to where the
  rocket is a pixel. (Reset restores the standard three-quarter opening view; Side is
  the straight profile; Aft looks up the tail like the 2D Aft view.)
- **Real internal shading**: the honest diagnosis is that v0.049's translucency was
  half-shipped — 0.88 opacity with depth-writes on *looked* opaque from most angles.
  The shell now renders at 0.62 opacity with depth-writes off and both faces drawn,
  which is the actual see-through technique: inner tubes and the loaded motor are
  genuinely visible through the wall now, with the far wall showing for depth. If it
  still doesn't feel comparable to RocketForge's after you look, say so — the next
  lever is theirs-style per-part contrast (darker internals, lighter shell), which is
  a color-table tweak, not surgery.

## Your "why do we start with a motor loaded?" question — answered honestly

There are real reasons, which is why I kept default-loaded as the default — but your
instinct drove the feature set, so both worlds now exist:

- **The file says so.** A .ork's `default="true"` configuration is the author's
  explicit "this is how I fly it" — desktop OpenRocket opens files with that
  configuration loaded, and matching the desktop is a standing design rule here.
- **The field flow.** At the pad (especially on the Fly screen), open → Launch is the
  most common repeat visit; an empty-open inserts a mandatory motor detour into it.
- **Share links.** "My rocket on a C6-5" — the flight state is usually the point.

Against that, your two points stand: empty mass is the design truth, and a silently
pre-loaded motor can get simmed unexamined. So: the multi-config picker now offers
**"Open with no motors loaded"** right next to the configurations, the new
configurations panel has a **"None — no motors loaded"** row, and ⏏ Unload stays one
click away — while the vitals strip always shows exactly what's loaded. If you use
"no motors" for a while and decide it should be the *default* answer, that's a
one-line flip; the machinery is identical either way.

## Stage B — built (you asked twice, and no, there was no good reason to wait)

- **A "Flight configurations" panel** on Motors & Launch (appears when the open file
  carries more than one — a single-config file's one-row panel would be noise on
  every ordinary design, and ⏏ Unload already covers its "None"): every configuration
  listed by name with its motor summary and the file's default marked — **Apply** any
  one, switch freely, or pick **None**.
  Once applied it stays loaded until you change or unload it — exactly the behavior
  you described.
- **Your motor edits belong to the configuration you're flying** — swap the delay or
  the motor and that's now what that configuration is.
- **Saving writes every configuration back to the .ork** with stable ids and names —
  open the file in desktop OpenRocket and they're all there, the one you were flying
  marked default. Share links carry the whole set for free (they encode the .ork).
- Sessions autosave the configurations; saved-flight CSVs record which configuration
  flew each run (new trailing column). Per-configuration deployment/separation/stage
  activeness remain Stage C, riding the next engine rebuild as scoped.

## The invite — cleared for the forum post

Per your call: the pre-rename migration note is gone (the 301 makes it wordless), no
non-affiliation clause added, version line rolled. `docs/beta-invite.md` is now
copy-paste ready — **the forum post is all that's left, and it's yours**.

# Waiting on you

1. **Post the forum beta invite** — nothing gates it anymore.
2. **Look at v0.050** — especially the 3D internals (say the word on the contrast
   lever above) and the configurations panel with one of your multi-config files.
3. Standing queue, unchanged: Stage C + geodetic selector (next engine rebuild),
   supersonic default-ON (ARCAS anchor), design optimization (post-beta), S4 stage 2
   + S5 polish (beta feedback).
