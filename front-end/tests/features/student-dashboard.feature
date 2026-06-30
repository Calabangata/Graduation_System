Feature: Student Dashboard
  Tests for the Student Dashboard page — verifies both the
  empty state (no application) and the application detail view.

  Scenario: Student with no active application sees the empty state
    Given I am logged in as a student
    When I view the student dashboard
    Then I should see the "No Active Application" message

  # This scenario requires student1@example.com to have an active thesis application.
  # To enable: log in as teacher1@example.com, assign them a department via the admin UI,
  # then submit a thesis application for student1. After that, remove the @skip tag.
  @skip
  Scenario: Student with an active application sees its details
    Given I am logged in as a student with an active thesis application
    When I view the student dashboard
    Then I should see the thesis application details
    And the approval status badge should be visible
