// ─── hooks.js ─────────────────────────────────────────────────────────────────
// Global Before/After hooks for the test suite.
//
// In Cucumber Java you would use @Before / @After annotations on methods.
// In playwright-bdd you import Before/After from createBdd() and register them
// as functions — same concept, different syntax.
//
// CURRENT STATE: no write tests exist yet, so no cleanup is needed.
// When write scenarios are added (e.g. submit application, register user),
// add teardown logic here using Playwright's `request` fixture to call
// your backend DELETE endpoints.
//
// HOW TO USE:
//   const { Before, After } = createBdd();
//
//   After(async ({ request }) => {
//     // `request` is Playwright's APIRequestContext — makes HTTP calls without a browser.
//     // Use it to call cleanup endpoints on the backend after each write scenario.
//     await request.delete('http://localhost:8080/api/some-resource/cleanup');
//   });
// ─────────────────────────────────────────────────────────────────────────────

import { createBdd } from 'playwright-bdd';

const { Before, After } = createBdd();

// Placeholder — no-op hooks that confirm the hook infrastructure is wired correctly.
// Remove these and replace with real setup/teardown as write tests are added.
Before(async () => {
  // Global preconditions before each scenario (e.g. seed test data via API)
});

After(async () => {
  // Global cleanup after each scenario (e.g. delete created records via API)
});
