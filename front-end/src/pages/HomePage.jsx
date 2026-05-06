import DashboardLayout from '../components/DashboardLayout';
import styles from '../styles/Dashboard.module.css';

export default function HomePage() {
  return (
    <DashboardLayout>
      <div className={styles.dashboard}>
        {/* Page Header */}
        <div className={styles.header}>
          <h1>Dashboard</h1>
          <p>Welcome back! Here's an overview of your graduation system.</p>
        </div>

        {/* Stats Cards (Placeholder) */}
        <div className={styles.statsGrid}>
          <div className={styles.statCard}>
            <div className={styles.statValue}>1,250</div>
            <div className={styles.statLabel}>Total Students</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statValue}>32</div>
            <div className={styles.statLabel}>Courses</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statValue}>85%</div>
            <div className={styles.statLabel}>Graduation Rate</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statValue}>3.2</div>
            <div className={styles.statLabel}>Average GPA</div>
          </div>
        </div>

        {/* Main Content Area (Placeholder) */}
        <div className={styles.contentArea}>
          <div className={styles.card}>
            <h2>Student List</h2>
            <p style={{ color: '#95a5a6' }}>Student list and data coming soon...</p>
          </div>
          <div className={styles.card}>
            <h2>Graduation Statistics</h2>
            <p style={{ color: '#95a5a6' }}>Analytics chart coming soon...</p>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}

