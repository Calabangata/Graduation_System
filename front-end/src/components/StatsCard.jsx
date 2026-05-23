import styles from '../styles/StatsCard.module.css';

/**
 * Reusable Stats Card Component
 * Displays a single statistic with value and label
 */
export default function StatsCard({ value, label }) {
  return (
    <div className={styles.card}>
      <div className={styles.value}>{value}</div>
      <p className={styles.label}>{label}</p>
    </div>
  );
}
