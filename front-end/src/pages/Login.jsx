import { useState } from "react";
import TextInput from "../components/TextInput";
import styles from '../styles/Login.module.css';
import axios from 'axios';

function Login() {
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
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email,
      password
    });

  // TODO: call the backend here
    console.log('Login success:', response.data);
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
            <h2>Login to graduation system</h2>
            <form onSubmit={handleSubmit}>
                <TextInput label="Email" value={email} onChange={(e) => setEmail(e.target.value)} error={errors.email}/>
                <TextInput label="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} error={errors.password}/>
                <button type="submit">Login</button>
            </form>
        </div>
        </div>
    );


}
export default Login;