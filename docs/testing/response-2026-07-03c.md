# Response to issues-2026-07-03c.md — item-by-item status

All items fixed and released as **v0.005** (pushed). Verified live in the built
app against your restored session (your design, motor, saved runs and settings
were left exactly as they were).

## Blockers

### Max motor length belongs in the main dialogs, not the motor chooser
**Fixed.** The input now lives in the main **Motor panel** (right under the
mount selector), labelled *Max motor length* with the usual clickable unit
chip. It's treated as what it is — a physical property of the rocket — and is
saved with your session. If you had a value set in the old browser filter, it
carries over automatically.

- **Motor browser:** unchanged behavior per your earlier rule — too-long
  motors are flagged ⚠ but stay selectable. The browser now just shows a small
  read-only "max motor length …" note so you know the flag's threshold.
- **Batch simulation:** now factored in, as requested — motors over the limit
  are **excluded** from the candidate list (no point flying motors that don't
  fit), and the dialog says how many it skipped, e.g.
  "42 candidate motors · 13 excluded (over max motor length)".

## Annoying

### CSV needs motor type / propellant / case
**Fixed.** The bundled thrustcurve.org database was regenerated with two more
catalog fields (propellant name, reload case) — 906 of the 1,129 motors carry
propellant info and 846 carry case info (the rest simply aren't cataloged).
Type (single-use / reload / hybrid) was already bundled and is now reported.
All three appear in the CSV, and also in the launch report's detail grid
(Motor type / Propellant / Motor case rows).

### Clean up motor designations
**Fixed, everywhere motors are displayed** (browser table, picked-motor line,
batch table and progress, saved-runs table, launch report, CSV):
- Cesaroni's leading total-impulse number is stripped: `381I224-15A` → `I224-15A`.
- `HP-` prefixes (AeroTech, Loki) are stripped: `HP-I140W` → `I140W`.
- Sorting and the search box understand the clean form; Estes fractionals like
  `1/2A6` are untouched (regression-tested).
- The raw catalog designation is still what goes into `.ork` files and API
  calls, so desktop compatibility is unchanged.

### CSV column labels and order
**Fixed.** The CSV (both saved-simulations and batch downloads — same builder)
now leads with your exact fourteen columns, in your units:

1. Designation · 2. Apogee (ft) · 3. Velocity (mph) · 4. Manufacturer ·
5. Diameter (mm) · 6. Type · 7. Propellant · 8. Case · 9. T:W ·
10. Guide (mph) · 11. Accel (Gs) · 12. Delay (s) · 13. Pad Weight (g) ·
14. Recovery Weight (g)

The remaining columns (dates, SI values, safety verdicts, deployments,
comments…) follow after, per your "any order" note. Two notes:
- **Pad Weight** is the simulated lift-off mass (motor installed);
  **Recovery Weight** is the simulated rocket mass at motor burnout — in the
  verification flight they differed by exactly the propellant mass (183 g for
  an I140W).
- Older saved runs (from before this build) predate the type/propellant/case/
  recovery-weight fields, so those cells are blank for them; re-run to fill.

### Why the "Motor G80T isn't built-in" error on every file?
**Fixed — that message is gone.** The old importer only checked the handful of
built-in Estes quick-picks. Now a file's motor is matched against the full
1,129-motor bundled database (by designation, its cleaned form, or common
name, using the file's motor diameter as a tie-breaker) and **loaded
automatically**, thrust curve and all, with the file's delay. Your
WM Wild Child repro now reports: *"Motor: AeroTech G80T-8 (loaded from the
motor database)."* You'll only see a notice if the motor genuinely isn't in
the database (then it says so and points at the browser), or if the curve
download fails while offline.

## Saved simulations should be clickable
**Fixed.** Click any row in Saved simulations and that run's full launch
report opens in the report section (deployment table, delay lines, all
details); the selected row highlights. The ✕ delete button still works
without opening the run. One honest limitation: the altitude/velocity plots
need a fresh simulation's full data series (too heavy to store per run), so a
re-opened saved run shows the complete report but not the charts — press
Launch to regenerate those.
