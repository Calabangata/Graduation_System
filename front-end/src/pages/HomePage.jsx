import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import DashboardLayout from '../components/DashboardLayout';
import AdminDashboard from '../components/dashboards/AdminDashboard';
import TeacherDashboard from '../components/dashboards/TeacherDashboard';
import StudentDashboard from '../components/dashboards/StudentDashboard';
import { useAuth } from '../contexts/AuthContext';

/**
 * HomePage - Main Dashboard Router
 * Routes to role-specific dashboards based on user type
 * - ADMIN/SUPER_ADMIN → AdminDashboard (all students)
 * - TEACHER → TeacherDashboard (supervised students)
 * - STUDENT → StudentDashboard (thesis applications)
 */
export default function HomePage() {
  const { userInfo, loading: authLoading } = useAuth();
  const navigate = useNavigate();

  // Show loading state while fetching user info
  if (authLoading || !userInfo) {
    return (
      <DashboardLayout>
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: '400px',
          color: '#7f8c8d',
          fontSize: '1.1rem'
        }}>
          Loading dashboard...
        </div>
      </DashboardLayout>
    );
  }

  // Render appropriate dashboard based on user role
  const renderDashboard = () => {
    const role = userInfo.role;

    if (role === 'ADMIN' || role === 'SUPER_ADMIN') {
      return <AdminDashboard />;
    } else if (role === 'TEACHER') {
      return <TeacherDashboard />;
    } else if (role === 'STUDENT') {
      return <StudentDashboard />;
    } else {
      // Unknown role - show error
      return (
        <div style={{ padding: '20px' }}>
          <h2>Unknown User Role</h2>
          <p>Your role '{role}' is not recognized. Please contact support.</p>
        </div>
      );
    }
  };

  return (
    <DashboardLayout>
      {renderDashboard()}
    </DashboardLayout>
  );
}

