# Phase 0 — Findings

Status as of 2026-07-02. Complements `online-openrocket-plan.md`.

## Verified facts from the local Java source (openrocket-release-24.12)

- **Module split confirmed:** `core` (`info.openrocket.core`) and `swing`
  (`info.openrocket.swing`) Gradle modules; JPMS; `standardJavaRelease(17)`.
- **License:** GPL **v3 or later** (plan doc corrected — research had said v2+), with a
  §7 additional permission for bundling non-compilable data files.
- **Physics kernel is clean.** `aerodynamics` (13 files), `masscalc` (4), `models` (10),
  `motor` (10), `simulation` (68), `rocketcomponent` (73), `unit` (16), `util` (65):
  - **No threading** (no `Thread`/`ExecutorService` in kernel packages).
  - **No AWT** except 3 files importing only `java.awt.geom.{Point2D, Line2D, Rectangle2D}`
    (`FinSet`, `FreeformFinSet`, `BoundingBox`) — pure-math classes, trivially shimmable.
  - `java.util.zip` only in `document/attachments/ZipFileAttachment`.
  - Java serialization only in the thrustcurve `.ser` build tool.
  - `ResourceBundle`/resource loading only in `util/BuildProperties`.
- **Heavy deps are localized, not pervasive:**
  | Dependency | Where it's used | Kernel impact |
  |---|---|---|
  | Guice (DI) | `startup`, `formatting`, `plugin`, 6 `simulation/extension*` files | Excludable |
  | GraalVM JS / Truffle | `scripting`, simulation scripting extensions | Excludable |
  | JAXB runtime | `file` (RockSim/RASAero), `preset` | Not needed for `.ork` |
  | classgraph | `plugin` | Excludable |
- **`.ork` parsing** uses OpenRocket's own `simplesax` over `org.xml.sax` — no JAXB.
  (SAX isn't in TeaVM's classlib; the XML boundary needs a JS-side parser or shim either way.)
- **`Application` singleton coupling is narrow:** 49 kernel files, but ~95% is
  `getTranslator()` (61×) + `getPreferences()` (14×) → replace with a small static shim.

## Spike A — CheerpJ oracle

- `spikes/cheerpj/index.html` loads the unmodified `OpenRocket-24.12.jar` (83 MB, fetched,
  not committed) with CheerpJ **4.3** (`cheerpjInit({version: 17})` — 24.12 targets Java 17).
- Serve with `python -m http.server -d spikes/cheerpj 8321` and open in a browser.
- **Pending:** in-browser confirmation that the app boots (needs a human browser; first
  load is large/slow by design — this is the oracle, not the product).

## Spike B — TeaVM transpile of the physics kernel ✅ EXECUTED

**Static verdict:**
- Transpiling the **whole `core` module: infeasible** — Guice, GraalVM JS, classgraph,
  JAXB runtime, logback, ICU4J, ASM will not go through TeaVM.
- Transpiling a **carved physics kernel** (~260 files listed above): promising — no threads,
  no real AWT, entanglements localized. Needed shims: `java.awt.geom` (3 classes),
  `Application` (translator/preferences), slf4j.

**Dynamic verdict (spikes/teavm — real compile + run, portable JDK 17.0.19 in scratchpad):**
- Compiled an UNMODIFIED slice of the real kernel (`ExtendedISAModel`,
  `InterpolatingAtmosphericModel`, `AtmosphericConditions`, `Coordinate`, `Quaternion`,
  `MathUtil`, `ModID`, …) to JS **and** WASM-GC with the TeaVM Gradle plugin.
- **Result: JVM and TeaVM-JS outputs are bit-for-bit identical** (ISA @ 0 m / 5000 m,
  quaternion rotation): `rho=1.2249994633486807`, `P5k=54019.9035758014`, etc.
- Output size: ~500 KB unobfuscated JS (slice + Java runtime) / ~272 KB WASM-GC.

**Critical catch — TeaVM version matters:**
- **TeaVM 0.10.0 (the version benchmarks reference) has a real NaN-semantics bug**: its JS
  backend compiles `if (a > b)` as inverted `if (a <= b)` branches, which flips behavior for
  NaN. OpenRocket's own `calculatePressure()` computes a benign `0/0 = NaN` lapse rate at
  layer boundaries; under TeaVM 0.10 the ISA pressure at 0 m silently became **NaN**, and the
  same inversion made the *assertion checking it* silently pass. `strict = true` did not fix it.
- **TeaVM 0.15.0 (current) fixes it** — correct values, honest assertions.
- Lessons locked in: **pin TeaVM ≥ 0.15**, and build **JVM↔JS differential tests** into CI
  (same inputs, assert digit-identical outputs) since miscompiles here are silent.

**Practical gotchas hit:**
- Referencing the OpenRocket source tree in place drags in its `module-info.java` and breaks
  javac module resolution → copy the slice files instead.
- Real slf4j-api breaks under TeaVM (classloader machinery); the TeaVM plugin puts slf4j on
  the classpath itself, so a hand-rolled `org.slf4j` shim gets shadowed → use
  **`org.teavm:teavm-extras-slf4j`**.

## Engine-track decision (recommendation)

**Adopt the TeaVM-reuse track as primary**, per plan §2, now validated end-to-end:
1. Carve the physics kernel out of `info.openrocket.core` (copy step, unmodified sources),
   shim `java.awt.geom` + `Application` + slf4j, compile with TeaVM ≥ 0.15 to JS/WASM.
2. Wrap it behind `@online-openrocket/engine`'s TypeScript API.
3. **Differential-test against the JVM** (portable JDK; golden values generated headlessly —
   this also de-emphasizes CheerpJ as oracle: the JVM itself is a better automated oracle).
4. TS-rewrite remains the fallback if the carve hits a wall at `simulation`/`rocketcomponent`
   scale (risk: moderate; nothing found so far suggests it).

## P1.2 addendum — two more TeaVM landmines (both caught by differential testing)

1. **Wrong devirtualization (default optimization):** at FlightConfiguration's recursive
   tree walk, TeaVM inlined `RocketComponent.getInstanceCount()`'s `return 1` as a literal
   loop bound (`while (i < 1)`), ignoring `FinSet`'s override — fin instance contexts
   silently collapsed 3->1 and aggregate masses became 0. **Fix: `optimization = NONE`.**
2. **Dependency-analyzer under-linking:** virtual-method implementations reached only via
   map-key dispatch or the recursive walk were pruned from the output
   (`TypeError: $component.$getComponentBounds is not a function` at runtime — or worse,
   silently-wrong results when a base impl existed). **Fix: `fastGlobalAnalysis = true`**
   (class-hierarchy analysis links every override of every called method).

Also hit: TeaVM's immutable empty list throws on `clear()` where the JDK no-ops
(-> ComponentAssembly patch), TeaVM's UUID lacks `(long,long)`/`getMostSignificantBits`/
`compareTo` (-> LongUUID shim + 2 patches), no `ConcurrentLinkedQueue` (-> LinkedList patch),
no `java.awt.geom` (-> `--patch-module` stub sourceset). All recorded in
`engine-java/patches/LEDGER.md`.

**Config rule: never change `optimization`/`fastGlobalAnalysis` without a full
differential pass — these two defaults silently corrupt physics.**

## Open items

1. Human check of Spike A in a browser (http://localhost:8321 while the spike server runs) —
   nice-to-have demo; no longer the primary oracle (see above).
2. Scale the carve: next slices are `masscalc` → `aerodynamics/BarrowmanCalculator` →
   `simulation/BasicEventSimulationEngine` with differential tests at each step.
3. User sign-off on the engine-track recommendation.
