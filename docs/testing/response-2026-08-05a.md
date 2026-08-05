# Response — issues-2026-08-05a.md (→ v0.033)

Item-by-item status. **16 of 19 items are fixed/built in v0.033**; the rest
have analysis + options below and wait on your call. Numbering follows your
list order.

---

## 1. Coupler shadow invisible in the tube to the right — **FIXED**

Not a clipping bug — paint order. The 2D view draws nose→tail, and each tube
is opaque: a coupler's forward overhang painted *on top of* the already-drawn
tube to the left, but the tube to the right was drawn *later* and covered the
aft overhang. All dashed "shadow" shapes (inner components AND nose/transition
shoulders, which had the identical asymmetry) now paint in a second pass over
the whole hull. Your e-bay coupler under a switch band shows in all three
tubes.

## 2. Motor delay "none"/"plugged" — **BUILT**

- The motor browser's Delay dropdown now offers **"Plugged — no ejection
  charge" on every motor** (your use case: pulling the charge for electronic
  deploy), plus the factory "P" option for the 623 motors whose delay lists
  carry it (those were silently dropped before — a "P"-only motor even showed
  a bogus 0 s delay).
- The mount's delay editor gets a **"plugged" checkbox**; un-checking restores
  the longest prescribed delay. Labels use the standard **-P** suffix.
