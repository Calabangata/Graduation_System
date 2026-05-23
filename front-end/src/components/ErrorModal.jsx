import { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExclamationCircle, faTimes } from '@fortawesome/free-solid-svg-icons';
import styles from '../styles/ErrorModal.module.css';

/**
 * Reusable Error Modal Component
 * Displays error messages in a modal popup
 * 
 * Props:
 * - isOpen: boolean - Whether modal is visible
 * - onClose: function - Called when modal is dismissed
 * - message: string - Error message to display
 * - title: string (optional) - Modal title, defaults to "Error"
 * - autoCloseSeconds: number (optional) - Auto close after X seconds, disabled if null
 */
export default function ErrorModal({ 
  isOpen, 
  onClose, 
  message, 
  title = 'Error',
  autoCloseSeconds = null 
}) {
  const [displayMessage, setDisplayMessage] = useState(message);

  // Update message when prop changes
  useEffect(() => {
    setDisplayMessage(message);
  }, [message]);

  // Auto-close functionality
  useEffect(() => {
    if (!isOpen || !autoCloseSeconds) return;

    const timer = setTimeout(() => {
      onClose();
    }, autoCloseSeconds * 1000);

    return () => clearTimeout(timer);
  }, [isOpen, autoCloseSeconds, onClose]);

  if (!isOpen) return null;

  return (
    <>
      {/* Backdrop */}
      <div className={styles.backdrop} onClick={onClose} />

      {/* Modal */}
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className={styles.header}>
          <div className={styles.headerContent}>
            <FontAwesomeIcon
              icon={faExclamationCircle}
              className={styles.headerIcon}
            />
            <h2 className={styles.headerTitle}>{title}</h2>
          </div>
        </div>

        {/* Content */}
        <div className={styles.content}>
          <p className={styles.message}>{displayMessage}</p>
        </div>

        {/* Footer */}
        <div className={styles.footer}>
          <button onClick={onClose} className={styles.okButton}>
            OK
          </button>
        </div>
      </div>
    </>
  );
}
