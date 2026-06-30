// defineConfig and devices come from Playwright's test runner
// defineConfig gives us autocomplete and type checking for configuration options
// devices is a dictionary of real-world browser/device presets (e.g. Desktop Chrome, iPhone 14)
import { defineConfig, devices } from '@playwright/test';

// defineBddConfig is a playwright-bdd helper that scans your feature files and step
// definitions, then tells Playwright where the generated test files will go.
// Think of it as the "glue" that bridges Gherkin with Playwright's test runner.
import { defineBddConfig } from 'playwright-bdd';

// defineBddConfig returns a path to a temp folder where playwright-bdd writes
// auto-generated test files from your .feature files.
// We store it in testDir and pass it to defineConfig below.
const testDir = defineBddConfig({
  // Where to find your .feature files (** means "any subdirectory")
  features: 'tests/features/**/*.feature',
  // Where to find your step definition files
  steps: 'tests/steps/**/*.js',
  // Where playwright-bdd will write the generated test files (do not edit these)
  outputDir: 'tests/.generated',
});

export default defineConfig({
  // Tell Playwright to look for tests in the generated folder
  testDir,

  // Maximum time one test can run before it is marked as failed
  timeout: 30_000,

  // fullyParallel: run tests inside a file in parallel (false = run them in order)
  // Keeping false for now so tests don't interfere with each other when sharing login state
  fullyParallel: false,

  // How many times to retry a failed test (0 = no retries)
  retries: 0,

  use: {
    // The base URL of the running app. Playwright will prepend this to page.goto('/login')
    // so you only write relative paths in your tests.
    // The frontend Docker container maps internal port 5173 → host port 3000
    baseURL: 'http://localhost:3000',

    // Send cookies with every request (needed for the httpOnly refresh token cookie)
    // This is equivalent to withCredentials: true in axios
    bypassCSP: true,

    // Take a screenshot automatically when a test fails
    screenshot: 'only-on-failure',

    // Record a video of the test when it fails (useful for debugging)
    video: 'retain-on-failure',

    // Record a Playwright trace on failure — this lets you replay the test
    // step-by-step in the Playwright Trace Viewer tool
    trace: 'retain-on-failure',
  },

  // Projects = which browsers to run tests against.
  // We start with just Chromium (Chrome without UI).
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
