import styles from '../styles/StudentTable.module.css';

/**
 * Reusable Student Table Component
 * Displays students in a formatted table
 */
export default function StudentTable({ students }) {
  if (students.length === 0) {
    return (
      <p className={styles.emptyState}>No students found.</p>
    );
  }

  return (
    <div className={styles.container}>
      <table className={styles.table}>
        <thead className={styles.thead}>
          <tr>
            <th className={styles.th}>First Name</th>
            <th className={styles.th}>Last Name</th>
            <th className={styles.th}>Email</th>
            <th className={styles.th}>Role</th>
          </tr>
        </thead>
        <tbody>
          {students.map((student, index) => (
            <tr key={index} className={styles.tr}>
              <td className={styles.td}>{student.firstName}</td>
              <td className={styles.td}>{student.lastName}</td>
              <td className={styles.td}>{student.email}</td>
              <td className={styles.td}>{student.role}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
