# Response — issues-2026-07-03b.md

Item-by-item status for the 3 July (batch b) test-run notes. Everything below is
implemented, unit-tested where the logic is testable, and builds clean — but per
our protocol I don't claim UI "works" until you've seen it: please verify visually.

## General

### Annoying

- **Session stored/restored** — ✅ Done. The whole working state (design tree,
  selected motor + delay, mount, launch conditions) autosaves to browser storage
  a moment after every change and restores on the next visit, with a note telling
  you what was restored and when it was saved. A crash or accidental close loses
  nothing.
- **Rocket name as default save filename** — ✅ Already worked (the Save button
  has used the rocket name since the units pass); verified.
- **Rocket name not imported** — ✅ Root cause found: our importer *does* read the
  name, but desktop OpenRocket's default rocket name is literally "Rocket" (in the
  desktop you name the file, not the rocket), so that's what many .ork files carry.
  Now: when the stored name is missing or generic ("Rocket", "New Rocket", …), the
  filename (underscores → spaces) becomes the rocket name.
- **"New" warning** — ✅ Done. "New" now opens a confirmation dialog that warns the
  design will be cleared and offers **Save .ork first**, **Discard & start new**,
  and **Cancel**.

### Cosmetic

- **3-decimal display cap** — ✅ Done. All numeric fields display at most 3
  decimals when you're not editing them; the full-precision number is stored and
  used for every calculation (focus a field to see/edit full precision). Readout
  tiles already capped at 2–3 decimals.

### Features

- **EX motor import (.eng / .rse)** — ✅ Done. In the motor browser: **Import
  .eng/.rse**. Both RASP (.eng, including multi-motor files) and RockSim (.rse)
  parse; .rse per-sample mass data is used directly (better mass-flow fidelity
  than the RASP approximation). Imported motors are stored in the app database
  permanently and appear under manufacturer **"EX"** in both the motor browser and
  batch simulation (original manufacturer string is kept and shown when you pick
  one). They can be deleted from the picker.
- **Launch results — significantly more data** — ✅ Done. After each launch a
  **Launch report** panel shows (grouped, with your units): max altitude /
  velocity / Mach / acceleration; times to launch-guide departure, burnout,
  apogee, and total flight time; velocity and thrust:weight at guide departure;
  launch mass / CG / CP / static margin; altitude and velocity at deployment;
  ground-hit velocity; **optimal delay** and **recommended delay**; safety
  verdicts (lift-off speed, thrust:weight, safe deployment, static margin,
  weathercocking risk); manufacturer + motor diameter; execution time; comments
  (engine warnings + anything flagged). Items I could not compute exactly as
  RockSim does and how I mapped them: *"Ninety-degree safety"* is folded into the
  safe-deployment check (velocity at deployment), *"RockSim recommended"* is our
  "recommended delay" (optimum snapped to the delays the motor actually comes in),
  and *weathercocking* is the standard wind ÷ rod-exit-speed proxy graded
  low/moderate/high. Tell me if you want different thresholds — they're constants
  (15 m/s lift-off, 5:1 T:W, 15 m/s deployment, 1–3 cal margin).
- **Simulations stored + CSV** — ✅ Done. Every run (manual or batch) appends to a
  **Saved simulations** table (persists across reloads, newest 500). Compare runs,
  delete rows, clear all, and **⬇ CSV** downloads all 33 columns.
- **Batch-simulate motors** — ✅ Done, as the "simulate all motors that fit"
  variant (your alternative suggestion): **⚡ Batch simulate motors…** under the
  motor picker. Filters: manufacturers, diameter classes (adapter-down logic as
  always), include-OOP; acceptance criteria: min rod-exit velocity, min
  thrust:weight, apogee window. Runs every candidate through the real engine with
  the **optimal delay picked per motor** (2 sims each: probe + final), live
  progress + Stop, results graded ✓ accepted / ✗ with the failed criterion,
  appended to Saved simulations, CSV download. Uploading a hand-picked list also
  works indirectly: import your EX file(s) and filter to "EX".
- **RockSim materials & parts CSVs** — ✅ Merged: 994 rows parsed, 450 were
  duplicates of OpenRocket data (kept OpenRocket per your rule), **487 new parts
  added** (preset DB now 3,936), 57 dimension conflicts kept on the OpenRocket
  side and logged. Full detail in `docs/testing/rocksim-merge-report-2026-07-03.md`;
  re-runnable via `packages/app/scripts/merge-rocksim-parts.mjs`. Fin and
  motor-retainer CSVs were skipped — no preset kinds for those yet (freeform fin
  libraries would be a nice follow-up given your workflow; say the word).
  **Two conflicts you should look at** (I kept OpenRocket per policy, but RockSim
  looks *right*): **BMS CR5060-W and CR5080-W** centering rings — OpenRocket's
  outer diameters (32.6 / 55.3 mm) don't fit the tubes the part names say they
  center into, while RockSim's (40.5 / 64.9 mm) do. Also noted milder
  exposed-vs-overall length disagreements (BMS BTC55Z, Semroc LT-115160/CR-9115,
  Giant Leap NC-3.00/3.90) — details in the report.

### Questions

- **"If the user overrides CG or mass, does the other stay calculated?"** —
  **Yes.** Verified in the kernel source (`RocketComponent.getCG()`): each
  override is independent. Override only mass → CG stays calculated; override
  only CG → mass stays calculated. The override boxes now also show the
  calculated value as a placeholder until you type (see Wild Child items).

## Wildman Wild Child

### Blockers

