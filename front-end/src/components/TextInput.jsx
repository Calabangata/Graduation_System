import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTriangleExclamation } from '@fortawesome/free-solid-svg-icons';
import styles from '../styles/Login.module.css';

function TextInput({ label, type = 'text', value, onChange, error}){
    // Generate a unique id from the label
    const inputId = label.toLowerCase().replace(/\s+/g, '-');

    return (
       <div className={styles.inputWrapper}>
  <label htmlFor={inputId}>{label}</label>
  <div className={styles.inputWithIcon}>
    <input
      id={inputId}
      name={inputId}
      type={type}
      value={value}
      onChange={onChange}
      className={styles.input}
    />
    {error && (
      <FontAwesomeIcon
        icon={faTriangleExclamation}
        title={error}
        className={styles.errorIcon}
      />
    )}
  </div>
</div>
    );
}
export default TextInput;
// This component can be used in the Login component to handle text input fields.