import { useState } from "react";
import TextInput from "../components/TextInput";
import styles from '../styles/Login.module.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGraduationCap } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext';

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({
    email: '',
    password: '',
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
      console.log('Login success');
    } catch (error) {
      console.error('Login failed:', error);
      if (error.response?.status === 401) {
        alert('Invalid credentials.');
      } else {
        alert('Something went wrong. Try again.');
      }
    }
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
    </div>
  );


}
export default Login;