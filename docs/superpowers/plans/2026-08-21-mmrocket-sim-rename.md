# MMRocket Sim Rename (v0.047) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rename the product from "Online OpenRocket" to **MMRocket Sim** and move it to **mmrsim.mountainmanrockets.com** in one release (v0.047), updating every in-repo surface, the three lockstep external surfaces (feedback tracker, main site, GitHub repo), and shipping a graceful old-origin migration path.

**Architecture:** One release contains the full rename plus an old-host migration layer (banner + PWA self-dismantle) so the old subdomain keeps serving until testers migrate; the 301 redirect is a **deferred phase-2 ops step**, not part of this release. The Cloudflare Pages project name never changes (invisible behind custom domains); both subdomains serve the same deploy during the grace period.

**Tech Stack:** Vite + React + TS (`packages/app`), vite-plugin-pwa/workbox, Cloudflare Pages + zone Single Redirects, GitHub (`gh` CLI) for repo + feedback-tracker changes.

**Spec:** `docs/handoff-2026-08-21.md` (rename audit + mechanics) + the decision block at the top of `docs/working-notes.md` (name, subdomain, vet results, TESS clear 2026-08-21).

## Global Constraints

- New product name is exactly **`MMRocket Sim`** (capital MMR, capital S, one space). New canonical URL: **`https://mmrsim.mountainmanrockets.com`**. New GitHub repo name: **`mtnmanak/mmrocket-sim`**. New tracker label: **`tool:mmrocket-sim`**; new issue-form dropdown option (byte-exact): **`MMRocket Sim`**.
- **KEEP all OpenRocket attribution** — it is nominative use and the GPL story: the identity line "OpenRocket-derived physics…", "based on OpenRocket 24.12" / "the real OpenRocket physics kernel" phrasing in the guide, `.ork` compatibility claims, the LICENSE. Only the *product identifier* changes.
- **Do NOT rename:** the 8 `online-openrocket.*` localStorage keys; npm package names `@online-openrocket/*` (descriptions in package.json DO change — the *text*, not the names); the Cloudflare Pages project `online-open-rocket` and its `deploy.yml` project/concurrency strings; local repo folder paths and CLAUDE.md's machine table; `engine-java/tools/*.ork` and `docs/testing/*.ork` fixture files (their `creator=` attrs are historical input data); past CHANGELOG entries, `docs/handoff-*.md`, `docs/testing/*`, `docs/working-notes.md` history, `docs/archive/**` (history stays as written).
- `vite.config.ts` keeps `base: './'`. Carved engine-java files are never edited (none are touched here).
- v0.047 with `version.json` paired — CI fails the deploy on drift.
- `git push` to `main` IS the deploy. Do not push until the release checkpoint (Task 8).
- All commits get the standard footer (Co-Authored-By + Claude-Session) per session rules.

---

### Task 1: Host-migration helper + moved-notice component (TDD)

**Files:**
- Create: `packages/app/src/services/hostMigration.ts`
- Create: `packages/app/src/services/hostMigration.test.ts`
- Create: `packages/app/src/components/MovedNotice.tsx`
- Create: `packages/app/src/components/MovedNotice.test.tsx`
- Modify: `packages/app/src/main.tsx` (SW registration branch)
- Modify: `packages/app/src/App.tsx` (render `<MovedNotice/>` next to the autosave banner at ~line 1080)

**Interfaces:**
- Produces: `isRetiredHost(hostname: string): boolean`, `dismantlePwa(): Promise<void>`, `CANONICAL_HOST: string` (from `hostMigration.ts`); `MovedNotice({ hostname }: { hostname: string })` React component returning `null` on non-retired hosts.

- [ ] **Step 1: Write the failing tests**

`packages/app/src/services/hostMigration.test.ts`:

