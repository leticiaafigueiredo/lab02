import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  GraduationCap,
  LayoutDashboard,
  CalendarCheck2,
  LogOut,
  Users,
  ShieldAlert
} from 'lucide-react';

export const Sidebar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map((n) => n[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  };

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col justify-between shrink-0 h-screen sticky top-0 border-r border-slate-800 select-none z-30">
      {/* Topo / Marca Institucional */}
      <div>
        <div className="h-16 flex items-center px-5 gap-3 border-b border-slate-800/80">
          <div className="w-8 h-8 rounded bg-blue-600 flex items-center justify-center text-white font-bold shadow-xs">
            <GraduationCap className="w-5 h-5" />
          </div>
          <div className="leading-tight">
            <span className="text-[10px] font-bold tracking-widest text-slate-400 uppercase block">
              PUC Minas
            </span>
            <span className="text-sm font-bold text-white tracking-tight">
              Portal Acadêmico
            </span>
          </div>
        </div>

        {/* Menu de Navegação Vertical */}
        <div className="px-3 py-4 space-y-1">
          <span className="px-3 text-[10px] font-bold uppercase tracking-wider text-slate-500 block mb-2">
            Navegação
          </span>

          {user.perfil === 'ALUNO' && (
            <>
              <NavLink
                to="/dashboard/aluno"
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3 py-2.5 rounded text-xs font-medium transition-all ${
                    isActive
                      ? 'bg-blue-600 text-white font-semibold shadow-xs'
                      : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/70'
                  }`
                }
              >
                <LayoutDashboard className="w-4 h-4 shrink-0" />
                <span>Visão Geral</span>
              </NavLink>

              <NavLink
                to="/matricula"
                className={({ isActive }) =>
                  `flex items-center justify-between px-3 py-2.5 rounded text-xs font-medium transition-all ${
                    isActive
                      ? 'bg-blue-600 text-white font-semibold shadow-xs'
                      : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/70'
                  }`
                }
              >
                <div className="flex items-center gap-3">
                  <CalendarCheck2 className="w-4 h-4 shrink-0" />
                  <span>Matrícula Semestral</span>
                </div>
                <span className="text-[10px] font-semibold px-1.5 py-0.5 rounded-xs bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                  Aberto
                </span>
              </NavLink>
            </>
          )}

          {user.perfil === 'PROFESSOR' && (
            <NavLink
              to="/dashboard/professor"
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded text-xs font-medium transition-all ${
                  isActive
                    ? 'bg-emerald-600 text-white font-semibold shadow-xs'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/70'
                }`
              }
            >
              <Users className="w-4 h-4 shrink-0" />
              <span>Minhas Turmas</span>
            </NavLink>
          )}

          {user.perfil === 'SECRETARIA' && (
            <NavLink
              to="/dashboard/secretaria"
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded text-xs font-medium transition-all ${
                  isActive
                    ? 'bg-purple-600 text-white font-semibold shadow-xs'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/70'
                }`
              }
            >
              <ShieldAlert className="w-4 h-4 shrink-0" />
              <span>Controle de Períodos</span>
            </NavLink>
          )}
        </div>
      </div>

      {/* Rodapé / Perfil do Usuário */}
      <div className="p-3 border-t border-slate-800/80 bg-slate-950/40">
        <div className="flex items-center justify-between p-2 rounded bg-slate-900 border border-slate-800">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-8 h-8 rounded bg-slate-800 border border-slate-700 flex items-center justify-center text-xs font-bold text-white shrink-0">
              {getInitials(user.nome)}
            </div>
            <div className="min-w-0">
              <span className="text-xs font-semibold text-white block truncate leading-tight">
                {user.nome}
              </span>
              <span className="text-[10px] text-slate-400 block truncate font-mono">
                {user.ra ? `RA ${user.ra}` : user.departamento || user.perfil}
              </span>
            </div>
          </div>

          <button
            onClick={handleLogout}
            className="p-1.5 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded transition-colors shrink-0 cursor-pointer"
            title="Sair do sistema"
          >
            <LogOut className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </aside>
  );
};
