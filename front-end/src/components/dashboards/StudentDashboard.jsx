import styles from '../../styles/Dashboard.module.css';

/**
 * Student Dashboard
 * Shows thesis applications and submission status
 * Available to: STUDENT
 */
export default function StudentDashboard() {
  return (
    <div className={styles.dashboard}>
      {/* Page Header */}
      <div className={styles.header}>
        <h1>Student Dashboard</h1>
        <p>Track your thesis application and defense progress.</p>
      </div>

      {/* Quick Stats */}
      <div className={styles.statsGrid}>
        <div style={{
          padding: '24px',
          backgroundColor: '#fff',
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
          textAlign: 'center',
          border: '1px solid #f0f0f0'
        }}>
          <div style={{
            fontSize: '2rem',
            fontWeight: '700',
            color: '#2980b9',
            marginBottom: '8px'
          }}>
            —
          </div>
          <div style={{
            fontSize: '0.95rem',
            color: '#7f8c8d',
            fontWeight: '500'
          }}>
            My Applications
          </div>
        </div>
        <div style={{
          padding: '24px',
          backgroundColor: '#fff',
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
          textAlign: 'center',
          border: '1px solid #f0f0f0'
        }}>
          <div style={{
            fontSize: '2rem',
            fontWeight: '700',
            color: '#27ae60',
            marginBottom: '8px'
          }}>
            —
          </div>
          <div style={{
            fontSize: '0.95rem',
            color: '#7f8c8d',
            fontWeight: '500'
          }}>
            Application Status
          </div>
        </div>
        <div style={{
          padding: '24px',
          backgroundColor: '#fff',
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
          textAlign: 'center',
          border: '1px solid #f0f0f0'
        }}>
          <div style={{
            fontSize: '2rem',
            fontWeight: '700',
            color: '#f39c12',
            marginBottom: '8px'
          }}>
            —
          </div>
          <div style={{
            fontSize: '0.95rem',
            color: '#7f8c8d',
            fontWeight: '500'
          }}>
            Defense Date
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      <div className={styles.contentArea}>
        <div className={styles.card}>
          <h2>Thesis Applications</h2>
          <p style={{ color: '#95a5a6', marginBottom: '16px' }}>
            Your thesis applications will appear here. This feature is coming soon.
          </p>
          <div style={{
            padding: '16px',
            backgroundColor: '#f8f9fa',
            borderRadius: '4px',
            borderLeft: '4px solid #3498db',
            color: '#555'
          }}>
            <strong>Next Steps:</strong> Submit your thesis application through the portal to get started.
          </div>
        </div>

        <div className={styles.card}>
          <h2>Documents</h2>
          <p style={{ color: '#95a5a6' }}>Your uploaded documents and supporting materials will appear here.</p>
        </div>
      </div>
    </div>
  );
}
