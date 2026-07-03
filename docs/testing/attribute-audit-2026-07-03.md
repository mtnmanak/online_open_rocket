# Component attribute audit — web app vs desktop OpenRocket 24.12

Date: 2026-07-03. Desktop reference: setters in
`core/.../rocketcomponent/*.java` (release 24.12). Web reference:
`packages/app/src/tree/schema.ts` (FIELDS) + `PropertyPanel.tsx` special sections +
`engine-java/src/api/java/api/ComponentFactory.java` (what the bridge already accepts).

**Gap class legend** — `UI-only`: ComponentFactory already accepts the JSON key, only a
schema/panel field is missing. `Bridge`: carved core has the setter but ComponentFactory
doesn't map it (add JSON key + UI). `OOS`: multi-stage / cluster / pod scope, deliberately out.

## Top 10 physics-significant gaps (prioritized)

| # | Gap | Physics impact | Class | Effort |
|---|-----|----------------|-------|--------|
| 1 | **Fin tabs** (tabHeight/tabLength/tabOffset+method, all fin sets) | Through-wall tabs add real mass & shift CG; every TTW build is wrong without them | Bridge | medium (bridge + fin-tab UI block) |
| 2 | **Material density on internal components** (inner tube, coupler, centering ring, bulkhead, engine block) | Their mass currently locked to carved defaults; airframe mass/CG off | UI-only (density key already bridged for StructuralComponent) | small |
| 3 | **Recovery-device & shock-cord materials** (surface kg/m² fabric, line material) | Chute/streamer/cord mass fixed at desktop defaults; recovery-bay CG wrong | Bridge (needs SURFACE/LINE Material in factory) | medium |
| 4 | **Motor overhang + ignition event/delay** | Overhang shifts loaded CG aft (stability at rail exit); ignition config drives sim events | Bridge (IgnitionConfiguration is carved) | small |
| 5 | **Fin fillets** (filletRadius + filletMaterial) | Adds mass at the root, shifts CG aft on big fins | Bridge | medium |
| 6 | **Shape parameter** for power/Haack/parabolic nose & transition | Changes profile → CP and pressure drag. Nose is *already bridged* (`shapeParameter`); transition needs bridge (+ `clipped`) | UI-only (nose) / Bridge (transition) | small |
| 7 | **Body tube as motor mount** | Minimum-diameter designs can't be modeled (mount is inner-tube-only today) | Bridge (BodyTube.setMotorMount exists) | medium |
| 8 | **Rail button geometry** (base/flange/screw heights, inner ⌀, instance count) | Desktop computes rail-button drag from total height × ⌀; web has only outer ⌀ so drag is wrong | Bridge | small–medium |
| 9 | **Tube fin thickness + density in UI** | Tube-fin mass currently default-material, default-thickness; mass/CG off | Bridge (thickness) + UI-only (density, already bridged) | small |
| 10 | **Packed length/radius + radial position of mass objects** (parachute, streamer, shock cord, mass component radial) | Packed length sets where the CG of recovery gear sits; radial offsets matter for 6DOF | Bridge (MassObject setters exist) | medium |

Near-misses: elliptical-fin cant (UI-only, roll only), fin base rotation / angle offsets
(cosmetic + roll), `perfectFinish` rocket flag (drag), reference type/custom caliber
(reported stability only), comment field (no physics).

## Common attributes (RocketComponent + ExternalComponent, all 18 types)

