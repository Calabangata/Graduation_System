import { useState } from 'react';
import Sidebar from './Sidebar';
import styles from '../styles/DashboardLayout.module.css';

export default function DashboardLayout({ children }) {
  const [isCollapsed, setIsCollapsed] = useState(false);

  return (
    <div className={`${styles.container} ${isCollapsed ? styles.mainCollapsed : ''}`}>
      <Sidebar isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
      <main className={styles.main}>
        {children}
      </main>
    </div>
  );
}
