// ─── auth.steps.js ────────────────────────────────────────────────────────────
// Step definitions for auth.feature.
//
// Key JS concepts explained:
//
// 1. import { X } from 'module'
//    Same as Java's: import com.example.X;
//    The curly braces mean we are importing a NAMED export (not the default export).
//
// 2. createBdd()
//    Returns the Given/When/Then functions configured to work with Playwright.
//    We destructure them out immediately:
//      const { Given, When, Then } = createBdd();
//    This is shorthand for:
//      const bdd   = createBdd();
//      const Given = bdd.Given;
//      const When  = bdd.When;
//      const Then  = bdd.Then;
//
// 3. Arrow functions: async ({ page }) => { ... }
//    This is the step callback.  The parameter { page } is destructuring —
//    playwright-bdd calls this function with a fixtures object and we extract
//    just the `page` property from it.  `page` is Playwright's browser page,
//    equivalent to WebDriver in Selenium.
//
// 4. expect(locator).toBeVisible() / toHaveText()
//    Playwright's built-in assertion library.  Unlike JUnit assertions, these
//    AUTOMATICALLY RETRY until the condition is true or the timeout expires.
//    No need for explicit waits.
// ─────────────────────────────────────────────────────────────────────────────

import { createBdd } from 'playwright-bdd';
import { expect }    from '@playwright/test';
import LoginPage     from '../pages/LoginPage.js';
import { USERS, EXPECTED } from '../data/testData.js';

// createBdd() wires Given/When/Then to Playwright's test runner
const { Given, When, Then } = createBdd();

// ── GIVEN ─────────────────────────────────────────────────────────────────────

Given('I am on the login page', async ({ page }) => {
  // Create a new LoginPage instance for this test's `page`
  // We attach it to `page._loginPage` so other steps in the same scenario can reuse it.
  // (In playwright-bdd, sharing state between steps in the same scenario is done via
  // custom fixtures or by attaching data to the page object itself — more on this below)
  page._loginPage = new LoginPage(page);
  await page._loginPage.navigate();
});

// ── WHEN ──────────────────────────────────────────────────────────────────────

// {string} in the Gherkin maps to a string parameter here.
// playwright-bdd automatically extracts the quoted values from the step text
// and passes them as function arguments — same behaviour as Cucumber in Java.
When('I enter email {string} and password {string}', async ({ page }, email, password) => {
  await page._loginPage.fillEmail(email);
  await page._loginPage.fillPassword(password);
});

When('I enter the student email and wrong password', async ({ page }) => {
  // Uses testData so the actual email is not hardcoded in the feature file
  await page._loginPage.fillEmail(USERS.student.email);
  await page._loginPage.fillPassword(USERS.wrongPassword);
});

When('I click the login button', async ({ page }) => {
  await page._loginPage.clickSubmit();
});

When('I log in as a student', async ({ page }) => {
  page._loginPage = new LoginPage(page);
  await page._loginPage.navigate();
  // login() is the convenience method that fills both fields and clicks submit
  await page._loginPage.login(USERS.student.email, USERS.student.password);
});

// ── THEN ──────────────────────────────────────────────────────────────────────

Then('I should see an error modal with message {string}', async ({ page }, expectedMessage) => {
  // expect() wraps a locator and gives us assertion methods.
  // toContainText() checks that the element's text includes the expected string.
  // This call automatically waits up to the configured timeout for it to be true.
  await expect(page._loginPage.getErrorModalMessage()).toContainText(expectedMessage);
});

Then('I should see the {string} heading', async ({ page }, headingText) => {
  // getByRole('heading') finds any h1/h2/h3... with the given accessible name
  await expect(
    page.getByRole('heading', { name: headingText })
  ).toBeVisible();
});
