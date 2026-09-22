import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';
import type { AuthResponse, Usuario } from '../types';

interface AuthContextData {
  user: Usuario | null;
  token: string | null;
  loading: boolean;
  login: (login: string, senha: string) => Promise<AuthResponse>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<Usuario | null>(() => {
    const storedUser = localStorage.getItem('usuario');
    return storedUser ? JSON.parse(storedUser) : null;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
        try {
          const res = await api.get<Usuario>('/auth/me');
          setUser(res.data);
          localStorage.setItem('usuario', JSON.stringify(res.data));
        } catch {
          logout();
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (login: string, senha: string): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', { login, senha });
    const data = response.data;

    const loggedUser: Usuario = {
      id: data.id,
      nome: data.nome,
      login: data.login,
      perfil: data.perfil,
      ra: data.ra,
      departamento: data.departamento,
    };

    setToken(data.token);
    setUser(loggedUser);

    localStorage.setItem('token', data.token);
    localStorage.setItem('usuario', JSON.stringify(loggedUser));

    return data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        logout,
        isAuthenticated: !!user && !!token,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
