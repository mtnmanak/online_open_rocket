# Spike A — OpenRocket under CheerpJ (golden-reference oracle)

Runs the unmodified OpenRocket 24.12 release JAR in the browser via CheerpJ.

Purpose (see `docs/online-openrocket-plan.md`, Phase 0):
1. Demo that the real app can run in a browser at all.
2. **Oracle**: a browser-reachable instance of the *real* engine to validate our
   web engine's numerical output against.

## Run

The JAR is not committed (gitignored). Fetch it once:

```
curl -L -o OpenRocket-24.12.jar https://github.com/openrocket/openrocket/releases/download/release-24.12/OpenRocket-24.12.jar
```

Then serve this directory over HTTP (CheerpJ requires http(s), not file://):

```
npx serve spikes/cheerpj
# or: python -m http.server -d spikes/cheerpj 8080
```

Open the served URL. Expect a *long* first load (~100 MB+ of JAR + runtime) —
that's the known CheerpJ trade-off and exactly why it's the oracle, not the product.

## Notes

- `cheerpjInit({ version: 17 })` — OpenRocket 24.12 targets Java 17 (CheerpJ 4.3+ supports it).
- The whole JAR is served at `/app/` (CheerpJ's virtual filesystem maps the web root there).
