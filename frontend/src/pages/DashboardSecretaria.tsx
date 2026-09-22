import React, { useState, useEffect } from 'react';
import api from '../services/api';
import type { Periodo } from '../types';
import {
  Calendar,
  ToggleLeft,
  ToggleRight,
  AlertTriangle,
  CheckCircle2,
  Check
} from 'lucide-react';

export const DashboardSecretaria: React.FC = () => {
  const [resumo, setResumo] = useState<any>(null);
  const [periodo, setPeriodo] = useState<Periodo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  const loadSecretariaData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [resumoRes, periodoRes] = await Promise.all([
        api.get('/secretaria/resumo'),
        api.get<Periodo>('/periodo/atual'),
      ]);
      setResumo(resumoRes.data);
      setPeriodo(periodoRes.data);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar dados da secretaria.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSecretariaData();
  }, []);

  const handleTogglePeriodo = async () => {
    try {
      setActionLoading(true);
      setError(null);
      setSuccessMsg(null);
      const res = await api.post<Periodo>(`/periodo/toggle?semestre=${periodo?.semestre || '2026.2'}`);
      setPeriodo(res.data);
      setSuccessMsg(
        `Período ${res.data.semestre} agora está ${res.data.aberto ? 'ABERTO' : 'FECHADO'} para matrículas.`
      );
      await loadSecretariaData();
    } catch (err: any) {
      setError(err.message || 'Erro ao alterar status do período.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleEncerrarComQuorum = async () => {
    if (
      !window.confirm(
        'Deseja encerrar o período de matrículas e aplicar a Regra de Quórum? Turmas com < 3 alunos serão canceladas e com >= 3 ficarão ativas.'
      )
    ) {
      return;
    }

    try {
      setActionLoading(true);
      setError(null);
      setSuccessMsg(null);
      await api.post(`/secretaria/encerrar-periodo?semestre=${periodo?.semestre || '2026.2'}`);
      setSuccessMsg(
        'Período encerrado com sucesso. Regra de quórum (mínimo 3 alunos) aplicada.'
      );
      await loadSecretariaData();
    } catch (err: any) {
      setError(err.message || 'Erro ao processar encerramento com quórum.');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-12 flex justify-center items-center">
        <div className="flex flex-col items-center gap-2">
          <div className="w-8 h-8 border-3 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs font-medium text-slate-500">Carregando painel da secretaria...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="pb-5 border-b border-slate-200">
        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-purple-100 text-purple-800 border border-purple-200 uppercase tracking-wide">
          Secretaria Acadêmica
        </span>
        <h1 className="text-xl sm:text-2xl font-bold text-slate-900 mt-1.5">
          Gestão de Períodos e Matrículas Semestrais
        </h1>
        <p className="text-slate-500 text-xs mt-0.5">
          Controle de abertura/encerramento de matrículas e quórum de turmas.
        </p>
      </div>

      {successMsg && (
        <div className="mt-4 p-3 bg-emerald-50 border border-emerald-200 rounded flex items-center justify-between text-emerald-800 text-xs">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
            <span>{successMsg}</span>
          </div>
          <button onClick={() => setSuccessMsg(null)} className="text-emerald-700 font-bold ml-2">
            ×
          </button>
        </div>
      )}

      {error && (
        <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded flex items-center justify-between text-red-800 text-xs">
          <div className="flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 text-red-600 flex-shrink-0" />
            <span>{error}</span>
          </div>
          <button onClick={() => setError(null)} className="text-red-700 font-bold ml-2">
            ×
          </button>
        </div>
      )}

      {/* Controle de Período */}
      <div className="mt-6 bg-slate-900 rounded-md p-5 text-white shadow-md">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          <div>
            <div className="flex items-center gap-1.5 text-slate-400 text-xs font-semibold uppercase tracking-wider">
              <Calendar className="w-3.5 h-3.5" />
              <span>Controle do Semestre</span>
            </div>
            <h2 className="text-lg font-bold mt-1">
              Semestre Vigente: {periodo?.semestre || '2026.2'}
            </h2>
            <p className="text-slate-400 text-xs mt-0.5">
              Estado atual: {periodo?.aberto ? 'Aberto (Inscrições e cancelamentos permitidos)' : 'Fechado (Inscrições bloqueadas)'}
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <button
              onClick={handleTogglePeriodo}
              disabled={actionLoading}
              className={`inline-flex items-center gap-1.5 px-3.5 py-2 rounded font-bold text-xs transition-colors cursor-pointer ${
                periodo?.aberto
                  ? 'bg-amber-500 hover:bg-amber-600 text-slate-900'
                  : 'bg-emerald-600 hover:bg-emerald-700 text-white'
              }`}
            >
              {periodo?.aberto ? (
                <>
                  <ToggleRight className="w-4 h-4" />
                  <span>Fechar Período</span>
                </>
              ) : (
                <>
                  <ToggleLeft className="w-4 h-4" />
                  <span>Abrir Período</span>
                </>
              )}
            </button>

            <button
              onClick={handleEncerrarComQuorum}
              disabled={actionLoading}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-slate-800 hover:bg-slate-700 border border-slate-700 rounded font-semibold text-xs text-slate-200 transition-colors cursor-pointer"
            >
              <Check className="w-4 h-4 text-emerald-400" />
              <span>Encerrar e Aplicar Quórum (≥ 3)</span>
            </button>
          </div>
        </div>
      </div>

      {/* Métricas */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-6">
        <div className="bg-white rounded-md p-4 border border-slate-200 shadow-xs">
          <span className="text-xs font-semibold text-slate-500">Total de Ofertas</span>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-bold text-slate-900">{resumo?.totalOfertas || 0}</span>
            <span className="text-xs text-slate-400 font-medium">turmas</span>
          </div>
        </div>

        <div className="bg-white rounded-md p-4 border border-slate-200 shadow-xs">
          <span className="text-xs font-semibold text-slate-500">Disciplinas Cadastradas</span>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-bold text-purple-700">{resumo?.totalDisciplinas || 0}</span>
            <span className="text-xs text-slate-400 font-medium">disciplinas</span>
          </div>
        </div>

        <div className="bg-white rounded-md p-4 border border-slate-200 shadow-xs">
          <span className="text-xs font-semibold text-slate-500">Matrículas Confirmadas</span>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-bold text-blue-700">{resumo?.totalMatriculas || 0}</span>
            <span className="text-xs text-slate-400 font-medium">inscrições</span>
          </div>
        </div>

        <div className="bg-white rounded-md p-4 border border-slate-200 shadow-xs">
          <span className="text-xs font-semibold text-slate-500">Usuários Cadastrados</span>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-bold text-slate-900">{resumo?.totalUsuarios || 0}</span>
            <span className="text-xs text-slate-400 font-medium">contas</span>
          </div>
        </div>
      </div>
    </div>
  );
};
