// ─── StudentDashboardPage.js ──────────────────────────────────────────────────
// Page Object for the Student Dashboard section of /home.
// Only knows about elements relevant to a student's view.
// ─────────────────────────────────────────────────────────────────────────────

export default class StudentDashboardPage {
  constructor(page) {
    this.page = page;

    // The main heading of the student dashboard
    this.heading = page.getByRole('heading', { name: 'Student Dashboard' });

    // The "My Thesis Application" card heading
    this.applicationCardHeading = page.getByRole('heading', { name: 'My Thesis Application' });

    // Empty state title shown when student has no active application
    // getByText() finds any element containing exactly this text
    this.emptyStateTitle = page.getByText('No Active Application');

    // Status badge — the pill that shows PENDING / APPROVED / REJECTED
    // We use a CSS class selector here because there is no semantic role for a badge
    this.statusBadge = page.locator('[class*="statusBadge"]');
  }

  // ── isLoaded ───────────────────────────────────────────────────────────────
  // Waits until the Student Dashboard heading is visible.
  // Used in step definitions to confirm navigation completed successfully.
  async isLoaded() {
    await this.heading.waitFor({ state: 'visible' });
  }

  // ── hasApplication ─────────────────────────────────────────────────────────
  // Returns true if the application detail list is visible (student has active thesis).
  async hasApplication() {
    // locator('[class*="detailList"]') matches any element whose class contains "detailList".
    // CSS Modules mangle the class name (e.g. "DetailList_detailList__xK3p"),
    // so we use the *= (contains) attribute selector instead of an exact name.
    return this.page.locator('[class*="detailList"]').isVisible();
  }

  // ── getDetailValue ─────────────────────────────────────────────────────────
  // Returns the Locator for a specific detail row's value by its label text.
  // e.g. getDetailValue('Topic') finds the <p> next to the "TOPIC" label.
  getDetailValue(labelText) {
    // We find the detailRow that contains a span with the given label text,
    // then get its sibling <p> (the value element).
    return this.page
      .locator('[class*="detailRow"]')
      .filter({ hasText: labelText })  // filter() keeps only rows that contain the label
      .locator('[class*="detailValue"]');
  }

  // ── getStatusBadgeText ─────────────────────────────────────────────────────
  // Returns the visible text of the status badge (e.g. "PENDING").
  async getStatusBadgeText() {
    return this.statusBadge.textContent();
  }
}
