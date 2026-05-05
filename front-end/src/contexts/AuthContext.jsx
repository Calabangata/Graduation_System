import React, { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios';

const AuthContext = createContext();

const API_BASE = 'http://localhost:8080/api';

// Setup axios to include credentials (cookies)
axios.defaults.withCredentials = true;

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // On mount: try to refresh from httpOnly cookie
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const response = await axios.post(`${API_BASE}/auth/refresh`);
        setToken(response.data.accessToken);
      } catch (error) {
        // No valid refresh token or already expired
        setToken(null);
      } finally {
        setLoading(false);
      }
    };
    initializeAuth();
  }, []);

  // Setup axios interceptor for auto-refresh on 401
  useEffect(() => {
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
            setToken(newAccessToken);
            
            // Retry original request with new token
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return axios(originalRequest);
          } catch (refreshError) {
            // Refresh failed - user must login again
            setToken(null);
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
    setToken(response.data.accessToken);
  };

  const logout = async () => {
    try {
      await axios.post(`${API_BASE}/auth/logout`);
    } finally {
      setToken(null);
    }
  };

  return (
    <AuthContext.Provider value={{ token, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