```ts
import { describe, expect, it, vi } from 'vitest';
import { CANONICAL_HOST, dismantlePwa, isRetiredHost } from './hostMigration.js';

describe('isRetiredHost', () => {
  it('flags the pre-rename subdomain', () => {
    expect(isRetiredHost('openrocket.mountainmanrockets.com')).toBe(true);
  });
  it('does not flag the canonical host, previews, or localhost', () => {
    expect(isRetiredHost(CANONICAL_HOST)).toBe(false);
    expect(isRetiredHost('abc123.online-open-rocket.pages.dev')).toBe(false);
    expect(isRetiredHost('localhost')).toBe(false);
  });
});

describe('dismantlePwa', () => {
  it('unregisters every SW registration and deletes every cache', async () => {
    const unregister = vi.fn().mockResolvedValue(true);
    vi.stubGlobal('navigator', {
      serviceWorker: { getRegistrations: vi.fn().mockResolvedValue([{ unregister }, { unregister }]) },
    });
    const del = vi.fn().mockResolvedValue(true);
    vi.stubGlobal('caches', { keys: vi.fn().mockResolvedValue(['a', 'b']), delete: del });
    await dismantlePwa();
    expect(unregister).toHaveBeenCalledTimes(2);
    expect(del).toHaveBeenCalledWith('a');
    expect(del).toHaveBeenCalledWith('b');
    vi.unstubAllGlobals();
  });
  it('never throws when SW/caches APIs are missing', async () => {
    vi.stubGlobal('navigator', {});
    vi.stubGlobal('caches', undefined);
    await expect(dismantlePwa()).resolves.toBeUndefined();
    vi.unstubAllGlobals();
  });
});
```

`packages/app/src/components/MovedNotice.test.tsx` (follow the render style of the existing component tests, e.g. `SiteBand.test.tsx`):

```tsx
import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { MovedNotice } from './MovedNotice.js';

describe('MovedNotice', () => {
  it('renders the move alert with the new address on the retired host', () => {
    render(<MovedNotice hostname="openrocket.mountainmanrockets.com" />);
    const alert = screen.getByRole('alert');
    expect(alert.textContent).toContain('mmrsim.mountainmanrockets.com');
    expect(alert.textContent?.toLowerCase()).toContain('export');
  });
  it('renders nothing on the canonical host', () => {
    const { container } = render(<MovedNotice hostname="mmrsim.mountainmanrockets.com" />);
    expect(container.firstChild).toBeNull();
  });
});
```

(If `@testing-library/react` is not already a devDependency, match whatever the existing component tests import for rendering — do not add a new library; adapt the test to the established pattern.)

- [ ] **Step 2: Run tests to verify they fail**

Run: `npm run test -w @online-openrocket/app -- src/services/hostMigration.test.ts src/components/MovedNotice.test.tsx`
Expected: FAIL — modules not found.

- [ ] **Step 3: Implement**

`packages/app/src/services/hostMigration.ts`:

```ts
/**
 * v0.047 rename: the app moved from openrocket.mountainmanrockets.com to
 * mmrsim.mountainmanrockets.com. The old origin keeps serving for a grace
 * period (a 301 would strand installed PWAs mid-update: workbox precache
 * fetches fail on cross-origin redirects), but it must stop behaving like
 * a PWA and tell the user where the app lives now. localStorage is
 * origin-scoped, so autosaved work does NOT follow the move — users export
 * .ork files and reopen them at the new address.
 */
export const CANONICAL_HOST = 'mmrsim.mountainmanrockets.com';

const RETIRED_HOSTS = ['openrocket.mountainmanrockets.com'];

export function isRetiredHost(hostname: string): boolean {
  return RETIRED_HOSTS.includes(hostname);
}

/** Unregister every service worker and drop its caches so the retired
 * origin always loads fresh from the network. Best-effort: never throws. */
export async function dismantlePwa(): Promise<void> {
  try {
    const regs = (await navigator.serviceWorker?.getRegistrations?.()) ?? [];
    await Promise.all(regs.map((r) => r.unregister()));
    const keys = (await globalThis.caches?.keys?.()) ?? [];
    await Promise.all(keys.map((k) => globalThis.caches.delete(k)));
  } catch {
    /* a broken cleanup must never block the app */
  }
}
```

`packages/app/src/components/MovedNotice.tsx`:

