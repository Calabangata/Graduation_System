/**
 * Design System - Centralized theme tokens
 * Use these constants across all components for consistency
 */

export const colors = {
  // Primary
  primary: '#2980b9',
  primaryDark: '#1e5f8f',
  primaryLight: '#5dade2',

  // Semantic colors
  error: '#e74c3c',
  errorLight: '#ffebee',
  errorDark: '#c62828',
  success: '#27ae60',
  warning: '#f39c12',
  info: '#3498db',

  // Neutral
  white: '#ffffff',
  black: '#000000',
  text: '#2c3e50',
  textSecondary: '#7f8c8d',
  textMuted: '#95a5a6',
  border: '#e0e0e0',
  borderLight: '#f0f0f0',
  background: '#ecf0f1',
  backgroundLight: '#f8f9fa',
};

export const spacing = {
  xs: '4px',
  sm: '8px',
  md: '12px',
  lg: '16px',
  xl: '20px',
  xxl: '24px',
  xxxl: '32px',
};

export const typography = {
  h1: {
    fontSize: '2rem',
    fontWeight: '700',
    lineHeight: '1.2',
  },
  h2: {
    fontSize: '1.5rem',
    fontWeight: '700',
    lineHeight: '1.3',
  },
  h3: {
    fontSize: '1.25rem',
    fontWeight: '600',
    lineHeight: '1.4',
  },
  body: {
    fontSize: '1rem',
    fontWeight: '400',
    lineHeight: '1.6',
  },
  bodySmall: {
    fontSize: '0.95rem',
    fontWeight: '400',
    lineHeight: '1.5',
  },
  bodyXSmall: {
    fontSize: '0.875rem',
    fontWeight: '400',
    lineHeight: '1.4',
  },
  label: {
    fontSize: '0.95rem',
    fontWeight: '500',
    lineHeight: '1.4',
  },
};

export const borderRadius = {
  sm: '4px',
  md: '8px',
  lg: '12px',
  full: '9999px',
};

export const shadows = {
  sm: '0 2px 8px rgba(0, 0, 0, 0.08)',
  md: '0 4px 12px rgba(0, 0, 0, 0.12)',
  lg: '0 10px 40px rgba(0, 0, 0, 0.3)',
};

export const transitions = {
  fast: '150ms ease-in-out',
  normal: '200ms ease-in-out',
  slow: '300ms ease-in-out',
};
