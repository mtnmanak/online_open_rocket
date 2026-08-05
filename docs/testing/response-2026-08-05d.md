# Response — issues-2026-08-05d.md (→ v0.039)

## 1. 2+2+2 (three motor types) in a 6-motor cluster — **BUILT**

You're right, and your framing sharpened the geometry: the fully general way
to mix motors in a 6-ring is to treat it as **three opposite-tube pairs** —
every pair is individually thrust-balanced, so ANY per-pair assignment is
symmetric. That one mechanism covers everything at once:

- **2+2+2** — three different motors in pairs (your affordable-hardware case),
- **4+2** — two pairs of one motor + one pair of another (falls out for free),
- and redundantly 6-of-one (already covered by the normal single pass).

The batch dialog now shows a second checkbox on 6-motor clusters:
**"mixed 4+2 / 2+2+2"**, alongside the existing "mixed 3+3". It flies every
candidate *multiset* across the three pairs, labels rows like
"4× J350W + 2× J270W" or "2× A + 2× B + 2× C", tags each row's Motor config
(mixed 4+2 vs mixed 2+2+2), sorts the CSV by config group, and gives each
config its own XLSX tab. The split geometry is exact (each pair lands on the
original tube positions — unit-tested to nanometers, including rotated and
re-spaced rings).

**One honest warning, on the dialog too:** this mode grows cubically. 10
candidates → 210 extra flights (fine); 30 candidates → ~4,930 (lunch break).
The count line tells you before you commit — filter candidates first.

## 2. Fin rotation + collision — **BOTH BUILT (and your file verified)**

What your Ultra Neon file revealed: RockSim doesn't store an interleave
angle at all — both your fin sets are at RadialAngle 0 in the file, and
RockSim just draws them apart. So this needed two real features, not an
import fix:

- **Rotation field on every fin set** (trapezoidal, elliptical, freeform,
  tube fins): rotates the whole set about the body axis, in degrees. It's
  wired through to the real physics — the kernel has always modeled fin-set
  base rotation; the engine bridge just never exposed it, so this release
  includes an engine rebuild (differential re-verified 3×, bit-identical at
  rotation 0, all 258 golden lines stable). The 3D view and the aft view
  draw the true angles; the 2D side view stays a stylized projection — check
  interleaving in the AFT view, that's what it's for. Round-trips through
  .ork (the desktop's own `<rotation>` tag — desktop OpenRocket will read
  it!) and RockSim (`RadialAngle`).
- **Auto-interleave on import**: any fin-type set that axially overlaps an
  earlier set at the same angle gets rotated by half the other set's pitch,
  with an import note saying exactly what moved. Your actual Ultra Neon now
  imports with the tube fins at 30° between the straight fins — verified
  against the real file. The same logic runs when you ADD a second fin set
  in the editor: it defaults between the existing fins instead of on top of
  them. Full free-form collision detection during editing (drag two sets
  into each other) is not attempted — the auto-default plus the aft view
  covers the practical cases; say the word if you want a live warning too.

A nice discovery while verifying: your Ultra Neon file contains a
hand-rolled "Camera Shroud" built as a 1-fin freeform set — which is
almost exactly how v0.034's real shroud component models one internally.
Consider replacing it with the native Camera shroud component: you'll get
the Hoerner drag model and the as-built mass field on top of the strake lift
you were already approximating.

## Test state

App 158 + engine 23 = **181 tests green** (5 new: pair-split geometry,
.ork/RockSim rotation round-trips, Ultra Neon de-collision regression).
Engine rebuilt for the rotation bridge — differential 258 lines stable ×3,
engine vitest green. v0.039 zip: `deploy/online-openrocket-v0.039.zip`.

## Waiting on you

1. Live-fire the new pieces: your Darkstar (6-ring batch, both mixed modes)
   and Ultra Neon (rotation/interleave) are the perfect test articles.
2. Still open from earlier: the RockSim full-audit sweep (optional), and the
   shroud calibration flight.
