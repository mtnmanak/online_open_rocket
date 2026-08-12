# Online OpenRocket — User Guide

> The complete guide to Online OpenRocket: a quick start, a full feature
> reference, and the physics, math, and citations behind the numbers. This is
> also available inside the app via the **❓ Guide** button in the header.

<a id="welcome"></a>

## Welcome to Online OpenRocket

Online OpenRocket is the real OpenRocket flight simulator, running entirely in your web browser. There is nothing to install: the same **24.12 physics kernel** that powers the OpenRocket desktop application has been compiled to JavaScript and verified bit-for-bit against the desktop program. When your browser reports an apogee, it ran the same code, in the same order, with the same rounding as the desktop app.

The workflow is simple and always the same. You design a rocket by building up its components, hang a motor, set your launch conditions, and press **Launch** to see how high it flies, how fast it goes, and whether it will fly straight and land safely. This guide is written for model and high-power rocketry hobbyists at every level, from your first Estes kit to a multi-stage high-power project.

The app is organized into **three workspaces**, selected by the tabs under the header, one per phase of that workflow: **Design** (the component tree, the 2D/3D view, and the property editor), **Motors & Launch** (motor selection, ejection delays, batch simulation, and launch conditions), and **Results** (flight stats, the launch report, plots, drag analysis, and saved runs). A **vitals strip** above the tabs stays visible everywhere — the rocket's name, stability margin, loaded mass, current motor, last apogee, and the **Launch** button — so you can tweak a fin, fly, and check the number without hunting through tabs. Launching switches you to Results automatically.

The guide has three parts:

- **Quick Start** gets you to your first successful flight in about a minute.
- **The In-Depth Feature Guide** is the complete reference for everything the app can do — designing, motors, launch conditions, simulation, staging, files, and preferences.
- **How It Works** opens up the physics, the math, and the references behind the numbers, so you can decide exactly how far to trust them.

## Contents

