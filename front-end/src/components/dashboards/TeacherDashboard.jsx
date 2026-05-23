import { useState, useEffect } from 'react';
import styles from '../../styles/Dashboard.module.css';
import studentService from '../../services/studentService';
import StatsCard from '../StatsCard';
import StudentTable from '../StudentTable';

/**
 * Teacher Dashboard
 * Shows only supervised students and relevant statistics
 * Available to: TEACHER
 */
export default function TeacherDashboard() {
  const [stats, setStats] = useState({
    supervisedStudents: 0,
    activeTheses: 0, // Placeholder
    avgDefenseScore: '0', // Placeholder
  });

  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Fetch supervised student count
      const supervisedCount = await studentService.getTotalStudentCount();
      setStats(prev => ({ ...prev, supervisedStudents: supervisedCount }));

      // Fetch supervised student list
      const studentList = await studentService.getStudentList();
      setStudents(studentList);
    } catch (err) {
      console.error('Error fetching teacher dashboard data:', err);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.dashboard}>
        <div className={styles.header}>
          <h1>Teacher Dashboard</h1>
        </div>
        <p style={{ textAlign: 'center', color: '#7f8c8d' }}>Loading...</p>
      </div>
    );
  }

  return (
    <div className={styles.dashboard}>
      {/* Page Header */}
      <div className={styles.header}>
        <h1>Teacher Dashboard</h1>
        <p>Monitor your supervised students and their progress.</p>
      </div>

      {error && (
        <div style={{ 
          padding: '12px 16px', 
          backgroundColor: '#ffebee', 
          color: '#c62828', 
          borderRadius: '4px',
          marginBottom: '20px'
        }}>
          {error}
        </div>
      )}

      {/* Stats Cards */}
      <div className={styles.statsGrid}>
        <StatsCard value={stats.supervisedStudents.toLocaleString()} label="Supervised Students" />
        <StatsCard value={stats.activeTheses} label="Active Theses" />
        <StatsCard value={stats.avgDefenseScore} label="Avg Defense Score" />
      </div>

      {/* Main Content Area */}
      <div className={styles.contentArea}>
        <div className={styles.card}>
          <h2>My Students</h2>
          <StudentTable students={students} />
        </div>
        <div className={styles.card}>
          <h2>Recent Activity</h2>
          <p style={{ color: '#95a5a6' }}>Activity log coming soon...</p>
        </div>
      </div>
    </div>
  );
}
