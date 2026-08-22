# MMRocket Sim — beta invitation

*Copy for emails, forum posts and club announcements. Version as of v0.050 (August 2026).
Cleared for the public forum post — Eric closed both open copy calls on 2026-08-21
(batch 08-21d): no non-affiliation clause added, and the pre-rename migration note is
dropped from the message ("move on from the old subdomain"). The old address 301s here,
so pre-rename testers are covered without a word of copy.*

**Link to send:** <https://mmrsim.mountainmanrockets.com>

---

## The message

MMRocket Sim is a browser-based rocket design and flight simulator, and it's now open
for beta testing at **https://mmrsim.mountainmanrockets.com**. It's built on the real
OpenRocket physics kernel — the same Java simulation code the desktop app runs, compiled to
run in your browser and checked against the original to make sure the numbers match — so
what you already know about designing and flying in OpenRocket carries straight over. The
rocketry software community has built a genuinely great set of tools over the years, and
this isn't an attempt to replace any of them; it started as a question about what becomes
possible when a simulator has no install step, runs on whatever's in your pocket at the
launch site, and can talk directly to the machines in your workshop. That led somewhere
interesting: designs that open from a link, a sim that keeps working when the cell signal
doesn't, exports that go straight to a 3D printer or a laser cutter, and a **second
aerodynamics model** built for the speeds where the classic method starts to drift — scored
against published NASA wind-tunnel data, with the scoring left out in the open.

That second model is the one thing I most want beta testers to weigh in on, because
**I haven't decided which model should be switched on by default** — there's a section
below asking you specifically to try both.

It's beta, so there will be rough edges — and honestly, finding them is the most useful
thing you can do right now. Bring a design you know well, fly it, and tell me where the
numbers or the workflow don't match your experience.

---

## Some things we've been trying

**Getting out of your way**

- **Free, open source, and no account — ever.** Nothing to sign up for, nothing to log
  in to: your designs live in your browser and in the `.ork` files you save, not on
  someone's server. The whole app is free software (GPL, full source public), it costs
  nothing, and it will stay that way.
- **Nothing to install, on anything.** It runs in a browser tab — Windows, Mac, Linux,
  iPad, phone. No Java runtime, no version to keep current.
- **Works with no internet.** The whole app — physics engine, motor database, component
  presets — caches on first visit, so it keeps working at a remote launch site with no
  signal. You can install it to your home screen like an app.
- **A daylight mode** built for reading a phone screen in direct sun at the pad, rather
  than for looking good indoors.
- **Your work saves itself** as you go, and survives closing the tab.
- **Send a design as a link.** "Copy share link" packs the whole rocket — geometry,
  motors, launch conditions — into the URL itself. No account, no upload, no server
  copy: the link *is* the file, and whoever clicks it gets the design open and ready
  to fly.

**Taking the design into the workshop**

- **3D-printable STL for individual components.** Pick a nose cone, transition, fin,
  centering ring, bulkhead, coupler or tube, and get a watertight solid ready to slice —
  hollow parts include their shoulders and end caps at your real wall thickness, fins come
  out with the through-the-wall tab already merged in, and centering rings take their bore
  from the motor mount you actually specified.
- **Parts too big for your printer get split for you.** Tell it your build volume once and
  an oversized nose cone comes back as numbered segments joined by a printed spigot, with a
  README covering orientation, clearance and glue-up. A 3" 4:1 nose cone is 381 mm printed —
  taller than most machines — and arrives as two pieces that each print support-free.
- **DXF export for CNC and laser cutting** of the flat parts people actually cut: fins,
  centering rings, bulkheads, couplers. One closed contour per part outline — a ring's
  bore comes through as a true circle — so CAM offsets it correctly in a single pass.
- **1:1 printable fin templates** with a calibration ruler, so you can confirm your printer
  didn't quietly rescale the page.
- **Drawings sized for a cert packet** — a 2D drawing at true 100% physical scale with a
  data header carrying length, diameter, span, mass, CG, CP and stability margin.
- **3D models with your colors** (.glb) that open in Blender, Fusion 360, PowerPoint or
  Windows 3D Viewer, plus image export up to 8K.

**Physics you can check — and a default I need your help choosing**

One pulldown in *Preferences → Aerodynamics* picks the model, and there are four choices:

- **OpenRocket — Extended Barrowman** — the desktop program's exact physics, bit-for-bit.
  Pick this when you want a number you can hand to someone running the desktop app.
- **Rogers Modified Barrowman (Kbf)** — adds the body-in-presence-of-fins lift carryover
  (NACA 1307) that classic Barrowman drops: a slightly more aft, more conservative CP that
  tracks real flight data better. **This is the current default.**
- **Our supersonic model** — the same kernel extended with corrected supersonic fin lift,
  the exact NACA 1307 interference, Mach-dependent nose lift, per-shape wave drag with
  physical hypersonic decay, and Van Driest II friction, so CP and drag move with Mach the
  way wind tunnels actually measure. Built from the open literature and scored against
  published NASA data (ARCAS, the Army–Navy Basic Finner) out to roughly **Mach 4.6**.
