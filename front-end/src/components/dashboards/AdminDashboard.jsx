import { useState, useEffect } from 'react';
import styles from '../../styles/Dashboard.module.css';
import studentService from '../../services/studentService';
import StatsCard from '../StatsCard';
import StudentTable from '../StudentTable';

/**
 * Admin Dashboard
 * Shows all students and comprehensive statistics
 * Available to: ADMIN, SUPER_ADMIN
 */
export default function AdminDashboard() {
  const [stats, setStats] = useState({
    totalStudents: 0,
    courses: 32, // Placeholder
    graduationRate: '85%', // Placeholder
    averageGPA: '3.2', // Placeholder
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

      // Fetch total student count
      const totalStudents = await studentService.getTotalStudentCount();
      setStats(prev => ({ ...prev, totalStudents }));

      // Fetch student list
      const studentList = await studentService.getStudentList();
      setStudents(studentList);
    } catch (err) {
      console.error('Error fetching admin dashboard data:', err);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.dashboard}>
        <div className={styles.header}>
          <h1>Admin Dashboard</h1>
        </div>
        <p style={{ textAlign: 'center', color: '#7f8c8d' }}>Loading...</p>
      </div>
    );
  }

  return (
    <div className={styles.dashboard}>
      {/* Page Header */}
      <div className={styles.header}>
        <h1>Admin Dashboard</h1>
        <p>System overview and student management.</p>
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
        <StatsCard value={stats.totalStudents.toLocaleString()} label="Total Students" />
        <StatsCard value={stats.courses} label="Courses" />
        <StatsCard value={stats.graduationRate} label="Graduation Rate" />
        <StatsCard value={stats.averageGPA} label="Average GPA" />
      </div>

      {/* Main Content Area */}
      <div className={styles.contentArea}>
        <div className={styles.card}>
          <h2>All Students</h2>
          <StudentTable students={students} />
        </div>
        <div className={styles.card}>
          <h2>Graduation Statistics</h2>
          <p style={{ color: '#95a5a6' }}>Analytics chart coming soon...</p>
        </div>
      </div>
    </div>
  );
}
