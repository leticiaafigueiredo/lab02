import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { Perfil } from '../types';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: Perfil[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, allowedRoles }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-sm text-slate-500 font-medium">Carregando dados da sessão...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.perfil)) {
    if (user.perfil === 'ALUNO') return <Navigate to="/dashboard/aluno" replace />;
    if (user.perfil === 'PROFESSOR') return <Navigate to="/dashboard/professor" replace />;
    if (user.perfil === 'SECRETARIA') return <Navigate to="/dashboard/secretaria" replace />;
  }

  return <>{children}</>;
};
