// ─── dashboard.steps.js ───────────────────────────────────────────────────────
// Step definitions for student-dashboard.feature.
//
// New JS concept here: we reuse the login helper from auth.steps indirectly
// by importing the Page Objects and USERS data directly.
// ─────────────────────────────────────────────────────────────────────────────

import { createBdd } from 'playwright-bdd';
import { expect }    from '@playwright/test';
import LoginPage            from '../pages/LoginPage.js';
import StudentDashboardPage from '../pages/StudentDashboardPage.js';
import { USERS, EXPECTED }  from '../data/testData.js';

const { Given, When, Then } = createBdd();

// ── GIVEN ─────────────────────────────────────────────────────────────────────

// "I am logged in as a student" — performs a full login flow so subsequent steps
// start from an authenticated state.
// We do NOT test the login UI here; login is just a precondition.
Given('I am logged in as a student', async ({ page }) => {
  const loginPage = new LoginPage(page);
  await loginPage.navigate();
  await loginPage.login(USERS.student.email, USERS.student.password);

  // Wait until the dashboard has loaded before handing control to the next step.
  // This prevents race conditions where the next step runs before navigation completes.
  const dashboardPage = new StudentDashboardPage(page);
  await dashboardPage.isLoaded();

  // Store the dashboard page object so When/Then steps can use it
  page._studentDashboard = dashboardPage;
});

// Same precondition, but used for the scenario where a student HAS an application.
// The step text is different so Gherkin can pick the right background context,
// even though the login logic is identical for now.
// In the future you could use API calls here to seed test data for this student.
Given('I am logged in as a student with an active thesis application', async ({ page }) => {
  const loginPage = new LoginPage(page);
  await loginPage.navigate();
  await loginPage.login(USERS.student.email, USERS.student.password);

  const dashboardPage = new StudentDashboardPage(page);
  await dashboardPage.isLoaded();
  page._studentDashboard = dashboardPage;
});

// ── WHEN ──────────────────────────────────────────────────────────────────────

When('I view the student dashboard', async ({ page }) => {
  // The student is already on the dashboard after login, so we just confirm it loaded.
  // isLoaded() waits for the heading to be visible — acts as an explicit sync point.
  await page._studentDashboard.isLoaded();
});

// ── THEN ──────────────────────────────────────────────────────────────────────

Then('I should see the {string} message', async ({ page }, expectedText) => {
  // getByText() finds any element containing exactly this text
  await expect(page.getByText(expectedText)).toBeVisible();
});

Then('I should see the thesis application details', async ({ page }) => {
  // Check that the detail list container is visible — means data was loaded from the API
  await expect(
    page.locator('[class*="detailList"]')
  ).toBeVisible();

  // Also verify at least the Topic row is rendered with some content
  await expect(
    page._studentDashboard.getDetailValue('Topic')
  ).not.toBeEmpty();
});

Then('the approval status badge should be visible', async ({ page }) => {
  // The status badge must be visible and contain one of the three valid statuses
  const badge = page._studentDashboard.statusBadge;
  await expect(badge).toBeVisible();

  // toHaveText() with a regex — checks the badge text matches one of the three values.
  // In Java regex you'd write: Pattern.compile("PENDING|APPROVED|REJECTED")
  // In JS: /PENDING|APPROVED|REJECTED/
  await expect(badge).toHaveText(/PENDING|APPROVED|REJECTED/);
});
