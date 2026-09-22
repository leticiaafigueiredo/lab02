import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { AppLayout } from './components/AppLayout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Login } from './pages/Login';
import { DashboardAluno } from './pages/DashboardAluno';
import { Matricula } from './pages/Matricula';
import { DashboardProfessor } from './pages/DashboardProfessor';
import { DashboardSecretaria } from './pages/DashboardSecretaria';

const RootRedirect: React.FC = () => {
  const { user, loading } = useAuth();

  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;

  if (user.perfil === 'ALUNO') return <Navigate to="/dashboard/aluno" replace />;
  if (user.perfil === 'PROFESSOR') return <Navigate to="/dashboard/professor" replace />;
  if (user.perfil === 'SECRETARIA') return <Navigate to="/dashboard/secretaria" replace />;

  return <Navigate to="/login" replace />;
};

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <RootRedirect />
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard"
            element={
              <ProtectedRoute allowedRoles={['ALUNO']}>
                <AppLayout>
                  <DashboardAluno />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/aluno"
            element={
              <ProtectedRoute allowedRoles={['ALUNO']}>
                <AppLayout>
                  <DashboardAluno />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/matricula"
            element={
              <ProtectedRoute allowedRoles={['ALUNO']}>
                <AppLayout>
                  <Matricula />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/professor"
            element={
              <ProtectedRoute allowedRoles={['PROFESSOR']}>
                <AppLayout>
                  <DashboardProfessor />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/secretaria"
            element={
              <ProtectedRoute allowedRoles={['SECRETARIA']}>
                <AppLayout>
                  <DashboardSecretaria />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
