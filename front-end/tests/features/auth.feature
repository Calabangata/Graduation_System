Feature: Authentication
  Tests covering the login page — error messages for bad credentials
  and successful redirects per role.

  Scenario: Login with an unknown email shows the correct error
    Given I am on the login page
    When I enter email "nobody@doesnotexist.com" and password "AnyPassword1!"
    And I click the login button
    Then I should see an error modal with message "No account found with this email address."

  Scenario: Login with a wrong password shows the correct error
    Given I am on the login page
    When I enter the student email and wrong password
    And I click the login button
    Then I should see an error modal with message "Email or password is incorrect. Please try again."

  Scenario: Successful student login redirects to the Student Dashboard
    Given I am on the login page
    When I log in as a student
    Then I should see the "Student Dashboard" heading
