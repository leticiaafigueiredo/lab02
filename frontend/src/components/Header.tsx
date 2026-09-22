import React from 'react';
import { useLocation } from 'react-router-dom';
import { ChevronRight, Calendar } from 'lucide-react';

export const Header: React.FC = () => {
  const location = useLocation();

  const getBreadcrumbTitle = () => {
    if (location.pathname.includes('/matricula')) return 'Inscrição Semestral';
    if (location.pathname.includes('/dashboard/professor')) return 'Portal do Docente';
    if (location.pathname.includes('/dashboard/secretaria')) return 'Secretaria Geral';
    return 'Visão Geral do Aluno';
  };

  return (
    <header className="h-16 bg-white border-b border-slate-200 px-6 sm:px-8 flex items-center justify-between sticky top-0 z-20">
      {/* Breadcrumb Sóbrio */}
      <div className="flex items-center gap-2 text-xs text-slate-500 font-medium">
        <span>Portal Acadêmico</span>
        <ChevronRight className="w-3 h-3 text-slate-400" />
        <span className="text-slate-900 font-semibold">{getBreadcrumbTitle()}</span>
      </div>

      {/* Lado Direito: Status */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 px-3 py-1 bg-slate-50 border border-slate-200 rounded text-xs">
          <Calendar className="w-3.5 h-3.5 text-slate-500" />
          <span className="text-slate-600 font-medium">Semestre 2026.2</span>
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
          <span className="text-emerald-700 font-semibold">Matrículas Abertas</span>
        </div>
      </div>
    </header>
  );
};