- **Auto** — flies Rogers Kbf, and re-flies the whole flight on the supersonic model only
  when it's projected past Mach 0.9.

One important thing: **a model applies to the entire flight**, subsonic portions included —
so expect stability and apogee to shift when you change it, not just the fast part.

Around the choice:

- **The scoring is published, including where it falls short.** An automated validation
  harness in the repository scores every physics change against those anchors, and the gaps
  it hasn't closed — transonic peak drag around Mach 1.5, blunt and flared bodies — are
  written down rather than smoothed over.
- **Every saved flight records which model produced it**, so you always know what you're
  comparing, and a flight that goes supersonic on the classic model says so in the report.
- **Deterministic runs** — the same design and conditions give the same answer every time.

**Workflow experiments**

- **Batch simulation:** fly every motor that fits the mount in one go, with your own
  acceptance criteria, and export the comparison. Clustered mounts can also fly mixed
  symmetric combinations (4+2, 2+2+2) — useful when buying six identical motors isn't the
  plan.
- **Clusters, pods and strap-on boosters** that separate and fly their own tracked descent.
- **A camera shroud component** that contributes both its drag and its shift in center of
  pressure, instead of being modeled as added mass.
- **Research and experimental motors** — import your own .eng/.rse files individually or
  point it at the folder where you keep them. They stay on your machine.
- **Sub-minimum diameter builds**, where the motor case is the airframe.
- **A component spreadsheet export** with the engine's computed mass, CG and position for
  every part, in your preferred units.

**Fitting into what you already use**

- **Opens and saves OpenRocket `.ork` files** natively, so designs move both directions.
- **Reads and writes RockSim `.rkt` and RASAero `.CDX1`**, so a design isn't trapped here.
- **The full thrustcurve.org motor database** and a component preset library of roughly
  4,700 parts.
- **Open source under the GPL**, inheriting from and crediting the OpenRocket project, whose
  work the whole thing rests on.

---

## What's most useful to test

Bring a rocket you've actually flown. Compare the predicted apogee and stability against what
it really did, and tell me where they disagree — that's worth more than any synthetic test
case. Beyond that: open your existing `.ork` or `.rkt` files and check nothing was lost, try
an export in whatever your workshop uses, and use it once on your phone at a launch.

**The one thing I'd most like data on: which aerodynamics model should be the default?**

This is a real open question, and I'd rather answer it with your flights than my opinion.
Rogers Kbf is the safe answer and it's what's on now. The supersonic model should be better
once a flight gets fast — but "should be" is exactly what I want checked. So:

1. Fly a design you've actually flown, on the default (**Rogers Modified Barrowman**), and
   note the apogee and stability margin.
2. Open *Preferences → Aerodynamics*, switch to **Our supersonic model**, and fly the same
   design again.
3. Tell me **which one landed closer to what the rocket really did** — and please include the
   design's **peak Mach**, because that's the number the answer probably turns on.

If you fly mostly low and mid power, that's still useful data — arguably the most useful.
If the two models agree down there, making the supersonic one the default costs nothing; if
they *don't*, that's precisely what I need to know before switching it on for everyone. Try
**Auto** as well if the idea appeals: part of what I'm trying to learn is whether a model
that switches itself mid-flight is reassuring or unsettling.

I'd rather hear "I couldn't tell the difference" than get no answer — that's a result too.

**Getting oriented:** the **Guide** button in the header has a your-first-flight-in-three-steps
quick start, the full feature reference, and the physics documentation — including exactly
which aerodynamic model flew each simulation.

**Found something, or want it to do something it doesn't?** There's a **Feedback** button in
the header — it files to a public tracker, and there's an email option if you'd rather not
use a GitHub account.

---

## A short version, if you just need a couple of lines

> I've been building a browser-based rocket design and flight sim on top of the real
> OpenRocket physics engine — nothing to install, works offline at the launch site, and it
> exports parts straight to a 3D printer or laser cutter (including splitting parts that are
> too big for your printer). It also has a second, supersonic aerodynamics model validated
> against NASA wind-tunnel data to about Mach 4.6, and I'm genuinely undecided about which
> model should be the default — so if you fly a design on both and tell me which one matched
> reality better, that's the single most useful thing you could do. It's in beta and I'd love
> a second set of eyes: https://mmrsim.mountainmanrockets.com

---

*Note for whoever maintains this file: `mmrsim.mountainmanrockets.com` (live August 2026,
with the v0.047 rename) is the only address to hand out. The previous address,
`openrocket.mountainmanrockets.com`, 301-redirects to the new subdomain (phase-2 redirect
fired 2026-08-21, after every pre-rename tester confirmed they had migrated). Every older
address (like the old `mountainmanrockets.com/online_open_rocket/` path) also redirects to
the new address above.*
