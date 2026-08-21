# Validation Anchor Set — Supersonic/Hypersonic Aero Build

Date: 2026-08-03. Assembled from the research workflow: local-PDF digitizations (ARCAS comparison doc, MESOS Rev B doc), dataset hunts (AGARD HB-2, Basic Finner, NTRS sounding rockets, amateur >M4 flights), and their adversarial verify passes. Companion: `docs/research/rasaero-supersonic-spec-2026-08-03.md`.

Read-tolerance convention: every graph-read value carries the digitizer's stated tolerance; table values marked *exact* are verbatim printed numbers. All CD/CA/CN referenced to body max cross-section unless noted; CP in % body length from nose tip or inches/calibers as stated.

## Summary table

| Dataset | What it validates | Confirmed Mach ceiling (verifier) | Verifier verdict |
|---|---|---|---|
| ARCAS Short/Long (NASA TN D-4013/D-4014 + RASAero comparison doc) | Body+fin CP vs Mach (transonic dip + supersonic forward march), CD/CA power-off, transonic peak, RASAero power-on split | **4.63** (measured; verify:ntrs) | solid |
| MESOS 293K flight (Rogers Rev B) | End-to-end: staging, supersonic CD, power-on drag, thrust-vs-altitude, apogee | **4.23** (sim) / 4.18 (accel-measured); spot-checked exact by verify:flights | solid (as part of flights set) |
| AGARD HB-2 (AEDC-TDR-64-137 + VTI 2016/2017/2025 + others) | Blunt-nose body CNα, xcp, forebody CA, base CA, transonic drag rise, hypersonic asymptotes | **19.8** (CAf only, viscous-interaction regime); full force+moment ceiling **16.5** (single run); well-populated multi-facility **10** | solid |
| Basic Finner ANF (DREV-TM-9703 + NAVORD 6652) | Fin-stabilized CNα/Cmα/CP travel/CD0/Cmq/Clp, M1.05–4.5, fully tabulated | **4.81** (Clp only); full coefficient set **4.47** (fin ablation at 4.4–4.5) | solid |
| NTRS sounding rockets (Cajun TM X-1771, Nike-Apache handbook) | Independent CP forward-travel cross-check (M2.3–4.63); operational power-on/off CD decks M1–8 | **4.63** measured (Apache deck M5–8 & M30 point are engineering estimates, not measurements) | solid |
| Amateur >M4 flights (Traveler IV, Aftershock II, GoFast 2004/2014, MESOS, GCC) | End-to-end apogee/velocity fixtures at M4.2–5.5 | **5.5** (Aftershock II, inertial with published ±0.15 3σ; no external speed track exists for any flight) | solid |

Verifier verdicts are quoted from the JSON verify results; "solid" = accessible ∧ quantitative ∧ spot-checks passed. Key verifier issues are reproduced per dataset below.

---

## 1. ARCAS Short / ARCAS Long

### 1.1 Provenance

- **Primary wind-tunnel reports (both retrieved from NTRS, verified byte-identical on re-fetch):**
  - NASA TN D-4014, Babb & Fuller, *Static Stability Investigation of a Sounding-Rocket Vehicle at Mach Numbers From 1.50 to 4.63*, June 1967 (L-5132). https://ntrs.nasa.gov/citations/19670020031 — PDF: https://ntrs.nasa.gov/api/citations/19670020031/downloads/19670020031.pdf (79 pp.). Langley Unitary Plan tunnel; M = 1.50, 1.80, 2.30, 2.96, 3.96, 4.63; Re = 3.0×10⁶/ft; α ≈ −4° to +20°+; stated accuracies CA ±0.004, CN ±0.03, Cm ±0.05, Cl ±0.01, Cn ±0.05, CY ±0.03; M ±0.015 (1.5–2.96) / ±0.05 (3.96–4.63); α,β ±0.1°. Boundary-layer trip: No. 60 carborundum. **CA is NOT base-corrected** (chamber axial force CA,c given separately, Fig. 4). Moment centers 63.37 %L (config 1) / 66.36 %L (config 2) — aft of flight CG; transfer Cm before comparison.
  - NASA TN D-4013, Ferris, *Static Stability Investigation of a Single-Stage Sounding Rocket at Mach Numbers From 0.60 to 1.20*, July 1967. https://ntrs.nasa.gov/citations/19670020050 — PDF: https://ntrs.nasa.gov/api/citations/19670020050/downloads/19670020050.pdf (64 pp.). Langley 8-ft transonic pressure tunnel; M = 0.60, 0.80, 0.90, (0.95 short only), 1.00, 1.20; Re = 3.0×10⁶/ft; α = −3° to +21°; **CA,corr IS corrected to free-stream base pressure**.
