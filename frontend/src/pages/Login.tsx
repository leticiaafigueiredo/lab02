import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  GraduationCap,
  Lock,
  User,
  AlertCircle,
  ArrowRight
} from 'lucide-react';

export const Login: React.FC = () => {
  const [loginInput, setLoginInput] = useState('');
  const [senhaInput, setSenhaInput] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginInput.trim() || !senhaInput.trim()) {
      setError('Por favor, informe o login e a senha.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      const res = await login(loginInput.trim(), senhaInput.trim());

      if (res.perfil === 'ALUNO') {
        navigate('/dashboard/aluno');
      } else if (res.perfil === 'PROFESSOR') {
        navigate('/dashboard/professor');
      } else if (res.perfil === 'SECRETARIA') {
        navigate('/dashboard/secretaria');
      } else {
        navigate('/');
      }
    } catch (err: any) {
      setError(err.message || 'Erro ao efetuar login. Verifique suas credenciais.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const fillCredentials = (u: string, p: string) => {
    setLoginInput(u);
    setSenhaInput(p);
    setError(null);
  };

  return (
    <div className="min-h-screen bg-slate-900 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <div className="w-12 h-12 rounded-md bg-blue-600 flex items-center justify-center text-white shadow-sm">
            <GraduationCap className="w-7 h-7" />
          </div>
        </div>
        <h2 className="mt-4 text-center text-xl font-bold tracking-tight text-white">
          Sistema de Matrículas
        </h2>
        <p className="mt-1 text-center text-xs text-slate-400">
          PUC Minas — Engenharia de Software (Semestre 2026.2)
        </p>
      </div>

      <div className="mt-6 sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0">
        <div className="bg-slate-800 border border-slate-700 py-8 px-6 shadow-xl rounded-md sm:px-8">
          {error && (
            <div className="mb-5 bg-red-950/40 border border-red-800/60 p-3.5 rounded flex items-start gap-2.5 text-red-300 text-xs">
              <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5 text-red-400" />
              <span>{error}</span>
            </div>
          )}

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Usuário / Login
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <User className="w-4 h-4" />
                </div>
                <input
                  type="text"
                  value={loginInput}
                  onChange={(e) => setLoginInput(e.target.value)}
                  placeholder="aluno1, prof, secretaria"
                  className="block w-full pl-9 pr-3 py-2 bg-slate-900 border border-slate-700 rounded text-white placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-xs"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Senha
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type="password"
                  value={senhaInput}
                  onChange={(e) => setSenhaInput(e.target.value)}
                  placeholder="••••••••"
                  className="block w-full pl-9 pr-3 py-2 bg-slate-900 border border-slate-700 rounded text-white placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-xs"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full flex justify-center items-center gap-2 py-2.5 px-4 rounded text-xs font-semibold text-white bg-blue-600 hover:bg-blue-500 active:bg-blue-700 focus:outline-none transition-colors disabled:opacity-50 cursor-pointer"
            >
              {isSubmitting ? (
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              ) : (
                <>
                  <span>Entrar</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </>
              )}
            </button>
          </form>

          {/* Atalhos de Preenchimento para Teste */}
          <div className="mt-6 pt-5 border-t border-slate-700">
            <span className="text-[11px] font-semibold text-slate-400 block mb-2 uppercase tracking-wider">
              Contas de Demonstração:
            </span>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => fillCredentials('aluno1', '123')}
                className="px-2.5 py-1.5 bg-slate-900/80 hover:bg-slate-700 border border-slate-700 rounded text-left text-xs text-slate-200 transition-colors"
              >
                <span className="font-semibold block">Aluno (João)</span>
                <span className="text-[10px] text-slate-400">aluno1 / 123</span>
              </button>

              <button
                type="button"
                onClick={() => fillCredentials('aluno2', '123')}
                className="px-2.5 py-1.5 bg-slate-900/80 hover:bg-slate-700 border border-slate-700 rounded text-left text-xs text-slate-200 transition-colors"
              >
                <span className="font-semibold block">Aluno (Maria)</span>
                <span className="text-[10px] text-slate-400">aluno2 / 123</span>
              </button>

              <button
                type="button"
                onClick={() => fillCredentials('prof', '123')}
                className="px-2.5 py-1.5 bg-slate-900/80 hover:bg-slate-700 border border-slate-700 rounded text-left text-xs text-slate-200 transition-colors"
              >
                <span className="font-semibold block">Professor (Ana)</span>
                <span className="text-[10px] text-slate-400">prof / 123</span>
              </button>

              <button
                type="button"
                onClick={() => fillCredentials('secretaria', '123')}
                className="px-2.5 py-1.5 bg-slate-900/80 hover:bg-slate-700 border border-slate-700 rounded text-left text-xs text-slate-200 transition-colors"
              >
                <span className="font-semibold block">Secretaria</span>
                <span className="text-[10px] text-slate-400">secretaria / 123</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
