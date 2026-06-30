// ─── LoginPage.js ────────────────────────────────────────────────────────────
// Page Object for the /login route.
// A Page Object is a class that knows about ONE page's structure (its elements
// and how to interact with them).  Step definitions call these methods without
// knowing anything about selectors — exactly like in your Java/Selenium setup.
// ─────────────────────────────────────────────────────────────────────────────

export default class LoginPage {
  // constructor() runs when you do: new LoginPage(page)
  // It receives Playwright's `page` object — think of this as WebDriver in Selenium.
  // We store it on `this` so every method in the class can use it.
  constructor(page) {
    this.page = page;

    // Locators — these are lazy references to elements on the page.
    // Playwright does NOT look for the element immediately when you define a locator;
    // it only searches when you actually interact with it (click, fill, etc.).
    // This is different from Selenium's findElement() which searches immediately.

    // getByLabel() finds an <input> that has a <label> pointing to it.
    // Our TextInput component generates id="email" from label="Email", so this works.
    this.emailInput    = page.getByLabel('Email');
    this.passwordInput = page.getByLabel('Password');

    // getByRole() finds elements by their semantic HTML role.
    // { name: 'Login' } narrows it to the button whose visible text is "Login".
    this.submitButton  = page.getByRole('button', { name: 'Login' });

    // The error modal — we look for a dialog role, or fall back to text lookup in assertions
  }

  // ── navigate ───────────────────────────────────────────────────────────────
  // Navigates the browser to the login page.
  // `async` means this method returns a Promise (it does async work).
  // `await` pauses here until the navigation is complete before moving on.
  // In Selenium you would just call driver.get(url) and it would block;
  // in Playwright everything is async, so you must use await.
  async navigate() {
    await this.page.goto('/login');
  }

  // ── fillEmail ──────────────────────────────────────────────────────────────
  // Types a value into the email input field.
  // fill() clears the field first then types — equivalent to clear() + sendKeys() in Selenium.
  async fillEmail(email) {
    await this.emailInput.fill(email);
  }

  // ── fillPassword ───────────────────────────────────────────────────────────
  async fillPassword(password) {
    await this.passwordInput.fill(password);
  }

  // ── clickSubmit ────────────────────────────────────────────────────────────
  // Clicks the Login button.
  async clickSubmit() {
    await this.submitButton.click();
  }

  // ── login ──────────────────────────────────────────────────────────────────
  // Convenience method: fills both fields and submits in one call.
  // This is the "happy path" helper used when login itself is not what we are testing.
  async login(email, password) {
    await this.fillEmail(email);
    await this.fillPassword(password);
    await this.clickSubmit();
  }

  // ── getErrorModalMessage ───────────────────────────────────────────────────
  // Returns the Locator for the error modal's message paragraph.
  // We use data-testid — a dedicated test attribute that never changes with
  // CSS refactoring or class name mangling (CSS Modules hashes class names,
  // so plain CSS selectors like '.modal p' would not match).
  getErrorModalMessage() {
    return this.page.getByTestId('error-modal-message');
  }
}
