/**
 * Reusable Student Table Component
 * Displays students in a formatted table
 */
export default function StudentTable({ students }) {
  if (students.length === 0) {
    return (
      <p style={{ color: '#95a5a6' }}>No students found.</p>
    );
  }

  return (
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
            <th style={{ 
              padding: '12px 8px', 
              textAlign: 'left', 
              fontWeight: '600', 
              color: '#2c3e50' 
            }}>
              First Name
            </th>
            <th style={{ 
              padding: '12px 8px', 
              textAlign: 'left', 
              fontWeight: '600', 
              color: '#2c3e50' 
            }}>
              Last Name
            </th>
            <th style={{ 
              padding: '12px 8px', 
              textAlign: 'left', 
              fontWeight: '600', 
              color: '#2c3e50' 
            }}>
              Email
            </th>
            <th style={{ 
              padding: '12px 8px', 
              textAlign: 'left', 
              fontWeight: '600', 
              color: '#2c3e50' 
            }}>
              Role
            </th>
          </tr>
        </thead>
        <tbody>
          {students.map((student, index) => (
            <tr key={index} style={{ borderBottom: '1px solid #f0f0f0' }}>
              <td style={{ 
                padding: '12px 8px', 
                color: '#2c3e50' 
              }}>
                {student.firstName}
              </td>
              <td style={{ 
                padding: '12px 8px', 
                color: '#2c3e50' 
              }}>
                {student.lastName}
              </td>
              <td style={{ 
                padding: '12px 8px', 
                color: '#2c3e50' 
              }}>
                {student.email}
              </td>
              <td style={{ 
                padding: '12px 8px', 
                color: '#2c3e50' 
              }}>
                {student.role}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
