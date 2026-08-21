# Three band findings for chrome.ref.js — handoff from Online OpenRocket v0.045

Paste-ready context for a session working in the repo that owns **chrome.ref.js** (the
reference implementation of the shared site band) and **docs/chrome-spec.md** (its spec).
You don't need to have seen the Online OpenRocket repo; everything relevant is below.

Background: the band's no-wrap fix — `flex-wrap: nowrap` plus the `overflow-x: auto`
scroll treatment promoted from the phone media query to base selectors — shipped in
Online OpenRocket v0.045 (2026-08-12) and works. While applying it, three defects were
found **in the reference block itself**. They were deliberately NOT fixed in that one
tool, because a local fix would make its band differ from the other three tools and the
whole point of the reference is that they agree. All three need a ruling here, a change
to chrome.ref.js, and then re-translation by every tool that vendors the block.

## Finding 1 — the focus-ring gutter is vertical-only, so rings still clip horizontally

**Wrong:** the gutter pair on the nav scroll container is `margin: -4px 0; padding: 4px 0`.
A scroll container clips its content at the padding edge on **all four sides**
(`overflow-x: auto` computes `overflow-y: auto` too), and the horizontal padding is
zero — so Chrome's 3px keyboard focus ring is still shaved on the *first* link's left
edge and the *last* link's right edge. That is the exact defect the pair exists to fix;
it currently fixes only the top and bottom.

**Change to chrome.ref.js:** on the nav scroll container, replace
`margin: -4px 0; padding: 4px 0;` with `margin: -4px; padding: 4px;` (all four sides).
This preserves the gaps and the 29/34px band heights — the pair stays height- and
width-neutral because the negative margin cancels the padding.

## Finding 2 — hiding the scrollbar removes the only overflow affordance at 768–924px

**Wrong:** the scrollbar is hidden in all three engines. Below 768px, touch swipe covers
it; at 768–924px desktop widths **nothing** does.

**Measured evidence (800px viewport):** the eight nav labels need ~700px and get ~527px,
so "Online Tools" and "Contact" are silently gone — no scrollbar, no fade, no hint that
the nav scrolls. Before the no-wrap fix they wrapped to a second row: ugly, but visible.

**Change proposed — pick one and record it in chrome-spec.md:**

1. A right-edge fade on the nav that only paints when the nav actually overflows, or
2. allow the compact magnifier at these desktop widths — spec §3 already permits forcing
   it at all widths, and it frees ~130px (the full search input's width).

**Do not fix it by raising the 767px breakpoint.** ~924px is a *measurement* of the
current eight labels at the current font size; a ninth label or a rename moves it. (That
ruling is already recorded at the rule inside Online OpenRocket.)

## Finding 3 — `gap: .6em` in the phone block resolves differently per tool

**Wrong:** it is the only `em` in an otherwise all-px block, so the phone gap scales
with each tool's band font-size.

**Measured evidence:** against Online OpenRocket's 11.5px band font, `.6em` computes to
6.9px, replacing the previous 12px — a ~40% phone-gap tightening that arrived inside a
*desktop* overflow fix. It happens to measure fine there, but if the four tools have
different band font sizes, one "shared" rule yields four different gaps.

**Change to chrome.ref.js:** state the phone gap in px. Decide the intended value first —
either restore 12px or bless the tighter spacing at an explicit px — and write that
number, so every tool computes the same gap regardless of its font size.

## After chrome.ref.js updates: what every downstream tool must do

**Re-translate the reference block into the tool's own markup — never paste it
verbatim.** Spec §5 leaves the band's markup to each tool on purpose, and two known
markup differences turn pasted reference rules into silent no-ops:

- **React tools render bare `<a>` children inside the `<nav>` — no `<ul>`/`<li>`.**
  Any `.mmr-band-nav li` rule must become `.mmr-band-nav a` there. This one is
  load-bearing, not cosmetic: `white-space: nowrap` only forbids a line break, it does
  nothing to stop flex *shrinking*, so `flex: none` must actually land on the anchors.
- **`.mmr-band-search` may BE the `<form>`, not a wrapper around one.** In that markup
  the reference's `.mmr-band-search form` descendant selector matches nothing, and the
  130px search input survives to phone widths. The rule must target `.mmr-band-search`
  itself there.

Two smaller conventions that made the v0.045 translation auditable, worth asking of
every tool:

- Each translated rule carries a comment naming the reference selector it corresponds
  to, so diffing a tool's CSS against the reference block explains every difference.
- Rules whose computed value the tool already has (e.g. `flex: 0 0 auto` where the
  reference says `flex: none`) are skipped, with the equivalence noted.

Finally: record the change wherever chrome-spec.md tracks revisions, so the tools that
vendor the block know to resync — finding 1 and finding 3 are one-line retranslations,
finding 2 depends on which option is chosen (a fade may need an `::after` or wrapper
element, and the spec should say which, since markup differs per tool).

## A fourth item, spec-level: bless the GitHub `target="_blank"` exception to MUST 6

Not a defect — a request to make the spec match a settled cross-tool ruling. MUST 6
requires `target="_top"` on band/footer links so an embedded tool escapes its frame.
The feedback ruling (12 Aug 2026, feedback-tracker adjudication) is that **github.com
links open a new tab** (`target="_blank" rel="noopener"`): sending a user's tab to
GitHub navigates them off a half-finished rocket design, and `_blank` escapes an
embedding frame exactly as completely as `_top` does — the spec's purpose is satisfied
while its letter bends. Online OpenRocket already implements this (github.com and
`*.github.com` only; every other footer link keeps `_top`).

Ask of the spec: add the explicit exception to MUST 6 ("external-tracker links MAY use
`target="_blank" rel="noopener"`; everything else MUST use `_top`"), so the other three
tools adopt the same behavior deliberately instead of each rediscovering the conflict.