- The launch report still computes and shows the **optimal delay** on plugged
  flights (it comes from a deployment-free ballistic probe, so it's exactly
  the number you'd drill to if you fly that motor with eject another day) and
  adds a comment saying so, plus a reminder that recovery must fire on
  apogee/altitude electronics.
- Also fixed while in there: plugged runs were being corrupted in saved
  history (JSON has no Infinity — they reloaded as blank delays), and a
  plugged motor's delay field rendered the literal text "Infinity".

## 3. Recovery weight on the results page — **FIXED**; customizable metrics — **discuss**

"Recovery weight (at burnout)" is now a row in the launch report details
(it was computed and in the CSV all along, just never displayed).

On a user-customizable results page: I'd hold off. The "Show all details"
report is already the full attribute list, and a show/hide-per-row dialog adds
a preferences surface we'd have to migrate forever. If you want customization,
my recommendation is a lighter step first: **pin/star individual rows** so
starred rows float to the top tiles — one boolean per label in prefs, no
layout engine. Say the word and I'll build that; a full metric-picker dialog
is also doable (~1 session) if you'd rather go straight there.

## 4 (+6). Overstability flagged too aggressively / design-vs-results contradiction — **PROVISIONALLY CHANGED, needs your thresholds**

You were right, and it was worse than inconsistent: the design page and vitals
strip used a bare "≥ 1 cal = green ✓" with **no upper bound at all**, while
the launch report enforced a 1.0–3.0 band and painted over-stable **red** with
the same severity as under-stable. (The user guide even claimed the design
page enforced the band. It didn't.)

v0.033 provisional scheme, applied identically on the design page, vitals
strip, launch report and saved-runs table:

| Margin | Flag | Meaning |
|---|---|---|
| < 1.0 cal | red ⚠ | under-stable — genuinely dangerous |
| 1.0 – 3.0 cal | green ✓ | the classic healthy band |
| > 3.0 cal | yellow △ "over-stable (caution)" | mostly a weathercocking-in-wind concern |

A △-only run no longer counts as "unsafe" in the saved-sims Safe column.

**Your call (tell me and it's a 5-minute change):**
- Where the yellow starts (3.0 is the classic textbook edge; many HPR fliers
  are comfortable to 4–5, and long/heavy minimum-diameter birds routinely fly
  higher).
- Whether there should be a red ceiling at all (e.g. red only above ~6 cal,
  or never red on the high side).
- Whether the yellow threshold should scale with wind speed (over-stability
  is only a problem in wind — we could flag at 3 cal with >10 mph average and
  5 cal in calm). Slightly more work, honest physics.

## 5 (+7). Saved sims not tagged to a rocket — **FIXED**

Every saved run always stored the rocket name — it was only surfaced as a
hover tooltip. There's now a **Rocket column** in the saved-simulations table
and the launch-report title reads "Launch report — {rocket} · {motor}". Runs
from old sessions show their stored name too (the field existed all along),
so your existing history disambiguates retroactively.

## 8. CP, CG, lengths, diameters to 3 decimals — **FIXED**

The readout formatter capped values ≥ 10 at one decimal (so inch readouts
lost precision). CP, CG, length and diameter readouts (design tiles, vitals,
component stats line, launch-report CG/CP, override placeholders) now show up
to 3 decimals.

## 9. "Rogers Modified Barrowman" toggle vs "Extended Barrowman" header — **PARTLY FIXED, tagline wording is yours**

What the settings actually do, plainly:

- **Default = Classic Extended Barrowman** (exact desktop OpenRocket parity),
  Kbf **off**. Nothing changed there.
- **Rogers Kbf** is a *refinement of the classic model* (the NACA-1307
  body-lift-carryover term classic Barrowman drops). Off by default because
  default = desktop parity.
- The **supersonic model contains the full 1307 interference already**, so
  when it's active the Kbf checkbox genuinely did nothing — but the UI left
  it live-looking. That was the confusion.

v0.033: the model dropdown now comes first in Preferences (it's the primary
choice, and "Classic" is labeled as the default); the Kbf checkbox is **greyed
out with an explanation** whenever the supersonic model supersedes it; each
saved run now records whether Kbf was on ("classic+kbf" in the CSV, "+ Rogers
Kbf" in the report); and the header tagline now **names the model actually
running** instead of always saying "the real OpenRocket physics engine
(Extended Barrowman)".

**Tagline/identity — your decision.** I agree we've outgrown "a fork with the
real OpenRocket engine": the kernel core is still bit-verified OpenRocket, but
the supersonic model, validation harness, and safety layer are ours. Candidate
framings, pick/edit one and I'll ship it:
1. "OpenRocket's proven physics core, extended with a validated supersonic
   model — in your browser." (accurate, keeps the lineage)
2. "Design, simulate, fly — OpenRocket-derived physics, validated to Mach 4.6
   against NASA wind-tunnel data." (leads with our validation)
3. Rename territory ("Mountain Man Rockets Flight Sim" etc.) — bigger
   conversation, also touches GPL attribution text, the guide, and the
   WordPress pages. Happy to discuss.

## 10. Batch CSV ignores unit preferences — **FIXED (with one deliberate exception)**

Both CSV downloads (saved sims + batch) shared one unit-blind generator. The
detail columns now convert to your unit preferences, with the unit named in
each header. **Deliberately unchanged:** the first 14 columns are the fixed
flight-day comparison set you specified back in v0.005 (ft / mph / Gs / g) —
they don't follow preferences on purpose so field CSVs always compare
like-for-like. If you'd rather they follow preferences too, one line each.

## 11. RockSim exports ship default 10 g mass components — **FIXED**

The exporter passed the `mass` parameter straight into `<KnownMass>`,
which short-circuited the override check — anything you set via **Overrides →
Mass** was ignored in the export (the sim itself always used the override, so
only exported files were wrong). Now the override, when present, is exported
as the real mass. Regression-tested.

One remaining RockSim quirk, deliberately not half-fixed: for components where
you override only the MASS (not CG), RockSim couples both under one flag and
we currently write CG 0 with the flag on — importing such a file into real
RockSim pins that component's CG to its front edge. Fixing it properly needs
the computed CG threaded into the exporter. Tell me if your workflow hits it.

## 12. RASAero export crash — **FIXED (root-caused from your error file)**

Your RASAero_Error.txt stack (`NullReferenceException at GetSimulations`)
pointed at our `<Simulation>` block. RASAero's loader reads **24 fields
unconditionally** — real RASAero files always contain all of them, even for a
motorless single-stage design — and we wrote only 5. We also wrote
`<SustainerEngine></SustainerEngine>` *empty*, which crashes its motor-list
lookup a second way (RASAero omits the element entirely when there's no
motor). Both fixed, plus two parser-rigidity cleanups found by diffing against
RASAero's own files (a stray `<PartType>` inside `<Fin>`; recovery fields
reordered to RASAero's grouped order). Please re-test with a real RASAero II
install — I've matched their file structure byte-for-byte where it matters,
but their parser is closed source and your install is the only oracle.

## 13. Aft end view / cluster visualization — **BUILT**

- Design tab now has a third view: **2D | 3D | Aft** — the rocket from
  behind: body circles, motor tubes at their true cluster positions (with
  loaded motors drawn at real case size), fin count and span, tube-fin rings,
  pods at their ring positions, launch lugs. Hover any shape for its name.
- The **Motors & Launch tab shows the aft view automatically** beside the
  schematic whenever the design has a cluster or pods — change layout,
  rotation or spacing in the motor dialog and watch it move.

## 14. Descent rate vs Fruity Chutes calculator — **ROOT-CAUSED & FIXED**

The physics was right and the preset data was right — and one missing line
threw the data away. The IFC-144-S preset carries Cd 2.2 (the Iris Ultra
rating) and the true 144 in diameter, but applying a preset **never copied the
drag coefficient**, so the kernel fell back to the generic Cd 0.8. Redoing
your case with Cd 2.2: **13.65 ft/s vs Fruity's 13.86** — agreement within
1.5% (our sim also carries the airframe's own drag, which their calculator
ignores; that's the remaining sliver).

Scope: 230 of 459 parachute presets carry a rated Cd, all previously
discarded — every Iris Ultra preset simulated 1.66× too fast. Fixed for
parachutes and streamers; the custom-preset CSV now round-trips Cd; the Cd
slider now reaches 3.0 (real toroidals exceed the old cap of 2.0). The guide
now says plainly: our descent number is an estimate — always cross-check the
manufacturer's sizing guidance. Existing designs where you applied a chute
preset before v0.033 keep their old Cd-less state — re-apply the preset (or
type the Cd) to pick up the rated value.

## 15. Notification box doesn't clear on new design — **FIXED**

"Start new" now clears the tan banner (and any stale simulation-error line).
It was only ever cleared by its × button.

## 16. RockSim cluster import loses the cluster — **FIXED**

RockSim has no cluster concept — files carry N separate tubes at radial
positions, and our importer dropped the radial data entirely. Import now
reads `RadialLoc`/`RadialAngle`, groups identical sibling motor tubes, fits
their positions against the kernel's cluster patterns (recovering **layout,
spacing multiplier and rotation**), and re-imports them as one tagged cluster
— motors and all. Layouts that fit no known pattern stay as separate tubes
with an import note (our schema can't hold a lone off-axis tube).
Round-trip regression tests added (including a rotated, 1.25×-spaced 4-ring).

## 17. RockSim tube fins "don't work at all" — **FIXED (they were invisible)**

Tube fins imported and simulated correctly the whole time — they were simply
**never drawn** in either view, so the import looked dead. They now render in
2D (silhouettes on the body), 3D (the actual ring of open tubes), and the aft
view. Also fixed while in there: wall thickness now imports from the RockSim
OD/ID (it silently defaulted before), thickness is editable in the properties
panel, and our exporter now writes the ID field back.

## 18. Camera shrouds — **discuss (I have a concrete proposal)**

Good feature, and you're right that it's geometrically simple. Two honest
notes first:

- **A shroud is not quite a fin.** A fin is a thin lifting surface; CP shifts
  from its *lift*. A camera shroud is a blunt protuberance: it adds mostly
  *drag* (big) and some lift (small, position-dependent). Modeling it AS a
  freeform fin would overstate the CP shift and understate drag.
- The kernel has no protuberance component, so anything we do is our own
  model (fine — that's what the validation harness is for).

**Proposal** (roughly one session): a new `externalpod`-style "Fairing /
shroud" component — length, width, height, position, a shape factor
(streamlined / half-cylinder / box), and an optional mass. Physics: drag from
classic protuberance CD data (Hoerner) referenced to frontal area +
interference factor; lift/CP as a low-aspect-ratio body strake (a fraction of
a same-planform fin — defensible and conservative). It would render in 2D/3D/
aft views and export as a mass+drag annotation in .ork (desktop would warn
and ignore, same as our airfoil tags). If you want it simpler still, phase 1
could be drag + mass only (no CP effect) with a note — that's the safe
direction (CP slightly forward of predicted = more stable than shown... no,
wait: ignoring shroud lift forward of CP would make the real rocket *less*
stable than shown if the shroud is far forward. So phase 1 would clamp to
"shrouds behind the CP only" or just include the strake term from day one —
my recommendation).

Where on the body it mounts matters a lot; tell me your typical mounting
position (forward of the fins on the booster? on the payload bay?) and I'll
calibrate the defaults to that.

## 19. Copy/paste into a different parent — **BUILT**

The tree's action buttons now include **⎘ copy** and **✂ cut** on every
non-stage component. With something on the clipboard, **"Paste into …"**
buttons appear for each legal destination (same containment rules as the add
menus — you can't paste a fin set into a parachute). Paste clones with fresh
ids, so "build one centering ring, paste it five times" works. The clipboard
holds the component as copied — deleting the original later doesn't affect
pending pastes. Duplicate (⧉) stays for the quick same-parent case.

## 20. Undo — **it exists; now it's visible everywhere**

Ctrl+Z (and ⌘Z) has worked globally since v0.013 — 50 steps, rapid edits
coalesced into one step — and there's been an "↩ Undo" button on the Design
tab's Components panel. The problem was discoverability: nothing on the other
tabs advertised it. The button now lives in the **header**, visible on every
tab. (No redo yet — say the word if you want it; it's a straightforward
extension of the same history stack.)

## 21. Inner components all look identical — **BASIC VERSION SHIPPED + options**

Shipped in v0.033: each inner-component type gets its **own outline color**
plus a **small text tag** when the box is large enough (chute / strmr / cord /
mass / CR / BH / EB). Couplers and motor tubes stay neutral grey — they
really are tube segments, and the motor silhouette already marks mounts.
Hovering any dashed box names it.

If you want more, in increasing order of effort — tell me which (if any):
1. **Miniature glyphs** drawn inside the box (canopy arc for a chute, coiled
   line for a shock cord, weight block for mass, ring cross-section with a
   hole for a centering ring). My pick — reads at small sizes without labels.
2. **Fill patterns** (SVG hatches: diagonal for rings, stipple for mass,
   crosshatch for bulkheads) — the traditional engineering-drawing answer,
   monochrome-safe, slightly busier on screen.
3. **A legend chip row** under the schematic mapping color → type.
4. Component-type icons from the tree (▣ ◌ ● ☂ ◆) floated over each box —
   cheapest, but they're abstract symbols, not pictures.

---

## Not in your list but done alongside (same files)

- Plugged motors imported from .ork that matched a built-in motor silently
  lost their plug (flew with the built-in's delay) — fixed.
- Batch simulation can no longer accidentally fly a motor "plugged" when P is
  its last listed delay (it compares motors at their longest *prescribed*
  delay, then the optimum).
- The saved-runs "Safe" flag and the report now treat a missing/None verdict
  consistently for plugged flights.

## Test state

App 138 + engine 23 = **161 tests green** (7 new regression tests this
batch). No kernel/engine changes — differential untouched at 258 lines.
v0.033 zip: `deploy/online-openrocket-v0.033.zip`.

## Waiting on you

1. **Stability thresholds** (#4): where yellow starts / whether red exists on
   the high side / wind-aware or not.
2. **Tagline wording** (#9): pick or edit one of the three framings.
3. **Results-page customization** (#3): pin-rows now, full picker later, or
   leave as is.
4. **Camera shrouds** (#18): green-light the fairing component? Typical
   mounting position?
5. **Inner-component visuals** (#21): is the color+tag version enough, or
   pick an upgrade (my vote: miniature glyphs).
