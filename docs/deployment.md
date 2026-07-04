# Deploying Online OpenRocket

The app is a fully static, relative-path build (`base: './'` in Vite): the
same files work uploaded to any web host, on GitHub Pages, or inside an
iframe. No server-side code, no database — everything runs in the browser.

## 1. Manual upload to your own web host (current flow)

Build and package:

```
npm run package
```

That produces `deploy/online-openrocket-v<version>.zip` containing the site
files (`index.html`, `assets/…`, `sw.js`, icons, manifest).

Upload the **contents** of the zip (not a nested folder, unless you want the
app at a sub-path — that works too) to wherever it should live, e.g.:

- `https://yourdomain.com/rocket/` — a subfolder
- `https://rocket.yourdomain.com/` — a subdomain

Requirements/notes:

- **HTTPS is required** for the offline/PWA features (service worker). Plain
  HTTP still runs the app, just without offline support. Any modern host's
  free TLS (Let's Encrypt) is fine.
- **Updates:** just upload the new zip's contents over the old files. The
  service worker picks up the new version automatically on the visitor's next
  visit (asset filenames are content-hashed, so there are no stale-cache
  issues).
- **Optional cache headers** (Apache hosts, `.htaccess` in the app folder) —
  makes updates propagate immediately while keeping big assets cached:

  ```apache
  <FilesMatch "^(index\.html|sw\.js|manifest\.webmanifest)$">
    Header set Cache-Control "no-cache"
  </FilesMatch>
  <FilesMatch "\.(js|css|png)$">
    Header set Cache-Control "public, max-age=31536000, immutable"
  </FilesMatch>
  AddType application/manifest+json .webmanifest
  ```

  (If you can't set headers, everything still works — updates may just take
  one extra reload.)

## 2. Embedding in WordPress

Add a **Custom HTML** block to any page/post and paste (replace the URL with
where you uploaded the app):

```html
<iframe
  src="https://yourdomain.com/rocket/"
  title="Online OpenRocket — rocket design and flight simulation"
  style="width: 100%; height: 85vh; min-height: 700px; border: 0; border-radius: 8px;"
  loading="lazy"
  allowfullscreen
></iframe>
<p style="text-align: right; font-size: 0.85em;">
  <a href="https://yourdomain.com/rocket/" target="_blank" rel="noopener">
    Open full-screen ↗
  </a>
</p>
```

Why an iframe: it's immune to the theme's CSS/JS, needs no plugin, and always
shows the latest deployed version. The `85vh` height gives the app most of
the viewport; tweak to taste. Designs, saved simulations, and preferences are
stored in the browser's localStorage **per origin** — visitors who use both
the embedded and the full-screen version keep one shared workspace since both
point at the same origin.

## 3. GitHub Pages (later, when the repo goes public)

A ready workflow lives at `.github/workflows/deploy-pages.yml`. It is
**manual-trigger only** while the repo is private (Pages is paid-only for
private repos). When ready:

1. Make the repository public (GitHub → Settings → General → Danger Zone).
   This also cleanly satisfies the GPL source-code offer (see below).
2. Settings → Pages → Build and deployment → Source: **GitHub Actions**.
3. Actions tab → "Deploy to GitHub Pages" → **Run workflow**.
   The app appears at `https://mtnmanak.github.io/online_open_rocket/`.
4. Optional: uncomment the `push:` trigger in the workflow for automatic
   deploys on every push, and/or attach a custom domain in the Pages settings.

The WordPress iframe can then point at the Pages URL instead of (or in
addition to) your own host.

## 4. Licensing obligations (GPL v3+)

The app is a derivative of OpenRocket (GPL-3.0-or-later), and serving it to
browsers is distribution — recipients must be **offered the corresponding
source**. The app header links to this GitHub repository for that purpose:

- Once the repo is **public**, the obligation is fully covered.
- While it is still **private**, be prepared to provide the source to anyone
  who asks (the GPL requires offering it on request, not proactive
  publication). Practically: make the repo public at or soon after the first
  real deployment.
