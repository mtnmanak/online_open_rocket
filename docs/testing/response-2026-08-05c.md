# Response — issues-2026-08-05c.md (→ v0.036)

## 9 (follow-up). Model terminology — **you were right; ONE pulldown now**

Your reading was correct and the layout was the problem: "Classic" appeared
twice meaning two different things (the model family in the pulldown, and the
Kbf refinement under it). The checkbox is gone. Preferences → Aerodynamics is
now a single pulldown with four explicit, honest choices:

1. **OpenRocket — Extended Barrowman (exact desktop parity)**
2. **Rogers Modified Barrowman (Kbf) — the default**
3. **Auto — Rogers Kbf, switching to our supersonic model past Mach 0.9**
4. **Supersonic — our extended model at all speeds (validated to Mach 4.6)**

To answer the terminology questions directly: yes, Kbf is on by default; the
old checkbox was the Kbf on/off switch (option 1 vs 2 above); and no, they
are not both "classic" in any meaningful sense — that label is retired.
One semantic note: under **Auto**, subsonic flight now uses Rogers Kbf (your
preferred model) rather than plain Extended Barrowman — that's the coherent
reading of "Kbf is our default."

**And yes — it's our model.** Agreed on all counts: no RASAero code exists
anywhere (closed source; even their equations were never published — our
supersonic model is an open-literature build from the sources they cite plus
NACA/NASA/AGARD material beyond them, validated against wind-tunnel data
directly). Every user-facing "RASAero-class" label is gone — the UI now says
"our supersonic/extended model"; the launch report says "Supersonic (our
extended model)". The guide keeps its RASAero references as the historical
inspiration and comparison target, as you suggested.

## Mixed-motor combination batching — **your symmetry analysis is right; my take before building**

You're correct that symmetry collapses the space dramatically, and I'd add
one refinement: the symmetric options are exactly the ways to partition the
cluster's tubes into opposite-pair groups —

- 2-motor, 3-motor, 5-motor: identical motors only (no combination batching
  possible — nothing to build).
- **4-motor: 2+2** (opposite pairs) — the first real combination case.
- **6-motor: 3+3** (alternating) — and 2+2+2 is also geometrically symmetric,
  but three different motor types in one cluster is exotic; I'd cap at two
  groups.
- 8+: 4+4 / 2+2 pairs exist but the sim count and the audience both shrink —
  agree with capping at 4- and 6-motor clusters with an explanatory note.

**How it maps to our engine** (the one structural requirement): one cluster
mount = one motor type, so a 2+2 flight is modeled as **two 2-tube "double"
cluster mounts** on the same centerline — which the kernel already flies
correctly today (that's the documented mixed-array pattern from the staging
work). The batch feature would:
1. Detect a design with two same-stage cluster mounts (or offer to SPLIT a
   4-ring/6-ring mount into the equivalent pair layout for the batch — I
   prefer offering the split, so you can keep designing with one 4-ring
   mount and let the batch do the bookkeeping).
2. Let you pick a candidate set per group with the existing filters.
3. Fly the cross-product (20×20 = 400 sims ≈ 30–60 s — fine), grade with the
   existing acceptance criteria, and add a motor column per group to the
   table/CSV/XLSX.
4. Enforce your symmetry rule by construction; skip airstarts entirely
   (agreed — that's a hand-tuned scenario, not a batch).

Estimate stands at about a session. **Say go and it's next.**

## Batch aero model — **built, default Auto**

Exactly as you reasoned: the batch dialog now has an **aero model** pulldown
(same four options as Preferences), defaulting to **Auto** — each candidate
motor flies Rogers Kbf and re-flies wholly supersonic when ITS OWN flight
projects past Mach 0.9, so a mixed field of G through M motors each gets the
right physics. Each row's model is recorded (the "Aero model" CSV/XLSX
column shows classic vs auto-supersonic per motor). The design's own model
setting is untouched — the batch restores the engine handle exactly as it
found it.

## XLSX export — **recommendation: yes, and it's built (the lean way)**

Your date-mangling concern is the real argument: CSV has no types, so
Excel/Sheets guess — and "6-8" delays or "3-ring" clusters become dates or
get truncated. Recommendation details:

- **Built without a spreadsheet library.** The usual JS xlsx libraries add
  0.5–1 MB to the bundle (which the PWA precaches). An .xlsx file is just a
  zip of small XMLs, and we already bundle the zip library for .ork files —
  so ours is ~150 lines and adds ~2 KB. Both saved simulations and batch
  results now have a **⬇ XLSX** button beside CSV.
- What you get over CSV: **typed cells** (numbers are numbers, text is text
  — nothing is ever reinterpreted), a **bold frozen header row**, an
  **autofilter** on the whole table, and **content-sized column widths**.
  Deliberately no formulas/charts/multi-sheet — that's analysis, which is
  yours to do in the spreadsheet.
- CSV stays for anything programmatic.

## RockSim gaps — **closed (the known list)**

- **External pods now import AND export.** RockSim `ExternalPod` ↔ our pod
  sets, using the desktop's exact semantics (single-instance pods, radius
  from the parent centerline, angles in radians) — and a RockSim
  **Detachable** pod becomes a strap-on **booster (parallel stage)** with
  its own flight branch, both directions. Multi-instance pod sets export as
  N separate pods around the ring, exactly like the desktop.
- **Fin cant angle** now round-trips (the desktop only exports it and drops
  it on import — we do both).
- **Spill holes** were closed in v0.034.
- **Ring tails stay unsupported deliberately**: desktop OpenRocket has no
  ring-tail component and its RockSim importer drops them too — ours says so
  in the import notes. Supporting them for real would be a kernel physics
  feature (an annular wing), not a file-format fix; it can go on the backlog
  if you ever hit one in the wild.
- Not gaps, for the record: RockSim's turbulence model and its subsonic-only
  Mach handling are physics differences, not file-format losses — nothing to
  import.

If you want the full systematic sweep anyway (checking for gaps we DON'T
know about), the half-session audit offer from last time stands.

## Test state

App 149 + engine 23 = **172 tests green** (4 new: xlsx writer ×2, pod
round-trip, cant round-trip — plus the batch/model changes covered by
existing suites). v0.036 zip: `deploy/online-openrocket-v0.036.zip`.

## Waiting on you

1. **Combination batch** — go/no-go on the design above (incl. the
   "offer-to-split a 4-ring into 2+2" approach).
2. **Full RockSim audit sweep** — still optional.
3. The shroud calibration flight, whenever a launch window opens.