```tsx
import { CANONICAL_HOST, isRetiredHost } from '../services/hostMigration.js';

/** Persistent (not dismissible) on purpose, like the autosave warning:
 * while the user is on the retired origin their autosaves are stranded
 * there, so the notice must survive until they actually move. */
export function MovedNotice({ hostname }: { hostname: string }) {
  if (!isRetiredHost(hostname)) return null;
  return (
    <div className="file-note autosave-warn" role="alert">
      📦 This app has moved to{' '}
      <a href={`https://${CANONICAL_HOST}/`}>{CANONICAL_HOST}</a> — update your
      bookmark, and reinstall the home-screen app from the new address.
      Designs autosaved in this browser stay with the old address: export your
      design (Save / Export → .ork) and open it at the new one.
    </div>
  );
}
```

`packages/app/src/main.tsx` — replace the unconditional `registerSW({ immediate: true });` (line 14) with:

```ts
import { dismantlePwa, isRetiredHost } from './services/hostMigration.js';

// Offline-first on the canonical host. On the RETIRED pre-rename host the
// PWA dismantles itself instead: no SW, caches dropped, banner in App.
if (isRetiredHost(location.hostname)) {
  void dismantlePwa();
} else {
  registerSW({ immediate: true });
}
```

`packages/app/src/App.tsx` — import `{ MovedNotice }` and render it directly ABOVE the `autosaveFailing` banner (the `{autosaveFailing && (` block at ~line 1080):

```tsx
<MovedNotice hostname={window.location.hostname} />
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `npm run test -w @online-openrocket/app -- src/services/hostMigration.test.ts src/components/MovedNotice.test.tsx`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add packages/app/src/services/hostMigration.ts packages/app/src/services/hostMigration.test.ts packages/app/src/components/MovedNotice.tsx packages/app/src/components/MovedNotice.test.tsx packages/app/src/main.tsx packages/app/src/App.tsx
git commit -m "feat: old-origin migration layer — moved notice + PWA self-dismantle on the retired host"
```

---

### Task 2: In-app rename sweep (every code/string surface)

**Files:**
- Modify: `packages/app/index.html:13-14` (pages.dev redirect target), `:23` (title)
- Modify: `packages/app/vite.config.ts:17-20` (manifest name/short_name + add `id`)
- Modify: `packages/app/src/App.tsx:922` (wordmark), `:1035` (queue label), `:1041` (mailto subject), `:1072` (GPL source link)
- Modify: `packages/app/src/services/orkFile.ts:1176`, `stlExport.ts:13,34`, `objExport.ts:30`, `finTemplate.ts:107`, `printPack.ts:316`, `schematicExport.ts:45`
- Modify: `packages/app/package.json:4`, `packages/engine/package.json:4` (description text only), `packages/engine/src/index.ts:2`, `scripts/build-user-guide.mjs:225` (comments)

**Interfaces:**
- Consumes: nothing from Task 1 (independent strings).
- Produces: the exact strings later tasks and the live deploy rely on (listed per file below).

- [ ] **Step 1: Apply the exact edits**

`index.html`: redirect target becomes `'https://mmrsim.mountainmanrockets.com'` (keep the `+ location.pathname + location.search + location.hash` carry-over and the exact-hostname pages.dev match untouched); `<title>MMRocket Sim — Rocket Design & Flight Simulator</title>`. The meta description is already name-free — leave it.

`vite.config.ts` manifest:

```ts
      manifest: {
        id: '/',
        name: 'MMRocket Sim',
        short_name: 'MMRocket Sim',
        description: 'Design model rockets and simulate flights — the real OpenRocket physics engine in your browser, offline-capable.',
```

(`id` is new — stable PWA identity on the new origin, per the audit. Description keeps its nominative OpenRocket mention.)

`App.tsx`:
- 922: `<h1><Icon name="rocket" size={19} /> MMRocket Sim</h1>`
- 1035: `'is:open label:tool:mmrocket-sim'`
- 1041: `` `MMRocket Sim v${APP_VERSION} feedback` ``
- 1072: `href="https://github.com/mtnmanak/mmrocket-sim"` (this link IS the corresponding-source offer — it must match the renamed repo of Task 7)
- The identity line at 1068 ("OpenRocket-derived physics…") does NOT change.

Export stamps (each replaces `Online OpenRocket` with `MMRocket Sim`, nothing else):
- `orkFile.ts:1176`: `emit(0, '<openrocket version="1.10" creator="MMRocket Sim">');`
- `stlExport.ts:34`: `` const header = `MMRocket Sim ${name} (units: mm)`; `` — and update the comment at line 13 that names the old prefix (its guarantee — header must not begin with `solid` — still holds).
- `objExport.ts:30`: `` `# MMRocket Sim — ${name}`, ``
- `finTemplate.ts:107`: `lines.push('<!-- MMRocket Sim fin template — PRINT AT 100% SCALE -->');`
- `printPack.ts:316`: `Made by MMRocket Sim. Dimensions are millimetres; the STLs are too.`
- `schematicExport.ts:45`: `` lines.push(`MMRocket Sim v${d.appVersion} — ${new Date().toISOString().slice(0, 10)}`); ``

Comment/description text (names stay `@online-openrocket/*`):
- `packages/app/package.json:4`: `"description": "Web UI for MMRocket Sim (design editor, plots, 3D view)",`
- `packages/engine/package.json:4`: `"description": "Flight-simulation engine for MMRocket Sim (6DOF, Extended Barrowman, ISA atmosphere)",`
- `packages/engine/src/index.ts:2` and `scripts/build-user-guide.mjs:225`: replace `Online OpenRocket` with `MMRocket Sim` in the comment.

- [ ] **Step 2: Sweep for stragglers**

Run: `git grep -n "Online OpenRocket" -- packages scripts .github`
Expected: zero hits. (Docs are Task 3; fixtures/history are exempt per Global Constraints.)

- [ ] **Step 3: Run both full test suites**

Run: `npm test`
Expected: all engine + app tests PASS. If any test asserts a renamed string (e.g. an export-stamp snapshot), update the assertion to `MMRocket Sim` — the test was pinning the product name, which legitimately changed.

- [ ] **Step 4: Commit**

```bash
git add -A packages scripts
git commit -m "feat: rename product to MMRocket Sim across app strings, manifest (+id), export stamps, and GPL source link"
```

---

### Task 3: Docs rewrite — user guide (regenerates in-app guide), README, beta invite, tracker doc, CLAUDE.md

**Files:**
- Modify: `docs/user-guide.md` (title + ~10 product mentions + live URL + queue-label URL)
- Modify: `README.md` (title, live URL, tracker queue link)
- Modify: `docs/beta-invite.md` (title, name mentions, both URLs, maintainer note)
- Modify: `docs/feedback-tracker.md` (dropdown row, queue URLs, `TRACKER` snippet context)
- Modify: `CLAUDE.md` (Deploying table live URL + canonical-address ruling, feedback preamble)
- Regenerated: `packages/app/src/data/userGuide.ts` (NEVER edited by hand — `npm run build` writes it)

**Interfaces:**
- Consumes: the label `tool:mmrocket-sim` and repo `mtnmanak/mmrocket-sim` fixed in Global Constraints.
- Produces: the invite copy Task 9 sends; the guide text the app ships.

- [ ] **Step 1: Edit `docs/user-guide.md`** — mechanical rules, applied line by line over `grep -n "Online OpenRocket\|openrocket.mountainmanrockets.com\|tool%3Aonline-openrocket\|tool:online-openrocket" docs/user-guide.md`:
  - Product-identifier mentions (title line 1, line 3, heading line 9, lines 11, 407, and the omitted-long lines 405/415/533/571 the grep surfaces) → `MMRocket Sim`. Rephrase line 11's opening so it never claims to BE OpenRocket: `MMRocket Sim runs the real OpenRocket flight physics entirely in your web browser.` — the rest of the sentence (24.12 kernel, bit-for-bit verification) stays verbatim.
  - Line 584 attribution: keep the OpenRocket project links; the sentence tail becomes `MMRocket Sim inherits this license.`
  - Live-URL mentions → `https://mmrsim.mountainmanrockets.com`; queue link (line 545) → `label%3Atool%3Ammrocket-sim`.
  - Every other `OpenRocket` mention (desktop app, kernel, `.ork` compatibility) stays.

- [ ] **Step 2: Edit README.md, beta-invite.md, feedback-tracker.md, CLAUDE.md**
  - `README.md`: `# MMRocket Sim`; add one lineage sentence directly under the title: `Formerly "Online OpenRocket" — renamed 2026-08 (same app; the name now makes clear this is an independent derivative of OpenRocket, not the OpenRocket project's own).` Live app → new URL; queue link → `label%3Atool%3Ammrocket-sim`; "pick **Online OpenRocket**" → "pick **MMRocket Sim**".
  - `docs/beta-invite.md`: retitle `# MMRocket Sim — beta invitation`; both URLs → `https://mmrsim.mountainmanrockets.com`; body mentions → MMRocket Sim; update the line-135 maintainer note to name the new subdomain (live 2026-08) and note the old one 301s once phase 2 lands.
  - `docs/feedback-tracker.md`: dropdown row (line 42) → `` `MMRocket Sim` `` / `` `tool:mmrocket-sim` ``; queue URLs (lines 6, 86) → the new label; line 111's historical note may keep the old name (it recounts history).
  - `CLAUDE.md`: Deploying table Live URL row → `https://mmrsim.mountainmanrockets.com — cutover 2026-08 (old openrocket.* subdomain serves a moved notice until the phase-2 301)`; the bold "canonical subdomain is the ONLY address to hand out" ruling now names `mmrsim`; the feedback banner keeps its warning but its examples reference the new label; update the repo-name mention in the corresponding-source sentence if present.

- [ ] **Step 3: Regenerate the in-app guide and verify**

Run: `npm run build`
Expected: build succeeds; `git diff --stat packages/app/src/data/userGuide.ts` shows the regenerated content; `git grep -c "MMRocket Sim" packages/app/src/data/userGuide.ts` ≥ 10; `git grep -n "Online OpenRocket" packages/app/src/data/userGuide.ts` → zero hits.

- [ ] **Step 4: Commit**

```bash
git add docs/user-guide.md README.md docs/beta-invite.md docs/feedback-tracker.md CLAUDE.md packages/app/src/data/userGuide.ts
git commit -m "docs: rename to MMRocket Sim — user guide (+regenerated in-app copy), README, beta invite, tracker doc, CLAUDE.md"
```

---

### Task 4: Version bump to 0.047 (paired)

**Files:**
- Modify: `packages/app/src/version.ts` (APP_VERSION + new CHANGELOG entry)
- Modify: `version.json`

- [ ] **Step 1: Bump `APP_VERSION` to `'0.047'` and prepend this CHANGELOG entry**

```ts
  {
    version: '0.047',
    date: '<release date>',
    title: 'A new name: MMRocket Sim',
    items: [
      'RENAMED: Online OpenRocket is now MMRocket Sim, at a new address — https://mmrsim.mountainmanrockets.com. Two honest reasons: "Online" undersold an app that works fully offline once loaded, and "OpenRocket" is the upstream project\'s name — this tool is proudly derived from OpenRocket 24.12 (and still says so, right under the logo), but it is not published by the OpenRocket project. Same app, same physics, same GPL source.',
      'The old address keeps working for now but shows a moved notice and no longer installs or updates as an app. Designs autosaved in your browser belong to the old address: export your design there (Save / Export → .ork) and open it at the new one. Home-screen installs should be reinstalled from the new address.',
      'Everything stamped with the old name now says MMRocket Sim: .ork files, STL/OBJ exports, fin templates, print packs, schematics, and the feedback tracker (reports now file under "MMRocket Sim").',
    ],
  },
```

- [ ] **Step 2: Update `version.json`**

```json
{
  "version": "0.047",
  "released": "<release date>",
  "note": "Online OpenRocket is now MMRocket Sim, at a new address: mmrsim.mountainmanrockets.com. Same app, same physics, same GPL source — the new name makes clear this is an independent derivative of OpenRocket, and drops the \"Online\" that undersold an offline-capable app. The old address shows a moved notice; export autosaved designs there and reopen them at the new one."
}
```

Fill `<release date>` with the actual push date in both files.

- [ ] **Step 3: Verify the pairing gate locally**

Run the same node one-liner as `.github/workflows/deploy.yml:50-62`.
Expected: `version 0.047 is consistent`.

- [ ] **Step 4: Run `npm test`** — Expected: all green (the mailto test, if any, already asserts via APP_VERSION).

- [ ] **Step 5: Commit**

```bash
git add packages/app/src/version.ts version.json
git commit -m "release: v0.047 — MMRocket Sim rename"
```

---

### Task 5: Cloudflare — attach the new subdomain (ops, BEFORE push)

No repo files. Cloudflare dashboard (or API with a suitably-scoped token — the repo's CI token is Pages-only and cannot edit DNS):

- [ ] **Step 1:** Pages → project `online-open-rocket` → Custom domains → **Add** `mmrsim.mountainmanrockets.com`. Same-account zone, so the proxied CNAME is created automatically. Do NOT detach `openrocket.mountainmanrockets.com` — both domains serve during the grace period.
- [ ] **Step 2:** Verify: `curl -sI https://mmrsim.mountainmanrockets.com | head -5` returns `HTTP/2 200` (serving v0.046 until Task 8 deploys — that is expected and fine: nothing links to the new subdomain yet).
- [ ] **Step 3:** Do NOT create any redirect rule yet — that is phase 2 (Task 10).

---

### Task 6: Lockstep surface — feedback tracker (`mtnmanak/mountainmanrockets-feedback`)

All three pieces move together or routing silently breaks (the workflow byte-matches the dropdown string). Work in a scratchpad clone.

- [ ] **Step 1: Rename the label** (GitHub renames in place — the one existing closed issue keeps it):

```bash
gh label edit "tool:online-openrocket" --repo mtnmanak/mountainmanrockets-feedback --name "tool:mmrocket-sim"
```

- [ ] **Step 2: Update form + workflow + README in one commit**

```bash
gh repo clone mtnmanak/mountainmanrockets-feedback <scratchpad>/feedback && cd <scratchpad>/feedback
```

- `.github/ISSUE_TEMPLATE/bug-report.yml` and `feature-request.yml`: dropdown option `Online OpenRocket` → `MMRocket Sim` (byte-exact, position unchanged).
- `.github/workflows/apply-tool-label.yml`: mapping line `'Online OpenRocket': 'tool:online-openrocket',` → `'MMRocket Sim': 'tool:mmrocket-sim',`.
- `README.md`: the tool table row name + its `label%3Atool%3A...` queue URL.

```bash
git add -A && git commit -m "Rename Online OpenRocket -> MMRocket Sim: dropdown option, label mapping, queue link" && git push
```

- [ ] **Step 3: Verify routing end-to-end:** open the bug form, confirm the dropdown shows `MMRocket Sim`; file a test issue selecting it; confirm the workflow applies `tool:mmrocket-sim`; close the test issue with a comment saying it was a routing test.

---

### Task 7: GitHub repo rename

- [ ] **Step 1:**

```bash
gh repo rename mmrocket-sim --repo mtnmanak/online_open_rocket --yes
```

Web + git redirects persist; Actions and secrets survive (audit-verified). raw.githubusercontent URLs do NOT redirect — `git grep -n "raw.githubusercontent"` found none, nothing to fix.

- [ ] **Step 2: Update the local remote (this machine):**

```bash
git remote set-url origin https://github.com/mtnmanak/mmrocket-sim.git && git remote -v
```

- [ ] **Step 3:** Note for the session handoff: the DESKTOP must run the same `git remote set-url` (its clone at `E:\git\online_open_rocket` keeps its folder name — only the remote changes).

---

### Task 8: 🚀 RELEASE — push (= deploy) and verify live

**CHECKPOINT: confirm with Eric before pushing — the push is the public deploy of the rename.**

- [ ] **Step 1:** `npm test` && `npm run build` one final time — all green, build clean.
- [ ] **Step 2:** `git push` (origin main). Watch the Deploy workflow: `gh run watch` — the version gate passes, wrangler publishes.
- [ ] **Step 3: Verify the canonical host:** https://mmrsim.mountainmanrockets.com loads v0.047 (version badge next to the logo); header wordmark reads MMRocket Sim; browser tab title correct; manifest reachable (DevTools → Application → Manifest: name `MMRocket Sim`, id present); install prompt works; NO moved banner.
- [ ] **Step 4: Verify the retired host:** https://openrocket.mountainmanrockets.com still loads the app, SHOWS the moved banner, and DevTools → Application shows no active service worker after a reload (dismantled) — autosaved designs still open, and Save/Export → .ork works (the migration path).
- [ ] **Step 5: Verify the pages.dev origin** redirects to `mmrsim.…` preserving path+hash (paste any share link's fragment).
- [ ] **Step 6: Spot-check stamps:** export an .ork (root element `creator="MMRocket Sim"`), an STL header, and the in-app Feedback → Browse open issues button (lands on the `tool:mmrocket-sim` queue).

---

### Task 9: Lockstep surface — main site (`mountainmanrockets` repo) + invite clearance

- [ ] **Step 1:** In the main-site repo (expected at `C:\git\mountainmanrockets` on the laptop; consult that repo's CLAUDE.md), update `src/data/tools.mjs`: the tool entry currently reading `Online Open Rocket` → name `MMRocket Sim`, URL `https://mmrsim.mountainmanrockets.com`, version `0.047`. Fix any other old-URL references that repo's grep finds.
- [ ] **Step 2:** Run `node scripts/check-tools.mjs` there — expected: no drift reported. Commit + deploy per that repo's own process.
- [ ] **Step 3:** The rewritten invite (`docs/beta-invite.md`, Task 3) is now clear for the **public rocketry-forum beta post** — posting remains Eric's manual step. (The private invite already went out on the OLD address per `docs/testing/issues-2026-08-21a.md`; those users are served by the old-origin moved banner, and the grace period before the phase-2 301 exists for them.) Update `docs/working-notes.md`'s pointer block: rename SHIPPED in v0.047, forum post unblocked, phase-2 redirect pending.

---

### Task 10: Phase 2 — the 301 (✅ EXECUTED 2026-08-21, second sitting after the release)

**Executed 2026-08-21** on Eric's confirmation in `docs/testing/issues-2026-08-21b.md`
("all invitees have confirmed they have moved"). Rule created via the Cloudflare
dashboard exactly as specified below (rule name: "openrocket subdomain 301 to mmrsim
(rename phase 2)", placed last, after the root→WWW template rule). Verified by curl:
`https://openrocket.mountainmanrockets.com/` → `301` `Location:
https://mmrsim.mountainmanrockets.com/`; `/some/path?x=1&y=2` → 301 preserving both path
and query; the canonical host still 200s. Original trigger + mechanics kept below for
the record:

**Trigger:** Eric confirms the handful of testers have moved (suggest ~2 weeks after Task 8, or after direct confirmation). Recorded here so the mechanics don't get lost:

- Zone → Rules → Redirect Rules → Single Redirect: expression `http.host eq "openrocket.mountainmanrockets.com"`, dynamic target `concat("https://mmrsim.mountainmanrockets.com", http.request.uri.path)`, status 301, "Preserve query string" ON. Fragments (share links) survive 301s natively — browsers re-attach them.
- **Deviation from the audit, with reasoning:** the audit proposed carving `/.well-known/` + the SW script out of the redirect so old installs could fetch a final self-destructing SW. Superseded: workbox's *precache* fetches (the hashed assets, not just `sw.js`) also fail across a 301, so a carve-out cannot deliver an update anyway. v0.047's app-level dismantle (Task 1) does the self-destruct while the old origin still serves — the redirect then needs no carve-outs at all. Old-origin PWAs that never launched during the grace period will break at the 301 and need a reinstall; the installed population is a handful of known testers.
- Optional phase 3 (months later): detach the old custom domain from the Pages project, keep a proxied placeholder DNS record (A 192.0.2.0) so the redirect rule keeps firing.

---

## Self-review notes

- **Spec coverage:** every audit surface mapped — in-repo strings (T2), guide (T3), manifest+id (T2), subdomain+grace (T5/T8/T10), tracker lockstep (T6), site `tools.mjs` (T9), repo rename + GPL link (T7/T2), CLAUDE.md + invite (T3/T9), do-not-rename list (Global Constraints). New surfaces the audit missed are included: `index.html` pages.dev redirect, package.json descriptions, `build-user-guide.mjs` comment, localStorage origin-scoping (banner copy).
- **Deliberate deviations from the audit:** (1) no SW carve-outs — see Task 10; (2) the 301 is deferred out of the release rather than shipped with it, because the old origin must keep serving while stranded autosaves exist.
- **Types:** `isRetiredHost`/`dismantlePwa`/`CANONICAL_HOST`/`MovedNotice` names are consistent across Tasks 1–2.
