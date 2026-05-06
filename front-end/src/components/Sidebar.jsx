import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChartLine,
  faUsers,
  faBook,
  faGraduationCap,
  faCog,
  faSignOut,
  faChevronLeft,
  faChevronRight,
} from '@fortawesome/free-solid-svg-icons';
import styles from '../styles/Sidebar.module.css';

export default function Sidebar({ isCollapsed, setIsCollapsed }) {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const [isMobileOpen, setIsMobileOpen] = useState(true); // Mobile sidebar visibility

  const navItems = [
    { icon: faChartLine, label: 'Dashboard', path: '/dashboard' },
    { icon: faUsers, label: 'Students', path: '/students' },
    { icon: faBook, label: 'Courses', path: '/courses' },
    { icon: faGraduationCap, label: 'Grades', path: '/grades' },
  ];

  const handleNavigation = (path) => {
    navigate(path);
    // On mobile, close sidebar after navigation
    if (window.innerWidth < 768) {
      setIsMobileOpen(false);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/', { replace: true });
    } catch (error) {
      console.error('Logout failed:', error);
      alert('Logout failed. Try again.');
    }
  };

  const toggleCollapse = () => {
    setIsCollapsed(!isCollapsed);
  };

  return (
    <>
      {/* Mobile toggle button */}
      <button
        className={styles.mobileToggle}
        onClick={() => setIsMobileOpen(!isMobileOpen)}
        aria-label="Toggle sidebar"
      >
        ☰
      </button>

      {/* Sidebar */}
      <aside
        className={`${styles.sidebar} ${isMobileOpen ? styles.open : styles.closed} ${
          isCollapsed ? styles.collapsed : ''
        }`}
      >
        {/* Logo/Brand */}
        <div className={styles.logo}>
          <FontAwesomeIcon icon={faGraduationCap} />
          <span>Graduation</span>
        </div>

        {/* Navigation Items */}
        <nav className={styles.nav}>
          {navItems.map((item) => (
            <button
              key={item.path}
              className={styles.navItem}
              onClick={() => handleNavigation(item.path)}
              title={isCollapsed ? item.label : ''}
            >
              <FontAwesomeIcon icon={item.icon} className={styles.icon} />
              <span className={styles.label}>{item.label}</span>
            </button>
          ))}
        </nav>

        {/* Spacer */}
        <div className={styles.spacer}></div>

        {/* Settings & Logout */}
        <div className={styles.footer}>
          <button
            className={styles.navItem}
            onClick={() => handleNavigation('/settings')}
            title={isCollapsed ? 'Settings' : ''}
          >
            <FontAwesomeIcon icon={faCog} className={styles.icon} />
            <span className={styles.label}>Settings</span>
          </button>

          <button
            className={styles.logoutBtn}
            onClick={handleLogout}
            title={isCollapsed ? 'Logout' : ''}
          >
            <FontAwesomeIcon icon={faSignOut} className={styles.icon} />
            <span className={styles.label}>Logout</span>
          </button>

          {/* User Profile (placeholder) */}
          <div className={styles.userProfile}>
            <div className={styles.avatar}>JD</div>
            <span className={styles.userName}>John Doe</span>
          </div>
        </div>
      </aside>

      {/* Collapse Toggle Button */}
      <button
        className={`${styles.collapseBtn} ${isCollapsed ? styles.expandMode : ''}`}
        onClick={toggleCollapse}
        aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        title={isCollapsed ? 'Expand' : 'Collapse'}
      >
        <FontAwesomeIcon icon={isCollapsed ? faChevronRight : faChevronLeft} />
      </button>

      {/* Mobile overlay */}
      {isMobileOpen && (
        <div className={styles.overlay} onClick={() => setIsMobileOpen(false)} />
      )}
    </>
  );
}
