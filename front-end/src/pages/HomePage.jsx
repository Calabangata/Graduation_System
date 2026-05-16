import { useState, useEffect } from 'react';
import DashboardLayout from '../components/DashboardLayout';
import styles from '../styles/Dashboard.module.css';
import studentService from '../services/studentService';
import { useAuth } from '../contexts/AuthContext';

export default function HomePage() {
  const { token, loading: authLoading } = useAuth();
  
  const [stats, setStats] = useState({
    totalStudents: 0,
    courses: 32, // Placeholder
    graduationRate: '85%', // Placeholder
    averageGPA: '3.2', // Placeholder
  });

  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dataFetched, setDataFetched] = useState(false);

  // Only fetch data once when token is available and auth is done loading
  useEffect(() => {
    // Skip if already fetched or if conditions aren't met
    if (dataFetched || authLoading || !token) {
      return;
    }

    // Mark as fetched immediately to prevent re-runs
    setDataFetched(true);
    fetchDashboardData();
  }, [dataFetched, authLoading, token]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Ensure token is actually available before making requests
      if (!token) {
        setError('Authentication token not available');
        setLoading(false);
        return;
      }

      // Fetch total student count
      const totalStudents = await studentService.getTotalStudentCount();
      setStats(prev => ({ ...prev, totalStudents }));

      // Fetch student list
      const studentList = await studentService.getStudentList();
      setStudents(studentList);
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
      setError('Failed to load dashboard data');
      // Reset dataFetched so user can retry
      setDataFetched(false);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <DashboardLayout>
        <div className={styles.dashboard}>
          <div className={styles.header}>
            <h1>Dashboard</h1>
          </div>
          <p style={{ textAlign: 'center', color: '#7f8c8d' }}>Loading...</p>
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout>
      <div className={styles.dashboard}>
        {/* Page Header */}
        <div className={styles.header}>
          <h1>Dashboard</h1>
          <p>Welcome back! Here's an overview of your graduation system.</p>
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
          <div className={styles.statCard}>
            <div className={styles.statValue}>{stats.totalStudents.toLocaleString()}</div>
            <div className={styles.statLabel}>Total Students</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statValue}>{stats.courses}</div>
            <div className={styles.statLabel}>Courses</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statValue}>{stats.graduationRate}</div>
            <div className={styles.statLabel}>Graduation Rate</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statValue}>{stats.averageGPA}</div>
            <div className={styles.statLabel}>Average GPA</div>
          </div>
        </div>

        {/* Main Content Area */}
        <div className={styles.contentArea}>
          <div className={styles.card}>
            <h2>Student List</h2>
            {students.length === 0 ? (
              <p style={{ color: '#95a5a6' }}>No students found.</p>
            ) : (
              <div style={{ 
                overflowX: 'auto',
                marginTop: '16px'
              }}>
                <table style={{
                  width: '100%',
                  borderCollapse: 'collapse',
                  fontSize: '0.95rem'
                }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #e0e0e0' }}>
                      <th style={{ padding: '12px 8px', textAlign: 'left', fontWeight: '600', color: '#2c3e50' }}>First Name</th>
                      <th style={{ padding: '12px 8px', textAlign: 'left', fontWeight: '600', color: '#2c3e50' }}>Last Name</th>
                      <th style={{ padding: '12px 8px', textAlign: 'left', fontWeight: '600', color: '#2c3e50' }}>Email</th>
                      <th style={{ padding: '12px 8px', textAlign: 'left', fontWeight: '600', color: '#2c3e50' }}>Role</th>
                    </tr>
                  </thead>
                  <tbody>
                    {students.map((student, index) => (
                      <tr key={index} style={{ borderBottom: '1px solid #f0f0f0' }}>
                        <td style={{ padding: '12px 8px', color: '#2c3e50' }}>{student.firstName}</td>
                        <td style={{ padding: '12px 8px', color: '#2c3e50' }}>{student.lastName}</td>
                        <td style={{ padding: '12px 8px', color: '#2c3e50' }}>{student.email}</td>
                        <td style={{ padding: '12px 8px', color: '#2c3e50' }}>{student.role}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
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

