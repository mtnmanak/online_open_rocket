# Response — issues-2026-07-04a.md

All three items shipped in **v0.014** (same day). Item-by-item:

## 1. "Add to [main component]" option missing — FIXED

The add buttons under the component tree now offer **every sensible target
for the current selection**:

- the selected component itself, when it can hold children ("+ Add to Nose cone"),
- its immediate parent tube, when nested ("+ Add to Body tube" while a fin
  set is selected),
- and **its enclosing stage** ("+ Add to Sustainer" / "+ Add to Booster") —
  the one you asked for.

So clicking the sustainer's nose cone now shows "+ Add to Nose cone",
"+ Add to Sustainer", and "+ Add stage". On a multi-stage rocket the stage
button targets the stage the selection actually lives in (selecting a
booster part offers "Add to Booster", not the sustainer).

## 2. Header/banner site menu — DONE

A menu bar now runs across the very top of the app with exactly your eight
items in order: Home, Builds, HPR Primer, Tools and Tips, Online Tools,
Gallery, Videos, Links (your URLs verbatim).

One technical note: the links are written with `target="_top"`. In the
WordPress **iframe embed** a plain link would navigate *inside the iframe*
(the menu would load your homepage into the little app window) — `_top`
makes the click take over the whole browser tab, which is the seamless
same-tab behavior you described. It behaves identically when the app is
opened standalone.

The bar uses the app's theme colors (light/dark aware). If you'd rather it
visually match the WordPress site header (fonts/colors/logo), send a
screenshot or the site CSS and it can be skinned to match.

## 3. Motor drawn in the rocket panel — DONE

When a motor is loaded, the 2D view now draws a **brownish translucent
silhouette of the motor case at true scale** — the real case length and
diameter from the motor database — seated flush against the aft end of its
motor mount tube (how a motor actually loads; a case longer than the mount
correctly sticks forward past it). Works per mount on staged rockets, and
clustered mounts draw one motor per cluster position. The silhouette is
non-interactive, so dragging the mount tube still works exactly as before.

3D view was not touched — say the word if you want the motor there too.

---

**Verification:** full suite green (127 tests), build clean,
browser-verified on the preview build: nose cone selected → "+ Add to Nose /
+ Add to Sustainer"; fin set selected → "+ Add to Body / + Add to
Sustainer"; all 8 menu links present with `_top`; C6-5 silhouette measured
in the SVG at exactly 70 mm × 18 mm at view scale.
