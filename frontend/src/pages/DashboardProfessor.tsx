import React, { useState, useEffect } from 'react';
import api from '../services/api';
import type { TurmaProfessor } from '../types';
import {
  BookOpen,
  Calendar,
  AlertCircle,
  ChevronDown,
  ChevronUp,
  UserCheck
} from 'lucide-react';

export const DashboardProfessor: React.FC = () => {
  const [turmas, setTurmas] = useState<TurmaProfessor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedTurmaId, setExpandedTurmaId] = useState<string | null>(null);

  const fetchTurmas = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await api.get<TurmaProfessor[]>('/professor/turmas');
      setTurmas(res.data);
      if (res.data.length > 0) {
        setExpandedTurmaId(res.data[0].ofertaId);
      }
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar turmas do professor.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTurmas();
  }, []);

  const toggleExpand = (ofertaId: string) => {
    setExpandedTurmaId((prev) => (prev === ofertaId ? null : ofertaId));
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-12 flex justify-center items-center">
        <div className="flex flex-col items-center gap-2">
          <div className="w-8 h-8 border-3 border-emerald-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs font-medium text-slate-500">Carregando turmas atribuídas...</p>
        </div>
      </div>
    );
  }

  const totalAlunosGeral = turmas.reduce((acc, t) => acc + t.totalInscritos, 0);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="pb-5 border-b border-slate-200">
        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-emerald-100 text-emerald-800 border border-emerald-200 uppercase tracking-wide">
          Portal do Docente
        </span>
        <h1 className="text-xl sm:text-2xl font-bold text-slate-900 mt-1.5">
          Turmas e Relação de Alunos Matriculados
        </h1>
        <p className="text-slate-500 text-xs mt-0.5">
          Semestre vigente: 2026.2
        </p>
      </div>

      {error && (
        <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded flex items-center gap-2 text-red-800 text-xs">
          <AlertCircle className="w-4 h-4 text-red-600 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mt-6">
        <div className="bg-white rounded-md p-4 border border-slate-200 shadow-xs">
          <span className="text-xs font-semibold text-slate-500">Disciplinas Lecionadas</span>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-bold text-slate-900">{turmas.length}</span>
            <span className="text-xs text-slate-400 font-medium">turmas</span>
          </div>
        </div>

        <div className="bg-white rounded-md p-4 border border-slate-200 shadow-xs">
          <span className="text-xs font-semibold text-slate-500">Total de Inscrições</span>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-bold text-emerald-700">{totalAlunosGeral}</span>
            <span className="text-xs text-slate-400 font-medium">alunos</span>
          </div>
        </div>

        <div className="bg-white rounded-md p-4 border border-slate-200 shadow-xs">
          <span className="text-xs font-semibold text-slate-500">Semestre</span>
          <div className="mt-2 flex items-center gap-1.5 text-base font-bold text-slate-900">
            <Calendar className="w-4 h-4 text-emerald-600" />
            <span>2026.2</span>
          </div>
        </div>
      </div>

      {/* Turmas */}
      <div className="mt-6 space-y-4">
        <h2 className="text-sm font-bold text-slate-900 flex items-center gap-1.5">
          <BookOpen className="w-4 h-4 text-emerald-600" />
          Turmas Atribuídas
        </h2>

        {turmas.length === 0 ? (
          <div className="bg-white rounded-md p-6 border border-slate-200 text-center text-xs text-slate-500">
            Nenhuma turma atribuída neste semestre.
          </div>
        ) : (
          turmas.map((turma) => {
            const isExpanded = expandedTurmaId === turma.ofertaId;
            const atingiuQuorum = turma.totalInscritos >= 3;

            return (
              <div
                key={turma.ofertaId}
                className="bg-white rounded-md border border-slate-200 shadow-xs overflow-hidden"
              >
                <div
                  onClick={() => toggleExpand(turma.ofertaId)}
                  className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 cursor-pointer hover:bg-slate-50 transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <span className="font-mono font-bold text-xs bg-emerald-50 text-emerald-800 border border-emerald-200 px-2.5 py-1 rounded">
                      {turma.codigoDisciplina}
                    </span>
                    <div>
                      <h3 className="text-sm font-bold text-slate-900">
                        {turma.nomeDisciplina}
                      </h3>
                      <p className="text-[11px] text-slate-500">{turma.cursoNome}</p>
                    </div>
                  </div>

                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <span className="text-xs font-semibold text-slate-700">
                        {turma.totalInscritos} inscrito(s)
                      </span>
                      <div className="mt-0.5">
                        {atingiuQuorum ? (
                          <span className="px-2 py-0.2 rounded text-[10px] font-bold bg-emerald-100 text-emerald-800">
                            Quórum Atingido (≥3)
                          </span>
                        ) : (
                          <span className="px-2 py-0.2 rounded text-[10px] font-bold bg-amber-100 text-amber-800">
                            Abaixo do Quórum (&lt;3)
                          </span>
                        )}
                      </div>
                    </div>

                    <button type="button" className="p-1 text-slate-400">
                      {isExpanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                    </button>
                  </div>
                </div>

                {isExpanded && (
                  <div className="border-t border-slate-100 bg-slate-50/60 p-4">
                    <h4 className="text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-2 flex items-center gap-1">
                      <UserCheck className="w-3.5 h-3.5 text-emerald-600" />
                      Lista de Alunos Matriculados
                    </h4>

                    {turma.alunos.length === 0 ? (
                      <p className="text-xs text-slate-500 italic">
                        Nenhum aluno matriculado nesta turma até o momento.
                      </p>
                    ) : (
                      <div className="overflow-x-auto bg-white rounded border border-slate-200">
                        <table className="w-full text-left border-collapse text-xs">
                          <thead>
                            <tr className="bg-slate-50 text-[10px] font-bold uppercase text-slate-500 border-b border-slate-200">
                              <th className="py-2 px-3">#</th>
                              <th className="py-2 px-3">Nome</th>
                              <th className="py-2 px-3">RA</th>
                              <th className="py-2 px-3">Login</th>
                              <th className="py-2 px-3">Modalidade</th>
                              <th className="py-2 px-3">Data</th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-slate-100">
                            {turma.alunos.map((aluno, index) => (
                              <tr key={aluno.matriculaId} className="hover:bg-slate-50">
                                <td className="py-2.5 px-3 text-slate-400 font-mono">
                                  {index + 1}
                                </td>
                                <td className="py-2.5 px-3 font-semibold text-slate-800">
                                  {aluno.nome}
                                </td>
                                <td className="py-2.5 px-3 font-mono text-slate-600">
                                  {aluno.ra || '-'}
                                </td>
                                <td className="py-2.5 px-3 font-mono text-slate-500">
                                  {aluno.login}
                                </td>
                                <td className="py-2.5 px-3">
                                  {aluno.tipo === 'OBRIGATORIA' ? (
                                    <span className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-blue-50 text-blue-700">
                                      Obrigatória
                                    </span>
                                  ) : (
                                    <span className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-indigo-50 text-indigo-700">
                                      Optativa
                                    </span>
                                  )}
                                </td>
                                <td className="py-2.5 px-3 text-slate-500 text-[11px]">
                                  {aluno.dataHora}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
