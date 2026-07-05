# RASAero II vs. OpenRocket: Capability Gap Analysis for a Web-Based OpenRocket Port

## Executive Summary

RASAero II's decisive advantage over OpenRocket (and therefore over our OpenRocket-kernel web app) is **aerodynamic fidelity in the transonic, supersonic, and hypersonic regimes** — it predicts a full set of coefficients (zero-α drag, drag/lift/normal-force vs. angle of attack, and center of pressure vs. both Mach and α) continuously from Mach 0.01 to Mach 25, whereas OpenRocket's Extended Barrowman is a subsonic-focused method whose accuracy degrades badly above roughly Mach 1.5–2 [rasaero.com; Rogers & Cooper 2011; sciencedirect.com]. Four concrete, discrete features flow from that: (1) a **distinct power-on vs. power-off drag coefficient** driven by a nozzle-exit-diameter base-drag model; (2) the **"Rogers Modified Barrowman Method"** for CP that adds three physics terms Barrowman omits, plus dedicated supersonic CP models; (3) **eight selectable fin airfoil cross-sections** with configurable leading-edge bluntness; and (4) **component-level drag breakdowns and power-on/power-off CD-vs-Mach plots**. Encouragingly, the underlying methods are largely **openly documented** — RASAero's own User Manual and the peer-reviewed Rogers & Cooper (2011) paper describe the approaches, and they lean on public sources (Barrowman, Jorgensen crossflow, NACA/DATCOM-style airfoil wave-drag) — so several gaps are implementable on top of our kernel, though the full supersonic/hypersonic aero rebuild is a large effort. RASAero's headline accuracy (3.47% mean apogee error across 41 flights to 293,000 ft) is **vendor self-published** and should be treated as directional, not independently verified.

---

## RASAero-Only Capabilities, Ranked by Value

### 1. Supersonic & hypersonic aerodynamic prediction (Mach 0.01 → Mach 25)

**(a) What it is / why flyers value it.** RASAero II predicts a complete aerodynamic coefficient set — zero-angle-of-attack drag, drag at non-zero α, lift and normal-force coefficients vs. α, and center of pressure as a function of both Mach and α — continuously across subsonic, transonic, supersonic, and hypersonic regimes, from Mach 0.01 to Mach 25, for both power-on (thrust) and power-off (coast) phases [rasaero.com Manual; Rogers & Cooper 2011]. This is *the* reason high-power and extreme-altitude flyers reach for RASAero: minimum-diameter and multi-stage projects routinely punch through Mach 2–4 (e.g., the MESOS two-stage hit Mach 4.18 at 62,301 ft en route to 293,488 ft), and both drag and stability behave very differently there than a subsonic method predicts.

**(b) RASAero vs. OpenRocket.** OpenRocket uses Extended Barrowman — a semi-empirical model the literature describes as "fundamentally limited to subsonic flow regimes" that "cannot accurately capture the aerodynamic behaviour of rockets operating in transonic and supersonic flight" [sciencedirect.com, Ironbark 2026]. OpenRocket's own roadmap lists "better support for supersonic simulation" as a still-pending feature. Practical validity is roughly Mach < 1.5–2; there is no hypersonic model and no explicit full power-on coefficient set. (Minor nuance: OpenRocket *does* cover low-supersonic drag and applies a Mach-dependent base-drag term, so the gap is most acute in the hypersonic range and in the explicit power-on aero.)

**(c) Implementability.** The *capability description* is openly documented (Manual + Rogers & Cooper 2011 paper), but the paper is a high-level method overview, not a reproducible spec with all coefficient tables and empirical constants. Reconstructing the full multi-regime model — transonic drag-rise, supersonic wave drag by nose/fin shape, hypersonic corrections, α-dependent coefficients — would require assembling published aerodynamics (Barrowman, Jorgensen, NACA/USAF-DATCOM wave-drag correlations, van Driest skin friction) and re-validating. **Complexity: large.** This is the flagship gap and the hardest to close well.

**(d) Confidence: high** (that the gap exists and is documented as a capability). Sources: rasaero.com User Manual; Rogers & Cooper 2011 (demec.ufpr.br mirror); sciencedirect.com S1270963826001392.

---

### 2. Power-on vs. power-off drag coefficient (nozzle-exit base-drag model)

