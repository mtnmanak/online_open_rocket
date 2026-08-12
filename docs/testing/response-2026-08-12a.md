# Response — issue batch 2026-08-12a

**Shipped as v0.044** (2026-08-12). Source batch: `docs/testing/issues-2026-08-12a.md`.

349 tests green (326 app + 23 engine, up from 270). No engine rebuild — nothing in
`packages/engine` or `engine-java` was touched, so the differential test is unaffected.

---

## Quick status

| Item | Status |
|---|---|
| JOB 1 — adopt the Nav Contract | ✅ Shipped |
| JOB 2 — keep `version.json` honest | ✅ Shipped — **v0.044**, see below |
| `src/version.ts:30` inert-prefill claim | ✅ Fixed (and the `&tool=` param removed everywhere) |
| Resolution picker praise | 🙏 Noted, nothing to do |
| Oversized parts / splitting for 3D printers | 💬 **Recommendation below — needs your go/no-go** |
| DXF export | ✅ Shipped |
| STEP / CNC scope | 💬 **Recommendation below — needs your go/no-go** |
| `Wildman_Darkstar_3_-3d.png` | ✅ Found & reviewed — one observation below |
| Elliptical fin outline bug (found while doing the above) | ✅ Fixed |

**Tell the site:** Online OpenRocket is now **v0.044**, so the Online Tools card can be updated.

---

## JOB 1 — the Nav Contract

Adopted per `docs/chrome-spec.md` in the site repo. I read the spec rather than re-deriving
the design, and followed the `online_open_rocket` checklist in §6.

**What's new**
- `packages/app/src/services/useMmrNav.ts` — the React hook from spec §5, adapted.
- `packages/app/src/components/SiteBand.tsx` — the band and the (opt-in) footer strip.
- `SITE_MENU` is gone from `App.tsx`; the old `.site-nav` CSS is replaced by a
  `.mmr-band-*` block that keeps the tracked-caps, orange-on-hover voice it had.

**All seven of the spec's §2 MUSTs pass**, each verified against the code by an independent
reviewer, not just asserted:

1. **Fail open** — the band is wrapped in an error boundary placed one level *below* the
   component that reads the contract, so a throw while mapping `nav[]` is actually caught.
   Every `localStorage` and `JSON.parse` and `fetch` path is guarded; a corrupt cache, a
   quota error in private browsing, a non-JSON 200, a `null` body and a CORS failure all
   land on the fallback silently. A test renders a deliberately broken contract beside a
   sibling element and asserts the sibling survives.
2. **Baked fallback, rendered synchronously** — no loading state exists in the type at all.
3. **Stamps** — `data-mmr-menu-version` and `data-mmr-source` on the band, read from the
   object actually being rendered.
4. **No global keyboard shortcuts** — search is a plain GET `<form>` with zero handlers.
5. **Namespaced** — every selector is `.mmr-band-*`; zero writes to `:root`.
6. **`target="_top"`** — brand, every menu link, the search form, the footer. *One scoped
   exception, below.*
7. **Cache** — `localStorage` key `mmr-chrome:v1`.

### Two judgment calls I made, both worth your eye

**1. The fallback is today's contract, not the old menu.** The spec says the fallback
should equal what the tool ships today, but what it shipped was eight WordPress URLs that
now 301 (`/index.php/builds/` → `/builds/`) with labels that no longer match the site
("HPR Primer" is "Rocketry U" now). Shipping those as the offline experience would be
strictly worse than shipping the current menu. So the baked literal is a snapshot of the
live contract, `menuVersion 480f0531d406` carried verbatim so the stamp stays honest.
Practical consequence: **offline users see exactly the same menu as online users.**

**2. GitHub links in the footer open a new tab, which is a letter-violation of MUST 6.**
The contract's footer includes `Bugs & requests` → github.com. MUST 6 says `target="_top"`;
your standing ruling in `docs/feedback-tracker.md` says GitHub links open a new tab *"so
the user is not completely taken away from the site"*. Those conflict. I gave your ruling
precedence, scoped as narrowly as possible: **github.com and its subdomains only, in the
footer only.** The band's own `nav[]` is 100% `_top`. MUST 6's stated purpose — escaping an
embedding frame — still holds, because `_blank` escapes too. It's recorded in a comment at
the call site. **If you'd rather the spec win, it's a two-line revert.**

### One open question for you

