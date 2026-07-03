# RockSim parts-library merge report — 2026-07-03

Merged the RockSim component CSVs in `docs/materials/` into the bundled preset
database `packages/app/src/data/presets.json`, per the policy: **OpenRocket data
wins**; RockSim rows that duplicate an existing preset are dropped; genuinely new
parts are appended with `"source": "rocksim"`; same-part-number rows whose
dimensions disagree by more than 2 % are conflicts (OpenRocket kept, logged below).

Script: `packages/app/scripts/merge-rocksim-parts.mjs` (re-runnable;
`--dry-run` to preview, `--json <path>` for a machine-readable dump).
Preset count went **3449 → 3936** (+487). The diff to `presets.json` is
append-only — every pre-existing entry is byte-identical.

## Totals

| File | Rows parsed | Skipped | Duplicates | Conflicts | Added |
|---|---:|---:|---:|---:|---:|
| Body_tubeDATA.CSV | 233 | 1 | 167 | 2 | 64 |
| BulkheadDATA.CSV | 99 | 0 | 8 | 4 | 87 |
| CRDATA.CSV | 249 | 0 | 95 | 10 | 144 |
| EBDATA.CSV | 19 | 0 | 5 | 3 | 11 |
| LaunchLugDATA.CSV | 29 | 0 | 2 | 9 | 18 |
| TubeCouplerDATA.CSV | 116 | 0 | 67 | 2 | 47 |
| NoseconeDATA.CSV | 135 | 0 | 76 | 9 | 50 |
| TransitionDATA.CSV | 46 | 0 | 18 | 17 | 11 |
| ParachuteDATA.CSV | 59 | 0 | 12 | 1 | 46 |
| StreamerDATA.CSV | 9 | 0 | 0 | 0 | 9 |
| **Total** | **994** | **1** | **450** | **57** | **487** |

Of the 450 duplicates, 159 matched an existing preset by dimensions under a
different part-number format (e.g. RockSim `Estes EST 3086` = existing
`Estes BT-50 (yellow), 30355`; `Balsa Machining T4+34` = existing
`BalsaMachining T4Plus-34`); the other 291 matched an existing preset by part
number outright, or were repeats inside the RockSim files themselves (several
parts are listed twice, once with the description shuffled into the part-number
column).

The one skipped row is a literal `Apogee, "test", "13 mm"` test row in
Body_tubeDATA.CSV (line 201).

Added parts by manufacturer: Apogee 232, LOC Precision 55, ProLine Composites 45,
Aerospace Speciality Products 41, Estes 19, SEMROC 17, Madcow 15, Sunward Group
LTD 13, Fruity Chutes 10, Quest 8, Dino Chutes 8, Aerotech 6, Mach I 6, Public
Missiles 5, Always Ready Rocketry 3, Giant Leap 2, BalsaMachining 1, Retro Rocket
Works 1. (Dino Chutes, Fruity Chutes, ASP, Mach I, ProLine, Sunward, Aerotech and
Retro Rocket Works are new manufacturers in the database.)

## Skipped files

`FinsDATA.CSV`, `FinsDATA1.CSV`, `Fins3DATA.CSV` (fin sets) and
`MotorRetainerDATA.CSV` have no corresponding preset kind in our database, and
`GRAPHS.CSV` is not parts data — all five were skipped, per the task scope.

## Unit detection findings

Verified against OpenRocket 24.12's own RockSim loader
(`info.openrocket.core.preset.loader.*`) and empirically against known parts:

