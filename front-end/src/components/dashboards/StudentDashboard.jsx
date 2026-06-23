import { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFileAlt } from '@fortawesome/free-solid-svg-icons';
import styles from '../../styles/Dashboard.module.css';
import thesisService from '../../services/thesisService';
import ErrorModal from '../ErrorModal';
import { getErrorMessage } from '../../constants/errorMessages';

/**
 * Student Dashboard
 * Shows thesis application details and submission status.
 * Available to: STUDENT
 */
export default function StudentDashboard() {
  const [application, setApplication] = useState(null);
  const [hasApplication, setHasApplication] = useState(false);
  const [loading, setLoading] = useState(true);
  const [errorModal, setErrorModal] = useState({ open: false, message: '' });

  useEffect(() => {
    fetchApplication();
  }, []);

  const fetchApplication = async () => {
    try {
      setLoading(true);
      const data = await thesisService.getMyApplication();
      setApplication(data);
      setHasApplication(true);
    } catch (err) {
      if (err.response?.status === 404) {
        // No active application — not an error, just an empty state
        setHasApplication(false);
      } else {
        const code = err.response?.data?.errorCode || err.response?.status;
        setErrorModal({ open: true, message: getErrorMessage(code) });
      }
    } finally {
      setLoading(false);
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'APPROVED': return styles.statusApproved;
      case 'REJECTED': return styles.statusRejected;
      default: return styles.statusPending;
    }
  };

  return (
    <div className={styles.dashboard}>
      {/* Page Header */}
      <div className={styles.header}>
        <h1>Student Dashboard</h1>
        <p>Track your thesis application and defense progress.</p>
      </div>

      {/* Thesis Application Card */}
      <div className={styles.contentArea}>
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2>My Thesis Application</h2>
            {application && (
              <span className={`${styles.statusBadge} ${getStatusClass(application.approvalStatus)}`}>
                {application.approvalStatus}
              </span>
            )}
          </div>

          {loading && (
            <div className={styles.loadingState}>Loading application...</div>
          )}

          {!loading && !hasApplication && (
            <div className={styles.emptyState}>
              <FontAwesomeIcon icon={faFileAlt} className={styles.emptyStateIcon} />
              <p className={styles.emptyStateTitle}>No Active Application</p>
              <p className={styles.emptyStateText}>
                You do not have an active thesis application. Contact your supervisor to get started.
              </p>
            </div>
          )}

          {!loading && hasApplication && application && (
            <div className={styles.detailList}>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>Topic</span>
                <p className={styles.detailValue}>{application.topic}</p>
              </div>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>Purpose</span>
                <p className={styles.detailValue}>{application.purpose}</p>
              </div>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>Tasks</span>
                <p className={styles.detailValue}>{application.tasks}</p>
              </div>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>Tech Stack</span>
                <p className={styles.detailValue}>{application.techStack}</p>
              </div>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>Supervisor</span>
                <p className={styles.detailValue}>{application.supervisorName}</p>
              </div>
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>Department</span>
                <p className={styles.detailValue}>{application.departmentName}</p>
              </div>
            </div>
          )}
        </div>
      </div>

      <ErrorModal
        isOpen={errorModal.open}
        onClose={() => setErrorModal({ open: false, message: '' })}
        message={errorModal.message}
        title="Failed to Load Application"
      />
    </div>
  );
}