- **RASAero comparison doc** (local `online_open_rocket_reference/RASAero II Comparisons with ARCAS CP and CD Data.pdf` (Dropbox), 19 pp., Rogers; also https://www.rasaero.com/dloads/RASAero%20II%20Comparisons%20with%20ARCAS%20CP%20and%20CD%20Data.pdf). TN D-4014 did NOT include Mach 1.5 for ARCAS Long.
- Verifier (verify:ntrs) verdict: **accessible: true, quantitative: true, machCeiling: 4.63, verdict: solid.** Issues (near-verbatim): all D-4013/D-4014/X-1771 per-α coefficient data exist only as plots (CP/CA tables are ±1–1.5 %L read-offs); D-4014 CA not base-corrected (confirmed in source text); TN D-2576 / DTIC AD-780544 / Aerobee coefficient sets not retrieved.

### 1.2 Geometry (enough to build the model)

Half-scale models, **d = 2.250 in (5.715 cm)**; all stations in inches from nose tip (TN D-4014 Fig. 1 / comparison doc p. 5):

| Station | Config 1 (Short) | Config 2 (Long) |
|---|---|---|
| Nose tip | 0.000 | 0.000 |
| End of nose | 10.600 | 10.600 |
| Moment center | 25.950 | 35.500 |
| Boattail/fin station | 37.300 | 49.850 |
| Overall length | 40.950 (18.20 cal) | 53.500 (23.77 cal) |

- Nose: tangent-ogive profile, length 10.600 in (4.71 d), tip radius 0.062 in (0.03 d). Ordinates (X in → R in): 2.375→0.414, 3.375→0.554, 4.375→0.688, 5.375→0.804, 6.375→0.908, 7.375→0.988, 8.375→1.062, 9.375→1.125, 10.600→1.125.
- Boattail: length 1.810 in (0.80 d), **15° half-angle**, base diameter 1.470 in (0.653 d); small reflexed base lip (motor-nozzle flange on the full-size vehicle) — RASAero did NOT model the lip (assumed buried in boundary layer).
- Fins: 4, cruciform, trapezoidal, double-wedge section; root chord 3.380 in, tip-region chord 1.568 in (tip chord 0.93 d = 2.09 in per D-4013 fig), exposed semispan 2.103 in, LE sweep 30° (15° reference-line callouts), wedge half-angle 1.47°, thickness 0.096 in (near tip) / 0.150 in (root station), LE radius ≈0.030 in, LE bevel 2.72° (typ) normal to the 15° line; total span across fins 2.91 d (6.55 in); fin-cant option 2°.
- RASAero II model entries (comparison doc pp. 6–7): Short = Tangent Ogive Nose + 28.54 in Body Tube + 1.81 in Boattail (Σ 40.95 ✓), CP(M0) = 32.05 in = 78.3 %L. Long = nose + 41.09 in Body Tube + 1.81 in Boattail (Σ 53.50 ✓), CP(M0) = 41.7 in = 77.9 %L. **Sref = 3.976 in²**.
- RASAero modeling conventions stated in the doc: (1) boattail-under-fin-root handled by an **equal-volume-cylinder** convention (compute volume of cylinder + boattail portion under the fin root; replace with same-length cylinder of equal volume; project fins to that cylinder); (2) fin anchors modeled as protuberance drag: total anchor frontal area **÷5** (rail guide ≈5× typical body drag) **÷2** (RASAero assumes 2 guides), entered as a square rail guide.
- Tunnel Re on model length: Short 1.024×10⁷, Long 1.338×10⁷ (derived). RASAero Mach-Alt table used to match tunnel Re (comparison doc p. 9): M0.00→0 ft, 0.42→1, 0.90→25,000, 1.05→30,500, 1.20→33,000, 1.50→37,000, 2.00→44,000, 4.00→59,000, 5.00→63,000, 25.00→122,500.

### 1.3 Anchor values — CD/CA (Sref = 3.976 in²)

**Exact printed RASAero II table values (α = 0):**

| Config | Mach | CD Power-Off | CD Power-On |
|---|---|---|---|
| Short | 0.01 | **0.449** | **0.432** |
| Short | 0.02 | **0.330** | **0.313** |
| Long | 0.01 | **0.482** | **0.465** |
| Long | 0.02 | **0.400** | **0.383** |

**Wind-tunnel CD points (power-off, α=0; tolerance ±0.005 CD, ±0.02 Mach; M1.2 from TN D-4013 CA data, M≥1.5 from TN D-4014):**

| M (nominal) | Short CD | Long CD |
|---|---|---|
| 1.2 (read 1.19) | 0.595 | 0.607 |
| 1.5 (1.49) | 0.532 | — (no M1.5 Long data) |
| 1.8 | 0.470 | 0.508 |
| 2.3 (2.29) | 0.376 | 0.410 |
| 2.96 (2.95) | 0.300 | 0.336 |
| 3.96 (3.95) | 0.223 | 0.269 |
| 4.63 (4.65/4.66) | 0.198 | 0.225 |

**Underlying TN D-4014 CA at α=0 (graph-read ±0.01; NOT base-corrected):** Short — M1.50: 0.535; M1.80: 0.472; M2.30: 0.381; M2.96: 0.303; M3.96: 0.235; M4.63: 0.203. Long — M1.80: 0.510; M2.30: 0.415; M2.96: 0.335; M3.96: 0.262; M4.63: 0.228. (Independent hunt-agent read of Fig. 5(b) Short: 0.53/0.465/0.38/0.305/0.23/0.205 — agrees within tolerance.)

**RASAero II CD curves, graph-read (±0.01, ±0.015 near peak; ~ = interpolated under gridline):**

Short (p17): M0.2 0.298/0.281 (off/on); 0.3 0.280 (subsonic min)/0.263; 0.5 0.291/0.274; 0.8 0.304/0.277; 0.9 0.319/0.288; 1.0 ~0.50/0.434; 1.05 0.552/0.504; 1.1 0.562/0.516; **1.15 0.575 (peak ≈0.578)/0.527 (peak ≈0.53)**; 1.3 0.517/0.472; 1.5 0.465/0.420; 1.8 0.408/0.362; 2.0 ~0.385/0.333; 2.3 0.349/0.298; 2.5 0.330/0.281; 3.0 ~0.295/0.256; 3.5 0.263/0.229; 4.0 ~0.240/0.209; 4.5 0.218/0.194; 4.63 0.214/0.190; 5.0 ~0.207/0.181.

Long (p19): M0.2 0.363/0.346; 0.3 0.342 (min)/0.326; 0.5 0.349/0.333; 0.8 0.363/0.337; 0.9 0.380/~0.35; 1.0 ~0.55/0.493; 1.05 0.607/0.557; 1.1 0.616/~0.575; **1.15 0.628 (peak ≈0.63)/0.582 (peak ≈0.585)**; 1.2 0.615/0.558; 1.3 0.568/0.523; 1.5 0.514/0.467; 1.8 0.453/0.408; 2.0 ~0.41/0.375; 2.3 0.387/0.337; 2.5 0.365/0.316; 3.0 ~0.325/~0.30; 3.5 0.291/~0.26; 4.0 ~0.263/0.235; 4.5 0.240/0.218; 4.63 0.229/0.214; 5.0 ~0.22/0.201.

**Transonic CA,corr wind-tunnel curves + RASAero dots (TN D-4013, α≈0, tunnel-Re-matched; dots ±0.003, curves ±0.01, ±0.02 in the M0.9–1.05 rise):**

Short (p11):

| M | RASAero dot | WT δF=0 solid | WT δF=2 dashed | WT fins-off |
|---|---|---|---|---|
| 0.60 | 0.295 | 0.295 | 0.325 | 0.222 |
| 0.70 | 0.299 | 0.300 | 0.331 | 0.222 |
| 0.80 | 0.302 | 0.309 | 0.337 | 0.229 |
| 0.90 | 0.314 | 0.350 | 0.40 ±0.02 | 0.259 |
| 0.95 | 0.375 | 0.46 ±0.03 | 0.55 ±0.03 | 0.290 |
| 1.00 | 0.464 | 0.683 | 0.710 | 0.414 |
| 1.05 | 0.547 | 0.685 (peak ≈0.69) | 0.710 (peak ≈0.715) | 0.432 |
| 1.10 | 0.566 | 0.666 | 0.688 | 0.432 |
| 1.15 | 0.574 | 0.635 | 0.652 | 0.432 |
| 1.20 | 0.556 | 0.596 | 0.616 | 0.430 |

Long (p12): RASAero dots 0.355 (M0.6), 0.355 (0.7), 0.362 (0.8), 0.376 (0.9), 0.433 (0.95), 0.522 (1.00), 0.606 (1.05), 0.613 (1.10), 0.626 (1.15), 0.606 (1.20); WT fins-on 0.348, 0.348, 0.353, 0.385 solid/0.40 dashed, 0.47 ±0.03, 0.63 ±0.03, 0.72 ±0.02, 0.735/0.745 (peak), 0.665, 0.635/0.645; WT fins-off 0.248→≈0.42 plateau. Note RASAero underpredicts the tunnel transonic peak by ≈0.10–0.13 CA for both configs (tunnel peaks at M1.05–1.10; RASAero peaks at M1.15).

Base axial-force panels CA,b (α≈0, ±0.01): fins-on ≈ −0.035 at M0.6, crossing 0 near M≈0.95–1.0, reaching ≈+0.04 to +0.05 at M1.2; fins-off ≈ −0.04 to −0.03 across M0.6–1.2.

**Supersonic CA vs α (source of the M≥1.5 CD dots; ±0.01):** TN D-4014 Fig. 5(b) Short CA(α=0): M1.50 0.535, M1.80 0.472, M2.30 0.381, M2.96 0.303, M3.96 0.235, M4.63 0.203 (M1.50 rises to ≈0.665 at α≈14°). Fig. 6(b) Long: M1.80 0.510, M2.30 0.415, M2.96 0.335, M3.96 0.262, M4.63 0.228.

### 1.4 Anchor values — CP (% body length from nose)

**Supersonic (comparison doc p14/p15; markers ±0.3 %L ±0.01 M, curves ±0.5 %L):**

| M | RASAero Short | WT Config 1 (Short) | WT Config 2 (Long) | RASAero Long |
|---|---|---|---|---|
| 1.5 | 79.3 | 78.2 | (starts ≈M1.8) | 79.5 |
| 2.0 | 75.5 | 75.3 | 75.3 | 75.6 |
| 2.5 | 71.9 | 72.3 | 72.3 | 71.8 |
| 3.0 | 68.6 | 68.1 ±0.7 | 67.9 | 68.3 |
| 3.5 | 65.8 | 64.9 | 64.7 | 65.3 |
| 4.0 | 63.5 | 60.7 | 59.9 | 62.5 |
| 4.5 | 61.6 | 57.7 | 56.6 | 60.5 |
| 4.63 | — | ≈57.2 | ≈56.0 | — |

Independent hunt-agent read of TN D-4014 Fig. 7 (±1 %L): Short 78.5 (M1.5), 76.5 (1.8), 75.0 (2.0), 71.5 (2.5), 68.0 (3.0), 64.5 (3.5), 61.5 (4.0), 57.5 (4.63); Long 77.0 (1.8), 75.0 (2.0), 71.0 (2.5), 67.5 (3.0), 64.0 (3.5), 60.5 (4.0), 56.0 (4.63). Verifier re-read: "≈78.5 %L at M1.5 declining near-linearly to ≈57 %L at M4.63, config 2 slightly below config 1 — agrees within ±1.5 %L." Document commentary: RASAero supersonic CP "very accurate from Mach 1.5 to Mach 3"; extrapolated tunnel CP approaches 50 %L by Mach 5.

**Transonic (comparison doc p11/p12; markers ±0.15 %L, curves ±0.5 %L, ±1 %L in M0.95–1.1):**

Short — RASAero: Rogers Mod. Barrowman squares 78.2 flat M0.6–0.9; Barrowman triangles 75.8–75.9; Transonic-Supersonic circles 78.5 (0.95), 79.3 (1.00), 80.0 (1.05), 80.2 (1.10), 80.3 (1.15), 80.2 (1.20). Wind tunnel δF=0: 75.7 (0.60), 74.3 (0.70), 72.6 (0.80), 70.4 (0.90, min ≈70.2), 75.8 ±1 (0.95), 82.5 ±1 (1.00), 86.3 (1.05), 86.9 (1.10, peak ≈87), 86.2 (1.15), 84.9 (1.20).

Long — RASAero squares 77.8–77.9, triangles ≈77.5, circles 78.7 (0.95) → 80.7 (1.15), 80.4 (1.20). Wind tunnel fins-on: 76.6 (0.60), 75.9 (0.70), 75.8 (0.80), 76.2 (0.90), 77.0 (0.95), ≈74 dropping (1.00), 72.1 (1.05), **71.6 (1.10, min ≈71.5)**, 72–73 (1.15), ≈72 rising steeply to ≈82 at M≈1.28. This is the **anomalous forward transonic CP dip** RASAero cannot reproduce — the doc's motivation for the 2.0-cal supersonic stability-margin recommendation.

TN D-4013 hunt-agent digitization (Fig. 11 Short δF=0): xcp %L = 74.5, 73, 71.5, 71, 71, 76, 82, 85.5, 87, 85 at M 0.60, 0.70, 0.80, 0.85, 0.90, 0.95, 1.00, 1.05, 1.10, 1.20 (systematically ≈1 %L below the comparison-doc read — within combined tolerances).

### 1.5 Reconciliation with pre-existing 2026-07-06 handoff §5 anchors

| Handoff anchor | Fresh extraction | Status |
|---|---|---|
| ARCAS-Short M0.01 off/on 0.449/0.432; M0.02 0.330/0.313 | identical, exact | **CONFIRMED** |
| ARCAS-Long M0.01 0.482/0.465; M0.02 0.400/0.383 | identical, exact | **CONFIRMED** |
| Short supersonic CP M2→75, M3→68.5, M4→64 (±2%) | RASAero dots 75.5/68.6/63.5; **wind tunnel 75.3/68.1/60.7** | CONFIRMED within ±2 %L against the RASAero dots; **flag: at M4 the handoff value (64) tracks RASAero's prediction, which diverges +2.8 %L from the tunnel (60.7)**. Above M3.5 "match RASAero" and "match the tunnel" are different targets — harness must state which (recommend tunnel primary). |
| Long supersonic CP M2→75.5, M3→68.5, M4→63 (±2%) | RASAero dots 75.6/68.3/62.5; WT Config 2 75.3/67.9/59.9 | Same situation: confirmed vs RASAero dots; M4 tunnel is 3 %L forward. |
| Transonic peaks Short ~0.58/~0.53 **@M1.1** | 0.575 (peak ≈0.578)/0.527 (peak ≈0.53) **@M1.15** | Values confirmed; **peak Mach is 1.15, not 1.1** (RASAero curve; the wind tunnel peaks earlier, M1.05–1.10, and higher, ≈0.69). |
| Transonic peaks Long ~0.63/~0.585 @M1.15 | 0.628 (≈0.63)/0.582 (≈0.585) @M1.15 | **CONFIRMED** exactly. |
| MESOS apogee 289,789 ft vs GPS 293,488 ft, −1.26% | identical, exact (verifier recomputed the percentage) | **CONFIRMED** |

### 1.6 Scoring-harness use

- **CD power-off**: score kernel CD(α=0) against the wind-tunnel dots (±0.005 read + tunnel accuracy) at M1.19–4.65, and against the RASAero curve reads (±0.01) across M0.2–5 for RASAero-parity tracking. Tolerance target: ±0.02 CD supersonic, ±0.03 near the transonic peak. Remember D-4014 CA is not base-corrected — compare total CD including base for the M≥1.5 dots, and use the D-4013 CA,corr values (base-corrected) transonic.
- **CD power-on**: the exact M0.01/0.02 anchors (ΔCD = 0.017 both configs) already gate feature #2; the supersonic power-on curve reads (Δ growing to ≈0.05 at M2.3–3) gate the deferred large-nozzle extension (feature #1).
- **CP**: score xcp %L at M1.5–4.63 vs tunnel (±2 %L target M1.5–3.5, document divergence beyond); transonic CP dip (Long) is a stretch goal — flag, don't gate.
- Re-matching: use the Mach-Alt table above when reproducing RASAero Aero-Plots numbers; use tunnel Re when comparing to tunnel.

---

## 2. MESOS 293K flight (Kip Daugirdas, Oct 1 2022)

### 2.1 Provenance

- Rogers, *RASAero II Flight Simulation Comparison with Kip Daugirdas MESOS 293K ft Altitude Rocket Flight Data*, Rev B, Jan 18 2023 (local `online_open_rocket_reference/RASAero II Comparison with MESOS 293K Flight Data - Rev B.pdf` (Dropbox), 15 pp.; https://www.rasaero.com/dloads/RASAero%20II%20Comparison%20with%20MESOS%20293K%20Flight%20Data%20-%20Rev%20B.pdf). Telemetry: Multitronix Kate-3 (https://www.multitronix.com/293k-flight.html).
- Verifier (verify:flights) spot-check: "predicted apogee 289,789 ft vs GPS 293,488 ft AGL (297,398 AMSL), predicted max velocity 4,095 ft/s at Mach 4.23, nozzle expansion 12.84 as-flown vs 6.5025 static-fired, launch angle 2.77 deg, sustainer ignition 33,944 ft — ALL match report exactly; percent diffs (−1.26%/+1.19%) recomputed and correct; measured values independently confirmed on multitronix.com." Verifier nit: **sustainer burnout 61,505 ft is the LOW-ER simulation column; the as-flown high-ER sim lists 62,471 ft — use the drawing for geometry, not that number.**

### 2.2 Geometry / fixture definition (pp. 3, 9; drawing-read ±0.5 in unless exact)

- Components (RASAero listbox, exact text): Von Karman Ogive Nose Cone (length not stated; **drawing-measured 15.0 ± 0.5 in**); Body Tube 51.85 in; FinCan 8 in (**sleeves the aft 8 in of the body tube — NOT additive**); Boattail 5.5 in; Booster 79.5 in.
- Sustainer total = 15 + 51.85 + 5.5 ≈ 72.35 in (matches drawn 72.1 ± 0.5); full stack ≈ 150.4 ± 1.5 in.
- Diameters (graph-read only, never stated): sustainer OD ≈ 3.0 ± 0.3 in; booster OD ≈ 5.4 ± 0.5 in. Sustainer fins: root ≈8 ± 0.7 in, tip ≈2.8 ± 0.7 in, semispan ≈3.5 ± 0.5 in, clipped delta. Booster fins: root ≈20 ± 1.5 in, tip ≈7 ± 1.5 in, semispan ≈5.3 ± 0.8 in. Fin count not determinable from the PDF. Boattail aft dia not stated (~2.2–2.6 in est.).
- Masses (exact): loaded 23.31 lb (sustainer) / 87.05 lb (full stack). No component breakdown.
- Static CP (exact): 55.39 in (sustainer alone), 109.88 in (full stack) at M0. CG (drawing ±1 in): ≈45.5 / ≈94.7 in.
- Motors: booster O4374 (KIP) [flight card: O4500], burn 6.2 s; sustainer M787 (KIP) [flight card: M830], burn 10.3 s. Sustainer nozzle ε = **12.84 as flown** (never static fired — thrust curve is analysis from a 5,000-ft-elevation static fire of the ε = 6.5025 nozzle via Rogers' "Departures from Ideal Performance"); nozzle exit diameters NOT stated anywhere in the PDF.
- Site: Black Rock, NV, ground 3,910 ft MSL; launch Oct 1 2022 11:30:58 MDT.

### 2.3 Anchor values (exact unless noted)

Flight telemetry (Kate-3 flash):

| Quantity | Booster | Sustainer |
|---|---|---|
| Burn time | 6.2 s | 10.3 s |
| Ignition delay | 0.0 s | 16.9 s after booster burnout (ignition ≈ t = 23.1 s) |
| Max accel | 15.6 G | 11.5 G |
| Tilt at ignition | 0° | 4.01° |
| Ignition alt / velocity | 0 | 35,707 ft AGL / 1,030 ft/s (M1.06) |
| Burnout alt / velocity | 8,623 ft | 62,316 ft AGL / 4,047 ft/s (M4.18) |

- Max GPS altitude **293,488 ft AGL** (297,398 ft MSL); accel-integrated 294,271 ft; baro 275,131 ft. Max velocity **4,047 ft/s = Mach 4.179** at 62,301 ft, t = 33.3 s. Time to apogee 154.3 s. Downrange at apogee 55,767 ft, bearing 318°. Apogee deploy fired 4.26 s early at 293,266 ft. Main at 5,968 ft; touchdown 21 ft/s; total flight 14 min 53.2 s.
- Simulations (No Wind, All Turbulent Flow):
  - Preflight (0° launch angle): Smooth Paint **293,299 ft (−0.06%)**; Rough Camo **266,294 ft (−9.27%)**.
  - Postflight (ignition delay matched, launch angle 2.77° matched to downrange): Smooth Paint **289,789 ft (−1.26%)**; Rough Camo 262,001 ft (−10.73%). Max velocity **4,095 ft/s M4.23 vs 4,047 measured (+1.19%)**.
  - Nozzle-ER study (postflight config): ε 6.5025 → burnout 61,505 ft, max V 3,834 ft/s, apogee **257,248 ft**; ε 12.84 → burnout 62,471 ft, max V 4,095 ft/s, apogee **289,789 ft**. High ER worth ≈ +30,000 ft — this is the altitude-thrust-correction anchor.
- Post-flight-tuned inputs (fixture honesty): sustainer ignition delay; launch angle 2.77°; Smooth Paint finish (preflight >M3 guidance had said Rough Camo); the analytic (never-fired) high-ER thrust curve.
- Sustainer CP vs Mach (Aero Plots p. 9, ±0.7 in): ≈55.4 (M0–0.9, anchor 55.39 exact), peak ≈56.5 (M1.1–1.2), ≈53.5 (M2), ≈50.5 (M3), ≈47 (M4), ≈45 (M5). Stability-margin minimum ≈1.9 cal at M4.23 (annotated "just under the 2.0 calibers recommended").
- Thrust-vs-altitude equations extracted from this PDF are recorded in the spec doc, area 5a.
- Known gaps (not in the PDF): nozzle exit/throat diameters, rasp.eng curves, propellant masses, body diameters (numeric), fin counts/thickness/airfoils, per-component masses, rail length, pad atmosphere. Highest-value manual retrievals: rocketryforum MESOS build thread + "MESOS Flight to 293K ft" thread (403 to bots, readable in a browser).

### 2.4 Scoring-harness use

End-to-end fixture (the only one with vehicle model + flight data + an independent RASAero baseline). Score: apogee (target within ±3% of GPS 293,488 ft AGL, i.e., match or beat RASAero's −1.26%), max velocity (±2% of 4,047 ft/s), staging-event altitudes (ignition 35,707 ft, burnout 62,316 ft, ±5%), and the ε-study delta (+32.5k ft from ε 6.5→12.84) once altitude thrust correction is implemented. Requires: geometry transcription from the drawing (±tolerances above), thrust-curve reconstruction, and the 16.9 s airstart. Treat the drawing-read diameters as free parameters within their stated tolerance bands.

---

## 3. AGARD/STA HB-2 hypersonic ballistic standard model

### 3.1 Provenance

- Gray, *Summary Report on Aerodynamic Characteristics of Standard Models HB-1 and HB-2*, AEDC-TDR-64-137, AD602769, July 1964. DTIC: https://apps.dtic.mil/sti/pdfs/AD0602769.pdf (403/maintenance during research; **working mirror verified**: https://web.archive.org/web/2024/https://apps.dtic.mil/sti/pdfs/AD0602769.pdf, full 57-pp. scan). Raw data: Gray & Lindsay, AEDC-TDR-63-137 (1963).
- Damljanović & Vuković, EUCASS 2017-213, DOI 10.13009/EUCASS2017-213 — https://www.eucass.eu/doi/EUCASS2017-213.pdf (verified live).
- Damljanović, Vuković, Ocokoljić, Rašuo, *New Transonic Tests of HB-2 … in the VTI T-38*, Aerospace 12(2):131, 2025, DOI 10.3390/aerospace12020131 (open access; MDPI 403s bots — retrieved via Wayback capture).
- Also: Damljanović et al. AST 52:189–197 (2016, paywalled); Aerospace 8(10):275 (2021); Kharitonov et al. (ITAM AT-303, 2006); JAXA-RR-04-035E / RR-05-030E / SP-05-019; Qiu et al., Advances in Aerodynamics 5:29 (2023, https://aia.springeropen.com/counter/pdf/10.1186/s42774-023-00160-2.pdf verified); Ceresuela ONERA NT 13/1879A (1964, no online copy); Malcolm & Chapman NASA TN D-4766 (1968).
- Verifier (verify:hb2): **accessible: true, quantitative: true, machCeiling: 19.8, verdict: solid.** Spot checks: tangency table exact; Fig. 15a CNα readings within ±0.001; Fig. 12b Tunnel-H CAf ≈0.79–0.80 with "CAb Assumed Zero" confirmed; MDPI Tables 4/5 exact match. Issues (near-verbatim): above Mach 10 data are forebody-CA only at very low Re (0.015–0.2×10⁶, viscous-interaction regime), base drag assumed zero; **CN/xcp/Cm real-data ceiling is M16.5** (single ONERA run); **well-populated multi-facility ceiling is Mach 10**; AEDC values digitized from 1964 scanned fairings (±0.001 CNα/deg, ±0.01 others); Cmα column derived (CNα × arm), not source data; ITAM/JAXA/CARDC claims not independently verified (figure-locked).

### 3.2 Geometry (exact, calibers of cylinder diameter d; AEDC Fig. 1a + tangency table)

| Element | Definition |
|---|---|
| Overall length | **4.900 d**; ref. area A = πd²/4 |
| Nose | Spherical cap, radius **0.300 d**, center 0.300 d aft of tip |
| Nose cone | **25° half-angle**, tangent to sphere |
| Cone–cylinder fairing | Circular arc, radius **0.700 d** (center 0.200 d off-axis, opposite side) |
| Cylinder | Diameter d |
| Cylinder–flare fairing | Circular arc, radius **4.000 d** (center 4.500 d off-axis, 2.050 d forward of base) |
| Flare | **10° half-angle** cone to base |
| Base | Diameter **1.600 d** (Ab/A = 2.56) |
| Moment reference | **1.950 d** from nose tip |

Tangency stations (x/l, l = 4.9 d → x/d): hemisphere–cone 0.0353 → 0.173; cone–shoulder 0.1064 → 0.521; shoulder–cylinder 0.167 → 0.818; cylinder–flare-fairing 0.583 → 2.857; fairing–flare-cone 0.725 → 3.553. (Internally consistent to 3 decimals.) Standard sting: ≤0.3 d diameter for ≥3.0 d behind base. HB-1 = same minus flare (full-diameter length 4.083 d).

### 3.3 Anchor values

**AEDC correlation fairings at max Re (Fig. 15a–c; digitization ±0.001 CNα/deg, ±0.01 xcp/l, CAf, CAb):** CNα per degree at α→0; xcp/l from nose; CAf = zero-lift forebody axial force; CAb = base; CA0 = CAf + CAb; Cmα derived = CNα·(1.95 − xcp·4.9/d)/d arm (researcher-derived, ref 1.95 d):

| M∞ | Red ×10⁻⁶ | CNα (/deg) | xcp/l | xcp (cal) | CAf | CAb | CA0 | Cmα (/deg, derived) |
|---|---|---|---|---|---|---|---|---|
| 1.5 | 1.4 | 0.080 | 0.460 | 2.25 | 0.82 | 0.345 | 1.17 | −0.024 |
| 2 | 1.7 | 0.0835 | 0.465 | 2.28 | 0.755 | 0.32 | 1.08 | −0.027 |
| 3 | 2.5 | 0.0835 | 0.520 | 2.55 | 0.665 | 0.23 | 0.90 | −0.050 |
| 4 | 2.0 | 0.0805 | 0.550 | 2.70 | 0.61 | 0.165 | 0.78 | −0.060 |
| 5 | 2.6 | 0.074 | 0.555 | 2.72 | 0.585 | 0.105 | 0.69 | −0.057 |
| 6 | 2.6 | 0.0665 | 0.535 | 2.62 | 0.565 | 0.065 | 0.63 | −0.045 |
| 7 | 1.3 | 0.062 | 0.530 | 2.60 | 0.57 | 0.045 | 0.62 | −0.040 |
| 8 | 2.2 | 0.056 | 0.520 | 2.55 | 0.56 | 0.03 | 0.59 | −0.034 |
| 10 | 1.4 | 0.054 | 0.475 | 2.33 | 0.565 | 0.01 | 0.58 | −0.020 |
| 10 (Mod. Newtonian, reference) | — | 0.072 | 0.60 | 2.94 | 0.57 | ~0 | 0.57 | — |

Re-dependence (AEDC Figs. 10–12, 14; correlation-curve endpoints): CNα e.g. M3: 0.105 @ 0.15×10⁶ → 0.083 @ (0.7–2.5)×10⁶; xcp/l M3: 0.64 → 0.515; M4: 0.65 → 0.55 (laminar flare separation — **a validation run must match Red, not just Mach**). CAf: M1.5 0.80→peak ≈0.83→0.81 (pressure-integration wave drag ≈0.81); M8 ≈0.555–0.56 (pressure integration ≈0.56); M16.5 0.60 @ ≈0.17×10⁶ (ONERA ARC1); **M18.1–19.8: 0.79–0.80 @ (0.015–0.02)×10⁶, ≈0.74 @ 0.023×10⁶, 0.67–0.74 @ (0.055–0.065)×10⁶ (VKF Tunnel H, strong viscous-interaction rise, CAb assumed zero)**. CAb: M1.5 0.35; M2 0.31–0.32; M3 0.225–0.23; M4 0.165–0.17; M5 0.11–0.12; M6 0.07–0.08; M7 ≈0.04; M8 0.01–0.04; M10 ≈+0.01.

**⚠ Known cross-facility inconsistency:** VTI (2025) reports AEDC base-drag data "significantly different from all other sources"; AEDC CA0 at M1.5 (≈1.17) is well below VTI/ONERA (≈1.5 at M1.4). Forebody CAf agrees well across facilities; **treat CAf as the trustworthy drag metric; total CA at M < 2 carries a several-percent-of-CA cross-facility band.**

**VTI T-38 transonic tables (Aerospace 12(2):131, 2025, Tables 4–5 — EXACT as published; α = 0; CNα per radian; CAb0 = −Cpb0×2.56):**

75 mm model (Red ≈ 2.2–2.6×10⁶):

| Balance | M | CA0 | CAf0 | Cpb0 | CNα (/rad) |
|---|---|---|---|---|---|
| Able Mk18 | 0.70 | 0.759 | 0.143 | −0.241 | 4.74 |
| | 0.79 | 0.810 | 0.199 | −0.239 | 4.96 |
| | 0.90 | 0.986 | 0.325 | −0.258 | 5.53 |
| | 1.00 | 1.541 | 0.647 | −0.349 | 4.78 |
| | 1.10 | 1.833 | 0.833 | −0.391 | 4.79 |
| | 1.19 | 1.716 | 0.820 | −0.350 | 4.76 |
| | 1.28 | 1.650 | 0.843 | −0.316 | 4.69 |
| | 1.39 | 1.575 | 0.850 | −0.283 | 4.66 |
| VTI KV44 | 0.69 | 0.724 | 0.099 | −0.244 | 4.70 |
| | 0.78 | 0.816 | 0.212 | −0.236 | 4.82 |
| | 0.88 | 1.004 | 0.361 | −0.251 | 5.49 |
| | 1.00 | 1.510 | 0.631 | −0.343 | 4.68 |
| | 1.10 | 1.841 | 0.812 | −0.402 | 4.66 |
| | 1.20 | 1.707 | 0.785 | −0.360 | 4.74 |
| | 1.28 | 1.661 | 0.823 | −0.327 | 4.55 |
| | 1.39 | 1.579 | 0.821 | −0.296 | 4.57 |

100 mm model (Red ≈ 3.0–5.4×10⁶):

| Balance | M | CA0 | CAf0 | Cpb0 | CNα (/rad) |
|---|---|---|---|---|---|
| Able Mk18 | 0.69 | 0.705 | 0.161 | −0.212 | 4.67 |
| | 0.79 | 0.726 | 0.230 | −0.194 | 5.17 |
| | 0.88 | 0.881 | 0.326 | −0.216 | 5.46 |
| | 1.00 | 1.270 | 0.651 | −0.242 | 4.76 |
| | 1.10 | 1.477 | 0.793 | −0.267 | 4.79 |
| | 1.19 | 1.586 | 0.835 | −0.293 | 4.76 |
| | 1.29 | 1.585 | 0.870 | −0.279 | 4.69 |
| | 1.38 | 1.546 | 0.825 | −0.276 | 4.66 |
| VTI KV44 | 0.69 | 0.711 | 0.160 | −0.215 | 4.78 |
| | 0.79 | 0.737 | 0.216 | −0.203 | 4.96 |
| | 0.88 | 0.885 | 0.352 | −0.208 | 5.37 |
| | 1.00 | 1.253 | 0.647 | −0.236 | 4.74 |
| | 1.10 | 1.452 | 0.769 | −0.267 | 4.88 |
| | 1.19 | 1.587 | 0.815 | −0.301 | 4.65 |
| | 1.28 | 1.586 | 0.828 | −0.296 | 4.60 |
| | 1.39 | 1.553 | 0.840 | −0.279 | 4.60 |

Transonic drag-rise peak at **M ≈ 1.10** (CA0 ≈ 1.83, 75 mm). CNα cross-check: 4.74/rad = 0.0827/deg, consistent with AEDC 0.080–0.084 at M1.5. Below M1.5 total CA differs between models (sting/base effect); CAf agrees. 2σ uncertainties: CA ±0.0059–0.035, CN ±0.011–0.046, Cm ±0.019–0.060 (balance-dependent).

**VTI supersonic high-Re high-α (EUCASS 2017-213; AST 2016):** M = 1.5–4.0 (10 values), **Red = 2.2×10⁶ (M1.5) → 4.5×10⁶ (M4) — highest-Re HB-2 data in existence**; α = −2° to +30°. Curves only: M1.5 CAf 0.80→0.45 falling with α, CA 1.35–1.75, CN 0→≈4.3 @ 30°, Cm 0→−3.5; M3.0 CAf ≈0.68 slightly rising, CA 0.9–1.28, CN 0→≈4.3, Cm 0→−3.9. 2σ: CA ±0.003–0.007, CN ±0.009–0.042, Cm ±0.013–0.054. CAf(α)-trend sign flips near M3.

Hypersonic re-tests (figure-locked, not digitized): ITAM AT-303 M9.7–15.6; JAXA 1.27 m HWT M≈10 (only high-α hypersonic force data) + HIEST; CARDC M12.7 (Re 4.44×10⁵) and FD17A M12.33 (Re 2.77×10⁶); NASA Ames ballistic range M≈2.

### 3.4 Scoring-harness use

The blunt-body/hypersonic anchor. Score: (1) body CNα(M) and xcp(M) at M1.5–10 against the AEDC fairing table (tolerance ±0.005/deg CNα, ±0.03 xcp/l — covers digitization + Re band at matched Red ≈ (1.3–2.6)×10⁶); (2) transonic CA0/CAf0/CNα against the exact VTI tables (tolerance ±0.05 CA0, ±0.3/rad CNα; prefer CAf0 below M1.5 given the base-pressure dispute); (3) hypersonic CAf asymptote ≈0.56–0.57 at M8–10 (±0.02); (4) the M10 modified-Newtonian reference row is a direct check on our MNT implementation (CNα 0.072 vs measured 0.054 — MNT overpredicts CNα by ~33% here; blending must not be naive). Nose bluntness (0.300 d cap) exercises the spherical-cap MNT term. Do not gate on total CA below M2; do not use M>10 rows except as viscous-interaction context.

---

## 4. Army–Navy Basic Finner (ANF)

### 4.1 Provenance

- Dupuis & Hathaway, *Aeroballistic Range Tests of the Basic Finner Reference Projectile at Supersonic Velocities*, DREV-TM-9703, Aug 1997 (DTIC ADA636861). Mirrors (verified): https://archive.org/details/DTIC_ADA636861 (·/download/DTIC_ADA636861/DTIC_ADA636861.pdf, 153 pp.); https://apps.dtic.mil/sti/pdfs/ADA636861.pdf (403 to bots); https://cradpdf.drdc-rddc.gc.ca/PDFS/zbb53/p505377.pdf
- Regan, *Roll Damping Moment Measurements for the Basic Finner at Subsonic and Supersonic Speeds*, NAVORD Report 6652, NOL White Oak, 1964 (AD0600975). https://archive.org/details/DTIC_AD0600975
- Supporting: DRDC TM 2002-136 (subsonic ANF — no open copy); NAVORD 4516 (1960); BRL Report 934 (1955); AEDC-TR-75-43; Bhagwandin & Sahu ARL ADA592550 (https://archive.org/details/DTIC_ADA592550); VTI Aerospace 11(7):579 (2024); Aerospace 8(11):354 (2021); JSR 10.2514/1.A35687; Aerospace 12(5):371 (2025); HiSA notes https://hisa.gitlab.io/archive/asc/basicFinner/notes/basicFinner.html
- Verifier (verify:finner): **accessible: true, quantitative: true, machCeiling: 4.81, verdict: solid.** Spot checks: Table VII rows verified against raw archive.org OCR (an AI-summarizer fetch had misassigned a column; direct inspection proved the researcher correct); NAVORD 6652 all 8 Clp values exact; geometry verbatim. Issues (near-verbatim): M4.81 is ONE quantity only (Clp); full set tops out M4.471 with **fin ablation on the M4.4–4.5 shots**; NAVORD per-degree convention (×57.3) is an inference, not source-stated; Table IX OCR genuinely garbled (Clp at M1.077 reads "18.47" vs −18.2; pull DTIC PDF pages before consuming Table IX to precision); shot ID DA95030801 partially garbled; AFF has NO numeric tables in hand (M≤2.5, secondary); subsonic ANF statics not retrieved and ARL flags them scattered — **numerically-usable set effectively starts near M1.05**.

### 4.2 Geometry (exact, calibers; cross-verified DREV / ARL / NAVORD)

| Item | Value |
|---|---|
| Total length | 10.0 cal |
| Nose | Cone, **10° half-angle** (DREV's "20° nose cone" = included angle), length **2.836 cal** (= 0.5/tan 10°); meplat radius 0.004 cal |
| Body | Cylinder 7.16 cal, diameter 1.0 cal, no boattail |
| Fins | 4, rectangular, **1.0 cal chord × 1.0 cal exposed semispan**, cruciform, flush with base; total span 3.0 cal |
| Fin section | Wedge: sharp LE (radius 0.004 cal), thickness linear to **0.08 cal at TE/base** |
| Fin cant | 0° (also 2°, 4° models fired) |
| CG / moment ref | **5.5 cal from nose** (some tunnel tests 6.1 cal pivot) |
| DRDC model | D = 30.00 mm, L = 300 mm, m 1.5894 kg, Ix ≈ 1.92 kg·cm², Iy ≈ 97.85 kg·cm² |

AFF (Modified Finner, secondary): 10.0 cal; tangent-ogive nose 2.5 cal; body 7.5 cal; 4 clipped-delta fins root 4/3 cal, semispan 0.5 cal, sharp bevels; CG 4.8 cal; D = 30 mm, m 0.6643 kg; Lref 0.03 m, Sref 7.07×10⁻⁴ m². Data M0.6–2.5 only, none numerically in hand.

### 4.3 Anchor values

**DREV-TM-9703 Table VIII (6-DOF single fits, per shot, verbatim; per radian; moment ref 5.5 cal; Cx0 = total axial force at α=0 incl. base; x_cp = 5.5 − Cmα/CNα):**

| M | Cx₀ | CNα /rad | Cmα /rad | Cmq+Cmα̇ | Clp | x_cp cal (%L) |
|---|---|---|---|---|---|---|
| 1.056 | 0.868 | 18.73 | −50.49 | −332 | −18.20 | 8.20 (82.0) |
| 1.058 | 0.868 | 18.02 | −51.61 | −350 | −18.24 | 8.36 (83.6) |
| 1.116 | 0.854 | 18.50 | −54.60 | −320 | −18.15 | 8.45 (84.5) |
| 1.254 | 0.760 | 18.81 | −52.60 | −386 | −23.56 | 8.30 (83.0) |
| 1.332 | 0.701 | 16.29 | −49.92 | −506 | −24.00 | 8.56 (85.6) |
| 1.377 | 0.708 | 16.37 | −45.88 | −500 | −28.99 | 8.30 (83.0) |
| 1.799 | 0.594 | 11.29 | −28.98 | −386 | −22.94 | 8.07 (80.7) |
| 1.846 | 0.598 | 11.80 | −27.45 | −370 | −22.19 | 7.83 (78.3) |
| 1.850 | 0.567 | 11.80 | −27.51 | −416 | −22.40 | 7.83 (78.3) |
| 2.348 | 0.500 | 10.37 | −19.74 | −295 | −18.32 | 7.40 (74.0) |
| 2.364 | 0.478 | 10.17 | −19.02 | −330 | −18.30 | 7.37 (73.7) |
| 2.414 | 0.473 | 9.92 | −18.12 | −299 | −18.27 | 7.33 (73.3) |
| 2.663 | 0.446 | 8.95 | −15.96 | −398 | −16.74 | 7.28 (72.8) |
| 2.741 | 0.400 | 8.92 | −14.82 | −254 | −16.70 | 7.16 (71.6) |
| 2.749 | 0.452 | 9.00 | −15.37 | −300 | −15.99 | 7.21 (72.1) |
| 2.969 | 0.385 | 8.00 | −13.90 | −250 | −13.30 | 7.24 (72.4) |
| 2.970 | 0.376 | 8.72 | −13.54 | −275 | −13.30 | 7.05 (70.5) |
| 3.312 | 0.350 | 8.41 | −11.49 | −232 | −13.89 | 6.87 (68.7) |
| 3.338 | 0.376 | 8.19 | −11.23 | −250 | −13.40 | 6.87 (68.7) |
| 3.682 | 0.310 | 8.13 | −10.17 | −211 | −13.00 | 6.75 (67.5) |
| 3.745 | 0.309 | 8.45 | −9.46 | −277 | −13.42 | 6.62 (66.2) |
| 3.775 | 0.308 | 7.92 | −9.28 | −205 | −12.55 | 6.67 (66.7) |
| 4.127 | 0.285 | 7.65 | −8.43 | −152 | −12.00 | 6.60 (66.0) |
| 4.425* | 0.268 | 8.97 | −7.95 | −155 | −12.00 | 6.39 (63.9) |
| 4.473* | 0.257 | 7.76 | −7.34 | −247 | −11.72 | 6.45 (64.5) |

\* fin LEs ablated at M≈4.5 — treat with caution. CP travel M1.06→4.13: 82 %L → 66 %L, monotone forward; static margin 3.1 → 1.0 cal about the 5.5-cal CG.

**Table VII (linear-theory single fits — companion set; per radian; δ̄² = mean squared yaw deg²; CDδ² = yaw-drag slope; verifier-confirmed rows):** e.g. DA95022010 M1.056: CD0 0.868, CDδ² 23.14, CNα 16.39, Cmα −50.79, Cmq −319, Cnpα 63.7; DA95021328 M2.348: 0.498/15.21/10.18/−18.76/−338/−29.4; full 26-shot table in DREV-TM-9703 (values also preserved in the hunt report). Magnus (Cnpα) noisy shot-to-shot; outliers flagged in-source.

**Table IX (6-DOF multiple-fit groups — OCR-garbled, pull PDF before precision use):**

| Mach group | CX0 | Cmα | Clp | Cmq+Cmα̇ |
|---|---|---|---|---|
| 1.077 | 0.863 | −52.6 | −18.2 (OCR "18.47") | −322 |
| 1.293 | 0.731 | −51.0 | −23.6 | −487 |
| 1.832 | 0.585 | −28.0 | −22.4 | −378 |
| 2.375 | 0.484 | −18.9 | −18.3 | −307 |
| 2.718 | 0.435 | −15.2 | −16.7 | −336 |
| 3.147 | 0.373 | −12.5 | −13.5 | −260 |
| 3.734 | 0.309 | −9.53 | −13.2 | −240 |
| 4.300 | 0.271 | −7.85 | −11.7 | −247 |

**NAVORD 6652 zero-AoA Clp (exact as tabulated; per-degree convention inferred, ×57.3 → per-radian):** M1.53: 0.532 (−30.5/rad); 2.03: 0.422 (−24.2); 2.27: 0.371 (−21.3); 2.54: 0.352 (−20.2); 2.76: 0.315 (−18.1); 3.51: 0.273 (−15.6); 4.10: 0.219 (−12.5); **4.81: 0.181 (−10.4)**. Brackets DREV free-flight Clp within ~10–15%.

**Pitch damping continuity (VTI Aerospace 2024, curves):** Cmq+Cmα̇ ≈ −350/rad (M0.4) → peak ≈ −450/rad (M0.95) → ≈ −250/rad (M3.5); matches DREV transonic peak (−487 at M1.29).

### 4.4 Scoring-harness use

The sharp-cone + rectangular-fin benchmark — complements ARCAS (ogive + swept fins) and HB-2 (blunt, finless). Score: CNα(M) (tolerance ±10% — free-flight fit scatter between duplicate-Mach shots is ~5–8%), x_cp(M) (±1.5 %L against the per-shot column; the M1.0–1.4 plateau at 82–86 %L then monotone forward march to 66 %L at M4.1 is the shape to match), CD0 = Cx0 (±0.03, remembering it includes base drag at flight Re), and optionally Clp/Cmq if dynamic derivatives are ever modeled. Use Table VIII (6-DOF) as primary, Table VII as cross-check; exclude the two ablated M4.4+ shots from gating. Fin thickness wave drag matters here (0.08 cal wedge): a kernel without per-shape thickness drag will underpredict Cx0 measurably.

---

## 5. NTRS sounding-rocket findings (Cajun, Nike-Apache)

### 5.1 Provenance

- Jernell, *Antenna Effects on the Aerodynamic Characteristics of a 0.410-Scale Model of the Cajun Rocket at Mach Numbers From 2.30 to 4.63*, NASA TM X-1771, March 1969. https://ntrs.nasa.gov/citations/19690011744 — PDF: https://ntrs.nasa.gov/api/citations/19690011744/downloads/19690011744.pdf (59 pp., verified). Langley Unitary Plan; M = 2.30, 2.96, 3.95, 4.63; Re = 2.0×10⁶/ft; α ≈ −4° to +12°; **CA adjusted to free-stream base pressure**.
- *Nike Apache Performance Handbook*, NASA GSFC X-721-66-568 (1966/67). PDF: https://ntrs.nasa.gov/api/citations/19670015760/downloads/19670015760.pdf — Appendix A operational drag decks, exact tables.
- Identified but NOT retrieved: NASA TN D-2576 (clean Cajun supersonic, not digitized on NTRS; local stub file is 40 bytes = never obtained); DTIC AD-780544 (Gerdian-condenser sounding rockets M2.5–8; 403); no Aerobee coefficient set found on NTRS (genuine gap — the 2011 manual's Aerobee references AST/E1R-13319 and Aerojet 1784 are contractor reports, DTIC-class).
- Verifier (verify:ntrs): **accessible: true, quantitative: true, machCeiling: 4.63, verdict: solid.** Issues: Apache deck M5–8 and the M30 point are **trajectory-program engineering estimates, not measurements** (CD constant 0.48 from M6 to M30; flat rows M7.5–8); Cajun/ARCAS per-α data are plots (±1–1.5 %L reads); combination-deck reference-area convention not restated next to the appendix table.

### 5.2 Geometry

Cajun 0.410-scale model (inches): length 67.65; conical nose 14.40 (tip radius 0.103, meplat 0.86); body dia 2.668 (full-scale 6.5 in); L/d = 25.36; moment center 38.84 (57.4 %L); 4 fins, root chord 8.96, TE thickness 0.48, LE sweep 19.87°; 7° wedge roll-control tab per fin TE; ref. area 0.0388 ft². Nike-Apache: Nike M5 booster 16.5 in dia (17.5 over lugs) + Apache 6.5 in dia (externally identical to Cajun); 11°-total-angle nose.

### 5.3 Anchor values

**Cajun (TM X-1771 Figs. 7(a)/8, clean config, α≈0, digitized ±0.01–0.02 coefficient, ±0.5 %L):**

| M | CNα (/deg) | Cmα (/deg, about 57.4 %L) | xac/l | CA,0 (free-stream base) |
|---|---|---|---|---|
| 2.30 | 0.41 | −2.75 | 0.84 | 0.63 |
| 2.80 | 0.36 | −2.25 | 0.815 | 0.55 |
| 2.96 | 0.35 | −2.1 | 0.81 | 0.53 |
| 3.20 | 0.33 | −1.95 | 0.80 | 0.51 |
| 3.60 | 0.30 | −1.7 | 0.78 | 0.47 |
| 3.95 | 0.28 | −1.5 | 0.765 | 0.45 |
| 4.63 | 0.26 | −1.2 | 0.74 | 0.42 |

Aerodynamic-center forward travel ~10 %L over M2.3→4.63 — third independent CP-travel dataset (with ARCAS ~21 %L over M1.5–4.63 and Finner ~16 %L over M1.06–4.13). Antenna deltas: ΔCA,0 ≈ +0.09 (Dovap) / +0.17 (turnstile) at M2.3, shrinking with M; negligible on CNα/Cmα.

**Nike-Apache handbook Appendix A (EXACT operational drag decks; wind-tunnel + flight-derived; ref. area = respective stage cross-section):**

Nike+Apache first-stage boost CD vs M: (0.46, 0.47), (0.50, 0.59), (0.54, 0.63), (0.60, 0.66), (0.66, 0.71), (0.70, 0.80), (0.76, 0.95), (0.79, 1.08), (0.84, 1.27), (0.90, 1.40), (0.96, 1.60), (0.99, 1.90), (1.02, 2.20), (1.04, 2.23), (1.06, 2.20), (~1.08, 2.00), (~1.09, 1.68), (1.10, 1.53), (1.16, 1.46), (1.20, 1.44), (1.26, 1.40), (1.38, 1.35), (1.40, 1.34), (1.60, 1.255), (1.80, 1.18), (2.00, 1.12), (2.20, 1.07), (2.40, 1.02), (2.60, 0.99), (2.80, 0.96), (3.00, 0.93), (6.00, 0.48), (30.0, 0.48).

Apache second stage alone, CD vs M, 4 configurations × coasting/thrusting (Case 1 = clean 11° cone; 2 = +2 DOVAP; 3 = +DOVAP+4 turnstile; 4 = +pitot+quadraloops+DOVAPs):

| M | C1 coast | C1 thrust | C2 coast | C2 thrust | C3 coast | C3 thrust | C4 coast | C4 thrust |
|---|---|---|---|---|---|---|---|---|
| 1.00 | 0.930 | 0.780 | 1.140 | 0.990 | 1.260 | 1.110 | 1.600 | 1.400 |
| 1.25 | 0.841 | 0.707 | 0.997 | 0.863 | 1.116 | 0.982 | 1.490 | 1.329 |
| 1.50 | 0.785 | 0.665 | 0.918 | 0.798 | 1.028 | 0.908 | 1.400 | 1.260 |
| 1.75 | 0.740 | 0.634 | 0.860 | 0.754 | 0.966 | 0.860 | 1.318 | 1.204 |
| 2.00 | 0.704 | 0.607 | 0.815 | 0.718 | 0.924 | 0.827 | 1.250 | 1.150 |
| 2.50 | 0.643 | 0.564 | 0.741 | 0.662 | 0.860 | 0.781 | 1.132 | 1.052 |
| 3.00 | 0.590 | 0.527 | 0.681 | 0.618 | 0.803 | 0.740 | 1.050 | 0.980 |
| 3.50 | 0.544 | 0.496 | 0.631 | 0.583 | 0.754 | 0.706 | 0.995 | 0.933 |
| 4.00 | 0.507 | 0.467 | 0.591 | 0.551 | 0.714 | 0.674 | 0.950 | 0.895 |
| 4.50 | 0.479 | 0.444 | 0.562 | 0.527 | 0.684 | 0.649 | 0.907 | 0.865 |
| 5.00 | 0.454 | 0.423 | 0.536 | 0.505 | 0.656 | 0.625 | 0.872 | 0.840 |
| 5.50 | 0.432 | 0.402 | 0.512 | 0.482 | 0.631 | 0.601 | 0.848 | 0.818 |
| 6.00 | 0.412 | 0.384 | 0.492 | 0.464 | 0.608 | 0.580 | 0.830 | 0.800 |
| 6.50 | 0.396 | 0.374 | 0.476 | 0.454 | 0.590 | 0.568 | 0.813 | 0.789 |
| 7.00 | 0.388 | 0.368 | 0.468 | 0.448 | 0.578 | 0.558 | 0.800 | 0.780 |
| 7.50–8.0 | 0.384 | 0.364 | 0.464 | 0.444 | 0.574 | 0.554 | 0.800 | 0.780 |

**The clean coasting-vs-thrusting split is a direct power-on drag anchor: ΔCD ≈ 0.15 at M1.0, 0.10–0.12 at M1.5–2, shrinking to ~0.02 by M8** — independent of RASAero.

### 5.4 Scoring-harness use

- Cajun: CP/xac forward travel cross-check (same harness metric as ARCAS §1.6; tolerance ±1.5 %L) and CNα(M) decay shape ±10%. Long-L/d (25.4) exercises the body-lift/crossflow terms harder than ARCAS.
- Nike-Apache Case-1 deck: power-on/off ΔCD(M) curve for the large-nozzle supersonic extension (feature #1) — engineering data, so use as a **shape** target (Δ decaying with M) with generous ±30% tolerance, not a gate. Do not treat M≥5 rows or the M30 point as measurements.

---

## 6. Amateur/experimental flights > Mach 4

### 6.1 Provenance & verifier

Verifier (verify:flights): **accessible: true, quantitative: true, machCeiling: 5.5, verdict: solid.** Key issues (near-verbatim): **all Mach >5 figures are inertial/accelerometer-integrated — no flight in the set has an independent external speed track** (GPS blacks out above ~1,600 ft/s); Aftershock II Mach 5.50 ± 0.15 carries published uncertainty; GoFast 2004 "FAA within 5%" phrasing not confirmed verbatim (official page: FAA post-flight analysis "concurs" with ~72 mi; Mach 5.18 figure is from astronautix, unverified secondary; official page says "Mach 5+"); Traveler IV whitepaper is 24 pp. not 23; rocketryforum/email-gated/PR-only claims not independently re-verified.

### 6.2 Fixture-ranked flight data

**1. MESOS 293K — best fixture by a wide margin** (full detail in §2: GPS apogee + accel velocity + published vehicle model + independent RASAero baseline).

**2. USC RPL Aftershock II — Oct 20, 2024, Black Rock (current amateur altitude+speed record):** apogee **470,400 ft (143.3 km) ± 27,300 ft (3σ) / ±9,100 ft (1σ)**; max velocity **5,283 ± 151 ft/s (3σ) ≈ Mach 5.5** (printed 5.50 ± 0.15); Mach 1 @ T+2 s, Mach 5 @ T+13 s, burnout T+19 s, apogee T+177 s, GPS relock T+311 s. Reconstruction: UKF fusing ADIS16467-1 IMU + KX134 (200 Hz) + Blue Raven + ZED-F9P GPS + MS5607 baro + BigRedBee. Vehicle (unusually complete): 13 ft × 8 in minimum-diameter; total 322 lbm (146 kg), dry 127 lbm (57 kg), propellant 200 lbm (90 kg); motor GEM8-R4000, total impulse 51,529 lbf·s (229,212 N·s), peak 4,220 lbf, burn 16.3 s; carbon case + silicone ablative TPS. Missing: fin planform, nose profile/length, thrust-curve shape, CG/CP; raw data email-gated (analysis@uscrpl.com). Whitepaper: https://www.uscrpl.com/s/Aftershock_II_Apogee_Whitepaper ; specs: http://www.uscrpl.com/aftershock-ii — Reconstructable to ~5–10% without USC's help.

**3. CSXT GoFast 2004 — May 17, 2004, Black Rock:** apogee **379,900 ft (115.8 km)** at ~T+158 s; 3,420 mph at T+10.5 s (official "Mach 5+"; astronautix: Mach 5.18 max / 4.99 burnout — secondary); >23 g; burnout T+13.4 s @ 49,000 ft; spin 8 rev/s. Crossbow accel + magnetometer; FAA/AST post-flight concurrence. Vehicle (astronautix, secondary): 350 kg gross, 4.45 m × 0.25 m; S-50,150 motor ≈431.5 kN·s, 272 kg, ~13.5 s burn, 16,000 lbf peak; **partial exit-cone ablation at ~60% impulse** (thrust-curve wrinkle). Raw accel data in a rocketryforum thread (403 to bots — manual fetch). https://csxtflight.com/2004-altitude-verified ; http://www.astronautix.com/g/gofast.html (http only). Reconstructable to maybe ~10%.

**4. Evolution Space "Gold Chain Cowboy" — Apr 22, 2023, Mojave:** 408,456 ft AGL; **Mach 5.2**; 20.3 g; burn 12.6 s; burnout @ 37,751 ft; apogee T+167.2 s; 10 in × 21 ft, 900 lb, full S motor. GPS through flight; **downloadable KMZ trajectory** (gcc_trajectory.kmz). Vehicle otherwise a black box; semi-professional. https://www.multitronix.com/408k-flight.html — coarse sanity fixture only.

**5. USC RPL Traveler IV — Apr 21, 2019, Spaceport America (first amateur Kármán-line flight):** apogee **339,800 ft ± 16,500 ft AMSL (95% CI)**, interval [324,200, 354,800]; Kármán confidence 90.4% (integration) / 85% (kinematic) / 90% combined; kinematic cross-check 338,300 ft; max vertical velocity 4,966 ft/s (1,514 m/s) at T+11.5 s; **Mach 5.1** at T+12.9 s (vs MSISE-00 sound speed); 605 ft/s² (18.8 g); burnout T+13 s; apogee T+~151 s. 1024-trial Monte Carlo on Raven-4 400-Hz z-accel (GPS lost at liftoff, regained T+278 s; baro −8,000 ft transonic error, caps 100 kft; apogee inertial-only). Vehicle: 8 in dia — essentially nothing else public. Whitepaper: https://static1.squarespace.com/static/549ce89be4b0cddb26c4894b/t/5ce58210e4966b7fc63fbe10/1558544951210/Traveler-IV-Whitepaper (mirror: https://skyweek.wordpress.com/wp-content/uploads/2019/05/67bb8-traveler-iv-whitepaper.pdf). Not reconstructable from public sources.

**6. CSXT GoFast 2014:** 385,800 ft AMSL ±0.6 mi (95%); 3,580 mph (~Mach 5.3); military-grade IMU calibrated against C-band radar. Vehicle/IMU data unpublished — not reconstructable today. https://www.prnewswire.com/news-releases/csxt-go-fast-rocket-confirms-multiple-world-records-273712291.html

**Others checked:** USC Fathom II (2017, M4, 144 kft) — data wiped by avionics brownout, dead end. BALLS 30 P-to-O attempt — forum-reported M4.9/M5.4, unverified, 403-blocked. Georgia Tech "Material Girl" (arXiv 2411.00807) — excellent documentation, but flight failed at ~30,000 ft: documentation fixture only. Sub-M4 stepping stones with superb data: **Qu8k** (2011, M3.2, 121 kft), **SVJ2** (2019, M3.66, 142 kft, https://www.multitronix.com/142k-flight.html), plus RASAero's own flight-comparison set (https://www.rasaero.com/comparisons-flight.htm).

### 6.3 Scoring-harness use

- MESOS = the gating end-to-end fixture (§2.4).
- Aftershock II = the M5.5 stretch fixture: build with estimated fins (photos) and a regressive thrust curve from impulse/peak/burn; score apogee within its published 3σ (±27,300 ft ≈ ±5.8%) and max velocity within ±5%; TPS-ablation drag is the dominant model uncertainty — do not gate, track.
- GoFast 2004 / GCC = sanity fixtures (apogee ±10%).
- Traveler IV / GoFast 2014 = reporting-only references unless USC/CSXT data is obtained.
- Caveat inherited from the verifier for ALL of these: peak-Mach numbers are inertial reconstructions; apogee (GPS-tracked where available — MESOS, GCC) is the harder, better anchor.

---

## Cross-dataset notes for the harness

1. **CP forward travel is the signature supersonic observable** and is now anchored by four independent measured datasets: ARCAS (2 configs, 78.5→57 %L over M1.5–4.63), Basic Finner (82→66 %L over M1.06–4.13), Cajun (84→74 %L over M2.3–4.63), HB-2 (xcp/l 0.46→0.55→0.475 over M1.5–10, non-monotone for the flare body). A frozen-CP kernel fails all four; that is the point.
2. **Base/total-drag accounting differs per dataset** — D-4014 CA not base-corrected; D-4013 CA,corr corrected; Finner Cx0 includes base at flight Re; HB-2 splits CAf/CAb and the CAb is facility-disputed below M2. The harness must compare like with like (add/remove modeled base drag per dataset convention).
3. **Reynolds matching matters**: ARCAS/Cajun tunnels at 3.0/2.0×10⁶ per ft; HB-2 coefficients visibly Re-dependent at M2–5; RASAero's Mach-Alt table exists precisely for this. Score at matched Re, fly at flight Re.
4. **Mach ceilings of hard data**: fully-tabulated multi-coefficient data end at M≈4.6–4.8 (ARCAS/Finner/Cajun); HB-2 extends body-only validation to M10 (multi-facility) and M16.5 (single run, CAf to 19.8 viscous-dominated); above that, validation is asymptote-shape only (RM A53D02 curve to M10, RASAero's own M25 plots). Nothing measured exists for finned vehicles above M≈4.8 — hypersonic fin behavior will be validated only indirectly (flight fixtures + HB-2 body physics).
