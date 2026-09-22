import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import type { AlunoDashboard } from '../types';
import {
  CalendarCheck2,
  Clock,
  ArrowRight,
  Trash2,
  ShieldCheck,
  CheckCircle2,
  AlertCircle,
  LogOut
} from 'lucide-react';

export const DashboardAluno: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [dashboard, setDashboard] = useState<AlunoDashboard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [cancelingId, setCancelingId] = useState<string | null>(null);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await api.get<AlunoDashboard>('/matriculas/dashboard');
      setDashboard(res.data);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar dados acadêmicos.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const handleCancelarMatricula = async (matriculaId: string, nomeDisc: string) => {
    if (!window.confirm(`Confirma a desistência da matrícula na disciplina "${nomeDisc}"?`)) {
      return;
    }

    try {
      setCancelingId(matriculaId);
      setError(null);
      setSuccessMsg(null);
      await api.delete(`/matriculas/${matriculaId}`);
      setSuccessMsg(`Matrícula na disciplina "${nomeDisc}" foi cancelada com sucesso.`);
      await fetchDashboard();
    } catch (err: any) {
      setError(err.message || 'Falha ao cancelar matrícula.');
    } finally {
      setCancelingId(null);
    }
  };

  const handleRelogin = () => {
    logout();
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="py-20 flex justify-center items-center">
        <div className="flex flex-col items-center gap-2.5">
          <div className="w-7 h-7 border-2 border-slate-900 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs text-slate-500 font-medium">Carregando painel acadêmico...</p>
        </div>
      </div>
    );
  }

  if (!dashboard) {
    return (
      <div className="bg-white border border-red-200 rounded p-5 flex items-center justify-between">
        <div className="flex items-center gap-2.5 text-xs text-red-700">
          <AlertCircle className="w-4 h-4 text-red-600 shrink-0" />
          <span>{error || 'Não foi possível carregar as informações do aluno.'}</span>
        </div>
        <button
          onClick={handleRelogin}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-red-50 hover:bg-red-100 text-red-800 rounded text-xs font-semibold border border-red-200 transition-colors"
        >
          <LogOut className="w-3.5 h-3.5" />
          <span>Entrar Novamente</span>
        </button>
      </div>
    );
  }

  const obrigatoriasProgresso = Math.min(
    100,
    (dashboard.totalObrigatorias / dashboard.maxObrigatorias) * 100
  );
  const optativasProgresso = Math.min(
    100,
    (dashboard.totalOptativas / dashboard.maxOptativas) * 100
  );

  return (
    <div className="space-y-6">
      {/* Alertas de Notificação */}
      {successMsg && (
        <div className="p-3.5 bg-emerald-50 border border-emerald-200 rounded flex items-center justify-between text-emerald-800 text-xs animate-fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
            <span>{successMsg}</span>
          </div>
          <button
            onClick={() => setSuccessMsg(null)}
            className="text-emerald-700 hover:text-emerald-900 font-bold ml-2 text-sm"
          >
            ×
          </button>
        </div>
      )}

      {error && (
        <div className="p-3.5 bg-red-50 border border-red-200 rounded flex items-center justify-between text-red-800 text-xs animate-fade-in">
          <div className="flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-red-600 shrink-0" />
            <span>{error}</span>
          </div>
          <button
            onClick={() => setError(null)}
            className="text-red-700 hover:text-red-900 font-bold ml-2 text-sm"
          >
            ×
          </button>
        </div>
      )}

      {/* Painel Superior Dividido (Banner Funcional) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
        {/* Lado Esquerdo: Boas-Vindas & Ação Principal */}
        <div className="lg:col-span-2 bg-white rounded-md border border-slate-200 p-6 shadow-2xs flex flex-col justify-between">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
                Portal do Aluno
              </span>
              <span className="text-slate-300">•</span>
              <span className="text-xs text-slate-500 font-mono">
                RA {dashboard.ra || user?.ra}
              </span>
            </div>
            <h1 className="text-xl font-bold text-slate-900">
              Olá, {dashboard.alunoNome || user?.nome}
            </h1>
            <p className="text-xs text-slate-500 mt-0.5">
              Curso de Bacharelado em Engenharia de Software • Semestre Letivo {dashboard.semestre}
            </p>

            <div className="mt-4 p-3 bg-slate-50 border border-slate-200/80 rounded text-xs text-slate-600 leading-relaxed">
              O período de matrícula para o semestre <strong className="text-slate-900">{dashboard.semestre}</strong> está aberto.
              Você pode selecionar até <strong className="text-slate-900">4 disciplinas obrigatórias</strong> e até{' '}
              <strong className="text-slate-900">2 optativas</strong>.
            </div>
          </div>

          <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
            <div className="flex items-center gap-2 text-xs text-slate-500">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span>Janela de Inscrição Ativa</span>
            </div>

            <Link
              to="/matricula"
              className="inline-flex items-center gap-2 px-4 py-2 bg-slate-900 hover:bg-slate-800 active:bg-slate-950 text-white rounded text-xs font-semibold shadow-xs transition-colors cursor-pointer"
            >
              <span>Montar Grade {dashboard.semestre}</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>

        {/* Lado Direito: Resumo Compacto de Disciplinas & Financeiro */}
        <div className="bg-white rounded-md border border-slate-200 p-5 shadow-2xs flex flex-col justify-between space-y-4">
          <div>
            <span className="text-xs font-bold text-slate-900 block pb-2 border-b border-slate-100">
              Resumo do Semestre
            </span>

            {/* Progresso Obrigatórias */}
            <div className="mt-3">
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-600 font-medium">Disciplinas Obrigatórias</span>
                <span className="font-semibold text-slate-900 font-mono">
                  {dashboard.totalObrigatorias}/{dashboard.maxObrigatorias}
                </span>
              </div>
              <div className="w-full bg-slate-100 rounded-xs h-1.5 overflow-hidden">
                <div
                  className="h-1.5 bg-blue-600 rounded-xs transition-all duration-300"
                  style={{ width: `${obrigatoriasProgresso}%` }}
                ></div>
              </div>
              <span className="text-[11px] text-slate-400 mt-1 block">
                {dashboard.maxObrigatorias - dashboard.totalObrigatorias > 0
                  ? `${dashboard.maxObrigatorias - dashboard.totalObrigatorias} vaga(s) disponível(is)`
                  : 'Limite atingido'}
              </span>
            </div>

            {/* Progresso Optativas */}
            <div className="mt-3.5">
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-600 font-medium">Disciplinas Eletivas / Optativas</span>
                <span className="font-semibold text-slate-900 font-mono">
                  {dashboard.totalOptativas}/{dashboard.maxOptativas}
                </span>
              </div>
              <div className="w-full bg-slate-100 rounded-xs h-1.5 overflow-hidden">
                <div
                  className="h-1.5 bg-indigo-600 rounded-xs transition-all duration-300"
                  style={{ width: `${optativasProgresso}%` }}
                ></div>
              </div>
              <span className="text-[11px] text-slate-400 mt-1 block">
                {dashboard.maxOptativas - dashboard.totalOptativas > 0
                  ? `${dashboard.maxOptativas - dashboard.totalOptativas} vaga(s) disponível(is)`
                  : 'Limite atingido'}
              </span>
            </div>
          </div>

          {/* Status Financeiro */}
          <div className="pt-3 border-t border-slate-100">
            <div className="flex items-center justify-between text-xs">
              <span className="text-slate-500">Situação Financeira</span>
              <span className="inline-flex items-center gap-1 font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded text-[11px]">
                <ShieldCheck className="w-3 h-3 text-emerald-600" />
                Regular
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Área Central: Grade Horária Provisória / Matrículas */}
      <div className="bg-white rounded-md border border-slate-200 shadow-2xs overflow-hidden">
        <div className="px-5 py-4 border-b border-slate-200 flex items-center justify-between">
          <div>
            <h2 className="text-sm font-bold text-slate-900">
              Grade Curricular — Semestre {dashboard.semestre}
            </h2>
            <p className="text-[11px] text-slate-500">
              Disciplinas confirmadas no seu plano de estudos semestral
            </p>
          </div>
          <span className="text-xs font-semibold px-2.5 py-1 bg-slate-100 text-slate-700 rounded font-mono">
            {dashboard.matriculas.length} disciplina(s)
          </span>
        </div>

        {dashboard.matriculas.length === 0 ? (
          /* Empty State Realista e Convidativo */
          <div className="p-8 text-center">
            <div className="max-w-md mx-auto space-y-3">
              <div className="w-10 h-10 rounded bg-slate-100 text-slate-500 flex items-center justify-center mx-auto">
                <CalendarCheck2 className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-semibold text-slate-900">
                  Sua grade de {dashboard.semestre} ainda não foi montada
                </h3>
                <p className="text-xs text-slate-500 mt-1">
                  Selecione suas turmas no catálogo semestral para reservar suas vagas e compor seu plano de estudos.
                </p>
              </div>
              <div className="pt-2">
                <Link
                  to="/matricula"
                  className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-semibold transition-colors"
                >
                  <span>Acessar Catálogo de Ofertas</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          </div>
        ) : (
          /* Tabela Corporativa Densa */
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="bg-slate-50/80 text-[10px] font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200">
                  <th className="py-2.5 px-5">Código</th>
                  <th className="py-2.5 px-5">Disciplina</th>
                  <th className="py-2.5 px-5">Docente</th>
                  <th className="py-2.5 px-5">Categoria</th>
                  <th className="py-2.5 px-5">Inscrição</th>
                  <th className="py-2.5 px-5 text-right">Ação</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {dashboard.matriculas.map((mat) => (
                  <tr key={mat.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3 px-5 font-mono font-bold text-slate-900">
                      {mat.codigoDisciplina}
                    </td>
                    <td className="py-3 px-5">
                      <span className="font-semibold text-slate-900 block">
                        {mat.nomeDisciplina}
                      </span>
                      <span className="text-[10px] text-slate-400">{mat.cursoNome}</span>
                    </td>
                    <td className="py-3 px-5 text-slate-600">
                      {mat.professorNome}
                    </td>
                    <td className="py-3 px-5">
                      {mat.tipo === 'OBRIGATORIA' ? (
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-semibold bg-blue-50 text-blue-700 border border-blue-200">
                          Obrigatória
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200">
                          Eletiva / Optativa
                        </span>
                      )}
                    </td>
                    <td className="py-3 px-5 text-slate-500">
                      <div className="flex items-center gap-1 font-mono text-[11px]">
                        <Clock className="w-3 h-3 text-slate-400" />
                        <span>{new Date(mat.dataHora).toLocaleDateString('pt-BR')}</span>
                      </div>
                    </td>
                    <td className="py-3 px-5 text-right">
                      <button
                        onClick={() => handleCancelarMatricula(mat.id, mat.nomeDisciplina)}
                        disabled={cancelingId === mat.id || !dashboard.periodoAberto}
                        className="inline-flex items-center gap-1 px-2 py-1 text-[11px] font-medium text-slate-600 hover:text-red-700 hover:bg-red-50 rounded border border-slate-200 hover:border-red-200 transition-colors disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                        title="Cancelar Matrícula"
                      >
                        {cancelingId === mat.id ? (
                          <div className="w-3 h-3 border border-red-600 border-t-transparent rounded-full animate-spin"></div>
                        ) : (
                          <Trash2 className="w-3 h-3" />
                        )}
                        <span>Desistir</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