| Desktop attribute | In web app? | Notes / physics impact |
|---|---|---|
| name | yes | |
| comment | no | Cosmetic. Bridge is trivial if wanted for .ork round-trip fidelity |
| appearance / color / lineStyle, inside appearance | no | Cosmetic (no 3D render yet). OOS for now |
| overrideMass / overrideCGX / overrideCD | yes | All three, blank = calculated |
| setSubcomponentsOverridden(Mass/CG/CD) | no | "Override for all subcomponents" flags — Bridge, small |
| axialMethod (TOP/MIDDLE/BOTTOM/ABSOLUTE/AFTER) + axialOffset | partial | UI offers top/middle/bottom; `absolute` already bridged (UI-only); AFTER is implicit top-level ordering |
| instanceCount (generic) | partial | finCount covers fin sets; lug/railbutton/ring line-instances missing (see per-type) |
| material (bulk density) | partial | UI field only on external components + fin sets; bridged for ALL Structural/External — internal components are UI-only gaps (top-10 #2) |
| finish (surface roughness) | partial | On nose/transition/bodytube/fin sets/tube fins; missing from launch lug & rail button UI (UI-only; skin-friction drag, minor area) |

## Per-component tables (type-specific attributes; common table above applies to all)

### Nose cone
| Desktop | Web? | Notes |
|---|---|---|
| length, baseRadius, thickness, filled | yes | |
| baseRadiusAutomatic | no | Convenience (match parent); Bridge, small |
| shapeType (6 shapes) | yes | |
| shapeParameter | no | **Bridged already** — power exponent / Haack C / parabolic k. CP+drag. Top-10 #6 |
| aft shoulder radius/length/thickness/capped | yes | Plus "fit shoulder to tube" helper |
| flipped (tail cone mode) | no | Tail cones must be faked with a Transition; Bridge, small |

### Transition
| Desktop | Web? | Notes |
|---|---|---|
| length, fore/aft radius, thickness, filled, shapeType | yes | Blank radius → automatic is already bridged (UI-only to expose) |
| shapeParameter, clipped | no | Bridge; CP + pressure drag on power/Haack transitions. Top-10 #6 |
| fore+aft shoulder radius/length | yes | |
| fore/aft shoulder thickness, capped | no | Bridge (factory skips them); small shoulder mass |

### Body tube
| Desktop | Web? | Notes |
|---|---|---|
| length, outerRadius, thickness | yes | |
| outerRadiusAutomatic | no | Bridge, convenience |
| motorMount + motorConfig + motorOverhang | no | Top-10 #4/#7 — mount flag exists only on inner tube; overhang/ignition unbridged |

### Trapezoidal fin set
| Desktop | Web? | Notes |
|---|---|---|
| finCount, rootChord, tipChord, sweep, height, thickness, cant, crossSection | yes | Cant + cross-section drive roll & profile drag |
| sweepAngle (alt param) | no | Convenience only (sweep length covers it) |
| tabHeight/tabLength/tabOffset(+method) | no | **Top-10 #1** — TTW tab mass/CG |
| filletRadius + filletMaterial | no | **Top-10 #5** — root fillet mass |
| baseRotation / angleOffset(+method) | no | Fin clocking; roll-coupling with cant, else cosmetic. Bridge, small |
| radius/radiusMethod (fin ring radius) | no | Pod-mounted fins; OOS |

### Elliptical fin set
| Desktop | Web? | Notes |
|---|---|---|
| finCount, length (root chord), height, thickness, crossSection | yes | |
| cant | no | **Bridged already** (factory sets it) — UI-only, roll |
| tabs, fillets, baseRotation | no | Same as trapezoid (#1, #5) |

### Freeform fin set
| Desktop | Web? | Notes |
|---|---|---|
| finCount, thickness, cant, crossSection, points | yes | Point editor: table + draggable SVG |
| setPoint (single-point API) | yes | Equivalent via editor |
| tabs, fillets, baseRotation | no | Same as trapezoid (#1, #5) |

### Tube fin set
| Desktop | Web? | Notes |
|---|---|---|
| finCount, length, outerRadius(+automatic) | yes | Blank radius → auto is bridged |
| thickness | no | Bridge — **Top-10 #9**; wall mass |
| material density | no | UI-only (common density key applies) — #9 |
| baseRotation / angleOffset | no | Cosmetic/roll; Bridge, small |

### Inner tube
| Desktop | Web? | Notes |
|---|---|---|
| length, outerRadius, thickness | yes | |
| motorMount flag | yes | Checkbox |
| motorOverhang, ignition event/delay | no | **Top-10 #4** — Bridge in OrkEngine.setMotor path |
| clusterConfiguration / clusterScale / clusterRotation / instanceCount | no | OOS (cluster) |
| material density | no | UI-only — top-10 #2 |

### Tube coupler
| Desktop | Web? | Notes |
|---|---|---|
| length, thickness | yes | |
| outerRadius manual (vs automatic) | no | Bridged (factory optional key) — UI-only; default auto-fit is fine |
| radialPosition/Direction | no | Bridge; 6DOF lateral CG, minor |
| material density | no | UI-only — top-10 #2 |

### Centering ring
| Desktop | Web? | Notes |
|---|---|---|
| length (axial thickness) | yes | |
| outer/inner radius manual (+automatic) | no | Bridged optional keys — UI-only; auto snap to tube/innertube is default |
| instanceCount + instanceSeparation | no | Bridge; N rings modeled as one component — mass ×N |
| material density | no | UI-only — top-10 #2 |

### Bulkhead
| Desktop | Web? | Notes |
|---|---|---|
| length | yes | |
| outerRadius manual (+automatic) | no | Bridged optional key — UI-only |
| instanceCount/separation | no | Bridge (RadiusRingComponent) |
| material density | no | UI-only — top-10 #2 |

### Engine block
| Desktop | Web? | Notes |
|---|---|---|
| length, thickness | yes | |
| outerRadius manual (+automatic) | no | Bridged optional key — UI-only |
| material density | no | UI-only — top-10 #2 |

### Launch lug
| Desktop | Web? | Notes |
|---|---|---|
| length, outerRadius, thickness | yes | |
| angleOffset (radial clocking) | no | Bridge; cosmetic + 6DOF asymmetry, minor drag placement |
| instanceCount + instanceSeparation | no | Bridge; two-lug builds carry ~2× lug mass/drag |
| finish, material density | no | UI-only (common keys apply); lug drag & mass |

### Rail button
| Desktop | Web? | Notes |
|---|---|---|
| outerDiameter | yes | Only field |
| baseHeight, flangeHeight, totalHeight, screwHeight, innerDiameter | no | **Top-10 #8** — desktop drag model uses height×⌀; mass too. Bridge |
| angleOffset, instanceCount/separation | no | Bridge; two buttons standard |

### Parachute
| Desktop | Web? | Notes |
|---|---|---|
| diameter, CD(+automatic), lineCount, lineLength | yes | Blank Cd = auto (0.8-style default) |
| deployEvent / deployAltitude / deployDelay | yes | Default flight config only (fine, single config). Missing event: LOWER_STAGE_SEPARATION — OOS |
| canopy material (SURFACE), line material (LINE) | no | **Top-10 #3** — chute mass |
| packed length/radius (+radiusAutomatic) | no | **Top-10 #10** — recovery CG placement |
| radialPosition/Direction | no | Bridge; 6DOF minor |
| area (alt param) | no | Convenience only |

### Streamer
| Desktop | Web? | Notes |
|---|---|---|
| stripLength, stripWidth, CD(+auto) | yes | |
| deployEvent/altitude/delay | yes | Same notes as parachute |
| material (SURFACE) | no | **Top-10 #3** — streamer mass; also feeds desktop's auto-CD formula (web auto-CD uses default material) |
| aspectRatio/area (alt params) | no | Convenience |
| packed length/radius, radial pos | no | #10 |

### Shock cord
| Desktop | Web? | Notes |
|---|---|---|
| cordLength | yes | |
| material (LINE) | no | **Top-10 #3** — cord mass scales with length only if material known |
| packed length/radius, radial pos | no | #10 |

### Mass component
| Desktop | Web? | Notes |
|---|---|---|
| mass, length, radius | yes | |
| density (auto-couples mass↔volume) | no | Convenience; mass field covers physics |
| massComponentType (altimeter/battery/…) | no | Label + default densities only; cosmetic |
| radiusAutomatic, radialPosition/Direction | no | #10; 6DOF lateral CG |

## Rocket / stage level (context)

| Desktop | Web? | Notes |
|---|---|---|
| Rocket: designer, revision, kitName, designType | no | Cosmetic / .ork metadata |
| Rocket: referenceType + customReferenceLength | no | Changes *reported* stability calibers, not the flight; Bridge, small |
| Rocket: perfectFinish flag | no | Zeroes surface-roughness drag; Bridge, small |
| Flight configurations (multiple) | no | Web uses single default config — acceptable for single-stage scope |
| AxialStage: separation event/delay, stage count/ordering | no | OOS (single-stage) |
| ParallelStage / PodSet / Boosters | no | OOS |
| InnerTube cluster (config/scale/rotation) | no | OOS (explicitly deferred with stages) |

## Bridged-but-no-UI quick list (cheapest wins, all `UI-only`)

nosecone `shapeParameter`; elliptical `cant`; density on inner tube / coupler /
centering ring / bulkhead / engine block / tube fins / launch lug; finish on launch lug;
transition auto fore/aft radius (blank = auto); manual outer/inner radius on coupler /
rings / bulkhead / engine block; position method `absolute`.
