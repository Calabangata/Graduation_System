/**
 * Reusable Stats Card Component
 * Displays a single statistic with value and label
 */
export default function StatsCard({ value, label }) {
  return (
    <div style={{
      padding: '24px',
      backgroundColor: '#fff',
      borderRadius: '8px',
      boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
      textAlign: 'center',
      border: '1px solid #f0f0f0'
    }}>
      <div style={{
        fontSize: '2rem',
        fontWeight: '700',
        color: '#2980b9',
        marginBottom: '8px'
      }}>
        {value}
      </div>
      <div style={{
        fontSize: '0.95rem',
        color: '#7f8c8d',
        fontWeight: '500'
      }}>
        {label}
      </div>
    </div>
  );
}
