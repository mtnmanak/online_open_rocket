# RASAero-Class Supersonic/Hypersonic Aerodynamics — Target-Model Equation Spec

Date: 2026-08-03. Assembled from the multi-agent research workflow (RASAero II Users Manual extraction, RASAero 2011 manual extraction, open-literature equation recovery, and the carved-kernel aero map). Companion document: `docs/research/validation-anchors-2026-08-03.md`.

**Critical provenance finding, up front:** neither RASAero document (local copies: `online_open_rocket_reference` in Dropbox) contains equations. `RASAero II Users Manual.pdf` (v1.0.2.0, © 2019, 154 pp.) and `Rogers_Cooper_2011.pdf` (which is **not** a methods paper — it is the *RASAero v1.0.2.0 Users Manual, © 2011*, 62 pp.) both describe methods only by name, behavior, regime boundaries, and worked-example outputs. Every closed-form equation in section (b) below is therefore from the **open literature** (NACA/NASA/Hoerner/Fleeman/DATCOM lineage) — the same lineage RASAero's outputs are consistent with — not transcribed from Rogers & Cooper. The RASAero-side content in each area's subsection (a) is what the manuals *do* state, plus the numeric output anchors they print. Where the two manuals differ in wording or content, both are recorded.

Open-literature provenance codes (carried over from the extraction): **[T]** = transcribed verbatim from an accessible copy of the cited source; **[R]** = standard closed form as reprinted in the named secondary source, cross-checked against limiting cases. SI/dimensionless throughout; angles in radians unless noted. `M` = freestream Mach, `beta = sqrt(M^2-1)`, `gamma = 1.4`, `q = 0.5*rho*V^2`.

Kernel file key (absolute paths; `[P]` = patch overlay is current truth per `engine-java/patches/LEDGER.md`):

| ID | Path | Status |
|---|---|---|
| BC | `G:\git\online_open_rocket\engine-java\patches\info\openrocket\core\aerodynamics\BarrowmanCalculator.java` | [P] |
| FC | `G:\git\online_open_rocket\engine-java\patches\info\openrocket\core\aerodynamics\FlightConditions.java` | [P] |
| FSC | `G:\git\online_open_rocket\engine-java\patches\info\openrocket\core\aerodynamics\barrowman\FinSetCalc.java` | [P] |
| SCC | `G:\git\online_open_rocket\engine-java\src\carved\java\info\openrocket\core\aerodynamics\barrowman\SymmetricComponentCalc.java` | carved, unpatched |
| TC/TFC/LLC | `...\barrowman\TubeCalc.java`, `TubeFinSetCalc.java`, `LaunchLugCalc.java` | carved, unpatched |
| ASS | `...\simulation\AbstractSimulationStepper.java` | [P] |

---

## 1. Flight regimes & Mach breakpoints

### 1a. RASAero's method

Valid Mach range **0.01 to 25** (both manuals, p. 4 / p. 3). Regime boundaries are explicit in the Run Test output structure (RASAero II manual Fig. 108, pp. 90–92; 2011 manual pp. 37–39 — identical):

| Regime | Mach range | Drag breakdown columns printed |
|---|---|---|
| Subsonic | **0.01 – 0.90** | CD Power-Off, CD Power-On, Body Frict, Body Press, Body Base, Fin Frict&Press, Fin Interference, Fin Base, Protuberance, Reynolds No |
| Transonic | **0.91 – 1.04** | CD Power-Off, CD Power-On, Reynolds No only — **no component breakdown printed** (strong hint the transonic band is interpolative; neither manual says how) |
| Supersonic–Hypersonic | **1.05 – 25** (one continuous regime) | CD Power-Off, CD Power-On, Body Frict, Nose Cone Wave, Body Base, Fin Frict, Fin Wave, Fin Interference, Fin Base Drag, Other Body Wave Drag, Protuberance, Reynolds No |

- Transonic drag rise observed to start "at approximately **Mach 0.90**" (RASAero II p. 121; 2011 manual p. 47).
- Aero coefficients recomputed every sim time step from Mach, α, Re (at current altitude), and power-on/off state; 3-DOF adds static + dynamic stability derivatives, damping, and a **jet damping** coefficient during powered flight.
- CNα and CP reported as α = 0–4° averages; Aero Plots at α = 0, 2, 4°; Run Test accepts α = 0–15°.
- Example output rows (RASAero II Fig. 108, exact; same rocket in 2011 manual):
  - M0.50 α=0: CD_off 0.481 / CD_on 0.459; BodyFrict 0.306, BodyPress 0.026, BodyBase 0.057, FinFrict&Press 0.050, FinInterference 0.042, FinBase 0.000, Re 39,146,410. CNα(0–4°)=19.51, CP=98.115 in.
  - M0.95 α=0: 0.554 / 0.513, Re 74,378,180. CNα=19.94, CP=101.059 in.
  - M2.00 α=0: 0.631 / 0.572; BodyFrict 0.189, NoseConeWave 0.059, BodyBase 0.163, FinFrict 0.037, FinWave 0.067, FinInterference 0.031, FinBase 0.000, OtherBodyWaveDrag 0.084, Re 156,585,600. CNα=20.64, CP=105.712 in.
- Printed note (p. 92): "Other Body Wave Drag CD is Wave Drag of Transition Sections, Reducers, Fin Canisters, and Boattails, if present."
- Reference area: **Sref = maximum cross-sectional area of the rocket body**; per-stage (booster stage's max for the stack, sustainer's own when alone). CP output = inches from nose tip.
- Stability doctrine (p. 114): margin (CP−CG)/D calibers; recommended **1.0 cal subsonic, 2.0 cal transonic & supersonic**; checked only at α < 5°; CP ahead of CG stops the sim.

### 1b. Open-literature equations we will implement

There is no literature "regime breakpoint equation"; the plan adopts RASAero's regime structure (0.90 / 1.05 boundaries for reporting) with these method validity windows from the equation set below:

- Transonic nose-drag rise: OpenRocket/TR R-100 machinery, M 0.9–1.3 (area 3).
- Supersonic body method (SOSE): validity `K = M/f_N ≈ 0.4–2`; applied M≈1.1 up to ~5–6, then blended to modified Newtonian by M≈8 (area 9). RASAero-class codes run SOSE to very high M with MNT for blunt regions (NAVSWC TR 91-683 practice).
- Fin Busemann theory: attached-shock thin-airfoil supersonic, M≈1.1–5+ (area 7).

### 1c. Current kernel state and gap

- Skin-friction compressibility blends linearly across **M∈[0.9, 1.1]** (BC:702–715).
- Fin CNα: subsonic branch M≤0.9, supersonic branch M≥1.5, 5th-order polynomial interpolation across **0.9–1.5** (FSC:421–424, 469–482).
- Nose pressure drag: analytic/table data spanning M≈0.8–4 with a 4th-order poly M 1.0–1.3 (SCC:288–443).
- Body CNα/CP: **no Mach regime structure at all** (frozen, area 6). Only acknowledgement: `if (M > 1.1) warnings.add(Warning.SUPERSONIC)` (SCC:152–155).
- **Gap:** no unified regime dispatch to M25; no drag-component breakdown matching RASAero's output taxonomy (useful for scoring); the M1.05–25 continuous supersonic regime must be built (areas 3, 6, 7, 9).

---

## 2. Skin friction (compressibility, roughness, transition)

### 2a. RASAero's method

- Default: **laminar → transition → turbulent**, transition Reynolds number = **500,000 (flat-plate value)** (RASAero II p. 55; 2011 p. 21).
- **All Turbulent Flow option** forces immediate transition. When to use (flight-data-derived, pp. 115–116): two-stage rockets with diameter increase (expansion trips flow on booster); Mach 3+ rockets (heating roughens surface) → **All Turbulent + Rough Camouflage Paint**.
- Equivalent sand roughness input (Table 1 p. 53 / Table 2 p. 20 — identical in both manuals):

| Surface finish | k_sand (in) |
|---|---|
| Smooth (Zero Roughness) — default | 0.0 |
| Polished | 0.00005 |
| Sheet Metal | 0.00016 |
| Smooth Paint | 0.00025 |
| Camouflage Paint | 0.0004 |
| Rough Camouflage Paint | 0.0012 |
| Galvanized Metal | 0.006 |
| Cast Iron (Very Rough) | 0.01 |

