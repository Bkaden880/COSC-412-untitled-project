import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

const API_BASE = 'http://localhost:8081/api/auth';

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Load user from localStorage on mount (persists session)
  useEffect(() => {
    try {
      const stored = localStorage.getItem('auth_user');
      if (stored) {
        setUser(JSON.parse(stored));
      }
    } catch (e) {
      console.error('Failed to load auth user', e);
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    // Validate inputs
    if (!email || !password) {
      throw new Error('Email and password required');
    }

    try {
      // POST to backend /api/auth/login
      const res = await fetch(`${API_BASE}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        const errData = await res.json();
        throw new Error(errData.message || 'Login failed');
      }

      const data = await res.json();
      // Expect response: { user: { id, email, name }, token? }
      const userData = {
        id: data.user.id,
        email: data.user.email,
        name: data.user.name,
        token: data.token || null, // store JWT if provided
      };

      setUser(userData);
      localStorage.setItem('auth_user', JSON.stringify(userData));
      return userData;
    } catch (err) {
      throw err;
    }
  };

  const signup = async (name, email, password) => {
    // Validate inputs
    if (!name || !email || !password) {
      throw new Error('Name, email, and password required');
    }

    try {
      // POST to backend /api/auth/signup
      const res = await fetch(`${API_BASE}/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password }),
      });

      if (!res.ok) {
        const errData = await res.json();
        throw new Error(errData.message || 'Signup failed');
      }

      const data = await res.json();
      // Expect response: { user: { id, email, name }, token? }
      const userData = {
        id: data.user.id,
        email: data.user.email,
        name: data.user.name,
        token: data.token || null,
      };

      setUser(userData);
      localStorage.setItem('auth_user', JSON.stringify(userData));
      return userData;
    } catch (err) {
      throw err;
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('auth_user');
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
