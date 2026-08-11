# Response — issue batch 2026-08-11a → v0.041

Item-by-item status for `issues-2026-08-11a.md`. Shipped as **v0.041**
(push-to-main deploys via GitHub Actions to Cloudflare Pages now — no zip).

---

## 1. Cloudflare hosting / new deployment workflow — VERIFIED ✅

Understood and verified end-to-end: this repo is its own Cloudflare Pages
project (`online-open-rocket` → https://online-open-rocket.pages.dev), and
**`git push` to `main` IS the deploy** — `.github/workflows/deploy.yml` builds
(tsc + vite; the TeaVM kernel is the committed artifact so CI needs no Java),
copies `version.json`, and publishes with wrangler. CLAUDE.md already
documented it; nothing was stale there.

## 2. Repo move G:\git → E:\git, desktop/laptop switching — VERIFIED ✅ (two real problems found & fixed)

Local `main` == `origin/main` (`7c5a8bd`) — no divergence, no stash, clean tree.
But the move did leave two landmines, both now fixed:

- **The npm workspace links broke.** `node_modules/@online-openrocket/engine`
  came through the copy as an **empty directory** (Windows junctions carry
  absolute paths), which made three test suites fail with "cannot resolve
  @online-openrocket/engine". Fixed with a fresh `npm install`. **If the
  laptop clone shows weird "module not found" errors, run `npm install`
  there too.**
- **A stale orphan patch file** rode along at
  `engine-java/patches/rocketcomponent/InstanceMap.java` (the *dead path* the
  2026-08-04 audit eliminated — the real patch lives at the full
  manifest-relative path). carve.mjs hard-fails on orphaned patch files, so
  the next engine rebuild would have died. Deleted.

Docs updated (CLAUDE.md, current handoff, working notes): repo path is
`E:\git\online_open_rocket`, and since you now alternate machines, every
session starts with `git fetch` + a local-vs-origin check. The Dropbox
reference source stays on `G:` and still resolves.

## 3. Component CSV/Excel export — SHIPPED ✅

**Save/Export ▾ → "Export .csv — component data" / "Export .xlsx — component
data".** One row per component (stages are a grouping column), with:

- Component, Type, Stage, Parent, Material
- **Engine-computed values**: starts-at (from nose tip), length, mass,
  mass-with-children, CG from the component's front — override-aware, the
  same numbers the property panel shows
- Every schema attribute for the component types present in the design
  (shape, diameters, thickness, fin geometry, deploy events, …), in **your
  preferred units** (headers say which), honoring your diameter-vs-radius
  preference; select fields export their labels ("Haack", not "haack")

The XLSX side reuses the v0.036 typed-cell writer (frozen bold header,
autofilter) — numbers stay numbers in Excel/Sheets.

## 4. Transition shape not drawn (always conical) — FIXED ✅
## 5. Nose cones always drawn as ogive — FIXED ✅

You were right, and it was worse than it looked:

- **2D**: the nose was a fixed Bézier "ogive-ish" swoosh and the transition a
  straight polygon — neither ever read the shape.
- **3D**: the nose *did* read the shape but with simplified math (power series
  hard-coded to exponent 0.5, no shape parameter anywhere); the transition
  was always a straight `CylinderGeometry` cone.

**The fix**: one shared profile module (`src/tree/shapeProfile.ts`) that is a
line-for-line port of the kernel's own `Transition.Shape.getRadius()`
equations — all six shapes, the shape parameter, secant ogives, and the
**clipped-transition** behavior the engine actually simulates for
ellipsoid/power/Haack transitions (the profile continues the virtual nose
shape, cut at the fore radius, solved by the same binary search). Both the 2D
schematic and the 3D lathe now sample this module, so **the drawing is the
geometry the physics flies**. Verified live: ogive → conical → haack noses all
draw distinctly and CP/stability move in step; ogive vs conical transitions
visibly differ in 2D; 23 new unit tests pin the math to analytic anchors.

**Bonus**: nose cones and transitions now expose the **Shape parameter** field
in the editor (only for shapes that use it; capped at each shape's legal max,
blank = OpenRocket default). Your RockSim imports carry real parameter values
— now you can see and edit them.

**Two kernel-fidelity gaps found while in there** (queued for the next engine
rebuild — not visual, both small):

1. The bridge never passes `shapeParameter` to *transitions* (nose cones are
   fine), so a transition with a non-default parameter simulates with the
   default. Rare case; 3-line bridge fix + rebuild ritual when we next touch
   the engine.
2. Our `.ork` export wrote `<shapeclipped>false</shapeclipped>` while the
   kernel simulates clipped ellipsoid/power/Haack transitions. The export now
   writes the truth (shipped in v0.041); honoring an *imported* `false` needs
   the same bridge touch as #1.

## 6. 2D/3D model & image export — CORE SHIPPED ✅, format matrix below for discussion

I studied your RockSim exports in `docs/2D_3D_Models/`. What "with data" means
there: a text header — name, length/diameter/span, mass, CG, CP, margin. Agreed
that recreating the 1990s format zoo would be a mistake. **Shipped now (v0.041):**

- **⬇ SVG on the 2D view** — the side view with that data header, white
  background, CG/CP markers. The SVG's width/height are **physical
  millimetres**, so it *prints at true 100 % scale* AND scales freely on
  screen — one modern file replaces RockSim's separate "2D model" and
  "100 % scale" exports. (Your Darkstar would print as a 1:1 banner,
  exactly like the RockSim example.)
- **⬇ PNG on the 2D view** — the same drawing rasterized at 3840 px wide.
  One PNG replaces the bmp/xbm/xpm/pnm zoo; anything that wants those dead
  formats can convert from PNG.
- **📷 PNG on the 3D view** — snapshot of the current 3D render (rotate/zoom
  first) with the same data header composited above it.
- **.obj 3D model** — already existed since earlier (Save/Export menu), same
  geometry the 3D view renders.

**Format matrix — my recommendation on the rest (your call):**

| RockSim format | Verdict | Modern replacement |
|---|---|---|
| 2D SVG (both variants) | ✅ shipped | one physically-sized SVG |
| png/jpg/bmp/xbm/xpm/pnm | ✅ shipped (PNG) | PNG only; others are dead |
| obj + mtl | ✅ have OBJ | could add .mtl colors — cheap, say the word |
| wrl (VRML), iv (Inventor), oogl, rib (RenderMan), pov | ❌ skip | all effectively dead ecosystems |
| x3d | ❌ skip | VRML's successor but never took off |
| 3D image | ✅ shipped (📷 PNG) | |

**Worth ADDING (not in RockSim) — say which you want:**

1. **glTF (.glb)** — *the* modern 3D interchange format: drag-and-drop into
   Windows 3D Viewer, PowerPoint, macOS, Blender, three.js, and web viewers,
   with colors/materials included (OBJ needs a sidecar .mtl). Moderate
   effort; three.js has an exporter we can bundle.
2. **STL** — if anyone wants to 3D-print a display model; trivially derived
   from the same meshes, but our shells aren't guaranteed watertight, so I'd
   label it "display/reference, not print-ready".
3. **Resolution picker for PNG exports** (2K/4K/8K) — the current default is
   3840 px (already far past RockSim's caps). Cheap.
4. **PDF one-pager** — the 2D drawing + data as a print-ready PDF for cert
   packets. More work; the SVG printed from a browser covers most of it today.

My instinct: glTF yes, STL cheap-yes with the caveat label, PDF only if a
cert reviewer actually asks for PDF.

**One caveat from the live check**: the 📷 3D snapshot button verified
code-wise (and the 2D SVG/PNG were verified end-to-end, content inspected),
but my browser session's GPU wedged before I could click-test the 3D snapshot
— the *live v0.040 site* showed the same wedge, so it's environmental, not
the build. Please give 📷 PNG one click in your smoke test.

## 7. Issue tracking repo + in-app reporting — RECOMMENDATION 💬

**Recommendation: give the tool its own public issues-only repo** (e.g.
`mtnmanak/online-openrocket-issues`), separate from both the website's repo
and this (private) code repo. Reasons:

- This code repo is **private** and should stay that way for now — you can't
  point public users at its issue tracker without opening the code.
- An issues-only public repo costs nothing, carries no code, and gives the
  tool a clean public home for bugs/feature requests with labels, search,
  and dedup — and its URL survives even if the code repo goes public later
  (issues can be migrated then).
- Mixing rocket-tool bugs into the website's tracker will bury both; the
  audiences barely overlap (a WordPress theme issue vs a CP calculation
  question).

**In-app mechanism** (next session, once the repo exists — I need its URL):
a "🐞 Report a bug / request a feature" entry in the Guide dialog and the
header overflow, opening a prefilled GitHub new-issue link (title, app
version, browser, current aero model — no design data unless the user pastes
it) plus a `mailto:` fallback for people without GitHub accounts. Zero
backend, works offline-installed. Create the repo (Issues enabled, README
pointing at the app, 2 issue templates — bug / feature) or say the word and
I'll spec the templates for you to paste in.

---

## Waiting on you

1. **Smoke-test v0.041 live** — especially one click of the 3D 📷 PNG button.
2. **2D/3D export follow-ups**: glTF / STL / PNG-resolution picker / PDF —
   which ones? (§6 matrix.)
3. **Issue repo**: create `online-openrocket-issues` (or name your
   preference) and paste me the URL; I'll wire the in-app links.
4. Queued from v0.040: the declined-shroud-conversion prompt re-asks on every
   import (flagged 2026-08-05); still open if it bothers you.