`CONTRACT` points at **`mountainmanrockets.pages.dev`**, verbatim from the spec's reference
hook. I used it deliberately rather than "improving" it, because four tools disagreeing
about the contract URL is exactly the failure the adjudication exists to prevent.

But `www.mountainmanrockets.com/chrome/nav.v1.json` serves **byte-identical content with
identical CORS headers** — I checked both — and now that `cutover: true`, www is arguably
the canonical host. If you agree, it's a one-line change here **and** a spec revision in the
site repo so all four tools move together. Your call; I didn't want to make it unilaterally.

---

## JOB 2 — `version.json`

The plumbing was already sound: `.github/workflows/deploy.yml` copies `version.json` into
`dist/` before the wrangler publish, and I confirmed the live endpoint serves it correctly
(`https://online-open-rocket.pages.dev/version.json` → `200`, `application/json`, not the
app's HTML). Bumped to **0.044** with a user-facing note, as part of this release.

---

## The inert-prefill correction

You were right, and it was in more places than the one line you flagged. GitHub issue forms
prefill only `input` and `textarea` fields — `tool` is a **dropdown**, so `&tool=` was inert
everywhere it appeared. Fixed in all five places:

- `App.tsx` — the `&tool=` parameter is deleted, with a comment citing
  `docs/feedback-tracker.md` so nobody re-adds it. `&version=` stays; that one works.
- `version.ts:30` — now reads *"the app version prefilled; you pick which tool from a
  dropdown."*
- `data/userGuide.ts` and `docs/user-guide.md` — `&tool=` removed from both issue links in
  each mirror. **`?template=` survives everywhere** — dropping it silently breaks every
  prefill, which is the trap your doc warns about.
- `docs/feedback-tracker.md` itself — its blockquote told every future session that this
  repo passes an inert `&tool=` and makes a false claim in its release notes. Both are now
  untrue, so I rewrote it in the past tense. **You may want the same correction in the site
  repo's `issue-tracking-consolidation.md`** if it carries the same wording — I didn't touch
  the site repo.

---

## §6 — 2D/3D model & image export

### The resolution picker

Noted, thank you. No change.

### The component STL exporter — your splitting question

> *"I am not sure if it is possible to offer to split parts for users … or if that is simply
> beyond the scope of what we can provide. Give me your thoughts."*

**Build it. It is in scope, and it is unusually cheap here.** The reason is architectural:
every revolved printable part — nose, transition, tube, coupler, ring, bulkhead, tube fin —
is generated by `revolveProfile()` from **one closed 2-D `(x, r)` loop**. Nothing is a mesh
boolean. So "split it and add a mating shoulder" is **clipping a simple polygon against a
vertical line and appending four points**, then re-revolving. Watertightness still holds by
construction, exactly as it does today. A general "split any STL" feature would be a CSG
library and a manifold-repair nightmare. This is neither.

**Your printer, your rocket, the actual numbers.** A 3" (76.2 mm) airframe with a 4:1
tangent ogive is 304.8 mm of cone; add a 1-caliber shoulder and the printed part is
**381 mm**. The H2D's Z is 325 mm, so ~317 mm usable after margin — **you miss by 64 mm.**
At 5:1 it's 457 mm and you miss by 140. You were right, and nothing on the consumer market
prints that part upright:

| Printer | Usable Z | 3" 4:1 nose (381 mm) |
|---|---|---|
| Bambu H2D | 317 mm | ❌ 64 mm over |
| Prusa XL | 352 mm | ❌ 29 mm over |
| Bambu X1C / P1S | 248 mm | ❌ |
| Prusa MK4S | 212 mm | ❌ |
| Ender 3 | 242 mm | ❌ |

**The diagonal doesn't save you.** A 4:1 tangent ogive's steepest wall is only 14.3° off
axis, so it can lean ~30° before anything overhangs — but leaning raises the base as fast as
it lowers the tip. It buys **6–10%**: 381 mm still misses the H2D by 36 mm. Laying it
near-flat does fit the bed diagonal, but that print has support scarring down one whole side
and the layer lines run *along the axis* — the direction the ejection charge loads the
shoulder. The tool should not suggest it. (For **fins** the opposite is true: a fin that
won't lie flat stands on its root edge and fits everywhere. Orientation genuinely solves
fins.)

**What today's exporter already handles fine:** couplers, switch bands, rings, bulkheads,
engine blocks, short inner tubes and most fins. It falls over on exactly one class of part —
**nose cones and long transitions at 3" and up.** That's a narrow, well-defined target.

**The recommended mechanism: axial segmentation with an internal tapered spigot** — what you
described doing in Fusion. Cut at `x = c` inside the body span only (never inside a shoulder;
see the constraint below). On the fore piece, build the male spigot as a **tapered offset of
the actual bore**, so it registers along its whole length rather than just at its root, and
the flat annulus at the cut becomes a hard axial stop — the land is the datum, the taper is
the register. The aft piece needs no modification at all; its bore already *is* the mating
surface.

- **Clearance: 0.15 mm per side** (0.30 on diameter) as the default, adjustable 0.05–0.40.
  CA seizes at 0.15 and wants ~0.05; 30-minute epoxy is happiest right where the default is.
- **Shrinkage mostly cancels** — 0.7% on a 72 mm bore is 0.5 mm, five times the clearance,
  but both halves shrink equally *if printed in the same material on the same machine*.
  That sentence has to go in the README inside the export.
- **Spigot length** `max(0.25 × D, 12 mm)`, capped at 30 — 19 mm on a 3" nose. Structurally
  it barely matters: a 25 mm spigot gives 5,671 mm² of lap area against 466 mm² for a butt
  joint, **12× more**. Length is about self-alignment during glue-up, not strength.
- **Your 381 mm nose becomes 2 segments of 190.5 mm on the H2D** — and, notably, also on an
  X1C, with 32 mm to spare.

**On asking for printer volume — yes, once, in Preferences.** A `printer` field on the
existing prefs object (same optional-key, tolerant-merge pattern as everything else), 8
presets plus Custom X/Y/Z, stored in **metres** and surfaced through the existing length-unit
plumbing so you can type it in inches if you like. Splitting should be **offered, never
silent**: the 🖨 button stays exactly as it is when the part fits, and gains "— 2 pieces"
plus an amber line ("381 mm — 64 mm too long for your Bambu H2D") when it doesn't. The
multi-part export is a zip via `fflate`, which is already a dependency, with a README
carrying the print orientation, the clearance used, and the same-material warning.

**Registration:** for a nose cone it genuinely doesn't matter — a body of revolution has no
clocking. v1 should ship a plain round spigot and say so. It *does* matter for a tube
segment carrying a rail button or a vent hole, and v1 should **refuse to split those** rather
than emit parts that can't be aligned. A proper keyed joint later needs no CSG either — it
generalizes `revolveProfile` into `sweepProfile(profile, radiusAt(θ))`, about 20 lines that
inherit the watertightness proof verbatim. Worth writing in the ledger so nobody reaches for
a boolean library.

**The one real constraint:** watertightness is guaranteed only for a *simple* closed loop,
and the loop doubles back inside shoulder spans. So cuts must be restricted to the body span.
That's the single thing that could leak a non-manifold STL, and a per-segment
`isWatertight()` test is what catches it.

**Cost: one session** for pref + fit badge + axial splitter + zip + tests. If it runs short,
**the fit badge alone is worth shipping** — knowing your 381 mm nose is 64 mm too long
*before* you start a 12-hour print is most of the value.

**→ Say the word and I'll build it.**

### DXF — done

Implemented and shipped in v0.044. `services/dxfExport.ts` writes **AutoCAD R12 (AC1009)
ASCII DXF in millimetres**. R12 deliberately, not the newer LWPOLYLINE form: R12 is what
LightBurn, Carbide Create, Easel, Fusion's sketch import and every cutter's own software
read without complaint.

- **Fins** (trapezoid, elliptical, freeform) — **one closed contour** with the TTW tab merged
  into it, because CAM offsets a single closed path correctly whereas an outline overlapping
  a separate tab box cuts a slot through the root.
- **Centering rings** — outer circle plus the **true bore** taken from the motor-mount tube,
  derived identically to the STL path so the printed and machined parts can't disagree.
- **Bulkheads** — disc plus centre cross-hairs on the REFERENCE layer for the eyebolt.
- **Tube couplers / engine blocks** — annulus.
- Layers: **CUT** is the geometry; **REFERENCE** (root chord, centre marks) and **TEXT** (the
  label block) are guides you switch off before cutting. No calibration ruler — DXF is
  dimensionally exact, the ruler only exists in the SVG because printers rescale.

Button is **✂ DXF (CNC/laser, 1:1)** — a different glyph from the 📐 SVG template deliberately,
since they sit next to each other and do different jobs.

**Both guide mirrors document it.** 18 tests, including a from-scratch group-code parser that
proves the container is well-formed R12, that layers referenced by entities are defined, and
that a 0.1 m root chord emits exactly 100.0 mm.

### STEP / CNC — your scope-narrowing question

> *"no one CNCs a nosecone or body tube … If we limit the scope to commonly CNCed
> components, it would be easier. Give me recommendations."*

**Your instinct is right, and it narrows the problem far enough to change the answer.** Once
nose cones and body tubes are out, every part left is either flat (DXF already wins) or a
revolve of straight-line segments. And revolving a straight segment produces exactly one of
three analytic surfaces — cylinder, cone, or plane. **B-splines become provably unnecessary.**

So STEP here is a **~600-line, zero-dependency text writer** — not the opencascade.js WASM
chunk the previous session proposed. **I now recommend against OpenCascade**, with numbers:

- `opencascade.full.wasm` is **48.9 MB raw / 9.1 MB brotli**; the trimmed build is 7.1 MB.
- This entire app's `dist` is **4.2 MB**. The full build is ~12× the whole app.
- Worse, there's a specific trap: `vite.config.ts` precaches `**/*.{js,css,html,png,…}` with
  **no `.wasm`**. A lazy import would emit a `.js` chunk that *does* get precached while the
  `.wasm` it needs does **not** — a STEP button that looks available offline at Black Rock
  and fails when clicked. Confidently broken is worse than absent.
- (Licence-wise OCCT's LGPL-2.1 + Exception is compatible with our GPL-3; it's just not
  worth it. **Avoid `replicad` regardless — it's AGPL-3.0**, which would make this whole
  publicly-served tool AGPL.)

**But here is the finding that actually matters, and it's a "wait":**

> Against the parameters the app models **today**, a STEP centering ring carries no more
> information than a DXF ring plus a thickness number. A `centeringring` currently has two
> properties: outer diameter and thickness. DXF gives you two circles; you extrude once in
> Fusion and you have a parametric body that is *better* than the STEP import, because you
> keep the sketch.

STEP earns its keep the moment a part stops being a constant cross-section — a **stepped**
ring that registers into the tube ID, a **chamfered** bulkhead, anything with a **bolt
circle**. The app doesn't model those yet. So:

**Recommended order**
1. **DXF** — done, this release. Covers most of what you'd actually CNC, and for flat parts
   it is the *better* file, not a cheap substitute.
2. **Fabrication geometry, not file formats.** **Hole patterns first** — bolt circle, eyebolt
   hole, vent holes, all-thread pass-throughs — on rings and bulkheads. This pays off
   *immediately in the DXF* as inner contours, so you get value even if STEP never happens.
   Then the ring shoulder/step, then chamfers. **If I only get one thing from this list, this
   is it.**
3. **Then STEP**, one session, two builders (extruded polygon with inner loops; revolved
   piecewise-linear profile) sharing the *same* profile generators the STL writer uses — which
   is the guarantee the two exports can never drift.
4. **Never OpenCascade in this app.**

Why STEP goes third: after step 2 it carries information DXF cannot. Before step 2 its honest
release note is *"does the same thing as the DXF button."* If you'd rather have it sooner
anyway, say so — it's the same 600 lines either way, you just get less from it initially.

**On the e-bay sled — my position is don't make it a component.** OpenRocket's tree is a
*physics* model; every node contributes mass, CG and aerodynamics. A sled contributes mass,
which `masscomponent` already models, and nothing else. Adding a `sled` type means either
double-counting mass or building a second mass path, plus `.ork` round-trip problems (desktop
OpenRocket has no sled either). **But the thing you actually want is nearly free:** a sled is
three flat blanks — a deck (a chord of the tube bore), two end plates (a disc with two flats
and two rod holes), and bought rods. All 2-D profiles, which fall straight out of the DXF
builder that just shipped. So: a small **"fabrication blanks"** dialog, not a tree component.
It touches nothing in the physics model.

**When you test a STEP file, the check that separates it from a faceted mesh:** in Fusion,
`Inspect ▸ Measure` on the ring's outer face must report **"Cylinder, diameter 76.200"** — not
a vertex-to-vertex distance, and not 76.159. In SolidWorks the pass condition is that **no
Import Diagnostics dialog appears at all**; any offer to "heal" the body is a failure even if
healing succeeds.

*(For the record, on precision: our 96-segment revolve is off by 0.020 mm on a 3" bore and
0.041 mm on a 6". Nobody should sell you STEP on accuracy — the STL is dimensionally fine.
It's editability and non-constant cross-sections that matter.)*

### The Darkstar image

Found at `docs/2D_3D_Models/Wildman_Darkstar_3_-3d.png` (committed last session). The 8K
export is correct — header data intact (2130.298 mm, 78.74 mm, 6910 g, margin 4.34 cal),
geometry right, genuine 8K.

**One observation:** the rocket occupies roughly 20% of the frame, because the snapshot uses
whatever camera framing is on screen and a long thin rocket in a wide canvas leaves large
margins. An **"auto-fit before snapshot"** option (or a trim-to-content pass on export) would
make those files much tighter — same pixels, much more rocket. Small job. Want it?

---

## Found while doing the above: elliptical fins exported the wrong shape

Not on your list, but it's a real bug and it's fixed in this release.

`solidMesh.ts` built the elliptical fin planform as `[root·u, height·sin(π·u)]` — a **sine
hump, not an ellipse**. I verified the ground truth directly in the OpenRocket 24.12 source
(`EllipticalFinSet.java:17-25`: `POINT_X[i] = (cos a + 1)/2`, `POINT_Y[i] = sin a`) — a true
ellipse, which `finTemplate.ts` had right all along.

**Consequences:** the exported STL enclosed `(2/π)·root·height` instead of `(π/4)·root·height`
— **19% less area** — and disagreed with the 1:1 SVG template of the same fin. For a
90 × 40 mm fin the curve passed through (22.5, 28.3) mm where it should pass through
(21.9, 34.3) mm.

**What it does and doesn't affect:** export only. Fin aerodynamics are computed by the Java
kernel from `rootChord`/`height`, never from these points, so **no simulation result changes
and the differential test is untouched**. But **if you printed or cut an elliptical fin from
v0.042 or v0.043, re-export it.** The STL, the DXF and the SVG template are now provably one
curve — a test asserts they agree vertex-for-vertex, and the old value would now fail loudly.

---

## Verification

- **349 tests** green (326 app + 23 engine). Suite grew 270 → 349.
- **Build clean** (`tsc -b && vite build`); precache unchanged at **21 entries**.
- **Adversarially reviewed.** Both features went through independent verifiers that re-derived
  the claims rather than trusting the implementers. They found **13 real defects pre-release**
  — including the elliptical-fin bug above, a `parseNav` case where one legal site-side edit
  (an empty `footer.links`) would have pinned this tool to its offline fallback **permanently
  and silently**, and duplicate vertices in the DXF tab contour that would have broken CAM
  kerf compensation on the most common tab anchorings. All fixed and re-verified.
- **PWA offline test, in Chrome on the built `dist` with the service worker installed:**
  - Killed the origin server outright and reloaded → **the app came up entirely from the
    precache**: rocket drawn, component tree (13 rows), vitals strip, Launch button, and the
    band with all 8 links and the footer strip. Nothing about the band affects the offline
    guarantee.
  - **The service worker does not swallow band navigations.** Its scope is the app origin, so
    a cross-origin navigation is never routed through it — confirmed structurally *and* by
    navigating: the search target `…/search/?q=darkstar` loaded the real
    *"Search — Mountain Man Rockets"* page, not the app shell.
  - Cleared the cached contract → reload → refetched and rewrote the cache correctly.
  - **Not exercised in-browser:** the true no-internet-at-all case, because I'm not going to
    take your network down. It's covered by unit tests (a rejected fetch leaves the
    fallback/cache standing and does not poison the cache), and it matters less than it
    sounds because **the baked fallback is byte-identical to the live contract** — an offline
    user sees the same 8 links either way.
- Live band verified against the real contract before the offline run: `data-mmr-source=live`,
  `data-mmr-menu-version=480f0531d406`, all links `target="_top"`.

---

## Waiting on you

1. **Splitting oversized parts** — go/no-go. One session for the useful version.
2. **Hole patterns on rings and bulkheads** — the highest-value item on the CNC list, and it
   improves the DXF that just shipped even if STEP never happens.
3. **STEP** — build it after (2), or sooner if you'd rather have the button.
4. **Contract host** — keep `pages.dev` per spec, or move all four tools to `www`?
5. **GitHub-link exception in the footer** — your feedback ruling currently beats chrome-spec
   MUST 6. Confirm, or I'll revert to `_top`.
6. **Auto-fit the 3D snapshot** before export?
7. Site repo may need the same past-tense correction to the `&tool=` note in
   `issue-tracking-consolidation.md`.