**(a) What it is / why flyers value it.** RASAero computes two distinct drag coefficients. A per-stage **nozzle exit diameter** input drives a base-drag calculation: motor exhaust pressurizes the base area during thrust, reducing base drag, so the **power-on CD is typically lower than the power-off CD**. The flight sim uses power-on CD during boost and power-off CD during coast [rasaero.com Manual; Rogers & Cooper 2011]. As nozzle exit area approaches the rocket's base area (large minimum-diameter motors, sustainers), the difference becomes significant — directly affecting boost velocity and predicted apogee. RASAero even extends this model for very large nozzle exit diameters at supersonic/hypersonic Mach for satellite-launch-vehicle stages [rasaero.com dl_software_ii].

**(b) RASAero vs. OpenRocket.** Verified directly against the carved 24.12 kernel: `BarrowmanCalculator.calculateBaseCD(double m)` is a pure function of Mach only (≈0.12+0.13·m² subsonic, 0.25/m supersonic) — no thrust state, no nozzle-exit-diameter input, no exhaust-plume term. OpenRocket produces no distinct power-on CD and models no motor-exhaust base-drag reduction. (OpenRocket does reduce base drag during burn in some paths, but not via a nozzle-exit-area model.)

**(c) Implementability.** The mechanism is standard rocket aerodynamics (base-pressure recovery from the exhaust plume) and is described conceptually in RASAero's docs; the exact empirical base-drag-vs-nozzle-ratio correlation is not fully published but can be approximated from open base-drag literature. The engine already has motor burn state and geometry; adding a nozzle-exit input and a power-on base-drag reduction term is a bounded change to the drag pipeline. **Complexity: small–medium.** High value-to-effort ratio.

**(d) Confidence: high.** Sources: rasaero.com Manual & dl_software_ii; Rogers & Cooper 2011; OpenRocket 24.12 `BarrowmanCalculator.java` (direct inspection).

---

### 3. Rogers Modified Barrowman CP + dedicated supersonic CP models

**(a) What it is / why flyers value it.** For subsonic potential CNα and CP, RASAero uses the **"Rogers Modified Barrowman Method,"** which adds three physics terms Barrowman omits: (i) the body-tube cylinder's contribution to body CNα, (ii) the body-in-presence-of-fins interference factor **Kbf** (from NACA TR-1307, which Barrowman set to zero), and (iii) **Jorgensen viscous crossflow** for the forward movement of CP with angle of attack [rasaero.com Manual]. On top of that, RASAero adds improved supersonic CP models; supersonic CP typically moves forward ~1 caliber, and RASAero's own validation shows "very accurate" supersonic CP from Mach 1.5–3 against NASA ARCAS wind-tunnel data (up to Mach 4.63) [rasaero.com dl_software_ii; ARCAS comparison PDF]. Forward CP travel at supersonic Mach is a real stability hazard for high-power flyers — knowing it lets them size margin correctly (RASAero recommends +1.0 caliber extra supersonic margin).

**(b) RASAero vs. OpenRocket.** OpenRocket uses Extended Barrowman (with a Galejs body-lift correction) but does not model Kbf interference, and its CP does not capture the supersonic forward shift with fidelity. The α-dependent viscous-crossflow CP movement is not part of OpenRocket's baseline output.

**(c) Implementability.** Highly documented and public: Barrowman, Jorgensen crossflow, and Kbf (NACA TR-1307) are all in the open literature, and RASAero's Manual names them explicitly. The subsonic modifications (Kbf, body-tube CNα, crossflow) are **medium** complexity to add to the existing CP calculator. The supersonic CP models are tied to gap #1 and are harder to reproduce precisely (some empirical/proprietary calibration). **Complexity: medium** for the subsonic modified-Barrowman terms; **large** for full supersonic CP accuracy.

**(d) Confidence: high** for the method description; **medium** for reproducing RASAero's exact supersonic CP accuracy (vendor-validated single/wind-tunnel datasets). Sources: rasaero.com Manual, dl_software_ii, ARCAS CP/CD comparison PDF; corroborating TRF threads and Rogers & Cooper 2011.

---

### 4. Eight selectable fin airfoil cross-sections + leading-edge bluntness

**(a) What it is / why flyers value it.** RASAero lets the user pick from **hexagonal, NACA, double-wedge, biconvex, hexagonal blunt-base, single wedge, rounded, and square** airfoils, each with airfoil-specific geometry inputs (e.g., diamond leading/trailing-edge lengths, thickness), plus a configurable **leading-edge bluntness radius** (Fin LE Radius, default 0 for a sharp edge). These feed the transonic/supersonic/hypersonic drag prediction [rasaero.com Manual; Rogers & Cooper 2011]. Fin wave drag dominates supersonic drag budgets, so airfoil choice materially changes predicted performance for fast flights.

