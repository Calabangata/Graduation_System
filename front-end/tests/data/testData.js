// Centralised test data — single place to change credentials or expected messages.
// Using environment variables (process.env) for credentials so they are never
// hardcoded in source control.  If the env var is not set, the fallback value
// after || is used (handy for local development).
//
// In Java you would read these with System.getenv("VAR_NAME").
// In Node.js you read them with process.env.VAR_NAME.

export const USERS = {
  // Credentials match the seeded users created by UsersSeeder.java
  student: {
    email:    process.env.TEST_STUDENT_EMAIL    || 'student1@example.com',
    password: process.env.TEST_STUDENT_PASSWORD || '12345',
  },
  teacher: {
    email:    process.env.TEST_TEACHER_EMAIL    || 'teacher1@example.com',
    password: process.env.TEST_TEACHER_PASSWORD || '12345',
  },
  unknownEmail:  'nobody@doesnotexist.com',
  wrongPassword: 'WrongPassword999!',
};

// Expected UI text values — keeps assertions consistent and easy to update
export const EXPECTED = {
  dashboardTitles: {
    student: 'Student Dashboard',
    teacher: 'Teacher Dashboard',
    admin:   'Admin Dashboard',
  },
  errorMessages: {
    userNotFound:    'No account found with this email address.',
    invalidPassword: 'Email or password is incorrect. Please try again.',
  },
  student: {
    noApplicationTitle: 'No Active Application',
  },
};
