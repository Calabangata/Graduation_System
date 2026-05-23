/**
 * Error Message Constants
 * Centralized error messages used throughout the application
 * Maps error codes/keys to user-friendly messages
 */

export const ERROR_MESSAGES = {
  // Authentication Errors
  INVALID_CREDENTIALS: 'Email or password is incorrect. Please try again.',
  INVALID_PASSWORD: 'Email or password is incorrect. Please try again.',
  USER_NOT_FOUND: 'No account found with this email address.',
  USER_INACTIVE: 'Your account is inactive. Please contact support.',
  UNAUTHORIZED: 'You do not have permission to perform this action.',
  SESSION_EXPIRED: 'Your session has expired. Please log in again.',
  
  // Validation Errors
  INVALID_EMAIL: 'Please enter a valid email address.',
  PASSWORD_REQUIRED: 'Password is required.',
  EMAIL_REQUIRED: 'Email is required.',
  WEAK_PASSWORD: 'Password does not meet security requirements.',
  
  // Network Errors
  NETWORK_ERROR: 'Network error occurred. Please check your connection and try again.',
  SERVER_ERROR: 'Something went wrong on the server. Please try again later.',
  REQUEST_TIMEOUT: 'Request timed out. Please try again.',
  
  // Data Errors
  DATA_NOT_FOUND: 'The requested data was not found.',
  DUPLICATE_ENTRY: 'This entry already exists.',
  
  // Generic Error
  UNKNOWN_ERROR: 'An unexpected error occurred. Please try again.',
};

/**
 * Get error message based on error code or HTTP status
 * @param {string|number} errorCode - Error code or HTTP status code
 * @returns {string} - User-friendly error message
 */
export const getErrorMessage = (errorCode) => {
  // If errorCode is a string key in ERROR_MESSAGES
  if (ERROR_MESSAGES[errorCode]) {
    return ERROR_MESSAGES[errorCode];
  }

  // Map HTTP status codes to error messages
  const statusCodeMap = {
    400: ERROR_MESSAGES.INVALID_CREDENTIALS,
    401: ERROR_MESSAGES.UNAUTHORIZED,
    403: ERROR_MESSAGES.UNAUTHORIZED,
    404: ERROR_MESSAGES.DATA_NOT_FOUND,
    409: ERROR_MESSAGES.DUPLICATE_ENTRY,
    500: ERROR_MESSAGES.SERVER_ERROR,
    503: ERROR_MESSAGES.SERVER_ERROR,
  };

  if (statusCodeMap[errorCode]) {
    return statusCodeMap[errorCode];
  }

  return ERROR_MESSAGES.UNKNOWN_ERROR;
};