- **Lengths** — the per-row `Units` column: `0` or `in.` = inches; `1` or `mm` =
  millimeters; blank/`?` = inches (OpenRocket's assumption). Checked against
  Estes BT-20 (18 mm) rows, ARR 29 mm Blue Tube (1.14"/1.28" × 48"), and rows that
  state the size in the description.
- **Masses** — the `Mass Units` column uses RockSim mass codes: `0`/`oz.` =
  ounces, `1` = pounds, `2`/`g` = grams, `3` = kilograms. Empirical checks: Apogee
  CR 10-13 paper ring, mass `0.11` code `2` → 0.11 g matches its ~0.12 g
  geometric mass; Estes BNC-50K balsa cone, mass `0.13` code `0` → 3.7 g (0.13 oz)
  is right where 0.13 g would be absurd; Estes 12" plastic chute `0.07` code `0`
  → 2 g. A mass of 0/blank/`?` means "not cataloged" and is omitted (the preset
  then computes mass from geometry+density, same as the desktop).
- **TubeCouplerDATA.CSV header is mis-ordered**: it says `Mass Units,CG,Mass` but
  the data is actually `CG, unit-string ("g"), Mass` — provable because the "Mass
  Units" cell always equals half the row's length (it's the CG). Parsed
  accordingly. (OpenRocket's own loader mis-reads this column; our masses for ARR
  Blue Tube couplers etc. check out against catalog weights.)
- Nose/transition **shape codes** follow RockSim's enum (via OpenRocket's
  `RockSimNoseConeCode`): 0=CONICAL, 1=OGIVE, 2→ELLIPSOID (RockSim "parabolic"),
  3=ELLIPSOID, 4=POWER, 5=PARABOLIC, 6=HAACK; text names also appear and are
  mapped likewise. Thickness 0/blank = solid part → `filled: true`.
- A geometric mass-plausibility check (catalog vs density×volume for tube/disc
  parts, 8× tolerance) flagged **nothing** in the final run — good sign the unit
  decoding is right.

## Manufacturer alias map

Normalization: lowercase, strip non-alphanumerics, then:

| RockSim name(s) | Merged into existing |
|---|---|
| LOC, LOC/Precision, LOC Precision | LOC Precision |
| Balsa Machining | BalsaMachining |
| Madcow Rocketry | Madcow |
| Quest | Quest (existing "Quest Aerospace" also aliased) |
| Public Missiles (PML, Public Missiles Ltd.) | Public Missiles |
| Semroc | SEMROC |
| Estes (Estes Industries) | Estes |
| Giant Leap (Giant Leap Rocketry) | Giant Leap |
| Sunward Group LTD (Sunward) | kept as "Sunward Group LTD" (new) |

New parts take the existing dataset's display spelling so UI grouping stays clean.

## Material mapping

The CSVs carry material *names only* (no densities), so per policy: clearly
equivalent names were mapped onto the app's built-in materials
(`packages/app/src/data/materials.ts`); everything else was **omitted** (most such
rows carry an explicit catalog mass, which acts as a mass override anyway).

Mapped (bulk): Balsa→Balsa · Paper→Paper (office) · Cardstock→Cardboard ·
Kraft phenolic→Kraft phenolic · Blue Tube→Blue tube · Fiberglass / all "G10 …"
spellings→Fiberglass · Plywood / Aircraft plywood (Birch) / Aircraft plywood
(LOC)→Plywood (birch) · Polystyrene PS→Polystyrene · Polycarbonate→Polycarbonate
(Lexan) · Aluminum (Al)→Aluminum · Brass→Brass · Delrin Plastic→Delrin ·
Maple (Hard)→Maple · Styrofoam→Styrofoam (generic EPS).

Mapped (surface, chutes/streamers): Rip stop nylon & "4oz. Ripstop Nylon"→Ripstop
nylon · Mylar→Mylar · Polyethylene LDPE→Polyethylene (thin).

Mapped (shroud lines): "1/16 In. braided nylon"→Braided nylon (2 mm, 1/16 in) ·
"Carpet String (Apogee 29500)"→Braided nylon (2 mm, 1/16 in) *(judgment: nearest
built-in light braided line)* · "30 Lb. kevlar"→Kevlar thread 138 (0.4 mm)
*(judgment: 30 lb ≈ 130 N ≈ thread size 138)*.

Left unmapped (preset has no material entry; counts are source rows, most of
which were duplicates — only 25 *added* presets lack a material):
Spiral/Glassine (85), Fiber (43), lite ply (20), Polypropylene (15), Foam Core
Board (13), Flexible Urethane Foam soft/firm (8), Urethane (3), bulk
"Polyethylene LDPE" nose cones (2), Foam (1). None has a safe density-equivalent
in materials.ts ("lite ply" is much lighter than birch ply; "Fiber" means
vulcanized fiber; glassine-wrapped kraft has no built-in entry).

Data-quality judgment call: Semroc PN-14/18/24 **nylon** parachutes list their
canopy material as `G10 (PML 0.062")` — obviously bogus source data; mapped to
Ripstop nylon per their descriptions.

## Conflicts (same part number, dimensions differ > 2 %) — OpenRocket kept

| Kind | Mfr | Part | Difference |
|---|---|---|---|
| BodyTube | Semroc | LT-115160 | insideDiameter: OR 28.96 mm vs RS 22.1 mm; outsideDiameter: OR 30.99 mm vs RS 24.13 mm |
| BodyTube | Semroc | ST-3630 | length: OR 762 mm vs RS 76.2 mm |
| BulkHead | Balsa Machining | BHC70-F | outsideDiameter: OR 53.44 mm vs RS 55.32 mm |
| BulkHead | Balsa Machining | BHC70-W | outsideDiameter: OR 53.44 mm vs RS 55.32 mm |
| BulkHead | Balsa Machining | BHC80-F | outsideDiameter: OR 62.84 mm vs RS 64.92 mm |
| BulkHead | Balsa Machining | BHC80-W | outsideDiameter: OR 62.84 mm vs RS 64.92 mm |
| CenteringRing | Balsa Machining | CR2050-P | length: OR 1.27 mm vs RS 6.35 mm |
| CenteringRing | Balsa Machining | CR50101-F | length: OR 3.17 mm vs RS 1.27 mm |
| CenteringRing | Balsa Machining | CR5052H-F | outsideDiameter: OR 28.3 mm vs RS 28.96 mm |
| CenteringRing | Balsa Machining | CR5060-W | outsideDiameter: OR 32.59 mm vs RS 40.46 mm; length: OR 3.17 mm vs RS 1.27 mm |
| CenteringRing | Balsa Machining | CR5080-W | insideDiameter: OR 25.4 mm vs RS 24.84 mm; outsideDiameter: OR 55.32 mm vs RS 64.92 mm |
| CenteringRing | Balsa Machining | CR520-F | length: OR 6.35 mm vs RS 1.27 mm |
| CenteringRing | Balsa Machining | CR5570-W | insideDiameter: OR 32.59 mm vs RS 33.65 mm |
| CenteringRing | Balsa Machining | CR6070-F | insideDiameter: OR 41.58 mm vs RS 40.51 mm |
| CenteringRing | Balsa Machining | CR6080-F | insideDiameter: OR 41.58 mm vs RS 40.51 mm |
| CenteringRing | Semroc | CR-9115 | length: OR 6.35 mm vs RS 25.4 mm |
| EngineBlock | Balsa Machining | EB29-P | length: OR 6.35 mm vs RS 12.7 mm |
| EngineBlock | Semroc | TR-5 | insideDiameter: OR 9.55 mm vs RS 10.49 mm; length: OR 12.7 mm vs RS 9.53 mm |
| EngineBlock | Semroc | TR-9 | insideDiameter: OR 18.11 mm vs RS 19.33 mm |
| LaunchLug | Balsa Machining | LL18-1200 | insideDiameter: OR 3.96 mm vs RS 4.06 mm; outsideDiameter: OR 4.39 mm vs RS 4.29 mm |
| LaunchLug | Balsa Machining | LL18-125 | insideDiameter: OR 3.96 mm vs RS 4.06 mm; outsideDiameter: OR 4.39 mm vs RS 4.29 mm |
| LaunchLug | Balsa Machining | LL316-1200 | outsideDiameter: OR 5.77 mm vs RS 6.1 mm; length: OR 304.8 mm vs RS 50.8 mm |
| LaunchLug | Balsa Machining | LL316-200 | insideDiameter: OR 5.33 mm vs RS 5.56 mm; outsideDiameter: OR 5.77 mm vs RS 6.1 mm |
| LaunchLug | Semroc | LL-115 | insideDiameter: OR 3.96 mm vs RS 3.81 mm |
| LaunchLug | Semroc | LL-117 | insideDiameter: OR 3.96 mm vs RS 3.81 mm |
| LaunchLug | Semroc | LL-122 | insideDiameter: OR 3.96 mm vs RS 3.81 mm |
| LaunchLug | Semroc | LL-320 | outsideDiameter: OR 5.77 mm vs RS 6.1 mm |
| LaunchLug | Semroc | LL-330 | outsideDiameter: OR 5.77 mm vs RS 6.1 mm |
| TubeCoupler | Semroc | HTC-20 | length: OR 50.8 mm vs RS 44.45 mm |
| TubeCoupler | Semroc | HTC-9 | insideDiameter: OR 23.01 mm vs RS 21.97 mm |
| NoseCone | Balsa Machining | BNC5AW | length: OR 55.12 mm vs RS 57.15 mm |
| NoseCone | Balsa Machining | CENBC89 | length: OR 152.4 mm vs RS 139.7 mm |
| NoseCone | Estes | PNC-60MS | length: OR 79.38 mm vs RS 63.5 mm |
| NoseCone | Giant Leap | NC-2.56 | outsideDiameter: OR 68.07 mm vs RS 65.02 mm |
| NoseCone | Giant Leap | NC-3.00 | outsideDiameter: OR 77.85 mm vs RS 76.2 mm; length: OR 387.35 mm vs RS 285.75 mm |
| NoseCone | Giant Leap | NC-3.90 | outsideDiameter: OR 101.85 mm vs RS 99.06 mm; length: OR 502.92 mm vs RS 419.1 mm |
| NoseCone | Giant Leap | NC-38 | outsideDiameter: OR 41.71 mm vs RS 38 mm; length: OR 222.25 mm vs RS 203.2 mm |
| NoseCone | Giant Leap | NC-54 | outsideDiameter: OR 57.68 mm vs RS 54 mm; length: OR 273.05 mm vs RS 279.4 mm |
| NoseCone | LOC Precision | PNC-1.52 | outsideDiameter: OR 41.53 mm vs RS 40.64 mm |
| Transition | Balsa Machining | BMS20V2B | aftOutsideDiameter: OR 12.3 mm vs RS 13.21 mm |
| Transition | Balsa Machining | BMS50V2B | aftOutsideDiameter: OR 15.88 mm vs RS 17.27 mm; length: OR 55.88 mm vs RS 50.8 mm |
| Transition | Balsa Machining | BMS60V2B | aftOutsideDiameter: OR 20.73 mm vs RS 26.67 mm |
| Transition | Balsa Machining | BMSV2BT1 | aftOutsideDiameter: OR 26.82 mm vs RS 28.19 mm |
| Transition | Balsa Machining | BTC55Z | aftOutsideDiameter: OR 12.7 mm vs RS 24.64 mm |
| Transition | Balsa Machining | TA2055 | aftOutsideDiameter: OR 33.65 mm vs RS 41.4 mm; length: OR 50.8 mm vs RS 38.1 mm |
| Transition | Balsa Machining | TA2060 | aftOutsideDiameter: OR 41.58 mm vs RS 66.04 mm; length: OR 25.4 mm vs RS 21.34 mm |
| Transition | Balsa Machining | TA5055 | aftOutsideDiameter: OR 33.65 mm vs RS 41.4 mm |
| Transition | Balsa Machining | TA5055L | aftOutsideDiameter: OR 33.65 mm vs RS 41.4 mm |
| Transition | Balsa Machining | TA520 | length: OR 19.05 mm vs RS 19.81 mm |
| Transition | Balsa Machining | TA555 | aftOutsideDiameter: OR 33.65 mm vs RS 41.4 mm |
| Transition | Balsa Machining | TA5560 | foreOutsideDiameter: OR 33.65 mm vs RS 41.4 mm |
| Transition | Balsa Machining | TA5560A | foreOutsideDiameter: OR 33.65 mm vs RS 41.4 mm; length: OR 31.75 mm vs RS 33.02 mm |
| Transition | Semroc | BR-085225 | length: OR 63.5 mm vs RS 66.04 mm |
| Transition | Semroc | BR-58 | length: OR 15.24 mm vs RS 12.7 mm |
| Transition | Semroc | BR-78 | length: OR 22.86 mm vs RS 22.23 mm |
| Transition | Semroc | BR-813 | length: OR 47.5 mm vs RS 43.18 mm |
| Parachute | LOC Precision | LP-18 | lineCount: OR 6 vs RS 8 |

## Serious conflicts worth a look

Most of the table above is small (2–10 %) disagreement where OpenRocket's curated
database is the safer bet, and in several big ones the part number itself proves
RockSim wrong (Semroc **ST-3630** is a 30" tube, not 3"; BMS **LL316-1200** is a
12" lug, not 2"; BMS **TA2055/TA5055/TA555/TA5560** give the T55 side a 41.4 mm
OD, which is a T60 dimension). But a few deserve owner attention because
**RockSim may actually be right**:

1. **BMS CR5060-W** — OR says OD 32.59 mm, but a ring centering a T50 inside a
   T60 needs OD ≈ 40.5 mm (T60 ID). RockSim's 40.46 mm fits; OR's 32.59 mm is a
   T55-ID dimension. The bundled OpenRocket value looks wrong.
2. **BMS CR5080-W** — same pattern: OR OD 55.32 mm ≈ BT-70 ID, RockSim 64.92 mm ≈
   BT-80 ID, and the part is a 50→80 ring. RockSim's value fits the name.
3. **BMS BTC55Z** — aft OD 12.7 vs 24.64 mm is a 2× disagreement on a tail cone;
   couldn't adjudicate from names alone.
4. **Semroc LT-115160** — OR has 28.96/30.99 mm (a 30 mm-class tube), RockSim
   22.1/24.13 mm; the LT-115 designation suggests 1.15" ID → OR right, but the
   RockSim entry is a different size entirely, not noise.
5. **Giant Leap NC-3.00 / NC-3.90** — lengths differ 20–35 %; likely exposed-vs-
   overall length conventions. OR kept.
6. **Semroc CR-9115** — ring length 6.35 vs 25.4 mm (4×).

Per policy the OpenRocket values were kept in all cases.

## Other notes

- Parachute `Cd` column ignored: every row says 0.75, which is already the app's
  default; `n sides` was kept as `sides` (matching existing presets).
- The RockSim files contain 34 rows with an empty part-number column but a
  perfectly good description (ProLine centering rings, Madcow fiberglass
  couplers/nose cones); the description was used as the part number, and rows
  that turned out to be re-listings of numbered parts were deduplicated.
- Verification: `npm test` passes (54 tests, incl. presets tests),
  `npx tsc -p packages/app --noEmit` clean, presets.json parses and the diff
  against the previous version is append-only.
