import { useState } from "react";
import TextInput from "../components/TextInput";
import ErrorModal from "../components/ErrorModal";
import styles from '../styles/Login.module.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGraduationCap } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext';
import { getErrorMessage } from '../constants/errorMessages';

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({
    email: '',
    password: '',
  });
  const [errorModal, setErrorModal] = useState({
    isOpen: false,
    message: '',
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Reset previous errors
    setErrors({ email: '', password: '' });

    const newErrors = {};
    if (!email) newErrors.email = 'Email is required';
    if (!password) newErrors.password = 'Password is required';

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }
    
    try {
      // Call login function with email and password
      // Backend will automatically set httpOnly refresh token cookie
      await login(email, password);
      navigate('/home', { replace: true }); // Redirect to home page after successful login
    } catch (error) {
      console.error('Login failed:', error);
      
      // Determine error message based on response data or status code
      let errorMessage;
      
      // Check if backend returned an errorCode in response body
      if (error.response?.data?.errorCode) {
        errorMessage = getErrorMessage(error.response.data.errorCode);
      } else if (error.response?.status === 401) {
        errorMessage = getErrorMessage('INVALID_CREDENTIALS');
      } else if (error.response?.status) {
        errorMessage = getErrorMessage(error.response.status);
      } else {
        errorMessage = getErrorMessage('NETWORK_ERROR');
      }

      // Show error modal
      setErrorModal({
        isOpen: true,
        message: errorMessage,
      });
    }
  };

  const handleCloseErrorModal = () => {
    setErrorModal({
      isOpen: false,
      message: '',
    });
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.container}>
        <h2>
          <FontAwesomeIcon icon={faGraduationCap} style={{ marginRight: '0.5rem' }} />
          Login to graduation system
        </h2>
        <form onSubmit={handleSubmit}>
          <TextInput 
            label="Email" 
            value={email} 
            onChange={(e) => setEmail(e.target.value)} 
            error={errors.email}
          />
          <TextInput 
            label="Password" 
            type="password" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)} 
            error={errors.password}
          />
          <button type="submit">Login</button>
        </form>
      </div>

      {/* Error Modal */}
      <ErrorModal
        isOpen={errorModal.isOpen}
        onClose={handleCloseErrorModal}
        message={errorModal.message}
        title="Login Failed"
      />
    </div>
  );


}
export default Login;