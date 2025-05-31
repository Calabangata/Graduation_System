import { useState } from "react";
import TextInput from "../components/TextInput";
import styles from '../styles/Login.module.css';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errors, setErrors] = useState({
  username: '',
  password: '',
});

const handleSubmit = (e) => {
  e.preventDefault();

  // Reset previous errors
  setErrors({ username: '', password: '' });

  const newErrors = {};
  if (!username) newErrors.username = 'Username is required';
  if (!password) newErrors.password = 'Password is required';

  if (Object.keys(newErrors).length > 0) {
    setErrors(newErrors);
    return;
  }

  // TODO: call the backend here
  console.log({ username, password });
};

    return (
        <div className={styles.wrapper}>
        <div className={styles.container}>
            <h2>Login to graduation system</h2>
            <form onSubmit={handleSubmit}>
                <TextInput label="Username" value={username} onChange={(e) => setUsername(e.target.value)} error={errors.username}/>
                <TextInput label="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} error={errors.password}/>
                <button type="submit">Login</button>
            </form>
        </div>
        </div>
    );


}
export default Login;