import { useState, useEffect } from 'react';
import Sidebar from './Sidebar';
import styles from '../styles/DashboardLayout.module.css';

export default function DashboardLayout({ children }) {
  // Initialize from localStorage, default to false (expanded)
  const [isCollapsed, setIsCollapsed] = useState(() => {
    const saved = localStorage.getItem('sidebarCollapsed');
    return saved ? JSON.parse(saved) : false;
  });

  // Persist to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem('sidebarCollapsed', JSON.stringify(isCollapsed));
  }, [isCollapsed]);

  return (
    <div className={`${styles.container} ${isCollapsed ? styles.mainCollapsed : ''}`}>
      <Sidebar isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
      <main className={styles.main}>
        {children}
      </main>
    </div>
  );
}
