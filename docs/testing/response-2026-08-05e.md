# Response — issue batch 2026-08-05e (v0.040)

All six items are built and shipped in **v0.040**. Item-by-item below; a few
judgment calls are flagged for your review.

---

## 1. "Auto-align multiple fin sets" button — DONE

Select any fin set that shares its tube with another fin set and the property
panel shows **🧭 Auto-align fin sets**. It rotates the tube's overlapping sets
so their fins interleave with the **widest possible angular clearance** — the
same physics the importer's auto-interleave uses, but on demand while you
build:

- 6 tube fins + 3 straight fins → straight set lands at 30° (the Ultra Neon
  arrangement).
- Two 4-fin sets → 45°.
- It generalizes: three or more sets, mixed fin counts, anything — the button
  grid-searches each later set's rotation for the maximum-minimum clearance
  against all the sets before it.

The first set on the tube keeps its rotation (your reference), sets that don't
overlap axially are left alone, and a set already sitting at an equally-clear
angle isn't touched — so the button is safe to press repeatedly. Every rotation
it makes is reported in the banner and is one Ctrl+Z from undone.

## 2. Tube-fin collision limit bypass — FIXED

Root cause: the collision limit only ever existed inside the **auto** rule
(blank diameter = kernel computes touching tubes). Once you typed explicit
values into both fields, the slider ranges came from the static schema
(0–100 mm regardless of fin count or body size) — no limit was being
"bypassed"; none existed on the explicit path.

Now both fields carry live cross-limits, however the values got there:

- **Outer diameter** caps at the touching diameter for the current fin count
  — slider AND typed input (typing above it is rejected with the red border,
  the slider simply stops at the limit).
- **Fin count** caps at how many tubes of the entered diameter physically fit
  around the body.

Also fixed while in there: a 2-tube-fin set used to compute an infinite auto
radius (division by zero — sin 90° = 1); below 3 fins the auto rule now
follows the kernel (body radius), and 1–2 tubes are exempt from the collision
cap since they can never meet each other around the tube.

## 3. Blank outer-diameter shows nothing — FIXED

A blank tube-fin outer diameter now shows the auto-computed value **grayed in
the field** (e.g. `auto: 98`), in your current units and radius/diameter
preference — the number you need to actually build the fins. It stays a
placeholder (auto keeps tracking fin count / body diameter changes) until you
type over it; clearing the field returns to auto. Same pattern as the
max-motor-length override field.

## 4. Camera-shroud detection on import — DONE

Opening a file (RockSim, .ork, or RASAero) that contains a **one-fin freeform
set named like a shroud** ("Camera Shroud", "shroud", "camera", "fairing")
pops a dialog: *"It looks like this file has a camera shroud… convert it to
the native camera-shroud component?"* One click converts — nothing else to do:

- Length/height come from the freeform outline, width from the fin thickness.
- Mass uses the set's override mass if the file had one (RockSim KnownMass
  does come through), otherwise an estimate from outline area × thickness ×
  material density — the conversion note shows the number so you can sanity-
  check it against the real part.
- Shape defaults to **half-round** (the usual camera cover) — flip to
  streamlined/box in its properties if the real shroud differs.
- Same component id, so nothing else in the tree moves; Ctrl+Z undoes it.

"Keep as freeform fin" declines. Judgment call: declining isn't remembered in
the file, so reopening the same file asks again — say the word if that gets
annoying and I'll store a "leave it alone" marker in the .ork.

## 5. Sub-minimum diameter rockets — DONE

You could *almost* model this before (body-tube mount + zero wall), but the
motor browser filtered motors by the tube's **bore** — so the one motor the
rocket is built around was invisible. That was the actual blocker.

New checkbox on a body-tube motor mount: **"Sub-minimum: motor case is the
airframe"**. With it checked, the motor browser fits motors against the
tube's **outer** diameter — a 98 mm case in a 98 mm-OD tube shows all the
98 mm motors. Covers both build styles you described:

- **Fins on a commercial case**: body tube = case OD and length, wall 0,
  fins attach to the tube as usual.
- **Propellant cast into the airframe**: same thing — the tube IS the case;
  import your EX motor file (item 6) with the case's diameter.

Modeling guidance (also in the new guide section): leave the wall at 0 unless
the airframe adds real structure on top of the case, because the motor file's
weight should include the case — it flies. The aft view now draws the motor
circle in body-tube mounts too (it previously only drew motors in inner
tubes), and the flag round-trips through .ork as an extension tag (desktop
warns-and-ignores it; RockSim has no equivalent so it doesn't survive a .rkt
round-trip).

Note the sim itself never gated on motor fit — fit filtering was always
advisory, in the browser only. So this is purely a workflow unlock, no
physics change.

## 6. EX motor import (.eng / .rse) — WAS ALREADY THERE, NOW FINDABLE + FOLDERS

Import of RASP `.eng` and RockSim `.rse` motor files has actually been in the
app since early July — the "⬆ Import .eng/.rse" control inside the motor
browser's filter row. It clearly failed the findability test, so v0.040:

- The mount button is now **"🔎 Browse motors / import EX (.eng, .rse)…"**.
- **Multiple files at once** — the file picker now takes several.
- **📁 Import EX folder** — point it at the folder where you keep your EX
  motor files; every `.eng`/`.rse` inside is added in one go, with a summary
  of what imported and what was skipped (bad files are reported by name, they
  don't abort the batch).
- New guide section: **EX motors & sub-minimum builds**.

How the library works (unchanged): imported motors persist in this browser's
local storage (nothing is uploaded — curves stay on your machine), appear
under manufacturer **EX** with the file's real manufacturer kept as a detail,
filter/sort/simulate like any database motor, and delete via the 🗑 button
when selected. `.rse` files with measured per-sample mass data use it
directly; `.eng` files get the impulse-proportional mass model (same as the
desktop). One caveat on "establish a folder the app watches": a browser PWA
can't watch a folder in the background — the folder button re-scans whenever
you press it, which is the closest a web app gets. If your separate motor-sim
tool grows an export format with more data than RASP carries, that's worth a
chat.

---

## Waiting on you

- **Shroud conversion defaults** (item 4): half-round shape + estimated mass
  — right defaults? And should declining be remembered per file?
- **Sub-minimum** (item 5): try it on a real project — especially whether
  fitting by outer diameter alone is enough, or you want the fit tolerance
  loosened further for odd-size EX cases (the +1 mm class snap still applies).
- Still open from earlier batches: RockSim full gap-audit (optional),
  camera-shroud calibration flight, live-fire of Darkstar/Ultra Neon combos.