1. [Quick Start](#quick-start)
2. [Designing the Rocket](#designing-the-rocket)
3. [Visualizing the Design](#visualizing-the-design)
4. [Motors](#motors)
5. [Launch Conditions](#launch-conditions)
6. [Simulating and Reading Results](#simulating-and-results)
7. [Multi-Stage and Clustered Rockets](#multi-stage-and-clusters)
8. [Files, Units, and Offline Use](#files-and-formats)
9. [How It Works: Physics & Math](#how-it-works-physics)
10. [Assumptions, Limitations & References](#limitations-and-references)

---

<a id="quick-start"></a>

## Quick Start

This section gets you to your first successful flight in about a minute. The rest of the guide then covers everything in depth.

## Your first flight in three steps

When the app opens, you already have a complete, flyable rocket called **My Rocket** — a nose cone, a body tube, a fin set, a motor mount, and a parachute. You don't have to build anything to get started. All it's missing is a motor.

**1. Load a motor.** Open the **Motors & Launch** workspace (the tabs under the header) and find the **Motors** panel. The fastest option is the **Quick picks (built-in, offline)** dropdown — pick a classic Estes motor like **B6-4** or **C6-5** and it loads instantly, no download needed. (The number after the dash is the ejection delay in seconds.) Want something specific? Click **🔎 Browse motor database…** to search the full thrustcurve.org catalog of about 1,100 motors — but for your first flight, a quick pick is all you need.

**2. Check your launch conditions (optional).** Beside the Motors panel is the **Launch conditions** panel. The defaults are sensible: a 1-meter launch rod, pointed straight up, no wind, standard sea-level atmosphere. You can leave every field alone for now.

**3. Press Launch.** Hit the **Launch** button — at the bottom of the Launch conditions panel, or the always-visible **Launch** in the vitals strip at the top. The simulation runs in a fraction of a second and the app switches to the **Results** workspace with your numbers.

That's it — you've flown a rocket.

## Reading your first result

After launching, the headline tiles across the top of the results give you the numbers that matter most:

- **Apogee** — the peak altitude the rocket reached. This is your "how high did it go" number.
- **Max velocity** — the fastest the rocket traveled (also shown as **Max Mach** in the detailed report).
- **Max accel** — peak acceleration, a feel for how hard it leaves the pad.
- **Apogee at** — seconds from liftoff to the top of the flight.
- **Descent hits** / **Flight time** — how fast it's coming down at landing and how long the whole flight lasted.

Two health checks tell you whether the flight is actually *safe and stable*, not just how high it went:

- **Stability margin** (shown in the design stats as **Stability**, in *calibers*). A healthy rocket sits between **1.0 and 3.0 cal**. Below 1.0 the rocket is *under-stable* and may wobble or go unstable — flagged red with a ⚠. Above 3.0 it's *over-stable* and tends to weathercock (turn into the wind) — flagged as a yellow △ caution, not a failure. Right in that band, marked ✓, is what you want. The design page, vitals strip and launch report all use this same rule.
- **Landing descent rate** — you want to touch down at **20 ft/s (about 6 m/s) or slower** so nothing breaks. The report flags a faster landing. Parachute presets apply the manufacturer's rated drag coefficient when the catalog carries one, but a simulated descent rate is still an estimate — **always cross-check the parachute manufacturer's own sizing guidance** before you fly.

Open the full launch report below the tiles for more: **thrust-to-weight at the rod** (aim for at least **5:1**), **rod-exit speed** (at least about 50 ft/s / 15 m/s so the fins have airflow to steer with), deployment timing, and plain-language safety comments that name any problem and where it is.

If your first rocket flies stable and lands soft — congratulations, you're done. If a value comes up flagged, the report tells you which one and why, so you know what to adjust.

## Where to go next

Now that you've seen a flight end to end, the rest of this guide shows you how to make it *your* rocket:

- **Build and edit components** — reshape the nose, resize the body, design custom fins (including freeform), and add stages using the component tree.
- **Choose the right motor** — filter the full database by manufacturer, diameter, and impulse; drill ejection delays to any whole second; and batch-test many motors at once.
- **Dial in launch conditions** — wind, launch angle, site altitude, and weather.
- **Read the plots and reports** — altitude, velocity, and stability over the whole flight, plus saving and exporting your results.
- **Import and export** — load and save `.ork`, RockSim, and RASAero files.

---

<a id="designing-the-rocket"></a>

## Designing the Rocket

Everything below is organized by the order you actually work: design the rocket, look at it, hang a motor, set the weather, fly it, read the results, then save or export. This section covers the design stage.

## The component tree

The **Design** workspace's left column is a **stage-rooted tree** of components. Each stage holds an axial chain of external body parts (nose cone → body tubes → transitions), and those in turn hold internal parts (fins, motor mounts, recovery gear, mass). Click a node to select it and its property panel opens in the column to the right of the rocket view; inline buttons on each node **move, duplicate, or delete** it. The tree enforces OpenRocket's containment rules — you can only add children a parent legally accepts (for example, fins and inner tubes go inside a body tube, a parachute goes inside any body part, and an engine block goes inside an inner tube).

## The nineteen component types

| Type | Category | Key parameters |
|---|---|---|
| Nose cone | External body | length, base radius, wall thickness, shape, shoulder |
| Transition | External body | length, fore/aft radius, thickness, shape, two shoulders |
| Body tube | External body | length, outer radius, thickness |
| Trapezoidal fins | Fin set | count, root/tip chord, sweep, height, thickness, cant, cross-section, tabs |
| Elliptical fins | Fin set | count, root chord, height, thickness, cross-section, tabs |
| Freeform fins | Fin set | count, thickness, cross-section, tabs, polygon outline |
| Tube fins | Fin set | count (up to 12), length, outer radius, wall thickness |
| Inner tube | Structure / mount | length, outer radius, thickness, cluster layout |
| Tube coupler | Structure | length, thickness |
| Centering ring | Structure | axial thickness |
| Bulkhead | Structure | axial thickness |
| Engine block | Structure | length, thickness |
| Launch lug | Guide | length, outer radius, thickness |
| Rail button | Guide | outer diameter |
| Camera shroud / fairing | External | length, width, height, shape, as-built mass, finish |
| Parachute | Recovery | canopy diameter, Cd, spill hole ⌀, lines, deploy event |
| Streamer | Recovery | strip length/width, Cd, deploy event |
| Shock cord | Recovery | cord length |
| Mass component | Ballast | mass, length, radius |
| Pod set | Assembly | instance count, radial distance & reference, angle |
| Booster (parallel stage) | Assembly | instance count, radial placement, angle, separation |

### Camera shrouds (fairings) — how they're modeled

A camera shroud is a real aerodynamic body: it shifts CP and adds drag, and Online
OpenRocket is (as far as we know) the only hobby simulator that computes both. The
side profile is flown as a **slender strake** through the kernel's own
low-aspect-ratio fin lift (which reduces to the classic Jones slender-body model —
the right physics for a strake), so the CP shift falls out of the same Barrowman
machinery as everything else. Drag uses **Hoerner protuberance coefficients**
referenced to the shroud's frontal area (streamlined ≈ 0.25, half-round ≈ 0.55,
box ≈ 1.05, body interference included). Enter the as-built mass — printed parts
weigh what they weigh. The radial mounting angle is not modeled (same as launch
lugs), and there is no wind-tunnel anchor for this model yet — treat the numbers
as good engineering estimates, and add margin on small-diameter rockets where a
shroud matters most.

### Parachute spill holes

A spill hole reduces the effective drag area: we fly the standard reduction
Cd_eff = Cd · (1 − (hole ⌀ / canopy ⌀)²) — the same treatment RockSim uses, and
spill holes round-trip through RockSim files.

## Dimensions: diameter vs. radius

Internally every size is stored in **pure SI** (meters, kilograms, seconds) and angles in radians. The property panel shows friendlier units (mm, degrees, grams) and converts at the edge. Fields that describe a round cross-section are stored as a **radius**, but if you set your preference to "Diameter" the panel doubles the value and relabels it — so "Base outer radius" becomes "Base outer diameter" and accepts the tube's full OD. This matters because catalog tubes are always quoted by diameter, so most builders switch to diameter mode.

## Nose cones and transitions

Both support six mathematical **shapes**: Ogive, Conical, Ellipsoid, Parabolic, Haack, and Power. The 2D and 3D views draw the **exact mathematical profile** the physics flies, including the shape parameter.

Four of the shapes are really *families* of profiles, selected by a **Shape parameter** field that appears next to the shape picker (Conical and Ellipsoid have a single fixed profile, so the field hides). A blank field always means OpenRocket's own default for that shape — a missing parameter never silently reshapes the part:

| Shape | What the parameter controls | Range | Blank = |
|---|---|---|---|
| Ogive | **1 = tangent ogive** (the circular arc meets the body tube smoothly — the classic kit profile). Below 1 it becomes a **secant ogive** (a shallower arc that meets the tube at an angle), straightening all the way to a cone at 0. | 0–1 | 1 |
| Parabolic | Which segment of a parabola to use: **1 = full parabola** tangent to the tube, 0.75 = ¾-parabola, 0.5 = ½-parabola, **0 = cone**. | 0–1 | 1 |
| Power | The exponent n in r ∝ (x/L)ⁿ: **0.5 = the classic ½-power** (parabola of revolution), 1 = cone, small values = increasingly blunt. | 0–1 | 0.5 |
| Haack | **0 = LD-Haack (Von Kármán)** — minimum drag for a given length and diameter, the usual choice; **1/3 = LV-Haack** — minimum drag for a given length and volume. The field caps at 1/3 (the engine clamps there too). | 0–1/3 | 0 |

Imported files keep whatever parameter they carried — RockSim designs in particular often store explicit values, which you can now see and edit. A nose cone can be **solid (filled)** or a hollow shell of a given wall thickness, and can carry a **shoulder** (radius, length, thickness, and an optional end cap) that plugs into the tube below. A transition is a shoulder-to-shoulder part with independent fore and aft radii plus a shoulder on **each** end — use it as a reducer, boat tail, or coupler flare.

## Fins

Four fin families are supported:

- **Trapezoidal** — the everyday fin, defined by root chord, tip chord, sweep, and height.
- **Elliptical** — a rounded planform from root chord and height.
- **Freeform** — an arbitrary polygon. Selecting one opens the **freeform point editor**, where you drag vertices to draw any outline (needs at least three points).
- **Tube fins** — a ring of full tubes instead of flat fins (up to 12).

Every flat-fin set also carries a **cant angle** (to induce roll), a **fin count** (1–8), and a **cross-section**: **Square**, **Rounded**, or **Airfoil (pointed)**. The cross-section is not cosmetic — a square edge produces markedly more pressure/skin drag than a streamlined airfoil, and the kernel models that difference, so choosing "Airfoil" on a carefully sanded fin gives you the lower drag you actually built.

For fast flights there is also an optional **supersonic airfoil** selection (RASAero-style): **hexagonal, NACA, double wedge (diamond), biconvex, hexagonal blunt-base, and single wedge**, plus **LE/TE chamfer lengths** and a **leading-edge bluntness radius**. Each shape gets its proper supersonic thickness wave drag, blunt-base shapes add fin base drag, and a blunt LE adds swept-cylinder drag. Leave it on "Classic" and the ordinary cross-section above drives the model exactly as before. These choices also round-trip through `.ork` files (the desktop app ignores the extra tags with a warning).

**Through-the-wall tabs**: a tab exists only when **both** its depth and length are greater than zero (OpenRocket's rule). You set the tab depth, length, offset, and the reference edge the offset is measured from (front / middle / end of fin). The engine clamps tab depth to the body radius, and the tab's volume counts toward fin mass and CG.

## Positioning and snapping

Internal and fin components are placed **axially** relative to their parent, using a method (top / middle / bottom / absolute) plus an offset. You can drag parts in the 2D schematic or nudge the offset slider. The editor provides **magnetic snapping** to meaningful anchors: the parent's ends and middle, every sibling's leading and trailing edges, and — importantly — the front and rear edges of a fin's tab, because in a real build the centering rings butt against the tab where it passes through the wall. Any "absolute" positions from an imported file are rewritten to parent-relative offsets so the drawn geometry always matches the simulated geometry.

## Motor mounts and clusters

A motor mount is usually an **inner tube** with the mount flag set. For **minimum-diameter rockets** — where the motor case is essentially the airframe and there is no room for a mount tube — check **"Motor mount"** on the **body tube** itself and the motor loads directly in it (the same kernel path the desktop uses; imported files with body-tube mounts now come in as real mounts). Every mount also takes a **motor overhang** — how far the motor protrudes past the tube's aft end (about 6 mm is standard min-diameter retention practice) — which shifts the motor's mass aft in the simulation and draws in the 2D view. For the extreme *sub-minimum* style (fins bonded straight to the motor case, or propellant cast into the airframe tube itself), model a body tube with the motor case's own outer diameter and check **"Sub-minimum: motor case is the airframe"** — the motor browser then fits motors against the tube's *outer* diameter instead of its bore, so the case-diameter motor you're building around stays selectable. Leave the wall thickness at 0 unless the airframe adds real structure on top of the case (the motor file's weight should include the case — it flies). An inner tube also carries a **cluster layout** — single, rows, rings, and stars up to nine motors, with a spacing multiplier (× tube diameter) and a rotation. One motor choice serves the whole cluster: thrust is multiplied by the count and mass is placed at the real tube positions, exactly matching the kernel's ClusterConfiguration.

## Pods and parallel boosters

Two assembly types place whole component chains **off-axis**, ringed around the airframe:

- **Pod set** — a non-separating pod (camera bay, external raceway, side-mounted tube). It stays attached for the whole flight, contributing its mass and drag rigidly.
- **Booster (parallel stage)** — a separable strap-on booster with its own separation trigger and delay, like the outboard boosters of a Falcon Heavy. After it separates, it flies (and lands) on its own tracked branch.

Both attach to a body component (body tube, nose cone, or transition) and hold their own axial chain — nose cone, body tubes, fins, even a motor mount — exactly like a miniature rocket. Placement is controlled by an **instance count** (1–8 copies ringed evenly around the body), a **radial distance** with two reference modes (a *gap from the parent surface*, where 0 = touching, or a distance *from the centerline*), and an **angle** around the body. A booster with a motor mount takes a motor like any other mount, and its ignition follows the same trigger rules as serial stages.

## Recovery

**Parachutes** take a canopy diameter, a drag coefficient (blank = automatic, defaulting to 0.8 after deployment), line count, and line length. **Streamers** take strip length and width. Both carry a **deployment trigger**: motor ejection charge, apogee, altitude (descending, with an AGL altitude), launch, or never — plus a deploy delay. A **shock cord** and a **mass component** (for nose weight, altimeters, or dead ballast) round out the bay.

## Mass, CG, and Cd overrides

Any component can override its computed **mass**, **axial CG position**, or **drag coefficient**. This is how you tell the sim what a part *actually* weighs when the geometry can't know — a machined coupler, a loaded electronics bay, a store-bought chute. Applying a component preset that carries a cataloged mass sets a mass override automatically, because a real part weighs what it weighs, not what its shell computes.

## Materials, finishes, and color

Solid and structural parts pick a **bulk material** from the desktop material database (writing its density); parachutes and streamers pick a **surface material**, and shock cords/lines pick a **line material**. Typing a custom density detaches the part from the named material. Each body part also has a **surface finish** — Rough, Unfinished, Regular paint, Smooth paint, Aircraft sheet-metal, or Polished — and surface roughness feeds the skin-friction drag model, so a polished airframe genuinely simulates lower drag. Finally, each component has a **display color** (a full picker plus one-click presets) used only in the 2D/3D views.

## Component presets

The bundled catalog holds roughly **4,700 real-world parts** (tubes, nose cones, transitions, rings, couplers, bulkheads, engine blocks, launch lugs, parachutes, streamers) from named manufacturers with part numbers, materials, and cataloged mass. Open the preset picker on a selected component, choose a part, and its dimensions, material, and mass override are patched in. The 1.3 MB bundle loads lazily on first use, and you can round-trip your own custom presets through CSV.

---

<a id="visualizing-the-design"></a>

## Visualizing the Design

As you build, four views keep the design honest — a scale side view, a rotatable 3D shell, printable 1:1 fin templates, and a drag-analysis chart.

## The 2D schematic

A true-scale side view drawn from the tree. It shows tubes, lathed nose/transition profiles, fins at their real planforms, fin tabs, and motor cases drawn to scale, plus **CG and CP markers** in standard rocketry symbols. You can **drag components** to reposition them (with the snapping described earlier) and pan/zoom. Pods and parallel boosters draw off-axis where they actually sit, projected above/below the body in the side view.

## The 3D view

A three.js render of the external shell you can **drag to rotate and scroll to zoom**. It marks the **CG** (a neutral sphere) and **CP** (a red sphere) so you can see the stability margin in space. Pods and boosters render ringed around the airframe at their true radius and angle — and they export to OBJ too.

## 1:1 fin templates (SVG)

From any fin set you can export a **true-scale printable cutting template**. It draws the fin outline as a hairline cut path, a dashed root-chord reference line, the through-the-wall tab rectangle where present, a label block (rocket/fin name, cut count, chord/height/thickness/cross-section/tab depth), and a **50 mm calibration ruler**. Print at 100% with no fit-to-page, then check the ruler measures exactly 50 mm — printers silently rescale, and the ruler is how you catch it. The SVG uses physical millimeter units and a 0.2 mm hairline, so it feeds a laser cutter directly.

## Drag analysis (CD vs Mach)

On the **Results** workspace, the **Drag analysis** panel plots your design's drag coefficient against Mach number — a static property of the geometry, no flight needed, recomputed live as you edit. Click **Show CD vs Mach** to open it.

- The main chart shows the **power-off** (coasting) drag curve. Give a stage a **nozzle exit diameter** (in the Stage property panel) and a dashed **power-on** curve appears: during the burn, the motor's exhaust plume pressurizes the base area, so boost drag is genuinely lower than coast drag. The bigger the nozzle exit relative to the base, the bigger the reduction — a minimum-diameter rocket sees a large difference, a small motor in a fat airframe almost none. For a clustered mount, enter one equivalent nozzle whose exit *area* is the sum of the individual exit areas. Zero (the default) means the two curves are identical.
- The **breakdown chart** splits the drag **by component** (nose, body, fins…) or **by type** (friction / pressure / base), so you can see *why* the rocket is draggy and where cleanup pays — the transonic drag rise starting near Mach 0.9 is plainly visible.
- A **CP-vs-Mach chart** shows the center of pressure (as % of body length) across the whole Mach range — with the supersonic model on, this is the chart to check before a fast flight: supersonic CP moves forward, and the accepted practice (RASAero's recommendation) is to keep **≥ 2 calibers** of margin through the transonic and supersonic regime.
- A Max-Mach selector (1–5 classic; up to **25** with the supersonic model) and a **CSV export** round it out. The CSV is a full **aerodynamic-coefficient table** (CD power-off/on, CP, CNα vs Mach) usable as input to external trajectory programs. With the classic model, values above roughly Mach 1.5 are Extended-Barrowman estimates and labeled approximate; with the **supersonic model** (Preferences → Aerodynamics) they are validated against NASA wind-tunnel data to ~Mach 4.6 and physically extrapolated to Mach 25.

---

<a id="motors"></a>

## Motors

With the airframe drawn, the next step is choosing a motor. The app bundles the full thrustcurve.org catalog metadata and downloads the actual thrust curves on demand.

## The database and browser

The app bundles metadata for about **1,129 thrustcurve.org motors** (designation, manufacturer, impulse class, diameter, length, average/max thrust, total impulse, burn time, loaded and propellant weight, delays, availability, propellant/case info, type). **Thrust curves are not bundled** — they download on demand the first time you pick a motor and cache in your browser. From a motor mount, click **Browse motor database** to open the browser; the header shows the source, date, and the mount's diameter. When a curve lacks CG data the kernel uses OpenRocket's model: mass drops from loaded to burnout weight in proportion to cumulative impulse, with CG fixed at half length.

## Filters

The browser gives you **manufacturer chips** (with live counts), **diameter-class chips**, an **include out-of-production** checkbox, and free-text search across the designation and common name. The results table (capped at 400 rows) sorts by designation, manufacturer, diameter, length, burn time, or total impulse (the default). Filters persist between sessions. Sorting the "Motor" column sorts by the cleaned-up *display* designation, so a Cesaroni motor like `381I224-15A` is shown and ordered as `I224-15A` rather than by its impulse-prefix digits, while the raw designation stays the file identity.

## Diameter-class fit (adapter-down)

The mount's inner diameter drives which classes appear. A mount fits **every class at or below its own** — smaller motors ride in adapters — but **never** a larger one. Motors too big are physically excluded from the list, not merely flagged. Note that **75 mm and 76 mm are the same class** (shown "75/76"), and diameters snap to the nearest common class within tolerance because tube IDs run oversize.

## Delays and the drill-to-fit rule

The prescribed delays from the motor's label are informational only. In the browser's **Delay** select you can choose Auto (optimal), any prescribed delay, or **Custom** and type a value; on the main Motors panel you can type **any whole-second delay** without reloading, or tick **auto (optimal)** on the sustainer. This is the drill-to-fit rule: real flyers drill an adjustable delay to whatever whole second they want, so the recommendation is `round(optimum)` and is **never snapped to the prescribed list**. One subtlety: the kernel's optimum delay is the coast from burnout to *ballistic* apogee (a deployment-free probe), and auto uses a two-pass approach — fly once, read the optimum, round it, and re-fly.

## Per-stage maximum motor length

Each **stage** has its own "Max motor length" — a booster and a sustainer have different room. Motors longer than the limit are flagged in the browser and excluded from batch simulation. This is separate from the diameter-fit rule; a motor can be the right diameter but too long.

## EX and custom motors, and built-in quick picks

You can **import experimental motors** from RASP `.eng` or RockSim `.rse` files — pick one or several files with **⬆ Import .eng/.rse**, or point **📁 Import EX folder** at the folder where you keep your motor files and every `.eng`/`.rse` inside is added in one go. They appear under an "EX" manufacturer (the real manufacturer name is preserved), persist in your browser between sessions, filter and simulate like any database motor, and — for `.rse` files with measured per-sample masses — use those masses instead of the impulse-proportional model. Curves are local; no network needed. Select an imported motor and press 🗑 to remove it. For instant offline use there are three **built-in quick picks** (A8-3, B6-4, C6-5) as full motor specs. When you import a design whose motor prefix-matches a built-in, the app loads the built-in curve but **keeps the file's ejection delay**, so a saved C6-7 doesn't silently become a C6-5.

---

<a id="launch-conditions"></a>

## Launch Conditions

Before you fly, the Launch Conditions panel (in the **Motors & Launch** workspace) sets the pad and the weather. It collects eight fields: **launch rod/rail length** (default 1 m), **rod angle** (0°, range −30…30°), **average wind**, **wind gust sigma** (standard deviation of gusts about the average), **site altitude** (0–10,000 m), **latitude** (default 28.61°, roughly Florida), **temperature** (blank = ISA standard), and **pressure** (blank = ISA standard). Values display in your preferred units and convert to SI on Launch. When temperature and pressure are blank the sim uses the ISA atmosphere (288.15 K, 101325 Pa, −6.5 K/km lapse).

**Deterministic by design**: wind turbulence is seeded (default seed 42), so identical inputs always produce an identical flight. This is intentional and differs from the desktop's time-seeded randomness — re-running won't vary the result, and that is not a bug. (The physics behind the wind model and this determinism choice is covered in *How It Works*.)

---

<a id="simulating-and-results"></a>

## Simulating and Reading Results

With a motor assigned and conditions set, press **Launch**. This section covers the single-flight run, the plots, the safety-graded report, saved runs, and batch simulation.

## Running a single flight

Assign a motor, set conditions, and press **Launch**. The app builds the flight options and simulates once; if the primary mount is on auto-delay it reads the kernel's optimum, rounds it, sets that delay, and re-flies. The primary mount is the topmost stage's mount that has a motor — it drives the lead columns and the auto-delay probe. Engine invariants: **RK4 with adaptive time step, Extended Barrowman aerodynamics, quaternion orientation, and a 6DOF→3DOF switch after recovery deployment.** (One automation caveat: the sim runs inside a requestAnimationFrame callback, which Chrome pauses in hidden tabs — a stuck "Simulating" means the window isn't visible.)

## Flight plots

Up to **eleven synchronized single-series panels**: Altitude, Velocity, Acceleration, Mass, Thrust, Drag force, Mach number, Stability margin (cal), CP location, CG location, and Angle of attack. A chip bar toggles panels (default: altitude/velocity/acceleration), and a series only appears if the kernel actually produced it. Following dataviz discipline, **different-scale measures are never dual-axed** — each gets its own panel and y-scale, with a fixed per-series color and theme-aware axes. Hover for synchronized crosshair readouts; the **CSV** button exports every populated series, time-aligned.

## The launch report and safety checks

The report header shows the **optimal / recommended(available) / flown** delay, then a per-device **recovery table** (drogue and main each get a row: deploy time, altitude, opening velocity, settled descent rate, and a verdict). "Show all details" expands roughly thirty attributes: max altitude/velocity/Mach/acceleration, times to guide departure / burnout / apogee / landing, velocity and thrust:weight at guide departure, launch mass/CG/CP/static margin, landing descent rate, plus the motor's diameter, manufacturer, type, propellant, and case.

Every flight is graded against these thresholds:

| Check | Threshold | Meaning |
|---|---|---|
| Lift-off speed | ≥ 15 m/s (~50 ft/s) | Enough airspeed leaving the guide to be stable |
| Thrust:weight | ≥ 5:1 | Adequate initial acceleration |
| Safe deployment | ≤ 21.34 m/s (70 ft/s) | Opening shock won't zipper the tube |
| Drogue descent | ≤ 21.34 m/s (70 ft/s) | Top of accepted drogue band |
| Landing rate | ≤ 6.1 m/s (20 ft/s) | Survivable ground hit |
| Static margin | 1.0–3.0 cal | Stable but not so over-stable it weathercocks |

The report also flags **weathercocking risk** and echoes the average wind, so an over-stable rocket in a stiff breeze reads as a warning rather than a silent number.

## Saved runs and CSV

Every flight is stored to a **run history** (up to 500 runs, surviving reloads). A typical flight-day flow is to fly many motors, compare the table, and download it as **CSV**. Note that reopening an old saved run shows its report but not its charts — charts need a fresh simulation's raw series.

## Batch simulate

Batch mode flies **every motor that fits the mount** (after your manufacturer/class/OOP filters) through the current design at each motor's auto-optimal delay, then grades each flight against your acceptance criteria — minimum rod-exit velocity, minimum thrust:weight, and an apogee window. Results append to the run history and download as CSV. This is the motor-shopping tool: pick the airframe, pour in a manufacturer's whole catalog, and read off which motors hit your target altitude safely.

---

<a id="multi-stage-and-clusters"></a>

## Multi-Stage and Clustered Rockets

Once you are comfortable with single-stage flights, the same tools scale up to staged and clustered rockets.

## Staging and separation

Add stages as siblings under the rocket root; they flatten nose-to-tail into one chain for layout. A **lower** stage carries a **separation trigger**: its own ejection charge (the default), its own burnout, its own ignition, the upper stage's ignition, launch, apogee, altitude ascending/descending, or never — plus a separation delay. The top stage ignores separation (it separates *from* nothing). After a stage drops, the kernel simulates the separated piece's own flight, which the report can summarize.

## Ignition and the G80 rule

Each staged motor has an **ignition trigger**: Automatic (launch-stage motors at launch, upper motors on the lower stage's ejection charge — right for low and mid power), lower-stage burnout + delay (electronics), launch + delay (timer), lower-stage ejection charge + delay, or never. The critical default is the **G80 rule**: when you drop a **high-power** motor into the **sustainer** of a multi-stage rocket, the app defaults its ignition to *burnout + 1 s* (electronics-timed), because nobody lights a high-power sustainer directly off the booster's ejection charge. You can always override it.

## Booster recovery and clusters

Boosters get their own recovery gear and their own landing-rate verdict in the report — a chuteless high-power booster is called out. **Clusters** (see *Designing the Rocket → Motor mounts and clusters*) let one motor selection drive several tubes; thrust scales with the count and mass sits at the real pattern positions.

## Parallel (strap-on) boosters and pods

Serial stages stack nose-to-tail; a **parallel stage** rides alongside. Build one with the **Booster (parallel stage)** assembly (see *Designing the Rocket → Pods and parallel boosters*): it carries its own separation trigger and delay, its motor ignites by the same trigger rules as any staged motor, and after separation it descends on its **own tracked flight branch** with its own recovery verdict. A non-separating **pod set** simply adds its mass and drag for the whole flight. One planning note: batch motor simulation is disabled while a separating booster is present, just as for serial staging — the motor combinations multiply beyond what a single sweep can grade.

## Power-on drag, per stage

Each stage carries its own **nozzle exit diameter** (see *Visualizing the Design → Drag analysis*), so a booster and a sustainer each get the correct power-on drag reduction during their own burn — the sim picks power-on or power-off drag per stage, per time step, the same way RASAero II does. Leave it at zero and nothing changes.

---

<a id="files-and-formats"></a>

## Files, Units, and Offline Use

Finally, save your work, move it between tools, and set up the app for offline use.

## File formats

The app reads and writes several formats. What survives a round-trip depends on how much each format can represent:

| Format | Import | Export | Notes |
|---|---|---|---|
| **.ork** (OpenRocket) | Yes | Yes | Full fidelity — all 19 types, motors, materials, overrides, tabs, clusters, shoulders, staging, pods/boosters, nozzle exit diameters. Accepts zipped or bare XML; handles legacy ≤15.03 files. |
| **.rkt** (RockSim) | Yes | Yes | Up to **3 stages** (export throws beyond that). Uniquely keeps motor designations the desktop drops, so it auto-loads motors. Clusters split into individual tubes; spill holes unsupported. |
| **.CDX1** (RASAero II) | Yes | Yes | Aerodynamics only — **no mass or material data**. Walls default to a faked 2 mm and the importer warns you to "review masses before trusting the numbers." No motor mounts; engine names surface as a note. Strict export validation (≤3 stages, one fin set per tube, 3–8 fins, conical transitions only). |
| **.obj** (Wavefront) | — | Yes | External shell only — the meshes the 3D view renders. Meters, nose at x=0. Not guaranteed watertight; for print-preview/CAD reference. |
| **.svg** (fin template) | — | Yes | True-scale 1:1 cut template with calibration ruler (see *Visualizing the Design*). |
| **.csv / .xlsx** (component data) | — | Yes | Every component as one row — dimensions, material, shape, and the engine's computed mass/CG/position — in your preferred units. For sharing measurement data with people who don't run a simulator. |
| **.svg / .png / .jpg** (2D drawing & 3D snapshot) | — | Yes | The 2D side view with a data header (dimensions, mass, CG/CP, stability margin) via **⬇ SVG** (physical-mm size — prints at true 100 % scale) and **⬇ Image** (PNG or JPG at HD / 4K / 8K width). The 3D view's **📷 Image** button re-renders the scene at the chosen resolution — an 8K snapshot is genuinely 8K — with the same data header. These are the drawings L3 and Tripoli Class 3 documentation packets ask for. |
| **.glb** (glTF binary) | — | Yes | Modern 3D model *with your component colors as real materials* — opens directly in Windows 3D Viewer, PowerPoint, Blender, Fusion 360, and web viewers. Meters; nose at the origin, rocket along +X. |
| **.stl** (whole rocket / per component) | — | Yes | Two very different exports, both in **millimetres**. Save/Export → whole-rocket *display shell* (reference only — not watertight). The real one: select a component and press **🖨 STL for printing** in its property panel — a *guaranteed-watertight solid* built for slicers: hollow nose cones and transitions with their shoulders and caps, single fins with the through-the-wall tab merged in, true-bore centering rings, bulkheads, and tubes. Fins print as flat prisms (airfoil shaping is left to sanding); verify fit before a long print. |

The most important domain caveat is **RASAero has no mass data** — a `.CDX1` import gives you accurate geometry but placeholder masses, so treat its predicted altitudes as provisional until you add real materials and overrides. `.ork` is the only lossless format; use it as your working save.

## Units and preferences

Open **Preferences** to switch between one-click **Metric** and **Imperial** presets, or set each quantity individually — length, motor dimensions, distance, mass, velocity, wind speed, acceleration, angle, density, temperature, and pressure. You also choose whether round parts are entered as **diameter or radius**. The same dialog sets the app's **Theme** — **Light**, **Dark**, or **Follow system** (which tracks your operating system's light/dark setting); this is what drives the theme-aware plot axes and the rest of the interface. An **Aerodynamics** section holds the optional **Rogers Modified Barrowman (Kbf)** method and the three-way **Aerodynamics model** choice — **Classic** (Extended Barrowman, desktop parity, the default), **Auto** (classic until a flight is projected past Mach 0.9, then the whole flight re-flies on the supersonic model), or **Supersonic** (the RASAero-class model at all speeds). What they do and when to use them is covered in *How It Works*. Every saved simulation records which aero model produced it, and a flight that goes supersonic on the classic model raises an in-report alert so you know the better model exists. Because everything is stored in SI internally, **switching units never changes your design** — it only changes how the numbers are displayed and entered.

## Installing, offline, and saving your work

Online OpenRocket is a **PWA** — install it from the browser and it runs offline, since the physics kernel, the motor database metadata, and the preset catalog are all bundled locally. Only motor thrust curves fetch on demand, and once fetched they cache in your browser. Your work — the design tree, assigned motors, launch conditions, per-stage motor-length limits, run history, and motor filters — persists to local storage and survives reloads. The app **header** carries **Open…** and the **Save / Export** menu (.ork, .rkt, .CDX1, .obj), alongside the **Guide**, **Changelog** (the version badge), and **Preferences**; **New** sits atop the component tree in the Design workspace. For a durable archive or to move a design to another machine or to the OpenRocket desktop, **Save as .ork** — it's the format that keeps everything.

Because the whole app is a self-contained static build, the same files can also be **embedded inside another web page** — for example a WordPress post — through an `<iframe>`. If you meet Online OpenRocket living inside someone else's site rather than at its own address, it is the identical app running the identical kernel, with the same design, motor, and simulation tools described in this guide.

---

<a id="how-it-works-physics"></a>

## How It Works: Physics & Math

Online OpenRocket is not an approximation *of* OpenRocket — it **is** OpenRocket. The simulation engine is the real OpenRocket 24.12 physics kernel (the Java `info.openrocket.core` package), compiled to JavaScript with TeaVM and verified bit-for-bit against the desktop program by an automated differential test. Every number below comes from that kernel: when your browser reports an apogee, it ran the same code, in the same order, with the same rounding as the desktop app. This section explains what that code does so you can decide how far to trust it — and where its honest limits are.

Throughout, the engine works in **pure SI units** (metres, kilograms, seconds, newtons) and **radians**. Degrees, feet and grams exist only at the file-format and screen boundaries.

## How a flight is simulated

The rocket is treated as a **six-degree-of-freedom (6DOF) rigid body**: three of position and three of orientation. Orientation is stored as a **quaternion**, never as Euler angles, which avoids gimbal-lock singularities when the rocket pitches over at apogee.

The equations of motion are integrated with the classical **fourth-order Runge–Kutta method (RK4)**. Each step evaluates the derivatives (velocity, acceleration, angular velocity, angular acceleration) four times — at the start, twice at the midpoint, and at the end — and combines them:

```
y(n+1) = y(n) + (h/6)·(k1 + 2·k2 + 2·k3 + k4)
```

RK4 is far more accurate for a given step size than simple Euler integration, and the fourth-order error term keeps energy drift small over a full flight.

**Adaptive time step.** The step length `h` is not fixed. Each step, the kernel takes the *smallest* of eight candidate limits, so the step shrinks automatically wherever the flight gets delicate:

- the user's requested time step (default 0.05 s), or one-fifth of it while still on the launch rod;
- a maximum pitch-angle change per step (default 3°) and a maximum roll-angle step;
- limits on how fast the roll rate and the pitch/yaw rates may change in one step;
- one-tenth of the launch-rod length while the rocket is still on the rod;
- no more than 1.5× the previous step (smooth growth).

A floor of 1/20th of the nominal step prevents numerical stall. This is why a wobbly, near-unstable flight is computed with many small steps while a clean boost coasts in large ones.

**Phases of flight.** Before lift-off the rocket cannot sink into the ground (downward acceleration is zeroed). While on the **launch rod/rail**, motion is projected onto the rod direction and angular acceleration is forced to zero — the rod holds the rocket straight until it clears. Once clear, full 6DOF applies: aerodynamic moments are shifted from the center of pressure to the current center of gravity, and pitch, yaw and roll accelerations follow from the body's moments of inertia.

After a **recovery device deploys**, the rocket is no longer a rigid airframe flying nose-first, so the simulation switches from 6DOF to a simpler **3-degree-of-freedom (point-mass) descent**: it tracks position under gravity and parachute drag but not tumbling orientation. A parachute's drag coefficient defaults to **0.8**. An airframe that goes unstable without a chute is handed to a separate tumble model instead.

## Aerodynamics — the Extended Barrowman method

Stability and normal force come from the **Extended Barrowman method** — the 1966 Barrowman equations, extended for body lift and for transonic/supersonic fins. The rocket's total normal-force coefficient slope (CNα) and center of pressure (CP) are built up component by component and summed.

**Nose cones and transitions.** A body of revolution generates normal force only where its cross-sectional area *changes*. For a transition from fore area `A₀` to aft area `A₁`, the kernel uses

```
CNα = 2·(A₁ − A₀) / A_ref      CP = (L·A₁ − V) / (A₁ − A₀)
```

where `L` is length, `V` the enclosed volume and `A_ref` the reference area. A pure cylindrical body tube produces no area-change normal force at all.

**Body lift (Galejs extension).** Straight Barrowman ignores the lift a long body generates at angle of attack. The kernel adds the Galejs correction, with a coefficient of 1.1, proportional to the body's planform area and to `sin²(α)/α`, acting at the planform centroid. This is what keeps a long, small-finned rocket from looking falsely over-stable at high angle of attack.

**Fins.** A fin set's lift-curve slope in the subsonic regime is

```
CNα1 = 2π·s² / ( 1 + √(1 + (1 − M²)·(s²/(A_fin·cosΓ))²) ) / A_ref
```

where `s` is the fin span, `A_fin` the fin area, `Γ` the mid-chord sweep angle and `M` the Mach number. This is the standard finite-span lifting result: aspect ratio and sweep both reduce slope, and the `(1 − M²)` term is the Prandtl–Glauert compressibility factor. Above Mach ~1.5 the kernel switches to a supersonic formula (with Mach-dependent K1, K2, K3 terms) and interpolates smoothly through the transonic gap. Multiple fins interfere with each other and with the body: fixed empirical factors reduce the per-fin slope for 4, 5, 6… fins, and a body-interference multiplier `(1 + τ)` accounts for the airflow the body diverts onto the fins (τ = r/(s+r), the body-radius-to-total-semispan ratio).

**Optional: the Kbf body carryover (Rogers Modified Barrowman).** Classic Barrowman keeps the fins-in-presence-of-body factor above but drops its reciprocal — the lift the fins induce *on the body* near the fin root, the `K_B(W)` carryover of NACA Report 1307. Turning on **Preferences → Aerodynamics → Rogers Modified Barrowman (Kbf)** adds it back: slender-body theory makes the fin-plus-carryover total `(1 + τ)²` times the fin-alone value, so the added body load is `τ · (fin CNα)`, acting at the fin root quarter-chord. The result is a somewhat higher total CNα and a slightly **more aft CP** — a more conservative stability margin, matching the direction RASAero II's "Rogers Modified Barrowman" method reports. Off (the default) is exactly classic Barrowman, bit-for-bit.

**Roll.** Canted fins produce a roll-driving moment proportional to the cant angle, opposed by a **roll-damping moment** that grows with roll rate — so a canted rocket spins up toward a steady-state roll rate rather than accelerating forever.

**Drag build-up.** Total drag is assembled from four independent pieces:

```
CD = CD_friction + CD_pressure + CD_base + CD_override
```

- **Skin friction** comes from the Reynolds number and a skin-friction coefficient (laminar, turbulent or transitional), with corrections for surface roughness/finish, for compressibility near Mach 1, and a whole-rocket fineness-ratio correction `(1 + 1/(2·f_B))`.
- **Pressure drag** for nose cones is interpolated from wind-tunnel/free-flight data (the kernel embeds experimental curves for ogive, conical, ellipsoid, power, parabolic and Haack shapes from NASA TR-R-100), plus stagnation drag at any forward-facing area increase.
- **Base drag** on the blunt tail follows `CD_base = 0.12 + 0.13·M²` subsonic and `0.25/M` supersonic.
- **Power-on base drag**: while a stage's motor is thrusting, its exhaust plume pressurizes the base over the nozzle-exit footprint, so the kernel subtracts the nozzle-exit area from the drag-producing base area — boost drag is lower than coast drag by `CD_base · A_nozzle/A_ref`. This is driven by the per-stage **nozzle exit diameter** (0 = no reduction, the default) and mirrors RASAero II's power-on/power-off CD distinction; the power-on curve is visible in the Drag analysis panel.
- **Override** lets you pin a component's CD to a measured value.

The drag coefficient is finally resolved into an axial component using an angle-of-attack multiplier, and pitch/yaw **damping moments** (which resist the rocket "weather-vaning" too sharply, especially the apogee turnover) are subtracted from the aerodynamic moments.

## The optional supersonic aerodynamics model (beta)

Classic Extended Barrowman is honest only to roughly Mach 1.5: it freezes body CP at its Mach-1 value forever, uses half the theoretical supersonic fin lift, clamps wave-drag data flat past Mach 2–4, and lets boattails ignore Mach entirely. Turning on **Preferences → Aerodynamics → Supersonic aerodynamics** replaces those with a RASAero-class model built from the open literature:

- **Fin lift** at the proper 2D Busemann level with a finite-span correction, evaluated analytically to any Mach (no grid clamp), plus the exact **NACA Report 1307** body-fin interference split with an afterbody carryover factor. This raises fin lift ~25–35% even subsonic (the physics behind RASAero's "Rogers Modified Barrowman"), so CP and stability shift slightly on all flights while the option is on.
- **Body (nose) lift grows with Mach**, bracketed by exact Taylor–Maccoll cone theory, so the combined CP moves with Mach the way wind tunnels measure instead of racing forward.
- **Drag**: per-shape supersonic thickness wave drag for fins, supersonic boattail/reducer wave drag, nose wave drag with its physical high-Mach decay, a vacuum-limit cap on base drag, fin-body junction interference drag, and **Van Driest II** compressible skin friction above Mach 4.

The model is scored continuously against an automated validation harness of published measured data — NASA's ARCAS sounding-rocket wind-tunnel tests (Mach 0.6–4.63), the Army-Navy Basic Finner free-flight range data (Mach 1.05–4.5), and the AGARD HB-2 hypersonic standard model (to Mach 10). Supersonic CP matches the ARCAS tunnel within ±2% of body length through Mach 4.6 — including above Mach 3.5, where RASAero's own published prediction diverges from that same tunnel. Known honest limits: transonic **peak** drag (Mach 0.95–1.2) reads low against tunnel data (a regime every engineering method struggles with), and everything above Mach 10 is physically-shaped extrapolation.

**Choosing a model.** The preference is three-way. **Classic** (the beta default) is bit-identical to desktop OpenRocket. **Supersonic** uses the new model at all speeds. **Auto** is the practical setting for mixed fleets: every flight first flies classic; if it's projected past **Mach 0.9** (the transonic onset, where classic aero starts degrading), the *entire* flight re-flies on the supersonic model — subsonic flights keep exact desktop parity, fast flights get the validated physics, and the design's displayed stability follows whichever model the flight used (an "M+ aero" chip appears in the header strip). A model always applies to the **whole flight**, subsonic portions included, so switching shifts stability and apogee. If you fly supersonic while on Classic, the report warns you and offers a one-click switch to Auto. Every saved run records which model flew it.

## Mass, center of gravity and inertia

At every step the kernel computes the rocket's total **mass**, **center of gravity** and **moments of inertia** (longitudinal and rotational) by summing every component — walls, nose, fins, mounts, mass objects — from its geometry and material density, honoring any user overrides. The **motor** is then added as a separate rigid body: as propellant burns, both its mass and its own CG shift, read from the thrust curve's mass/CG data points, and the two bodies are combined. This is why your stability margin (the CP-to-CG gap, measured in calibers) changes continuously through the boost as propellant leaves the tail.

## Atmosphere — the International Standard Atmosphere

Air properties follow the **International Standard Atmosphere (ISA)**, with sea-level defaults of **288.15 K (15 °C)** and **101 325 Pa**, and a troposphere temperature **lapse rate of −6.5 K per km** up to 11 km. Above that the kernel models the full layered ISA profile: the isothermal tropopause to 20 km, the warming stratosphere, and so on up past 84 km.

Within each layer, pressure follows the **barometric formula**. For a layer with a non-zero lapse rate `L`:

```
p = p_b · (1 + (h − h_b)·L / T_b)^(−g / (L·R))
```

and the isothermal exponential form where the lapse rate is zero. Temperature is linear within a layer; density and speed of sound then come from the ideal-gas relations `ρ = p/(R·T)` and `a = √(γ·R·T)` with `γ = 1.4`. You can override the launch-site temperature and pressure, and the model rebuilds a consistent profile above you. One documented caveat carried straight from OpenRocket: above ~32 km the layered values drift from the exact standard by a few percent — irrelevant for essentially all hobby altitudes.

## Gravity and geodesy

Gravity uses a **WGS84 ellipsoid** model rather than a single constant. Sea-level gravity varies with latitude φ via the Somigliana formula:

```
g₀ = 9.7803267714 · (1 + 0.00193185138639·sin²φ) / √(1 − 0.00669437999013·sin²φ)
```

so it is weaker at the equator and stronger at the poles. An altitude correction `(R_earth / (R_earth + h))²` reduces it as the rocket climbs. When a launch latitude/longitude is supplied and geodetic computation is enabled, the kernel also adds the **Coriolis acceleration** from Earth's rotation. The altitude correction assumes a spherical Earth — the kernel's own comment notes this is a deliberately small approximation.

## Motor thrust-curve model

A motor is a table of measured samples: time, thrust, remaining mass and propellant CG, exactly as published in RASP/RSE thrust-curve files. At any simulation instant the kernel finds the bracketing samples and does **linear interpolation** between them (snapping to a sample when it lands within 0.1 ms of one). Instantaneous thrust, motor mass and motor CG are all read this way; total impulse and average thrust are derived from the same curve. There is no internal combustion model — the certified thrust curve *is* the input, which is exactly what you want for matching real motor performance.

## Wind, turbulence, and why our results are repeatable

Wind is modeled as **pink noise** around a mean speed. You set an average wind speed and a standard deviation (their ratio is the *turbulence intensity*); the kernel drives a two-pole pink-noise filter (spectral exponent α = 5/3, sampled every 0.05 s and linearly interpolated) to produce gusts with a realistic frequency content — more low-frequency wander than white noise. In this single-level model the wind blows in one horizontal direction and does not vary with altitude.

There is also a deliberately tiny random perturbation (±0.0005) added to the pitch and yaw moments each step, which prevents an unnaturally perfect, knife-edge-symmetric flight from never tipping over.

Here is the one intentional difference from the desktop. Desktop OpenRocket seeds these random streams from the wall clock, so two identical runs give slightly different gust histories. **Online OpenRocket seeds them deterministically** (a fixed seed, 42). Same design + same settings ⇒ **exactly the same flight, every time** — better for teaching, sharing and comparing, and it makes a clean before/after comparison of a design change possible. (For the same reason the engine also fixes a couple of internal iteration orders that the desktop leaves free to wander at the sub-rounding-error level. Physics results stay inside the desktop's own run-to-run envelope.)

---

<a id="limitations-and-references"></a>

## Assumptions, Limitations & References

The physics above is powerful, but every simulation makes modeling choices. Knowing them tells you exactly where to trust the numbers and where to add engineering margin.

## Assumptions and honest limitations

- **Speed regime.** Extended Barrowman is at its best **subsonic and through the transonic region**. Fins get a genuine supersonic treatment, but body CNα and CP above Mach 1 are assumed equal to their subsonic values, and the engine raises a *supersonic* warning past Mach 1.1. Treat high-supersonic stability numbers as indicative, not precise — the Drag analysis chart labels its values above ~Mach 1.5 as approximate for the same reason.
- **Small-angle / attached-flow aerodynamics.** The method assumes slender bodies and largely attached flow. A stall angle around 17.5° caps fin normal force, and the body-lift term is damped at very low speed near apogee to avoid an artifact. Extreme angles of attack are approximations.
- **Descent is a point mass.** Under parachute the rocket is 3DOF: drag (default Cd 0.8) and gravity only. Swinging, spilling, and canopy dynamics are not modeled, and **streamers** — especially at extreme length-to-width ratios — are represented by an effective drag area, not by true flexible-body aerodynamics.
- **Wind is horizontal and altitude-uniform** in the standard model; real wind shear and vertical gusts are not captured here.
- **Geodesy approximations.** The altitude gravity correction assumes a spherical Earth; the atmosphere drifts a few percent above ~32 km.
- **The thrust curve is trusted verbatim.** Motor-to-motor manufacturing variation, temperature effects on propellant, and off-axis thrust are outside the model.

None of these are bugs — they are the modeling choices of a mature, widely validated tool. Because you are running that tool's actual kernel, its accuracy *and* its limits are inherited exactly, and your results will match the OpenRocket desktop program.

## Licensing and source code

Online OpenRocket is **free software**, released under the **GNU General Public License, version 3 or later (GPL v3+)** — the same license as OpenRocket itself, which it inherits. You are free to use, study, share, and modify it under that license. Because the GPL requires that anyone running a distributed build be offered its corresponding source code, the app's header carries a **source (GPL)** link — titled *"This app is free software under the GPL v3 or later — source code for this build"* — that opens the public source repository. If you want to see exactly what the app does, that link is the front door.

## References & further reading

- **Barrowman, J. S., & Barrowman, J. A. (1966).** *The Practical Calculation of the Aerodynamic Characteristics of Slender Finned Vehicles.* (J. S. Barrowman, M.S. thesis, The Catholic University of America, 1967.) The origin of the center-of-pressure and normal-force method used here.
- **Niskanen, S. (2009, rev. 2013).** *Development of an Open Source model rocket simulation software* / *OpenRocket technical documentation.* The definitive derivation of OpenRocket's extended Barrowman aerodynamics, drag build-up, mass model and simulation loop. https://openrocket.info/documentation.html
- **Galejs, R. (2006).** *Wind instability — what Barrowman left out.* Sport Rocketry / online. Source of the body-lift extension applied to long airframes.
- **NASA (1963).** *Collection of Zero-Lift Drag Data on Bodies of Revolution from Free-Flight Investigations,* NASA TR-R-100 (NTRS 19630004995). Source of the nose-cone pressure-drag data tables.
- **Pitts, W. C., Nielsen, J. N., & Kaattari, G. E. (1957).** *Lift and Center of Pressure of Wing-Body-Tail Combinations at Subsonic, Transonic, and Supersonic Speeds,* NACA Report 1307. Source of the optional body-in-presence-of-fins (Kbf) interference factor.
- **Rogers, C. E., & Cooper, D. (2011).** *RASAero Aerodynamic Analysis and Flight Simulation Program.* Rogers Aeroscience. Documents the power-on/power-off drag distinction and the Rogers Modified Barrowman method that inspired those options here.
- **International Standard Atmosphere:** ISO 2533:1975; and the *U.S. Standard Atmosphere, 1976* (NOAA/NASA/USAF). Basis of the temperature, pressure and density model.
- **WGS84 / Somigliana gravity formula:** *Department of Defense World Geodetic System 1984,* NIMA TR8350.2. Basis of the latitude- and altitude-dependent gravity model.
- **Runge–Kutta integration:** Butcher, J. C., *Numerical Methods for Ordinary Differential Equations;* or Press et al., *Numerical Recipes,* ch. 16. The RK4 scheme used for the equations of motion.
- **OpenRocket project:** https://openrocket.info — source at https://github.com/openrocket/openrocket. Licensed under the **GNU General Public License v3.0-or-later**: https://www.gnu.org/licenses/gpl-3.0.html . Online OpenRocket inherits this license.