**(b) RASAero vs. OpenRocket.** OpenRocket's `FinCrossSection` is limited to square / rounded / airfoil with a thickness parameter — no hexagonal/wedge/biconvex options and no configurable LE radius, and its supersonic fin wave-drag treatment is coarse.

**(c) Implementability.** The airfoil list and the fact that they feed wave-drag are documented; the specific wave-drag equations per airfoil come from open supersonic thin-airfoil / NACA / DATCOM theory. The UI/data-model side (extra airfoil enum + geometry inputs) is small; the *payoff* only materializes once the supersonic drag model (gap #1) exists to consume it. **Complexity: small** for the input model alone; **medium–large** to make it aerodynamically meaningful (coupled to #1).

**(d) Confidence: high.** Sources: rasaero.com Manual; Rogers & Cooper 2011; OpenRocket `FinSetCalc`/`FinCrossSection`.

---

### 5. Component-level drag breakdown + power-on/power-off CD-vs-Mach plots and tables

**(a) What it is / why flyers value it.** RASAero outputs drag decomposed by contributor — nose-cone wave drag, boattail wave/base drag, fin friction drag, and combined protuberance drag (launch lug / rail guide / launch shoe) — and can plot **power-on and power-off CD versus Mach number**, showing features like the transonic drag rise starting near Mach 0.90 [rasaero.com dl_software_ii; comp-flight-Cal]. This diagnostic view lets flyers see *why* their rocket is draggy and where to optimize (e.g., boattail, LE radius, protuberance cleanup).

**(b) RASAero vs. OpenRocket.** OpenRocket computes component drag internally but its baseline UI does not surface a full power-on/power-off CD-vs-Mach table/plot, and it has no power-on curve at all (see #2).

**(c) Implementability.** OpenRocket already computes per-component CD internally, so surfacing a component-drag breakdown and a CD-vs-Mach sweep is mostly a data-extraction + charting task on the web side. Adding the power-on curve depends on #2. **Complexity: small** (power-off breakdown/plots) to **medium** (with power-on curve). Good demo/UX win.

**(d) Confidence: high.** Sources: rasaero.com dl_software_ii, comp-flight-Cal; ARCAS CD comparison PDF.

---

### 6. Aerodynamic-coefficient export for external orbital/trajectory simulators

**(a) What it is / why flyers value it.** RASAero can export its predicted aerodynamic coefficients for use as inputs to *other* flight-simulation programs, including orbital launch-vehicle trajectory codes (RASAero itself does not run ascent-to-orbit) [rasaero.com Manual / scribd]. Advanced/amateur-rocketry and student teams value this as a bridge to higher-fidelity 3-DOF/6-DOF trajectory tools. This is the lowest-ranked because it serves a narrow, advanced slice of users.

**(b) RASAero vs. OpenRocket.** OpenRocket has no equivalent "coefficient table export for external trajectory sims" workflow.

**(c) Implementability.** Trivial as a *format* problem once the coefficients exist — it's a CSV/table export. But it is only worth anything if the underlying multi-regime coefficients (#1) are trustworthy. **Complexity: small** (mechanically), gated on #1. **Confidence: high** on the capability existing; sources: scribd RASAero II Manual copy; rasaero.com/home.

---

## What We Already Match (Do Not Rebuild)

Per the project baseline, our app already covers the entire OpenRocket design/sim surface, so **do not** re-implement: full component design editor (all component types, freeform fins, clusters, serial multi-stage); thrustcurve.org motor database; launch-condition modeling (wind, altitude, temperature, pressure, latitude); 6-DOF RK4 flight simulation with reports and safety checks; batch motor simulation; and file interchange (.ork/.rkt/.CDX1 import-export, OBJ/SVG export). Notably, RASAero-style **launch-site/altitude-specific launch conditions** are already matched by our launch-conditions feature, and RASAero's **.CDX1** interchange is already supported — those are *not* gaps. The genuinely missing items are the six aerodynamic-fidelity capabilities above, all of which live in the drag/CP/coefficient layer, not the design or trajectory-integration layers.

---

## Caveats & Uncertainty

- **Vendor self-published validation.** RASAero's accuracy figures are first-party and self-selected: the 41-flight dataset showing **3.47% mean apogee error, 80.6% within ±10%, 41.7% within ±5%** (3,577 ft to 293,488 ft) and individual flights (MESOS −1.26%; Caliber −2.22%; Violent Agreement CD-vs-in-flight-CD to Mach 2.4) all come from rasaero.com [comparisons-alt, comparisons-flight, comp-flight-Cal]. They are directionally credible and partly rest on real third-party telemetry (MESOS is independently documented at multitronix.com), but they are **not independent, peer-reviewed validation.** Treat as "RASAero's own claims," not ground truth.
- **Hindcast tuning.** The headline MESOS −1.26% apogee match is a *postflight* simulation with stage-2 ignition delay tuned to the flight data — a defensible adjustment, but not a pure a-priori prediction.
- **Method documentation is partial.** The Manual and Rogers & Cooper (2011) describe *what* methods are used (Barrowman, Jorgensen, Kbf, DATCOM-style wave drag) but do not publish every empirical coefficient/table. Some supersonic/hypersonic calibration is effectively proprietary, so a re-implementation would need its own validation rather than a line-by-line port. Implementability ratings for gaps #1 and #3 (supersonic side) carry this uncertainty.
- **Marketing framing.** RASAero's "most accurate available… equivalent to professional engineering methods" is a vendor claim [scribd Manual], reported here only as an attributed statement, not endorsed.
- **PDF-fetch limitations.** Several primary PDFs (User Manual, Rogers & Cooper 2011) could not be rendered directly during verification and were confirmed via search excerpts and mirrors; wording is consistent across mirrors but exact page/figure references should be re-checked against the live PDFs before implementation.
- **Time-sensitivity.** RASAero II is actively revised (release notes cite ongoing base-drag/CP model changes). Feature specifics (e.g., large-nozzle-exit extensions) reflect the current release and may shift.
- **Alternative approach (context, not a gap).** The peer-reviewed **Ironbark** framework couples the higher-order panel code **PANAIR** with FRICTION-derived viscous skin-friction models for subsonic+supersonic aero, ingesting OpenRocket files for geometry/mass/thrust [sciencedirect.com]. It is a *different, potentially higher-fidelity path* than replicating RASAero's semi-empirical method — worth noting for the maintainer's strategy, though PANAIR cannot handle transonic/separated flow and requires heavier compute.

## Open Questions

1. **Build vs. bridge:** Is the goal to *replicate* RASAero's semi-empirical supersonic aero inside our kernel, or to *interoperate* (import RASAero/.CDX1 coefficient tables, or bridge to a PANAIR/Ironbark-style backend)? The two paths have very different complexity and validation burdens.
2. **How much supersonic fidelity is "enough"** for our high-power users — does a good power-on/power-off base-drag model plus modified-Barrowman CP (gaps #2 and #3, both medium effort) capture most of the practical value without the full Mach-0.01–25 rebuild (#1)?
3. **Can we obtain the empirical coefficient tables** (transonic drag-rise, airfoil wave-drag, Kbf, supersonic CP) in reproducible form from open literature (Barrowman thesis, Jorgensen NASA reports, USAF DATCOM, NACA TR-1307) with enough completeness to validate against RASAero and against real flight data?
4. **Validation dataset:** Do we have access to independent, non-vendor flight telemetry (accelerometer/GPS) to benchmark any new aero model, given RASAero's own numbers are self-published?

## Sources

- RASAero II User's Manual — https://www.rasaero.com/dloads/RASAero%20II%20Users%20Manual.pdf (and scribd/studylib mirrors)
- Rogers & Cooper (2011), *RASAero Aerodynamic Analysis and Flight Simulation Program* — http://servidor.demec.ufpr.br/CFD/bibliografia/aerodinamica/Rogers_Cooper_2011.pdf (also ftp.demec.ufpr.br mirror)
- RASAero II download / release notes & feature list — https://www.rasaero.com/dl_software_ii.htm
- RASAero II flight-prediction validation — https://www.rasaero.com/comparisons-flight.htm
- RASAero II apogee-altitude validation (41 flights; MESOS) — https://www.rasaero.com/comparisons-alt.htm
- RASAero II Caliber flight comparison — https://www.rasaero.com/comp-flight-Cal.htm
- RASAero II vs. ARCAS wind-tunnel CP/CD comparison — https://www.rasaero.com/dloads/RASAero%20II%20Comparisons%20with%20ARCAS%20CP%20and%20CD%20Data.pdf
- RASAero II product scope (Manual excerpt) — https://www.scribd.com/document/928315619/ ; https://www.rasaero.com/home.htm
- Ironbark (2026), *Trajectory and aerodynamic simulator for high-power student rockets*, Aerospace Science and Technology — https://www.sciencedirect.com/science/article/pii/S1270963826001392
- OpenRocket 24.12 core reference — `core/.../aerodynamics/BarrowmanCalculator.java`, `FinSetCalc`, `FinCrossSection` (local carved source, direct inspection)