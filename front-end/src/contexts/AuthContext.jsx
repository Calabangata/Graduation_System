import React, { createContext, useState, useEffect, useContext, useRef } from 'react';
import axios from 'axios';

const AuthContext = createContext();

const API_BASE = 'http://localhost:8080/api';

// Setup axios to include credentials (cookies)
axios.defaults.withCredentials = true;

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const initializationRef = useRef(false);
  const interceptorSetupRef = useRef(false);

  // Fetch current user info
  const fetchUserInfo = async () => {
    try {
      const response = await axios.get(`${API_BASE}/auth/me`);
      setUserInfo(response.data);
    } catch (error) {
      console.error('Failed to fetch user info:', error);
      setUserInfo(null);
    }
  };

  // On mount: try to refresh from httpOnly cookie (runs only once)
  useEffect(() => {
    // Prevent double execution in React StrictMode
    if (initializationRef.current) return;
    initializationRef.current = true;

    const initializeAuth = async () => {
      try {
        const response = await axios.post(`${API_BASE}/auth/refresh`);
        const accessToken = response.data.accessToken;
        // Set axios header IMMEDIATELY before state update
        axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
        // Now update state (this triggers re-renders after axios is ready)
        setToken(accessToken);
        // Fetch user info
        await fetchUserInfo();
      } catch (error) {
        // No valid refresh token or already expired
        setToken(null);
        setUserInfo(null);
      } finally {
        setLoading(false);
      }
    };
    initializeAuth();
  }, []);

  // Setup axios interceptor for auto-refresh on 401 (runs only once)
  useEffect(() => {
    // Prevent double setup in React StrictMode
    if (interceptorSetupRef.current) return;
    interceptorSetupRef.current = true;

    const interceptor = axios.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        // If 401 and not already retried
        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            // Try to refresh token using httpOnly cookie
            const response = await axios.post(`${API_BASE}/auth/refresh`);
            const newAccessToken = response.data.accessToken;
            // Set axios header IMMEDIATELY before state update
            axios.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
            setToken(newAccessToken);
            // Fetch user info
            await fetchUserInfo();
            
            // Retry original request with new token
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return axios(originalRequest);
          } catch (refreshError) {
            // Refresh failed - user must login again
            setToken(null);
            setUserInfo(null);
            return Promise.reject(refreshError);
          }
        }
        return Promise.reject(error);
      }
    );

    return () => axios.interceptors.response.eject(interceptor);
  }, []);

  // Update Authorization header when token changes
  useEffect(() => {
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common['Authorization'];
    }
  }, [token]);

  const login = async (email, password) => {
    const response = await axios.post(`${API_BASE}/auth/login`, {
      email,
      password
    });
    // refreshToken is automatically set as httpOnly cookie by backend
    const accessToken = response.data.accessToken;
    // Set axios header IMMEDIATELY before state update
    axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
    setToken(accessToken);
    // Fetch user info
    await fetchUserInfo();
  };

  const logout = async () => {
    try {
      await axios.post(`${API_BASE}/auth/logout`);
    } finally {
      setToken(null);
      setUserInfo(null);
    }
  };

  return (
    <AuthContext.Provider value={{ token, userInfo, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
