# Feedback and issue tracking — DONE, centralized, do not rebuild

**Status:** ✅ Implemented and live, 12 August 2026. There is nothing to set up.
**Tracker:** https://github.com/mtnmanak/mountainmanrockets-feedback
**This tool's label:** `tool:mmrocket-sim` (renamed from `tool:online-openrocket` with the
product, 2026-08 — GitHub renamed the label in place, so older issues keep it)
**This tool's queue:** https://github.com/mtnmanak/mountainmanrockets-feedback/issues?q=is%3Aopen+label%3Atool%3Ammrocket-sim

## Why this file exists

On 11 August 2026 Eric closed **every** working session at once, because three of them were
independently designing the same issue tracker and pasting conflicting setups into the same
repos. The adjudication that followed lives in the site repo at
`docs/issue-tracking-consolidation.md` and it is the single source of truth.

This file is that decision, delivered to the repo you are actually working in, so a session
here does not have to find it — or worse, re-derive it and get a different answer.

## DO NOT, in this repo

- ❌ **Do not create a feedback or issues repo.** Especially not
  `mountainmanrockets-tools-feedback` — that name is explicitly forbidden, and several
  older docs in this repo still recommend it. See *Stale docs* below.
- ❌ **Do not create labels.** The taxonomy is fixed and lives in the tracker.
- ❌ **Do not add issue templates, `config.yml`, or a labeling workflow here.** That work is
  central and done.
- ❌ **Do not enable this repo's Issues tab.** When this repo goes public,
  **disable Issues in the same sitting** — see *Going public* below.
- ❌ **Do not invent label names**, and do not create `type:bug` / `type:feature`. A label
  that does not exist in the target repo is **silently dropped** from the issue.

## What exists now

### The two axes

Every issue gets a **surface** label and a **kind** label, both applied automatically by a
workflow in the tracker that reads the issue form's dropdown answers.

**Surface** — which thing the report is about:

| Dropdown option (byte-exact) | Label |
|---|---|
| `MMRocket Sim` | `tool:mmrocket-sim` |
| `Motor Dashboard` | `tool:motor-dashboard` |
| `Motor Simulator` | `tool:motor-simulator` |
| `Black Powder Calculator` | `tool:bp-calculator` |
| `The mountainmanrockets.com website itself` | `area:site` |
| `Something else / not sure` | *(none — triaged by hand)* |

**Kind** — what sort of problem it is. **Bug form only**; the feature form is always
`enhancement`:

| Dropdown option (byte-exact) | Label |
|---|---|
| `Something is broken or does not work` | `bug` |
| `It looks wrong (spacing, layout, a bad crop)` | `design` — and `bug` is removed |
| `The words, photos or data are wrong or out of date` | `content` — and `bug` is removed |
| `Not sure` | falls through to `bug` |

Plus `needs-triage` on everything, and GitHub's nine stock labels.

> ⚠️ **Those option strings are matched byte-for-byte** by
> `.github/workflows/apply-tool-label.yml` in the tracker. If you ever change form copy,
> change the workflow in the same commit. **The failure is silent** — the workflow runs
> green and applies no label.
>
> They are deliberately plain ASCII. Do not "improve" the hyphens into em dashes.

### The two forms and their field IDs

| Form | `template=` | Fields (`id`) |
|---|---|---|
| Bug report | `bug-report.yml` | `tool` ▾, `kind` ▾, `what`, `version`, `browser`, `design` |
| Feature request | `feature-request.yml` | `tool` ▾, `request`, `workaround` |

▾ = dropdown. **This matters — see the next section.**

## Building this tool's feedback UI — everything you need

This repo **is** the reference implementation. The 🐞 Feedback button in the app header is the pattern the other tools should copy: `packages/app/src/App.tsx` (see `FEEDBACK_REPO` / `feedbackIssueUrl`), with the long-form version in `packages/app/src/data/userGuide.ts`.

### The URLs

```
Report a bug        https://github.com/mtnmanak/mountainmanrockets-feedback/issues/new?template=bug-report.yml
Request a feature   https://github.com/mtnmanak/mountainmanrockets-feedback/issues/new?template=feature-request.yml
Browse this tool    https://github.com/mtnmanak/mountainmanrockets-feedback/issues?q=is%3Aopen+label%3Atool%3Ammrocket-sim
All open issues     https://github.com/mtnmanak/mountainmanrockets-feedback/issues
No GitHub account   admin@mountainmanrockets.com
```

