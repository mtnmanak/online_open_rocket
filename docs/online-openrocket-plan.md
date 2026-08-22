# Online OpenRocket — Technical Plan & Architecture Design

> **Goal:** Re-create the OpenRocket desktop application (a GPLv2+ Java/Swing model-rocketry
> design and 6-DOF flight-simulation tool) as a browser-based app that can run **standalone**
> *and* be **embedded as a page in an existing WordPress site**, delivered **MVP-first** and
> expanded toward feature parity.
>
> Sources: OpenRocket [Technical Documentation (techdoc.pdf)](https://openrocket.sourceforge.net/techdoc.pdf),
> [dev docs / codebase walkthrough](https://openrocket.readthedocs.io/en/latest/dev_guide/codebase_walkthrough.html),
> [GitHub repo](https://github.com/openrocket/openrocket), plus web/toolchain/licensing research (cited inline).
> This document was produced from a deep-research pass; **it is research-grade, not legal advice** — see the
> licensing caveat at the end.

---

## Status — PUBLIC BETA, live since 22 August 2026

This document is the original research-grade plan and is kept as written (it predates the
2026-08 rename, so it still says "Online OpenRocket" throughout — that is history, not a
to-do). Read it for the *why*; read this section for *where things actually stand*.

**Phases 0–3 are delivered.** The engine track was decided in Phase 0 (TeaVM-compiled
`info.openrocket.core`, differentially tested against the JVM) and everything through
Phase 3 has shipped: staging, clustering and pods; the bundled motor database; `.ork`,
`.rkt` and `.CDX1` both ways; OBJ/STL/glTF/DXF/SVG exports with oversized-part splitting;
the PWA; and a second, supersonic aerodynamics model validated against NASA wind-tunnel
data to ~Mach 4.6. Design optimization is the one Phase 3 item deliberately still open.

**The beta invite went out 22 August 2026** on The Rocketry Forum:
<https://www.rocketryforum.com/threads/mmrocket-sim-browser-based-rocket-simulator.198622/>
That thread and the central tracker (`docs/feedback-tracker.md`) are now the two inbound
feedback channels. Live at <https://mmrsim.mountainmanrockets.com>.

### Phase 4 — Public beta (current)

The beta is not just a bug hunt; it exists to answer questions that cannot be answered
from this side of the screen.

**The decision the beta is FOR:**
- **Which aerodynamics model should be the default?** Rogers Modified Barrowman (today's
  default) or the supersonic model. The invite asks testers directly: fly a rocket you
  have really flown on both, report which matched reality and the design's peak Mach.
  This is what unblocks "supersonic default-ON", which was previously gated on the ARCAS
  anchor alone. **Do not decide it from the validation harness alone — that was the point
  of asking.**

**Also being learned from testers:**
- Whether "open with no motors loaded" should become the default (one-line flip; the
  reasoning is in `docs/testing/response-2026-08-21d.md`).
- Whether Auto — a model that switches itself mid-flight — reads as reassuring or
  unsettling.
- Where predicted apogee/stability diverge from real flights, which is worth more than
  any synthetic test case.

**Queued, ordered by what unblocks what:**
1. **Next engine rebuild** carries the items that need `ComponentFactory` changes:
   multi-config **Stage C** (per-configuration deployment/separation/stage activeness),
   the geodetic selector, recovery `<packedlength>`/`<packedradius>`, and fin-fillet MASS.
   The `.ork` reader already PRESERVES the last two and says so in an import note — the
   file is safe, the simulation just does not count them yet. Landing the reader/writer
   half alone would make the file disagree with the stability number on screen.
2. **Design optimization** — the last Phase 3 item, and still the prime candidate to
   offload to a server-side API if it is too heavy for the client.
3. **Performance**: the simulation runs on the main thread (287–521 ms measured, no
   worker), and the 3D view repaints every frame while open — a battery cost at the pad.
4. **S4 stage-2 bottom sheets and S5 polish**, both explicitly deferred to beta feedback.
5. **Accessibility remainder**: MotorBrowser's sortable column headers claim
   `role="button"` without being focusable.

**Standing rule for the beta:** an issue list from testing may be waiting in
`docs/testing/` — fix that before new feature work.

### Parked for discussion — loading designs from public design repositories

Eric's idea (2026-08-22), **not decided, not scheduled** — worth a conversation
before any work. The pitch: let a user pull a design straight from the public
places rocketeers already share them, instead of downloading a file and then
opening it.

Why it is attractive here specifically: this app opens in a browser with no
install, so "see this design, open this design" could be one click — something the
desktop cannot really offer. It also compounds with share links, which already
encode a whole design in a URL.

**Start with the cheap 90 %:** "Open from URL" — paste a link to a `.ork`/`.rkt`/
`.CDX1` and we fetch and import it. That needs no partnership, no index and no
curation, and the importers already exist. Most of the value, almost none of the
cost. A repository *browser* is a much bigger thing and should only follow if
people actually ask for it.

**The questions to settle before building anything:**
- **CORS is the gating technical fact.** A browser can only fetch a file another
  site chooses to allow. If the sources do not send permissive headers we need a
  proxy — and that would be **this project's first server dependency**, against a
  design that is deliberately 100 % client-side and works offline at the pad.
  Check real sources with a `HEAD` before assuming either way.
- **Licensing and permission.** Someone else's design file is someone else's work.
  Hotlinking with attribution is a different proposition from mirroring an index,
  and "it was on a public forum" is not a licence. Worth being conservative — the
  project's own GPL posture only makes carelessness here more conspicuous.
- **Trust boundary.** Arbitrary files from the internet, parsed by our importers.
  The pre-beta audit found three separate importer inputs that imported cleanly and
  then failed to build; a paste-a-URL feature turns a local file chooser into an
  open input. Fuzz the importers before, not after.
- **Which sources actually carry designs**, and in what formats — verify rather
  than assume. thrustcurve.org is motors, not designs, and is already integrated.
- **Does it earn its place?** The beta will say whether people want this or whether
  their friction is elsewhere. Let feedback decide the priority.

---

## 1. What OpenRocket actually is (what must be re-created)

### 1.1 Module split — the single most important architectural fact
OpenRocket is already cleanly separated into two Gradle modules under the Java Platform Module System:

| Module | Contents | Web relevance |
|---|---|---|
| **`info.openrocket.core`** | Simulation engine, aerodynamics (Barrowman), `RocketComponent` hierarchy, atmosphere/gravity/wind models, motor database, `.ork` document model | **This is the only part worth reusing.** GUI-free physics + models. |
| **`info.openrocket.swing`** | Swing UI, JOGL 3D view, JFreeChart plots, FlatLaf theme | **Must be fully rewritten for the web** — do not try to preserve it. |

**Consequence:** every strategy below reuses or reproduces `core` and throws away `swing`.

### 1.2 The simulation engine (the hard part)
- **6-DOF flight simulator** built around `BasicEventSimulationEngine` with pluggable
  `SimulationStepper` implementations (e.g. `RK4Simulator`) and a replaceable aerodynamic
  calculator (`BarrowmanCalculator`) — modular by design, which helps a port.
- **Integration:** fourth-order **Runge–Kutta (RK4)**, total error ~O(Δt⁴), with an **adaptive time
  step** that shrinks when angular velocity/acceleration exceed limits.
- **Per-step loop:** compute atmospheric/wind conditions → flight parameters → aerodynamic forces
  & moments → motor thrust + gravity → mass & moments of inertia → integrate linear + rotational
  acceleration. **Orientation is tracked with a rotation quaternion**, not Euler angles.
- **Aerodynamics:** **Extended Barrowman method** for center of pressure, normal force, and drag —
  extended well beyond Barrowman's original trapezoidal 3–4 fin cases. Aero can be **overridden with
  CSV lookup tables** (wind-tunnel/CFD), linearly interpolated in both Mach and angle-of-attack.
- **Atmosphere:** International Standard Atmosphere (ISA) — sea level +15 °C, −6.5 °C/km lapse rate,
  101325 Pa — with a configurable base at sea level or launch-site altitude.
- **Wind:** `PinkNoiseWindModel` and `MultiLevelWindModel`.
- **Events:** motor ignition, stage separation, recovery deployment. **After recovery deployment the
  sim switches from 6-DOF to a simpler 3-DOF (position-only) model**; parachute Cd defaults to 0.8.
- **Launch conditions:** latitude, longitude, altitude, wind, atmosphere; selectable Earth-shape /
  geodetic model (flat / spherical / WGS84).
- **Invariant to preserve:** the engine works in **pure SI internally (m, kg)** and **radians
  internally / degrees in the file format**. Any re-implementation and `.ork` parser must honor this.

### 1.3 Data & file formats
- Native **`.ork`** design file (a zip of XML). Exports: RockSim, RASAero II, **OBJ** (3D printing),
  **SVG** (laser cutting), **CSV** (simulation data).
- **Motor / thrust-curve database:** dedicated `core` package handling motor files (`.rse`/`.eng`)
  **plus thrustcurve.org API integration**.

### 1.4 Feature surface (for the parity target)
Staging, clustering, pods; fin geometries (trapezoidal, elliptical, free-form, tube, canted);
real-time CG/CP readout and zoomable schematic; **automatic design optimization** (e.g. maximize
altitude or velocity); simulation extensions/listeners.

### 1.5 Visualization & build
JOGL for 3D, JFreeChart for 2D, Swing themed with FlatLaf. Built with Gradle, shipped as a fat JAR,
native installers, and a Linux Snap.

---

## 2. Three implementation strategies — comparison & recommendation

### Strategy (a) — Run/compile Java in the browser
Two very different sub-approaches:

- **CheerpJ 4.0 (full JVM → WASM).** Runs **unmodified JARs**, including Swing/AWT, rendering the UI
  to an HTML5 canvas; free for FOSS/personal/solo use; deploys to a **static host only**.
  **But** it's heavy: ~26 MB+ download even for a trivial app, Lighthouse 27/100, 7.7 s first
  contentful paint — the worst of all tools benchmarked — and heavy apps are only "satisfactory."
  Java-version support lags (4.3 = Java 8/11/17; 21 later). *Great as a throwaway spike/oracle, poor
  as the actual product; and it keeps the Swing UI you want to replace.*
- **Transpilers (TeaVM / GWT / JSweet / J2CL).** Compile Java to small JS/WASM. **TeaVM** is the
  standout: ~200 KB output, Lighthouse 100, 1.3 s FCP, has JS **and** WASM-GC targets, and has already
  compiled a Java physics engine (jbox2d) to run in-browser. GWT ~720 KB/99, JSweet ~65 KB/100.
  These transpile **logic only** — you still write a fresh UI. (bck2brwsr is not viable.)

**Licensing:** compiling/translating GPL code is legally a *modification*, so transpiled/WASM output
**stays GPL**, and shipping it to a browser is *distribution* → you must offer the corresponding source.

### Strategy (b) — Server-side Java engine + web front-end
Reuse the **unmodified `core` JAR** behind a REST/WebSocket API; build a new web UI.
- **Fidelity:** highest — it *is* the real engine.
- **Licensing:** the **GPL "SaaS gap"** applies — running GPL code as a network service is **not
  distribution**, so plain GPLv2+ imposes **no source-disclosure obligation** (that's AGPL, which
  OpenRocket does *not* use). A GPL front-end and a back-end communicate at arm's length (mere
  aggregate) and don't infect each other.
- **Cost/UX:** requires server compute and rules out true offline use. A forum thread flagged that a
  naïve *interactive-desktop-per-user* AWS deployment is impractical — but a **stateless sim API** is a
  different, tractable problem.

### Strategy (c) — Full rewrite of the engine in TypeScript
Reimplement Barrowman, ISA, wind, RK4 6-DOF, `.ork` parser, and motor handling in TS; runs fully
client-side, offline-capable, best web integration and UX, no Java toolchain.
- **Effort/risk:** highest effort and real risk of numerical drift from the reference.
- **Licensing:** a genuine clean-room rewrite that copies no GPL code is *not* derivative; but a
  practical *port* of the actual source is a modification → derivative → GPL.
- **Precedent that it works:** **RocketForge** is an existing no-install browser rocket sim doing
  6-DOF (altitude/velocity/stability) with **`.ork` import/export** and thrustcurve.org motors — proof
  the client-side approach is viable. (Notably it *doesn't yet do multi-stage* → staging is a hard,
  late feature.)

### Recommendation
**Primary: a client-side engine (portable, offline-capable, WordPress-embeddable), reusing the real
Java physics where feasible rather than blindly rewriting it.** Concretely, a two-track spike decides
the engine:

1. **Preferred — transpile `info.openrocket.core` with TeaVM** to a small JS/WASM engine bundle. This
   **reuses the validated physics** (low fidelity risk) while producing a lightweight client-side
   artifact (unlike CheerpJ). Accept that the bundle is GPL + must ship source — fine for an open
   project.
2. **Fallback — TS rewrite (strategy c)** if `core`'s dependencies/reflection don't transpile cleanly,
   using `techdoc.pdf` as the spec.

In **both** tracks, run the **real OpenRocket JAR under CheerpJ as a golden-reference oracle** to
validate numerical parity. Reserve **strategy (b) server-side** for genuinely heavy, non-interactive
work later (e.g. design optimization) — offloaded behind an API without affecting the client's
offline core. This directly satisfies "standalone + embeddable + MVP-first," and matches the
RocketForge precedent.

---

## 3. Front-end technology

| Concern | Recommendation | Why |
|---|---|---|
| **Framework** | **React + TypeScript** (Vite) | Pairs with the 3D and charting picks; strong embedding story. |
| **3D rocket view** | **Three.js via [react-three-fiber](https://github.com/pmndrs/react-three-fiber)** | Declarative, exposes the *full* Three.js API, no runtime overhead; mature `drei`/physics/postprocessing ecosystem. Pin versions: R3F v8→React 18, v9→React 19. |
| **2D plots** (altitude/velocity/accel vs time) | **[uPlot](https://github.com/leeoniya/uPlot)** | **MIT-licensed (GPL-compatible)**, ~50 KB, canvas-based, time-series-specialized; ~166 k points in ~25 ms; 10% CPU / 12 MB RAM at 3600 pts @ 60 fps → ideal for live-streaming sim data. |
| Plot alternative if GPU needed | SciChart.js / LightningChart | Fastest (WebGL/WebGPU) but **commercial**; only if uPlot proves insufficient. |

---

## 4. Hosting, integration & portability

- **Decouple the engine from the UI**: ship the engine as a standalone JS/WASM package, and the UI as
  an embeddable web component / iframe app. This is what makes "standalone *and* WordPress" cheap.
- **Standalone:** static host + CDN. Client-side approaches (TeaVM/rewrite, or even CheerpJ) need
  **only a static host** — no server compute.
- **WordPress embedding:** deliver the front-end bundle via a **shortcode/block that mounts the app**
  (or an `<iframe>` to the standalone build). Because front-end and any back-end are arm's-length,
  GPL'd client JS shipped to visitors does **not** infect WordPress or a server back-end.
- **Offline (important):** community objection — remote launch sites (e.g. Black Rock) often have no
  internet, so a web-*only* tool is a non-starter for real use. Make the client-side build a **PWA /
  local-first app** so it works offline; this is a strong argument for keeping the core client-side.

---

## 5. Licensing summary (GPLv3+)

> **Correction (verified against the local 24.12 source):** `LICENSE.TXT` in OpenRocket 24.12 is
> **GPL version 3 or later** — not v2+ as earlier research stated — with an additional §7 permission
> to bundle non-compilable data files (thrust curves, component databases). GPLv3 is compatible with
> Apache-2.0 dependencies, which simplifies front-end library choices.

OpenRocket is **GPLv3+** (strong copyleft). Practical implications:

- **Server-side reuse (b):** running the engine as a service is **not distribution** → **no source
  disclosure** required. Safest place to keep anything proprietary. (AGPL would change this; OpenRocket
  is *not* AGPL.)
- **Client-side (a WASM / c port):** shipping to a browser **is distribution** → must **offer
  corresponding source** under GPL; transpiled/compiled Java stays GPL.
- **Mixing licenses:** a GPL front-end and separate back-end are an *aggregate* — neither forces its
  license on the other. MIT deps (uPlot, Three.js) are fine, and since OpenRocket is GPLv3+,
  **Apache-2.0 dependencies are also compatible**.
- The GPL requires offering source to *recipients on request*, not proactively publishing it.

> ⚠️ **Not legal advice.** Several licensing points above come from secondary/blog sources and the FSF
> FAQ, not a lawyer. Confirm with qualified counsel before committing to a distribution model.

---

## 6. Phased delivery roadmap

> **As planned in 2026-06; Phases 0–3 are delivered and the app is in public beta.**
> Kept verbatim as the original plan of record — see [Status](#status--public-beta-live-since-22-august-2026)
> at the top for what actually shipped, what is still open, and Phase 4.

### Phase 0 — Foundations & engine decision (spikes) — DONE (TeaVM track chosen)
- Scaffold repo: Vite + React + TypeScript; engine as a separate package.
- **Spike A:** run the real OpenRocket JAR under **CheerpJ** → quick demo **and** numerical **oracle**.
- **Spike B:** attempt **TeaVM** compile of `info.openrocket.core`; assess dependency/reflection gaps.
- **Decide engine track** (TeaVM-reuse vs TS-rewrite). Lock the SI-units / radians invariants and a
  `.ork` (zip+XML) parse/serialize contract.

### Phase 1 — MVP — DONE
- Minimal component model (nose cone, body tube, trapezoidal fins, motor mount); **`.ork` import/export**.
- Barrowman **CP/CG + stability margin** and mass properties.
- **6-DOF RK4 flight sim**: single stage, ISA atmosphere, simple/no wind; events: ignition → burnout →
  apogee → recovery (3-DOF, Cd 0.8).
- Motor data via **thrustcurve.org**.
- **uPlot** plots: altitude / velocity / acceleration vs time; simple 2-D schematic with CP/CG markers.
- Deploy **standalone** and a **WordPress embed** (block/shortcode or iframe). Validate outputs against
  the CheerpJ oracle.

### Phase 2 — Design editor + 3D — DONE
- Full component editor with real-time CG/CP; more fin types (elliptical, free-form, tube, canted).
- **3-D rocket view** via react-three-fiber.
- Realistic wind (pink-noise / multi-level); launch conditions (lat/lon/alt, launch rod).

### Phase 3 — Toward parity — DONE except design optimization
- **Staging, clustering, pods** (hard — expect it here, per RocketForge).
- Bundled/searchable full motor database; recovery variants; geodetic/Earth-shape models.
- File **imports and exports** beyond CSV: RockSim (`.rkt`) and RASAero II design files
  **both ways** (the desktop reads them; so must we), plus export-only OBJ (3D print) and
  SVG (laser-cut fin templates).
- **Design optimization** — the prime candidate to **offload to a server-side (b) API** if too heavy
  for the client.
- Harden the **PWA/offline** experience for launch-site use.

### Cross-cutting throughout
- Validate every physics module against **golden outputs from the CheerpJ-run original**.
- Keep the **engine package decoupled from the UI** so both the standalone site and the WordPress
  embed consume the same artifact.

---

## Appendix — key sources
- OpenRocket internals: [techdoc.pdf](https://openrocket.sourceforge.net/techdoc.pdf) ·
  [codebase walkthrough](https://openrocket.readthedocs.io/en/latest/dev_guide/codebase_walkthrough.html) ·
  [advanced flight simulation](https://openrocket.readthedocs.io/en/latest/user_guide/advanced_flight_simulation.html) ·
  [GitHub](https://github.com/openrocket/openrocket)
- Java-to-web toolchains: [CheerpJ 4.0](https://labs.leaningtech.com/blog/cheerpj-4.0) ·
  [JVM-alternatives-to-JS benchmark](https://github.com/renatoathaydes/jvm-alternatives-to-js) ·
  [TeaVM jbox2d demo](https://www.teavm.org/gallery/jbox2d/index.html)
- Front-end: [react-three-fiber](https://github.com/pmndrs/react-three-fiber) ·
  [uPlot](https://github.com/leeoniya/uPlot) ·
  [JS charting benchmark](https://www.scichart.com/blog/chart-bench-compare-javascript-chart-libraries/)
- Licensing: [GNU GPL FAQ](https://www.gnu.org/licenses/gpl-faq.html) ·
  [GPL SaaS gap](https://www.revenera.com/blog/software-composition-analysis/understanding-the-saas-loophole-in-gpl/) ·
  [GPL & JavaScript](https://greendrake.info/publications/js-gpl)
- Precedent: [RocketForge](https://rocketforge.space/) ·
  [Rocketry Forum: "OpenRocket should be a web app"](https://www.rocketryforum.com/threads/openrocket-should-be-a-web-app.133060/)