- **Fin tabs** — ✅ Done, end-to-end. Trapezoidal, freeform, and elliptical fin
  sets now have **Tab depth / Tab length / Tab offset / Tab offset from**
  (front/middle/end of fin). This is the real OpenRocket kernel tab model, so tab
  volume counts toward fin mass and CG automatically (a mass-override on the set
  still wins). Extras you asked for:
  - **Fit tab to motor tube** button — sets depth = body tube outer radius −
    motor-mount tube outer radius (falls back to wall thickness if no mount), and
    seeds a sensible tab length if none is set.
  - The kernel clamps over-deep tabs to the body radius (differential-tested).
  - Tabs draw in the 2D view as dashed through-the-wall rectangles.
  - **Centering rings snap** to the tab's front/rear edges when dragged or slid.
  - Tabs round-trip through .ork in the desktop's exact format (works both ways
    with desktop OpenRocket).
- **Typing negatives / cleared field resets to 0** — ✅ Done. Every numeric field
  was rebuilt: you can clear it and type anything (including a leading "-") and it
  never snaps back to 0; the value commits as soon as what you've typed is a valid
  number, and reverting happens only if you leave the field with nonsense in it.
- **Validation** — ✅ Done. Letters (including a stray "e"), negative values where
  they make no sense (dimensions, thickness), and out-of-range values turn the
  field red and are **not** committed — the design keeps its last valid value.
  Negative input stays allowed where it's meaningful (sweep, cant, offsets, CG
  override).
- **Optimal delay** — ✅ Done, both ways you suggested:
  - Every simulation reports **Optimal delay** (from the kernel's ballistic probe
    flight — burnout to *ballistic* apogee, so it's correct even when the chute
    changes the deployed flight's apogee) plus **Recommended delay** = optimum
    snapped to the delays that motor is actually available in.
  - The motor browser's delay dropdown has **"Auto (optimal)"**: each launch first
    probes, snaps to the nearest available delay, and flies the real run with it.
    Batch simulation uses the same logic per motor.

### Annoying

- **Wall thickness ⇄ inner diameter** — ✅ Done for every tube (body, inner,
  coupler, lug, …): both fields show; editing either updates the other.
- **Change finish of all components at once** — ✅ Done: a small **→ all** button
  next to the Surface-finish selector applies the current finish everywhere;
  individual components stay editable afterwards.
- **Component + whole-rocket attributes together** — ✅ Done (first iteration): the
  property panel header now shows the selected component's engine-computed length,
  mass (labelled "(all fins)" for fin sets), mass including children, CG from the
  component's front, and where it starts measured from the nose tip — while the
  whole-rocket readout stays visible above. A second visualization window for the
  edited component is bigger surgery — say the word if the numbers aren't enough.
- **Two add buttons** — ✅ Done: **+ Add to [selected component]** and **+ Add to
  rocket** now sit side by side under the tree.
- **New component defaults from the previous one** — ✅ Done: a new component
  inherits the airframe line (nose base diameter → tube OD → transition fore
  diameter), material, finish, and tube wall thickness from the component it
  follows (previous sibling, else its parent).
- **Copy & paste components** — ✅ Done as **duplicate** (⧉ button on the selected
  tree row): deep-copies the component *including its children* and inserts the
  copy right after it. If you need cross-parent paste specifically, tell me and
  I'll add a clipboard.
- **Internal components visible** — ✅ Done: nose and transition shoulders now draw
  as dashed outlines sliding into their adjacent tubes (couplers, inner tubes,
  rings already drew dashed).
- **Override boxes show calculated value** — ✅ Done: mass and CG override boxes
  show the engine-calculated value as a grey placeholder until you override; Cd
  shows "auto".
- **Freeform chart dot highlight** — ✅ Done: clicking into a coordinate-table row
  highlights that point on the planform (amber ring); the dragged point highlights
  too.

### Cosmetic

- **Color quick presets** — ✅ Done: ten swatches (white, black, primaries, etc.)
  next to the picker.
- **2D zoom / pan / reset** — ✅ Done: mouse-wheel zooms around the cursor (up to
  12×), dragging the background pans, **⤢ Reset view** brings the whole rocket
  back. Dragging a component still repositions it (with snap), zoomed or not.
- **Motor tube default name** — ✅ Done: ticking "Acts as motor mount" renames an
  unrenamed inner tube to **"Motor Mount Tube"** (and back if unticked; your
  custom names are never touched).

### Questions

- **Fin set mass override: all fins or one?** — **All fins combined.** Verified in
  the kernel: a fin set's computed mass is per-fin × fin count, and the override
  replaces that total. The override field is now labelled **"Mass (all fins
  combined)"** on fin sets, and the component readout says "(all fins)" too.

---

## Under the hood (worth knowing)

While wiring the new summary fields I found the **differential test was flaky at
HEAD** — the same golden harness produced different flight numbers run-to-run on
the JVM. Root cause: OpenRocket's `InstanceMap` is a `HashMap` keyed by objects
with identity hash codes, so the per-step aerodynamic force summation ORDER varied
per process; the ULP-level noise chaos-amplifies over a long flight (it even
flipped the sample count). Fixed with a documented 2-token kernel patch
(`LinkedHashMap` — insertion order, identical on JVM and TeaVM;
`engine-java/patches/LEDGER.md` has the full story), the turbulent golden scenario
is capped at 8 s so chaos can't flip structural counts, and the differential now
passes stably (verified 5 consecutive runs). Simulations are now genuinely
deterministic run-to-run, which your stored-runs comparisons rely on.

Verification state: engine tests 12/12, app tests 54/54 (plus the RockSim merge
suite run), differential 211 lines stable, production build clean. As always —
the UI claims above await your eyes.