- Re for Aero Plots/Run Test computed at **sea level** by default; the **Mach-Alt** table (first point always M0, last always M25, linear interpolation) pins altitude (hence Re) per Mach point — affects tabular output only, never the flight sim, which always uses Mach + altitude each time step.
- No friction-law equation printed in either manual (which compressible flat-plate law is used is not stated).

### 2b. Open-literature equations

**Van Driest II, Hopkins–Inouye standard form** (the accurate branch; Van Driest 1951; Hopkins & Inouye NASA TN D-6945, 1972; equations [T] from NASA's turbulence-modeling validation page). Accuracy ±5% for M = 1.5–9, `Tw/Taw > 0.3`:

```
Incompressible baseline (Karman-Schoenherr, momentum-thickness Re):
  cf_inc(Re_theta) = 1 / ( 17.08*(log10 Re_theta)^2 + 25.11*log10 Re_theta + 6.012 )

Transformation:
  Cf = (1/Fc) * cf_inc( Re_theta' ) ,     Re_theta' = F_Retheta * Re_theta

  Fc = ( Taw/Te - 1 ) / ( asin(A) + asin(B) )^2
  A = (2*a^2 - b) / sqrt(b^2 + 4*a^2)
  B =  b          / sqrt(b^2 + 4*a^2)
  a = sqrt( (Taw/Te - 1) * Te/Tw )
  b = Taw/Tw - 1
  Taw = Te * ( 1 + r*(gamma-1)/2 * Me^2 ) ,    r = 0.89  (turbulent recovery; 0.9 in TN D-6945)
  F_Retheta = (Te/Tw)^(3/2) * (Tw + 110.4 K)/(Te + 110.4 K)     (mu ratio via Sutherland)
```

`Te` = edge (freestream) static temperature [K], `Tw` = wall temperature, `Taw` = adiabatic wall temperature. Adiabatic wall: `Tw = Taw` ⇒ `b = 0` — implement the general form directly; the limit is continuous. If using x-Reynolds: `Re_x' = Re_x * F_Retheta/Fc` with the Kármán–Schoenherr x-form.

**Laminar compressible** (Eckert reference temperature, J. Aeronaut. Sci. 22, 1955):

```
Cf_lam = 1.328/sqrt(Re_x) * (T*/Te)^(-0.12)
T* = Te*(0.5 + 0.5*Tw/Te + 0.16*r*(gamma-1)/2*Me^2)
```

**Engineering fits (Hoerner lineage; what OpenRocket ships — see 2c)**: incompressible laminar `1.328/sqrt(Re)`, transitional `1/(1.50 ln Re − 5.6)^2 − 1700/Re`, turbulent `1/(1.50 ln Re − 5.6)^2`, roughness-limited `0.032 (eps/L)^0.2`; compressibility factors per branch (below). Note: the kernel's `1/(1+0.15 M^2)^0.58` tracks Van Driest II (adiabatic, r=0.89) within a few percent to M≈4; **switch to Van Driest II above M≈4 and for hot/cold walls**.

### 2c. Current kernel state and gap

BC.calculateFrictionCD (BC:506–617; helpers BC:627–745):

- `Re = V * L_aero / nu` — **single Re for the whole rocket** on the full aerodynamic length (BC:627–630).
- Fully turbulent (default, not perfect finish): `Re<1e4: Cf=1.48e-2`; else `Cf = 1/(1.50 ln Re − 5.6)^2` (BC:691–698). Compressibility (BC:702–715): `c1 = 1 − 0.1 M^2` (M<0.9), `c2 = 1/(1+0.15 M^2)^0.58` (M>1.1), linear blend M 0.9–1.1.
- Perfect finish (partial laminar): `Re<1e4: 1.33e-2`; `1e4≤Re<5.39e5: 1.328/sqrt(Re)`; `Re≥5.39e5: 1/(1.50 ln Re − 5.6)^2 − 1700/Re` (BC:646–655). Laminar compressibility `1/(1+0.045 M^2)^0.25` supersonic, fades in over Re 1e6–3e6 (BC:659–686).
- Roughness: `Cf_rough = 0.032 (eps/L_aero)^0.2 × roughnessCorrection(M)`; correction `1−0.1M^2` (M<0.9), `1/(1+0.18 M^2)` (M>1.1), blend between (BC:545–572, 729–745). Selection `max(Cf, Cf_rough)` (perfect finish: only if Re>1e6 and Cf_rough>Cf).
- Scaling: bodies `Cf·A_wet/A_ref` (SCC:183–185); fins `Cf·(1+2t/c_mac)·2A_fin/A_ref` (FSC:643–651); body fineness factor `(1 + 1/(2 fB))` applied to body friction (BC:603–616).

**Gap:** (1) transition model differs — RASAero defaults to laminar→turbulent at Re 500k on *any* finish, the kernel is fully-turbulent by default and only "perfect finish" gets a laminar run (to Re 5.39e5); an "All Turbulent" equivalent exists (the default) but the RASAero default (transition at 500k) does not. (2) Roughness is by OpenRocket `Finish` enum, not RASAero's 8-entry sand-roughness table — map the table onto finish roughness heights. (3) Supersonic turbulent correction is a fit that degrades above M≈4; hypersonic flight needs Van Driest II (2b). (4) No wall-temperature input anywhere (adiabatic assumed) — acceptable, but record it.

---

## 3. Nose/body wave drag per shape + transonic drag rise

### 3a. RASAero's method

- Supersonic breakdown prints **"Nose Cone Wave"** separately (example: 0.059 at M2.0). Effects of **nose cone shape and nose bluntness** included transonic through hypersonic (p. 4).
- Nose shapes (RASAero II p. 10): **conical, tangent ogive, Von Karman ogive, power law (exponent n), LV-Haack, parabolic, elliptical** + blunt spherical tip via nose tip radius. (2011 manual: tangent ogive, conic, Von Karman only.)
- No wave-drag equation printed. Calibration anchors printed in the 2011 manual:
  - **Fig. 2 (NACA RM A53D02** free-flight ballistic model, fin-stabilized cone-cylinder): CD at zero lift vs M 0–10, read ±0.01 CD: M0.7 ≈0.36; peak ≈0.54 at M≈1.0–1.1; M1.5 ≈0.50; M2.1 ≈0.45; M3.0 ≈0.31; M4.0 ≈0.24; M5.4 ≈0.16; M7.2 ≈0.15; M10.0 ≈0.10–0.11. RASAero dashed curve tracks within ≈±0.03 CD everywhere (slight overprediction M1.0–1.2 at ≈0.56 peak).
  - **Fig. 3 (NASA TR R-100** Configuration 98 family): transonic CD M0.8–1.5 for nose lN/d = 7.13, afterbody lA/d = 1.78/3.50/5.00, boattail rb/R = 1.000/0.700/0.438/0; RASAero overlays within ≈±0.02–0.03 CD, **except "prediction not valid for supersonic boattails rb/R < 0.5"** (printed limit).
- Transonic drag-rise onset ≈ M0.90 (both manuals).

### 3b. Open-literature equations

**Bonney/Jerger/Fleeman correlation** — ogive or cone nose, all-supersonic, ref. area = body cross-section [T]:

```
CD0_body_wave = (1.59 + 1.83/M^2) * [ atan( 0.5 / (lN/d) ) ]^1.69
```

`lN/d` = nose fineness; `atan` in radians (equivalent cone half-angle). 4-digit variants 1.586/1.834. Identical degree form for a sharp cone of half-angle θc (deg): `CD_wave = (0.083 + 0.096/M^2)(θc/10)^1.69`, valid M≈1.2–4, θc < ~30° (`0.083·(180/(10π))^1.69 = 1.586`, `0.096·(...)^1.69 = 1.834`).

**Exact linearized minimum-drag noses** (slender, supersonic, ≈Mach-independent plateau M≳1.2):

```
Von Karman (LD-Haack): D_wave/q = 4*A_b^2/(pi*lN^2)  =>  CD_wave (ref A_b) = (d/lN)^2
Sears–Haack:           D_wave/q = 128*V^2/(pi*l^4)   =>  CD_wave (ref A_max) = 9*pi^2/8 * (d/l)^2
```

**Transonic onset/peak, shape-by-shape (NASA TR R-100 data — this is what OpenRocket ships and RASAero calibrated against the same report):** conical & ogive analytic branch (`sinphi` = sine of aft-end slope; `param` = 0 conical, 1 full tangent ogive):

```
CD(M=1.0)  = sinphi
CD(M=1.3)  = 2.1*sinphi^2 + 0.6019*sinphi
dCD/dM|M=1   = 4/(gamma+1) * (1 - 0.5*CD(M=1))          (Wu & Aoyoma transonic slope)
dCD/dM|M=1.3 = -1.1341*sinphi
M in [1,1.3]: 4th-order poly matching the above 4 conditions
M > 1.3:   CD = mul * ( 2.1*sinphi^2 + 0.5*sinphi/sqrt(M^2-1) )
mul       = 0.72*(param-0.5)^2 + 0.82
Subsonic:  CD = a*M^b + 0.8*sinphi^2   (a,b matched to value+slope at first transonic point)
```

Free-flight pressure-drag tables (fineness 3, ref. frontal area) `{M ; CD}` [T, TR R-100 p. 16]:

```
Ellipsoid:      {1.2:0.110, 1.25:0.128, 1.3:0.140, 1.4:0.148, 1.6:0.152, 2.0:0.159, 2.4:0.162}
Power x^1/4:    {1.2:0.140, 1.3:0.156, 1.4:0.169, 1.6:0.192, 1.8:0.206, 2.2:0.227, 2.6:0.241, 3.0:0.249, 3.6:0.252}
Power x^1/2:    {0.925:0, 0.95:0.014, 1.0:0.050, 1.05:0.060, 1.1:0.059, 1.2:0.081, 1.3:0.084, 1.7:0.085, 2.0:0.078}
Power x^3/4:    {0.8:0, 0.9:0.015, 1.0:0.078, 1.06:0.121, 1.2:0.110, 1.4:0.098, 1.6:0.090, 2.0:0.084, 2.8:0.078, 3.4:0.074}
Von Karman:     {0.9:0, 0.95:0.010, 1.0:0.027, 1.05:0.055, 1.1:0.070, 1.2:0.081, 1.4:0.095, 1.6:0.097, 2.0:0.091, 3.0:0.083}
LV-Haack:       {0.9:0, 0.95:0.010, 1.0:0.024, 1.05:0.066, 1.1:0.084, 1.2:0.100, 1.4:0.114, 1.6:0.117, 2.0:0.113}
Parabolic(k=1): {0.95:0, 0.975:0.016, 1.0:0.041, 1.05:0.092, 1.1:0.109, 1.2:0.119, 1.4:0.113, 1.7:0.108}
Parabolic(1/2): {0.8:0, 0.9:0.016, 0.95:0.042, 1.0:0.100, 1.05:0.126, 1.1:0.125, 1.3:0.100, 1.5:0.090, 1.8:0.088}
Parabolic(3/4): {0.9:0, 0.95:0.023, 1.0:0.073, 1.05:0.098, 1.1:0.107, 1.2:0.106, 1.4:0.089, 1.7:0.082}
Fineness extrapolation (fN != 3): CD(fN) = CD_stag * ( CD_table / CD_stag )^( ln(fN+1)/ln(4) )
```

**Blunt (stagnation) reference** [T, BC.calculateStagnationCD]:

```
M <= 1: q_stag/q = 1 + M^2/4 + M^4/40
M > 1 : q_stag/q = 1.84 - 0.76/M^2 + 0.166/M^4 + 0.035/M^6
CD_stag = 0.85 * (q_stag/q)                       (ref. frontal area)
```

For a consistent pressure-based supersonic body model (drag AND lift from one machinery), run the **SOSE method of area 6b** over the whole body — nose wave drag falls out of the axial pressure integral.

### 3c. Current kernel state and gap

SCC.calculatePressureCD (SCC:190–221; interpolator SCC:288–443) already implements everything in 3b's transonic block: analytic conical/ogive branch (SCC:302–309, 418–443) with `sinphi` from the last 1% of profile length (SCC:87–88); the nine TR R-100 tables hard-coded (SCC:235–261); parameterized-shape blending (SCC:315–375); fineness extrapolation (SCC:377–384); subsonic fill `a·M^b + 0.8 sinphi^2` (SCC:386–412); stagnation CD (BC:916–924) plus radius-step stagnation drag (BC:793–818).

**Gap:** (1) tables end at M 2–4 (per shape) and the `LinearInterpolator` clamps flat beyond — no decay of wave drag toward the hypersonic values RASAero shows (CD falling to ≈0.15–0.17 by M19–25, Fig. 164/17); (2) the analytic branch is sampled only to M4 (SCC:105); (3) nose-tip bluntness is only the blunt-blend on fineness, not a spherical-cap model; (4) no per-component "Nose Cone Wave" reporting channel. The build must extend body pressure drag M4→25 (Fleeman correlation and/or SOSE + MNT cap, area 9) while leaving the M<4 tables (already TR R-100-consistent, i.e., RASAero-consistent) intact.

---

## 4. Boattail / transition wave drag

### 4a. RASAero's method

- Transitions (reducers), boattails, and fin canisters all contribute to **"Other Body Wave Drag"** supersonic (example: 0.084 at M2.0). Boattail inputs: body tube dia, boattail length, base dia. Fin canister: dia, length, shoulder length.
- **Validity limit (2011 manual Fig. 3 annotation, p. 6): "RASAero Prediction Not Valid for Supersonic Boattails with rb/R < 0.5."**
- Calibrated against NASA TR R-100 Config-98 family boattail data (rb/R = 1.000/0.700/0.438/0; see 3a).
- No equation printed.

### 4b. Open-literature equations

Subsonic/transonic boattail (OpenRocket [T], `gamma_bt` = fineness `= length/(d_fore − d_aft)`):

```
fineness >= 3:               CD_bt = 0
1 <= fineness < 3:           CD_bt = CD_base * (A_fore - A_aft)/A_ref * (3 - fineness)/2
fineness <= 1 (near-step):   CD_bt = CD_base * (A_fore - A_aft)/A_ref
```

Supersonic conical boattail of angle `theta_bt` (rad), first-order strip estimate from linearized 2D pressure `Cp = −2 theta_bt/beta` on the expansion surface:

```
CD_bt,wave = (2*theta_bt/beta) * (d_max^2 - d_b^2)/d_ref^2      [ref A_ref; M > ~1.2, small theta_bt]
```

Recommended consistent treatment: run the boattail (and conical transitions, increasing or decreasing) through the **SOSE machinery of area 6b** — Prandtl–Meyer expansion at the corner + exponential recovery toward the cone pressure of the negative-slope frustum. DATCOM §4.2.3.1 provides equivalent charts. Increasing transitions: nose-wave machinery of area 3 applies (kernel already does this).

### 4c. Current kernel state and gap

- Boattail: subsonic formula only (SCC:206–213, exact match of 4b's first block, with `fineness = length/(2|r_aft − r_fore|)` SCC:70). **No Mach dependence — the same base-scaled formula is used at all Mach.**
- Increasing transitions: treated with the nose-cone interpolators (Mach-aware to ~M4) plus stagnation-step drag (BC:793–818).
- **Gap:** supersonic boattail/reducer wave drag ("Other Body Wave Drag") does not exist; no rb/R < 0.5 validity guard; fin canisters are not a component concept in OpenRocket (modeled as body steps — wave drag of the step handled only by stagnation/base heuristics).

---

## 5. Base drag — power-off AND power-on (nozzle exit model, altitude thrust correction)

### 5a. RASAero's method

- Base drag is a separate component subsonic and supersonic ("Body Base"), plus separate **Fin Base** drag for blunt-TE airfoils.
- **Nozzle exit diameter per stage** is a required input "to get the correct power-on drag coefficient (CD) for each stage" (p. 86); stored per stage in .CDX1, not in the RASP motor file. 2011 manual (p. 14): "rocket motor exhaust helps to pressurize the base area of the rocket reducing base drag, thus typically the power-on drag coefficient is lower than the power-off"; **nozzle exit dia = 0 forces CD_power-on = CD_power-off**.
- **Multiple nozzles**: sum exit areas → single equivalent-area nozzle diameter (p. 50). **Boosted dart**: "Dart (NoThrust)" motor + exit dia 0.0 gives correct power-off CD (pp. 112–113).
- **Altitude thrust correction** (p. 93; MESOS doc pp. 10–12, exact equations): rasp.eng curves are assumed **sea-level**; RASAero computes exit area from the entered diameter and adds the pressure-differential thrust:

```
F_alpha = F_ref + (pinf_ref - pinf) * Ae
```

with the full nozzle model behind it (MESOS doc p. 10–11):

```
F_alpha = lambda * (mdot * Ve + (pe - pinf) * Ae),   lambda = (1 + cos(alpha_nozzle))/2
mdot = Ath * pc * { gamma * (2/(gamma+1))^((gamma+1)/(gamma-1)) * Mf/(R*Tc) }^(1/2)
Ve   = sqrt( (2*gamma/(gamma-1)) * (R*Tc/Mf) * [ 1 - (pe/pc)^((gamma-1)/gamma) ] )
eps  = Ae/Ath = [ ((gamma-1)/2)^(1/2) * (2/(gamma+1))^((gamma+1)/(2*(gamma-1))) ]
                / [ (pe/pc)^(1/gamma) * ( 1 - (pe/pc)^((gamma-1)/gamma) )^(1/2) ]
```

Only `(pe − pinf)·Ae` varies with altitude; mdot, Ve, λ do not. Atmospheric pressure profile anchored to launch-site conditions. The same exit-diameter input feeds both power-on CD and Ae.

- Small vs large nozzle (worked examples): CTI I205 (0.630 in exit, 3.10 in body) — "little difference" power-on vs off; Kosdon O10000 (2.5 in exit, 4.25 in base) — "noticeable decrease" power-on. Fig. 165 reads (±0.02): off/on ≈0.52/0.51 (M≈0.02), min ≈0.42/0.40 near M0.8, peak ≈0.695/0.635 at M≈1.05–1.1, plateau ≈0.56/0.52 at M1.8–2.2, ≈0.42/0.38 at M3.25. Fig. 164 (M0–25, ±0.01): power-off ≈0.24 (M6), ≈0.165 (M12.5), flat ≈0.150–0.155 by M19–25; power-on ≈0.01 lower.
- ΔCD(off−on) anchors from Fig. 108 example rocket: 0.022 (M0.5), 0.041 (M0.95), 0.059 (M2.0). ARCAS anchors: constant 0.017 at M0.01–0.02 (both configs).
- **Neither manual prints the power-on base-drag equation or the "large-nozzle supersonic extension".**

### 5b. Open-literature equations

Power-off base drag vs Mach (Hoerner-family, ref. base area) [T]:

```
M <= 1:  CD_base = 0.12 + 0.13*M^2
M >  1:  CD_base = 0.25 / M
```

Hard floor (vacuum base): `Cp_base >= -2/(gamma*M^2)` i.e. `CD_base <= 2/(gamma*M^2) = 1.43/M^2` — the 0.25/M law respects this to M≈6; above that switch to the vacuum limit scaled by measured recovery, `CD_base ≈ K * 2/(gamma*M^2)` with K≈0.55–0.7 (Hoerner Ch. 16 supersonic base-pressure data).

Power-on with nozzle exit area (Fleeman, *Tactical Missile Design* 2nd ed. Ch. 2) [R] — only the annulus outside the nozzle exit carries base drag; the plume-covered area carries none:

```
CD_base,power-on = (1 - Ae/A_b) * CD_base,coast(M)
```

Large `Ae/A_b` (minimum-diameter) → base drag → 0 during burn. Plume-induced pressurization / plume expansion at high altitude adds a further decrement **not covered by any open closed form**.

### 5c. Current kernel state and gap

- Base CD (BC:933–939): exactly 5b's Hoerner form. Applied per SymmetricComponent whose aft radius exceeds the next fore radius: `CD = CD_base(M) · π(r_aft²−r_next²)/A_ref` (BC:834–908).
- **Power-on nozzle patch (v0.023, RASAero feature #2, BC:885–897 + LEDGER.md:88–116):** `A_base,power-on = max(0, A_base − A_nozzleExit)` while the owning stage thrusts — i.e., exactly Fleeman's annulus scaling implemented as an area subtraction. Plumbing: `FlightConditions.thrustingStages` (FC:85, 418–437), populated per step in ASS:111–135 from active motors with thrust > 0; `AxialStage.nozzleExitDiameter` input. Chosen to reproduce RASAero's ARCAS off↔on split (≈0.017 ΔCD at low Mach).
- **Gap:** (1) the **large-nozzle supersonic extension** (RASAero's supersonic power-on behavior, ΔCD growing with Mach: 0.022→0.041→0.059 in the example; ARCAS power-on curves) is deferred (feature #1) and has **no published equation** — must be calibrated against the ARCAS/Fig-165 anchors; (2) **altitude thrust correction** `F = F_ref + (pinf_ref − pinf)·Ae` is not in the kernel (OpenRocket thrust curves are used as-is) — MESOS shows this is worth ≈32,000 ft of 290k (ε 6.5 vs 12.84 study); (3) 0.25/M has no vacuum-limit switch above M≈6; (4) no fin base drag / blunt-TE accounting beyond ROUNDED (+CD_base/2) and SQUARE (+CD_base) TE.

---

## 6. Body CNα/CP vs Mach (the frozen-above-M1 gap)

### 6a. RASAero's method

- Subsonic default: **Barrowman per Centuri TIR-33**. Option: **Rogers Modified Barrowman** = Barrowman + (1) body-tube-cylinder CNα term, (2) Kbf body-in-presence-of-fins factor, (3) Jorgensen viscous crossflow (verbatim list, both manuals — see area 10).
- Transonic/supersonic: the ARCAS comparison doc shows a distinct **"Transonic-Supersonic CP" method** (circles) engaging at M0.95–1.20 (values: Short 78.5→80.3 %L, Long 78.7→80.7 %L over M0.95–1.2 — a *rearward* transonic shift), then the **Supersonic CP** method from M1.5 up (red dots): Short 79.3 (M1.5), 75.5 (M2), 71.9 (M2.5), 68.6 (M3), 65.8 (M3.5), 63.5 (M4), 61.6 (M4.5) %L; Long 79.5, 75.6, 71.8, 68.3, 65.3, 62.5, 60.5 %L. No method name or equation printed anywhere.
- High-Mach CP: "by Mach 5 the Center of Pressure can move up to **60–70% of the body length** from the nose" depending on fin design; Fig. 83 (Full Metal Jacket1, ±1.5 in): α=0 CP ≈102–103 in subsonic, peak ≈108–109 at M1.1–1.5, then ≈90 (M5), ≈80 (M8), ≈73 (M12.5), ≈65 (M18.7), ≈58–59 (M25) — total travel ≈42% of body length.
- Rogers commentary (ARCAS doc p. 13): supersonic CP "very accurate from Mach 1.5 to Mach 3"; extrapolating tunnel data, ARCAS CP approaches 50 %L by Mach 5.
- Calibration basis (2011 manual p. 3): "calibrated against NACA and NASA wind tunnel model, free-flight model and sounding rocket data, published professional aerodynamic data for missiles, and several professional engineering method aerodynamic analysis programs."

### 6b. Open-literature equations — Second-Order Shock-Expansion (SOSE)

Primary source: Syvertson & Dennis, NACA Report 1328 (1957) = NACA TN 3527 (1955). Working equations [T] via the Cambridge Aeronautical Journal ROM-evaluation restatement (their Eqs. (5)–(11) restating TN 3527 Eqs. (8)–(11)).

Discretize the body of revolution into tangent conical frustums. On each frustum, downstream of the corner at `x2`:

```
p(x) = pc - (pc - p2) * exp(-eta)                            (TN 3527 Eq. 8)
eta  = (dp/ds)_2 * (x - x2) / [ (pc - p2) * cos(delta2) ]
```

- `p2` = pressure just downstream of the corner from a Prandtl–Meyer expansion (or oblique shock if compression) through `delta1 − delta2` starting from surface Mach `M1`, pressure `p1` at the end of the previous frustum.
- `pc` = pressure on a cone of half-angle `delta2` at freestream `M` (exact Taylor–Maccoll solution or NASA SP-3004 tables).
- `delta_i` = local surface inclination; `s` = surface distance; `x` axial.

Pressure gradient just downstream of the corner:

```
(dp/ds)_2 = (B2/r) * [ (Omega1/Omega2)*sin(delta1) - sin(delta2) ]
            + (B2/B1) * (Omega1/Omega2) * (dp/ds)_1

B_i     = gamma * p_i * M_i^2 / ( 2*(M_i^2 - 1) )
Omega_i = (1/M_i) * [ (1 + (gamma-1)/2 * M_i^2) / ((gamma+1)/2) ]^((gamma+1)/(2*(gamma-1)))
```

`r` = body radius at the corner; `(dp/ds)_1` = gradient at end of previous frustum (zero on the nose-tip cone); `Omega` = 1-D isentropic area-ratio function.

Cone boundary condition (exact Taylor–Maccoll; solve once per (M, angle) or tabulate):

```
(gamma-1)/2 * [1 - Vr^2 - (dVr/dtheta)^2] * [2*Vr + (dVr/dtheta)*cot(theta) + d2Vr/dtheta2]
    - (dVr/dtheta) * [ Vr*(dVr/dtheta) + (dVr/dtheta)*(d2Vr/dtheta2) ] = 0
```

`Vr` = radial velocity normalized by max speed; integrate from the oblique-shock jump (NACA Report 1135 Eqs. 128–132) inward to surface-parallel flow.

Angle-of-attack derivatives (the CP that moves with Mach) — TN 3527 "Lifting bodies", Eqs. (14)–(18) [T, partial OCR]:

```
dCN/dalpha = (2/A_ref) * Integral_0^l [ Lambda(x) * r(x) ] dx        (structure of Eq. 14)
Lambda = 2 * Integral_0^pi  d(p/p_inf)/dalpha * cos(phi) dphi        (Eq. 15)
d(p/p_inf)/dalpha = (1-e)*d(pc/p_inf)/dalpha + e*d(p2/p_inf)/dalpha
                    + (pc/p_inf - p/p_inf)* e * d(eta)/dalpha,   e = exp(-eta)   (Eq. 16)
```

Practical implementation (what modern reimplementations and RASAero-class codes do): evaluate the SOSE pressure machinery per meridian with local effective inclination `delta_eff(phi) = delta − alpha·cos(phi)`, integrate `CN = (1/(q A_ref)) ∬ p cos(phi) r ds dphi`, `x_cp = −Cm/CN`. Because `pc`, `p2`, `eta` all depend on M, CP moves with Mach above M1 — unlike frozen Barrowman.

Validity: hypersonic similarity `K = M/f_N ≈ 0.4–2` (Rep. 1328); within ~10% for tangent ogives at moderate supersonic M; RASAero-class application M≈1.1–25 (with MNT for blunt regions — area 9). Real-gas extension: NAVSWC TR 91-683 (DTIC ADA247191, citation only). DATCOM §4.2.1.1/4.2.2.1 is the chart-based empirical alternative (no open closed form) — use SOSE + Jorgensen instead.

Note: TN 3527's own closed-form CNα/x_cp expressions (Eqs. 19–24) could **not** be OCR-transcribed — implement the meridian integration (equivalent).

### 6c. Current kernel state and gap

**The freeze is total: body CNα and CP have no Mach dependence at all** (SCC:29–31 javadoc: "Supersonic CNa and CP are assumed to be the same as the subsonic values"; repeated SCC:106–107).

- SCC:113–131: slender-body values computed **once**, cached behind `Double.isNaN(cnaCache)`:

```
cnaCache = 2*(A1 - A0)          A0 = pi*r_fore^2, A1 = pi*r_aft^2
cpCache  = (L*A1 - V_full)/(A1 - A0)      (Barrowman slender-body CP)
tube (r0==r1): cnaCache = 0
```

- SCC:137–140: per-call CP = `Coordinate(cpCache, 0, 0, cnaCache·sinc(AOA)/A_ref)` averaged with the Galejs body-lift term; **`conditions.getMach()` is never read** in this path. There is not even Prandtl–Glauert growth on the body.
- **Gap (the headline gap of the whole build):** implement Mach-dependent body CNα(M)/CP(M) — SOSE (6b) or a calibrated surrogate matched to the ARCAS/HB-2/Finner CP anchors. Change points (from the kernel map): break the `cnaCache` freeze inside `calculateNonaxialForces` (SCC:95–141, receives `conditions`); add a supersonic body-CNα term for tubes (nose-carryover lift on cylinders); gate behind a `supersonicAero` flag threaded exactly like `rogersKbf` (template BC:73/95–101/84–88/1112–1153, new patch on SCC — first-ever SCC patch). FC already provides `getMach()`/`getBeta()`.

---

## 7. Fin CNα/CP vs Mach

### 7a. RASAero's method

- Transonic/supersonic/hypersonic fin predictions include **fin sweep angle, fin airfoil, fin bluntness** (p. 4). Supersonic breakdown prints separate **Fin Frict** and **Fin Wave**; both regimes print **Fin Interference** and **Fin Base**.
- Whole-rocket CNα output samples (example rocket): 19.51 (M0.5) → 19.94 (M0.95) → 20.64 (M2.0) per rad — mild growth through transonic, then decay implied at higher M by the CP forward march.
- Aerobee 150A validation (2011 manual Fig. 5): measured CNα/deg ≈0.30–0.33 subsonic, peak ≈0.42 at M≈1.5, decays to ≈0.26 (M4), ≈0.22 (M6), ≈0.19–0.20 (M10); RASAero Rogers-Modified subsonic markers ≈0.33 vs plain Barrowman ≈0.25–0.27 (the manual's implicit argument for the Modified method).
- 3 or 4 fins in RASAero (2011); RASAero II allows multiple fin sets, fins per body tube or fin canister; boosters must have fins.
- No supersonic fin CNα equation named (Busemann-class behavior implied by "fin sweep/airfoil effects").

### 7b. Open-literature equations

Busemann second/third-order supersonic thin-airfoil theory (Barrowman's thesis lineage; Liepmann & Roshko §4.15–4.17) — already the kernel's supersonic branch:

```
beta = sqrt(M^2 - 1)
K1 = 2/beta
K2 = ((gamma+1)*M^4 - 4*beta^2) / (4*beta^4)
K3 = ((gamma+1)*M^8 + (2*gamma^2 - 7*gamma - 5)*M^6 + 10*(gamma+1)*M^4 + 8) / (6*beta^7)
CNa1 = A_fin * (K1 + K2*alpha + K3*alpha^2) / A_ref
```

Busemann second-order surface pressure (for airfoil pressure work):

```
Cp = C1*delta + C2*delta^2
C1 = 2/beta
C2 = [ (gamma+1)*M^4 - 4*(M^2-1) ] / ( 2*(M^2-1)^2 )
```

Fin-body interference, exact slender-body (NACA Report 1307, Pitts/Nielsen/Kaattari, Eq. (14); [R], limits OCR-verified `K_W(B)→1` as r/s→0, `→2` as r/s→1):

```
lambda = r/s      (r = body radius at fin station, s = fin tip radius = exposed semispan + r)

K_W(B) = (2/pi) * [ (1 + lambda^4) * ( (1/2)*atan( (1/2)*(1/lambda - lambda) ) + pi/4 )
                    - lambda^2 * ( (1/lambda - lambda) + 2*atan(lambda) )
                  ] / (1 - lambda)^2

K_B(W) = (1 + lambda)^2 - K_W(B)          (lift carried on the BODY due to the wing)
K_N    = K_W(B) + K_B(W) = (1 + lambda)^2
```

`K_W(B)` multiplies isolated-fin CNα; `K_B(W)` is the body carryover (placeable at its own CP). Barrowman's `1 + r/s` is the truncation; TR-1307 splits panel + carryover correctly. Deflection-case factors k_W(B)/k_B(W) (Rep. 1307 Eqs. 15–20) are chart-distributed — not needed for fixed fins.

Supersonic fin CP (already in kernel, keep): `f(M≥2) = (ar·beta − 0.67)/(2·ar·beta − 1)`, `ar = 2s²/A_fin`.

### 7c. Current kernel state and gap

FSC is the most complete part of the kernel:

- Subsonic (M≤0.9) Barrowman/Diederich: `CNa1 = 2π s²/(A_ref (1 + sqrt(1 + (1−M²)(s²/(A_fin cosGamma))²)))` (FSC:458–461).
- Supersonic (M≥1.5): the exact Busemann K1/K2/K3 form of 7b, precomputed on a Mach grid **1.5–4.9 step 0.1** and linearly interpolated, **clamped beyond 4.9** (FSC:426–445, 464–467); α clamped at STALL_ANGLE 20°.
- Transonic 0.9–1.5: 5th-order polynomial with value/slope constraints at both ends and second-derivative 0 at 0.9 (FSC:469–482).
- Multipliers: instance orientation sin²; fin-count interference 5→×0.948, 6→×0.913, 7→×0.854, 8→×0.81, >8→×0.75 (FSC:130–159); body-fin `(1+tau)`, `tau = r/(s+r)` (FSC:162–166); the `(1+tau)²` alternative commented out (FSC:167).
- Fin CP vs Mach: quarter-chord M≤0.5; `(ar·beta−0.67)/(2·ar·beta−1)` M≥2; 5th-order poly between (FSC:556–606). `beta = sqrt(|1−M²|)` floored at 0.25 (FC:27, 306–311).
- Rogers Kbf patch (v0.022, feature #3, FSC:56–65/193–214): opt-in `K_B(W)`-equivalent carryover `tau·(1+tau)·(fin-alone) = tau·cna` placed at the fin **root quarter-chord**, weight-averaged CP; net CP moves aft; flag off = bit-identical.
- Roll damping: strip Busemann supersonic, transonic linear interp M0.89–1.51 (FSC:485–546).

**Gap:** (1) K1/K2/K3 grid stops at M4.9 — clamped-flat CNα above Mach 5 (should keep decaying ∝1/beta); extend the grid or evaluate analytically to M25, with a hypersonic fin treatment (MNT wedge pressures) above the Busemann validity; (2) the supersonic CP formula is used unchanged to any M — validate/blend against hypersonic behavior; (3) Kbf carryover placement (root quarter-chord) vs Rep. 1307's body-CP placement — acceptable, documented in LEDGER; (4) fin-count interference constants differ from RASAero's (unknown) treatment — leave as-is, score against data.

---

## 8. Fin airfoil cross-sections (all 8) + leading-edge bluntness wave drag

### 8a. RASAero's method

The 8 airfoil sections (RASAero II Figs. 14a–14c, pp. 14–16), with required geometry inputs:

1. **Hexagonal** — thickness; LE diamond length; TE diamond length (flat mid-panel; diamond lengths measured parallel to body tube, at half-span).
2. **NACA** — thickness only (max-thickness point; inherently rounded LE).
3. **Double-Wedge** — thickness; LE diamond length (max-thickness point need not be at half-chord).
4. **Biconvex** — thickness only.
5. **Hexagonal Blunt-Base** — thickness; LE diamond length (square-cut base ⇒ fin base drag).
6. **Single-Wedge** — thickness only (thickest at TE, blunt base).
7. **Rounded** — thickness only (round LE and TE; no LE-radius input allowed).
8. **Square** — thickness only (flat plate; "specifically has a non-rounded leading edge").

- **Fin LE Radius**: cylindrical blunt LE addable to any airfoil **except** NACA, Rounded (already round) and Square (deliberately sharp-cornered); default 0 = sharp. Feeds fin-bluntness effects transonic→hypersonic.
- Diamond lengths and thickness entered as half-span/average values if varying along span.
- Blunt-base sections (hexagonal blunt-base, single wedge, square) produce nonzero **Fin Base** drag.
- No wave-drag equations printed.

### 8b. Open-literature equations (linearized/Busemann; DATCOM §4.1.5.1; Hoerner)

Section wave drag per unit span, ref. chord `c`, `tau = t/c`, valid M > ~1.1, attached LE shock, `tau ≲ 0.1`. Master linearized formula:

```
cd_wave = (2/(beta*c)) * ClosedIntegral_upper+lower (dy/dx)^2 dx  +  4*alpha^2/beta
```

Per-shape thickness term `cd_t` (add `4 alpha²/beta` lift-induced term in all cases):

```
Flat plate:                       cd_t = 0
Single wedge (sharp LE, full-height blunt TE):
                                  cd_t = (1/beta)*tau^2   + base term: -Cp_b * tau
Double wedge (diamond), max t at x/c = m:
                                  cd_t = tau^2 / ( beta * m*(1-m) )
  symmetric diamond (m = 1/2):    cd_t = 4*tau^2 / beta
Biconvex (circular/parabolic arc):cd_t = (16/3)*tau^2 / beta
Hexagonal, LE chamfer a1*c, TE chamfer a2*c (flat mid-panel):
                                  cd_t = (tau^2/beta) * (1/a1 + 1/a2)
Hexagonal blunt TE, LE chamfer a1*c:
                                  cd_t = (tau^2/beta) * (1/a1)  +  (-Cp_b)*tau_TE
Square (slab):                    cd_t = CD_stag(M)*tau  +  CD_base(M)*tau
```

`−Cp_b·tau` base terms use CD_base from area 5b per unit base height; CD_stag from area 3b. RASAero's 8 sections map: Hexagonal → hexagonal; NACA → treat as biconvex-class + rounded LE; Double-Wedge → diamond with `m = a1` from LE diamond length; Biconvex → biconvex; Hexagonal Blunt-Base → hexagonal blunt TE; Single-Wedge → single wedge; Rounded → biconvex-class + rounded LE + half base; Square → slab.

**Blunt leading edge** (radius r_LE, sweep Λ) — swept-cylinder stagnation-line drag via the crossflow independence principle, normal Mach `M_n = M·cos Λ`, force ∝ cos³Λ:

```
D'_LE (per unit span) = CD_cyl(M_n) * q * (2*r_LE) * cos^3(Lambda)
Newtonian swept-cylinder limit:  CD_cyl = (2/3) * Cp_max(M_n)        (ref. diameter; → 1.22 as M_n → inf)
```

Empirical CD_cyl(M) transonic/supersonic fit (OpenRocket rounded-LE implementation [T]):

```
M < 0.9:        cd_LE = (1 - M^2)^-0.417 - 1
0.9 <= M < 1:   cd_LE = 1 - 1.785*(M - 0.9)
M >= 1:         cd_LE = 1.214 - 0.502/M^2 + 0.1095/M^4
applied as:     cd = cd_LE * cos^2(Gamma_LE);  TE adds CD_base/2 (rounded) or CD_base (square)
```

### 8c. Current kernel state and gap

FSC:654–699 supports exactly **three** cross sections: AIRFOIL, ROUNDED (both use the cd_LE fit above), SQUARE (CD_stag LE + CD_base TE); sweep via cos²Γ_LE; scaled by span×thickness frontal area. Friction gets the `(1 + 2t/c)` factor (FSC:643–651).

**Gap:** 5 of RASAero's 8 sections have no kernel counterpart (hexagonal, double-wedge, biconvex, hexagonal blunt-base, single-wedge), and there are no LE/TE diamond-length or fin-LE-radius inputs. Implementing 8b gives per-shape thickness wave drag `∝ tau²/beta` — currently entirely absent (kernel fin "pressure drag" is LE/TE-only, no thickness wave term), which materially underpredicts fin wave drag for thick sections at supersonic Mach. New geometry inputs required on the FinSet component (section type, a1/a2 lengths, LE radius).

---

## 9. Hypersonic treatment (modified Newtonian blending)

### 9a. RASAero's method

- The supersonic–hypersonic regime is **one continuous method band, M1.05–25**; no hypersonic method is named in either manual. Behavior anchors: CD decays smoothly to a flat ≈0.150–0.155 by M19–25 (Fig. 164); CP marches forward ~42 %L by M25 (Fig. 83/19); calibrated to NACA RM A53D02 free-flight data M1–10 within ≈±0.03 CD (Fig. 2).
- Nose and fin bluntness effects included at hypersonic Mach (p. 4). US-1976 atmosphere extended to 1,000,000 ft.

### 9b. Open-literature equations

Modified Newtonian theory (Lees 1955; working equations [T] from the Cambridge ROM-evaluation, Eqs. (2)–(3)):

```
Cp = Cp_max * sin^2(theta)          (theta = local surface impact angle; Cp = 0 in shadow, theta <= 0)

Cp_max = (2/(gamma*M^2)) * [ ( ((gamma+1)^2 * M^2) / (4*gamma*M^2 - 2*(gamma-1)) )^(gamma/(gamma-1))
                              * ( (1 - gamma + 2*gamma*M^2)/(gamma+1) )  -  1 ]
```

`Cp_max` = stagnation Cp behind a normal shock (Rayleigh pitot, NACA Rep. 1135 Eq. 100); limit 1.839 (γ=1.4, M→∞); classical Newtonian uses 2. Working component values:

- Sharp cone at α: `Cp = Cp_max sin²(theta_c)` meridian-wise with `theta = theta_c + alpha·cos(phi)`.
- Blunted nose (spherical cap R_n): `CD_cap = (Cp_max/2)·[1 − sin⁴(theta_match)]` (ref. cap base area), matched to afterbody at `theta_match`.
- Fins: wedge LE half-angle δ: `Cp = Cp_max sin²(δ+α)` windward, 0 leeward; blunt LE via swept cylinder `CD_cyl = (2/3) Cp_max(M_n)` (area 8b).

Blending guidance (Cambridge evaluation §2.8/§4; NAVSWC TR 91-683):

- Tangent-cone: within 20% everywhere tested, within 10% for M > 2. SOSE preferred M ≲ 3–6; MNT alone only for local inclinations ≳ 55°.
- Jackson's blunt-nose matching: MNT from stagnation point to the station where local inclination = maximum attached-shock cone angle at that M; SOSE/tangent-cone downstream. Loses accuracy below M = 5.
- DeJarnette–Ford low-hypersonic fix (1.5 ≤ M ≤ 10): `Cp = Cp_max(1 − D·cos^A(theta))`, A(M), D(M) tabulated (their Eq. 13).
- **Practical schedule for a RASAero-class code: SOSE for 1.1 < M < ~5–6; fade linearly in M to full modified-Newtonian by M ≈ 8; below M1.1 use the transonic tables (area 3).** (RASAero itself appears to run SOSE to very high M with MNT for blunt regions, per NAVSWC TR 91-683 practice — inference, not documented.)

### 9c. Current kernel state and gap

Nothing hypersonic exists. Ceilings: nose-drag tables clamp flat at their last entry (M2–4 per shape); analytic cone/ogive branch sampled to M4; fin Busemann grid clamps at M4.9; friction fit degrades above M≈4; base 0.25/M lacks the vacuum-limit switch; ISA atmosphere (not US-1976 extended — check altitude ceiling separately). **Gap: the entire M≈4–25 band.** Build order implied: extend drag correlations with correct high-M asymptotes (areas 3, 5, 7, 8), add MNT for CP/CN at high M blended per 9b, and validate against HB-2 (M1.5–10 solid, to M16.5 thin) and the RM A53D02 curve read from the 2011 manual Fig. 2.

---

## 10. Angle-of-attack terms (Jorgensen crossflow, Kbf)

### 10a. RASAero's method

Rogers Modified Barrowman (verbatim list, both manuals):

1. "more accurate body normal force slope with angle of attack (CNalpha) at low angles of attack by including the influence of the **body tube cylinder** (left out of the Barrowman Method)";
2. "the **body in the presence of the fins interference factor (Kbf)** (left out of the Barrowman Method)";
3. "**body viscous crossflow using the Jorgensen Method** for the forward movement of the rocket center of pressure with angle of attack."

- CN = **Potential + Viscous**; Barrowman = potential only. Output samples: M0.5 α=3.5°: CN_pot 1.028, CN_visc 0.143, CN_tot 1.172, CP_tot 98.619 in. M2.0 α=3.5°: CN_pot 1.131, CN_visc 0.127, CN_tot 1.258 (viscous *decreases* at M2 — crossflow-Mach effect). Fig. 85 (M0.5): viscous ≈ potential near α≈4–5°, viscous exceeds potential above ~5°, reaching ≈5.6 vs ≈3.75 at α=15°.
- ARCAS assessment (comparison doc p. 10): for those two vehicles plain Barrowman happened to be slightly *more* accurate than Rogers Modified (Barrowman's dropped nose and tail terms partially cancel); "very close" for ARCAS Long.
- No Jorgensen report number or Kbf formula printed.

### 10b. Open-literature equations

**Jorgensen crossflow** (NASA TR R-474, 1977; TN D-7228, 1973) [R], validated M 0.6–2.9, α to 60° (subsonic)/180° (M2.9):

```
Body alone, total angle of attack alpha' (0-180 deg), ref. area A_r, ref. length d:

CN = (A_b/A_r) * sin(2*alpha') * cos(alpha'/2)                 [slender-body potential term]
     + eta * Cd_n * (A_p/A_r) * sin^2(alpha')                  [viscous crossflow term]

Cm (about x_m from nose) =
     [ (V - A_b*(l - x_m)) / (A_r * d) ] * sin(2*alpha') * cos(alpha'/2)
     + eta * Cd_n * (A_p/A_r) * ( (x_m - x_c)/d ) * sin^2(alpha')
```

- `A_b` base area; `A_p` body planform area; `V` volume; `l` length; `x_c` planform centroid from nose.
- `Cd_n` = circular-cylinder crossflow drag coefficient at crossflow Mach `M_n = M·sin(alpha')` — **chart data**: ≈1.2 for M_n ≤ 0.4, rising to ≈1.55–1.8 near M_n = 1, relaxing to ≈1.3–1.4 for M_n ≥ 2 (read from TR R-474 figure).
- `eta` = finite-length proportionality factor — **chart data**: ≈0.6–0.75 for l/d = 6–20 subcritical; `eta = 1.0` for supersonic crossflow.
- `x_cp = x_m − Cm·d/CN` — moves aft with α (crossflow acts at `x_c`), complementing the Mach-driven CP motion of area 6.

**Kbf** — the NACA Report 1307 `K_B(W)` factor of area 7b: `K_B(W) = (1+lambda)² − K_W(B)` with the closed-form `K_W(B)` given there.

### 10c. Current kernel state and gap

- **Galejs body lift is Jorgensen's crossflow term in disguise** (memory note `openrocket-galejs-is-crossflow.md`): SCC:162–180 adds `CNα_lift = K · A_planform/A_ref · sin²α/α` with `K = BODY_LIFT_K = 1.1` at the planform center — i.e., the `eta·Cd_n·(A_p/A_r)·sin²α` term with `eta·Cd_n` frozen at 1.1 and no crossflow-Mach dependence. Only Mach use is an apogee-anomaly damper for M<0.05 ∧ α>45°.
- **rogersKbf patch (v0.022)** already implements the Kbf carryover: `tau·cna` added at fin root quarter-chord, weight-averaged CP, opt-in flag threaded BC→FSC (FSC:56–65, 193–214; LEDGER.md:118–143). Note: the memory index says v0.022 shipped RASAero features #2/#5/#3 — Kbf is feature #3.
- The "body tube cylinder CNα" element of Rogers Modified is partially present via Galejs (cylinder planform contributes to A_p).
- **Gap:** (1) `eta·Cd_n = 1.1` constant vs Jorgensen's Mach-dependent Cd_n(M_n) and length-dependent eta(l/d) — matters at supersonic α (RASAero's viscous CN *falls* from 0.143 to 0.127 between M0.5 and M2 at α=3.5°; a constant-1.1 model cannot reproduce that); (2) crossflow acts at the planform center in the kernel — same as Jorgensen's `x_c`, fine; (3) no potential-term `sin(2α')cos(α'/2)` high-α shaping (kernel uses `sinc(AOA)` on the slender-body term — acceptable below ~20°).

---

## Consolidated references

Primary methods sources (open literature):

1. Syvertson & Dennis, *A Second-Order Shock-Expansion Method Applicable to Bodies of Revolution Near Zero Lift*, NACA Report 1328 (1957) = NACA TN 3527 (1955). https://ntrs.nasa.gov/citations/19930084226 ; OCR: https://digital.library.unt.edu/ark:/67531/metadc57654/m1/9/ocr/
2. *Evaluation of reduced-order models for the rapid aerodynamic analysis of supersonic and hypersonic bodies*, The Aeronautical Journal (Cambridge). https://www.cambridge.org/core/journals/aeronautical-journal/article/evaluation-of-reducedorder-models-for-the-rapid-aerodynamic-analysis-of-supersonic-and-hypersonic-bodies/E1B02263B7A331EC850CEC4E4EB87BDB (SOSE + MNT working restatement)
3. Moore, Armistead & Rowles, *Second-Order Shock-Expansion Theory Extended to Include Real Gas Effects*, NAVSWC TR 91-683, DTIC ADA247191 (1992). https://apps.dtic.mil/sti/html/tr/ADA247191/index.html (403 to non-browser fetches)
4. Sims, *Tables for Supersonic Flow Around Right Circular Cones at Zero Angle of Attack*, NASA SP-3004 (1964).
5. NACA Report 1135, *Equations, Tables, and Charts for Compressible Flow*. https://ntrs.nasa.gov/citations/19930091059
6. Stoney, *Collection of Zero-Lift Drag Data on Bodies of Revolution from Free-Flight Investigations*, NASA TR R-100. https://ntrs.nasa.gov/citations/19630004995
7. Fleeman, *Tactical Missile Design*, 2nd ed., AIAA 2006 (Bonney/Jerger nose correlation; power-on base drag). Reprint: https://kirill200281.narod.ru/Maximizing_Missile_Flight_Performance.pdf
8. Bonney, *Engineering Supersonic Aerodynamics*, 1950. Jerger, *Systems Preliminary Design*, 1960.
9. Hoerner, *Fluid-Dynamic Drag*, 1965, Ch. 16 (base drag, LE data).
10. Van Driest, J. Aeronaut. Sci. 18(3), 1951; Hopkins & Inouye, NASA TN D-6945 (1972). https://ntrs.nasa.gov/citations/19730001588 ; equations: https://tmbwg.github.io/turbmodels/ZPGflatplateSS_val.html
11. Eckert, J. Aeronaut. Sci. 22, 1955 (reference-temperature laminar).
12. Lees, *Hypersonic Flow*, IAS 1955 (modified Newtonian).
13. Jorgensen, NASA TR R-474 (1977). https://ntrs.nasa.gov/citations/19770026166 ; NASA TN D-7228 (1973): https://ntrs.nasa.gov/api/citations/19730012271
14. Pitts, Nielsen & Kaattari, NACA Report 1307 (1957). https://digital.library.unt.edu/ark:/67531/metadc65599/ ; text mirror: https://www.pdas.com/refs/rep1307.pdf
15. Liepmann & Roshko, *Elements of Gasdynamics*, Wiley 1957, §4.15–4.17 (Ackeret/Busemann airfoil theory).
16. USAF Stability & Control DATCOM §4.1.5.1, 4.2.1.1, 4.2.2.1, 4.2.3.1, 4.3.1.2; Missile DATCOM AFRL-VA-WP-TR-1998-3009 (chart-based alternatives).
17. Nielsen, *Missile Aerodynamics*, McGraw-Hill 1960, Ch. 5.
18. Ashley & Landahl, *Aerodynamics of Wings and Bodies*, §9-6 (von Kármán ogive); Sears 1947; Haack 1941.
19. Barrowman, *The Practical Calculation of the Aerodynamic Characteristics of Slender Finned Vehicles* (thesis). https://ntrs.nasa.gov/citations/20010047838
20. Niskanen, *OpenRocket technical documentation*, ch. 3 (the kernel's current model). https://openrocket.info/documentation.html

RASAero-side sources (methods named / calibration data exhibited):

21. Rogers & Cooper, *RASAero II Users Manual*, v1.0.2.0, © 2019 (local: `online_open_rocket_reference/RASAero II Users Manual.pdf` in Dropbox).
22. Rogers & Cooper, *RASAero Users Manual*, v1.0.2.0, © 2011 (local: `online_open_rocket_reference/Rogers_Cooper_2011.pdf` in Dropbox — mislabeled as a methods paper; it is a users manual).
23. Centuri Report TIR-33 (Barrowman) — default subsonic CP method.
24. NACA RM A53D02 — free-flight CD calibration, M≈0.7–10 (2011 manual Figs. 1–2).
25. NASA TR R-100 — transonic/boattail calibration (2011 manual Fig. 3); source of rb/R < 0.5 limit.
26. Vought Astronautics AST/E1R-13319, *Performance Summary for the Aerobee 150A*, April 1961; Aerojet-General Report 1784, *Wind Tunnel Tests of the Aerobee 150A (AJ60-13)*, March 1960 (2011 manual Fig. 5 validation data).
27. Rogers, "Departures from Ideal Performance" (Rogers Aeroscience technical article, rasaero.com Technical Report Downloads) — nozzle/thrust-curve methodology used in the MESOS comparison.
28. 1976 US Standard Atmosphere (extended to 1,000,000 ft in RASAero).
29. Rogers, *RASAero II Comparisons with ARCAS CP and CD Wind Tunnel Data* (local: `online_open_rocket_reference/RASAero II Comparisons with ARCAS CP and CD Data.pdf` in Dropbox; https://www.rasaero.com/dloads/RASAero%20II%20Comparisons%20with%20ARCAS%20CP%20and%20CD%20Data.pdf)
30. Rogers, *RASAero II Comparison with MESOS 293K Flight Data — Rev B*, Jan 18, 2023 (local: `online_open_rocket_reference` in Dropbox; https://www.rasaero.com/dloads/RASAero%20II%20Comparison%20with%20MESOS%20293K%20Flight%20Data%20-%20Rev%20B.pdf)

---

## Unknowns & risks (what the sources did NOT yield)

1. **No RASAero equations exist in our possession, anywhere.** Both PDFs believed to be "Rogers & Cooper methods papers" are users manuals with zero equations and (2011) no references section. The equation-level methods live in Rogers' separate technical publications (rasaero.com "Technical Report Downloads", High Power Rocketry technical series) which were not in the repo and were not fetched. Everything in the (b) subsections is a literature reconstruction of a RASAero-*class* model; fidelity to RASAero itself must come from scoring against the anchors, not from the spec.
2. **Power-on base drag / large-nozzle supersonic extension: no equation, open or RASAero.** The annulus model `(1 − Ae/A_b)` is the only open closed form; RASAero's supersonic power-on behavior (ΔCD growing with Mach, plume/altitude effects) must be reverse-engineered from Fig. 165/164, the Fig. 108 samples (ΔCD 0.022/0.041/0.059 at M0.5/0.95/2.0), and the ARCAS power-on curves.
3. **The transonic band (M0.91–1.04) treatment is unknown** — RASAero prints totals only there; likely interpolative, but the manuals do not say. Kernel's poly interpolations are a defensible substitute; score against the ARCAS transonic peaks and HB-2 transonic tables.
4. **RASAero's supersonic/hypersonic CP method is unnamed.** SOSE is the strong inference (behavior matches, NAVSWC lineage, "calibrated against professional engineering method programs"), but it is an inference. The ARCAS comparison shows RASAero itself diverging from the tunnel above M3.5 (RASAero 63.5 %L vs tunnel 60.7 %L at M4, Short) — matching RASAero exactly and matching the wind tunnel are different targets above M3.5; the scoring harness must pick one (recommend: tunnel data primary, RASAero secondary).
5. **Chart-only data:** Jorgensen's Cd_n(M_n) and eta(l/d) curves, Rep. 1307 deflection factors k_W(B)/k_B(W), DATCOM boattail charts, DeJarnette–Ford A(M)/D(M) — numeric ranges captured, full curves not. TN 3527 Eqs. (19)–(24) closed forms not transcribed (meridian integration substitutes).
6. **DTIC full texts 403-blocked** to automated fetch (ADA247191 real-gas SOSE, ADA109180): citations only; archive.org mirrors exist for some (see validation doc).
7. **Protuberance / rail-guide / launch-lug drag correlations**: RASAero models them (2 guides/lugs/shoes, inclined-flat-plate protuberances with angle input) with claimed high accuracy; no equations found in any source. Kernel has LaunchLugCalc = 0 friction and simple tube handling. Low priority for the supersonic build but a known scoring confounder at low Mach (ARCAS models fin anchors as an equivalent rail guide with a stated ÷5·÷2 area convention — see validation doc).
8. **Jet damping and dynamic-derivative formulas**: named in the manuals, no equations. Finner/HB-2 Cmq data exist for future validation but the target model here is static coefficients.
9. **Atmosphere ceiling**: RASAero extends US-1976 to 1,000,000 ft; our ISA implementation's validity ceiling must be checked before simulating 300k-ft flights (MESOS apogee ≈293k ft).
10. **Wall temperature**: all friction treatments assume adiabatic wall; sustained M5+ flight has cold-wall effects the Hopkins–Inouye form can handle but we have no Tw model. Accept adiabatic; document.
11. **RASAero boattail validity** rb/R < 0.5 supersonic: our model should emit a warning in that region rather than silently predict.