> ⚠️ **`?template=` is not optional.** The tracker sets `blank_issues_enabled: false`, so a
> bare `/issues/new` bounces a public filer to the template chooser **and drops every query
> parameter on the way.** Any prefill you build is silently lost. The site's own 404 page
> shipped with exactly this bug for a few hours on 12 Aug.

### ⚠️ Prefill rules — the one thing that will waste your afternoon

GitHub prefills issue-form fields from query parameters **only for `input` and `textarea`
field types.**

| Works | Does not work |
|---|---|
| `&title=` | `&tool=` — it is a **dropdown** |
| `&what=`, `&version=`, `&browser=`, `&design=` | `&kind=` — also a dropdown |
| `&request=`, `&workaround=` | `&body=` — ignored when a form is used; use the field id |

**So you cannot preselect which tool the report is about.** The user picks it. That is not a
bug to route around — the dropdown is `required`, and the labeler reads whatever they pick,
which is what makes the routing work at all.

> **Do not reintroduce `&tool=`.** This tool (then called Online OpenRocket) passed that
> inert parameter, and its release notes claimed *"Bug reports arrive with the tool and
> app version prefilled"*, through v0.043. **Both were corrected on 12 Aug 2026** — the
> parameter is gone and the notes now say the app version is prefilled and you pick the
> tool from a dropdown. Nothing to chase here; the table above is why, and it still holds.

A working example:

```js
const TRACKER = 'https://github.com/mtnmanak/mountainmanrockets-feedback';
const bugUrl = (opts = {}) => {
  const u = new URL(TRACKER + '/issues/new');
  u.searchParams.set('template', 'bug-report.yml');   // required — see above
  if (opts.title) u.searchParams.set('title', opts.title);
  if (opts.what) u.searchParams.set('what', opts.what);      // the "What happened?" textarea
  if (opts.version) u.searchParams.set('version', opts.version);
  return u.toString();   // dropdowns are left for the user
};
```

### Standing UI rulings — these are Eric's, already made

- **GitHub links open in a new tab** (`target="_blank" rel="noopener"`). His reason: *"so the
  user is not completely taken away from the site."*
- **`mailto:` links do NOT** — a new tab for a mail client leaves a blank tab behind.
- **Always offer the email fallback.** `admin@mountainmanrockets.com`, framed as *"no GitHub
  account needed — I'll file it for you."* Reading the tracker needs no account; filing does.
- **Link `/issues` for browsing, `/issues/new` only where the user already has a concrete
  problem in hand** — an in-app "report this" button is exactly that context.
- Feature requests explicitly include **content** requests ("write up how to assemble an
  Aerotech RMS motor"). Say so if you write help copy.

## Going public — the step that undoes everything if you skip it

These repos are expected to go public at or near the end of beta. **GitHub enables Issues by
default on a public repo.** Flip this repo public and change nothing else and you have
silently created a second door; people will file there, and — because the repo was private
when they did — **those issues can never be transferred into the public tracker.** GitHub
blocks private→public transfers outright.

So, in the same sitting:

```bash
gh repo edit mtnmanak/<repo> --visibility public --accept-visibility-change-consequences
gh repo edit mtnmanak/<repo> --enable-issues=false
```

A repo earns its own Issues tab when **a second person is working on its code** — an outside
PR, an outside issue citing a file and line, a request for `good first issue`. Not when it
goes public, and not when it leaves beta.

## ⚠️ Stale docs in this repo — do not follow them

- `docs/archive/feedback-repo-kit/SETUP.md` is superseded as a PLAN (its files were adopted, its steps were not) and its `.yml` files still carry all five pre-edit defects, including the node20 `github-script@v7`. Do not copy that directory anywhere.

They predate the adjudication and were left in place rather than rewritten, because each
edit here is a production deploy. **This file overrides all of them.**

## If you think this decision is wrong

Say so to Eric — do not implement an alternative. The whole reason this document exists is
that three sessions once each implemented a different reasonable-sounding answer. The
argument, the rejected options and the reasoning are in `docs/issue-tracking-consolidation.md`
in the `mountainmanrockets` site repo. Changes to the taxonomy, the forms or the labeler are
made **there and in the tracker**, never here.
