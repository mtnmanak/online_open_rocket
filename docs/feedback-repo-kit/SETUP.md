# Feedback-repo kit — setup steps

> ## ⛔ SUPERSEDED AS A PLAN — 11 Aug 2026. Do not execute these steps.
>
> Three sessions were independently designing this tracker and pasting conflicting
> setups into the same repo. Eric closed them all and adjudicated centrally.
>
> **The decision now lives in `E:\git\mountainmanrockets\docs\issue-tracking-consolidation.md`
> (rev. 3, marked ADJUDICATED). Read it before touching anything here.**
>
> **This kit's `.yml` and `README.md` files were ADOPTED** — they are the source of
> truth for the tracker's contents, and its label spellings won over the competing
> proposal. Nothing here was wasted. But the **steps below are not yours to run**:
>
> - The rename, the labels, the file placement and the site-side link updates are
>   being done once, centrally, from the mountainmanrockets repo.
> - Three edits were made to this kit during adoption: `config.yml` gets
>   `blank_issues_enabled: false`, `contact_links` gains the contact page and the
>   `rocket_locator` Issues tab, and **`type:bug` / `type:feature` are NOT created**
>   (they duplicate the stock `bug` / `enhancement` plus Eric's existing `design` /
>   `content`, which he ruled on 9 Aug).
> - Step 7 — wiring the in-app links — **is still yours**, and is unblocked: the
>   repo URL is `https://github.com/mtnmanak/mountainmanrockets-feedback`.
>   Do that, and the one README line for this repo. Nothing else.
>
> `online_open_rocket` also has **no `README.md` and no `LICENSE`**, while being a
> publicly deployed GPL-3.0-or-later derivative (`docs/deployment.md:105-110`).
> Both are noted in the adjudication as open items for this repo.

Ready-to-paste contents for the single public feedback tracker
(recommendation in `docs/testing/response-2026-08-11b.md` §7: ONE tracker for
the site AND all tools). Everything here works equally if you instead create
the separate `mountainmanrockets-tools-feedback` repo — only the dropdown's
"website itself" entry changes.

## Steps (10 minutes, all in the GitHub UI) — SUPERSEDED, see banner above

1. **Rename** `mountainmanrockets-site-feedback` → `mountainmanrockets-feedback`
   (repo Settings → General → Rename). GitHub redirects the old URL forever,
   so nothing that already links to it breaks.
2. **Replace `README.md`** with this kit's `README.md`.
3. **Add the issue forms**: create `.github/ISSUE_TEMPLATE/` in that repo and
   copy in `bug-report.yml`, `feature-request.yml`, `config.yml`.
4. **Add the auto-labeler**: copy `apply-tool-label.yml` to
   `.github/workflows/`. It reads the "Where did this happen?" dropdown of a
   new issue and applies the matching label — public filers can't set labels
   themselves, so the Action does it.
5. **Create the labels** (Issues → Labels):
   `tool:online-openrocket`, `tool:motor-dashboard`, `tool:motor-simulator`,
   `tool:bp-calculator`, `area:site`, `type:bug`, `type:feature`,
   `needs-triage`. (Colors don't matter; pick a shared hue for the tool: set.)
6. **Bookmark your per-tool queues** — saved filters, e.g.
   `https://github.com/mtnmanak/mountainmanrockets-feedback/issues?q=is%3Aopen+label%3Atool%3Aonline-openrocket`.
7. Tell me the final repo URL and I'll wire the in-app
   "🐞 Report a bug / request a feature" links (Guide dialog + header) with
   the template and title prefilled.

## Adding a future tool

Add a dropdown option in both YML forms, a `tool:<slug>` label, and one line
in the workflow's mapping. Nothing else.
