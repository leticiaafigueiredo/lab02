import React, { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import type { AlunoDashboard, OfertaDisponivel, TipoMatricula } from '../types';
import {
  Search,
  XCircle,
  GraduationCap,
  ArrowLeft,
  Trash2,
  Check,
  ShieldAlert,
  Info,
  Layers,
  BookOpen,
  CheckCheck
} from 'lucide-react';

export const Matricula: React.FC = () => {
  const [dashboard, setDashboard] = useState<AlunoDashboard | null>(null);
  const [ofertas, setOfertas] = useState<OfertaDisponivel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [toastMsg, setToastMsg] = useState<string | null>(null);

  // Filtros e busca
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFilter, setSelectedFilter] = useState<'TODAS' | 'VAGAS' | 'OBRIGATORIAS' | 'OPTATIVAS'>('TODAS');

  // Estado do Modal de Inscrição
  const [selectedOferta, setSelectedOferta] = useState<OfertaDisponivel | null>(null);
  const [selectedTipo, setSelectedTipo] = useState<TipoMatricula>('OBRIGATORIA');
  const [submitting, setSubmitting] = useState(false);
  const [cancelingId, setCancelingId] = useState<string | null>(null);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [dashRes, ofertasRes] = await Promise.all([
        api.get<AlunoDashboard>('/matriculas/dashboard'),
        api.get<OfertaDisponivel[]>('/ofertas/disponiveis'),
      ]);
      setDashboard(dashRes.data);
      setOfertas(ofertasRes.data);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar catálogo de ofertas.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openMatriculaModal = (oferta: OfertaDisponivel) => {
    const obrigatoriasFull = dashboard && dashboard.totalObrigatorias >= dashboard.maxObrigatorias;
    const optativasFull = dashboard && dashboard.totalOptativas >= dashboard.maxOptativas;

    if (obrigatoriasFull && !optativasFull) {
      setSelectedTipo('OPTATIVA');
    } else {
      setSelectedTipo('OBRIGATORIA');
    }

    setSelectedOferta(oferta);
    setError(null);
  };

  const handleConfirmarMatricula = async () => {
    if (!selectedOferta) return;

    try {
      setSubmitting(true);
      setError(null);

      await api.post('/matriculas', {
        ofertaId: selectedOferta.id,
        tipo: selectedTipo,
      });

      setToastMsg(`Matrícula em "${selectedOferta.nomeDisciplina}" realizada com sucesso!`);
      setSelectedOferta(null);
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Falha ao realizar matrícula.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancelarMatricula = async (matriculaId: string, nomeDisciplina: string) => {
    if (!window.confirm(`Confirma a desistência da matrícula na disciplina "${nomeDisciplina}"?`)) {
      return;
    }

    try {
      setCancelingId(matriculaId);
      setError(null);

      await api.delete(`/matriculas/${matriculaId}`);
      setToastMsg(`Matrícula em "${nomeDisciplina}" cancelada.`);
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Erro ao cancelar matrícula.');
    } finally {
      setCancelingId(null);
    }
  };

  // Filtragem em tempo real
  const filteredOfertas = useMemo(() => {
    return ofertas.filter((oferta) => {
      const matchSearch =
        oferta.nomeDisciplina.toLowerCase().includes(searchTerm.toLowerCase()) ||
        oferta.codigoDisciplina.toLowerCase().includes(searchTerm.toLowerCase()) ||
        oferta.professorNome.toLowerCase().includes(searchTerm.toLowerCase());

      if (!matchSearch) return false;

      if (selectedFilter === 'VAGAS') {
        return oferta.vagasRestantes > 0 && !oferta.jaMatriculado;
      }
      if (selectedFilter === 'OBRIGATORIAS') {
        return !oferta.jaMatriculado || oferta.tipoMatricula === 'OBRIGATORIA';
      }
      if (selectedFilter === 'OPTATIVAS') {
        return !oferta.jaMatriculado || oferta.tipoMatricula === 'OPTATIVA';
      }

      return true;
    });
  }, [ofertas, searchTerm, selectedFilter]);

  if (loading) {
    return (
      <div className="py-24 flex justify-center items-center">
        <div className="flex flex-col items-center gap-2.5">
          <div className="w-7 h-7 border-2 border-slate-900 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs text-slate-500 font-medium">Carregando catálogo semestral...</p>
        </div>
      </div>
    );
  }

  const obrigatoriasCheias = dashboard ? dashboard.totalObrigatorias >= dashboard.maxObrigatorias : false;
  const optativasCheias = dashboard ? dashboard.totalOptativas >= dashboard.maxOptativas : false;
  const limiteGeralAtingido = obrigatoriasCheias && optativasCheias;
  const periodoAberto = dashboard ? dashboard.periodoAberto : true;

  const matriculasObrigatorias = dashboard?.matriculas.filter((m) => m.tipo === 'OBRIGATORIA') || [];
  const matriculasOptativas = dashboard?.matriculas.filter((m) => m.tipo === 'OPTATIVA') || [];

  return (
    <div className="space-y-6">
      {/* Toast Notificação Flutuante no Topo */}
      {toastMsg && (
        <div className="fixed top-5 right-5 z-50 bg-slate-900 text-white px-4 py-3 rounded-md shadow-xl flex items-center gap-3 text-xs animate-fade-in border border-slate-800">
          <CheckCheck className="w-4 h-4 text-emerald-400 shrink-0" />
          <span>{toastMsg}</span>
          <button
            onClick={() => setToastMsg(null)}
            className="text-slate-400 hover:text-white font-bold ml-2 text-sm"
          >
            ×
          </button>
        </div>
      )}

      {/* Navegação de Retorno */}
      <div className="flex items-center justify-between">
        <Link
          to="/dashboard/aluno"
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-900 transition-colors"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Voltar ao Meu Dashboard</span>
        </Link>
      </div>

      {/* Header Institucional & Barra de Status Discreta */}
      <div className="bg-white rounded-md border border-slate-200 p-5 shadow-2xs">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <h1 className="text-xl font-bold text-slate-900">
              Matrícula Semestral — {dashboard?.semestre || '2026.2'}
            </h1>
            <p className="text-slate-500 text-xs mt-0.5">
              Selecione suas disciplinas obrigatórias e optativas para compor seu plano de estudos.
            </p>
          </div>

          {/* Status Pills */}
          <div className="flex flex-wrap items-center gap-2">
            <div className="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded text-xs flex items-center gap-2">
              <span className="text-slate-500">Obrigatórias:</span>
              <span className={`font-semibold font-mono ${obrigatoriasCheias ? 'text-amber-600' : 'text-slate-900'}`}>
                {dashboard?.totalObrigatorias || 0} de {dashboard?.maxObrigatorias || 4} preenchidas
              </span>
            </div>

            <div className="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded text-xs flex items-center gap-2">
              <span className="text-slate-500">Eletivas:</span>
              <span className={`font-semibold font-mono ${optativasCheias ? 'text-amber-600' : 'text-slate-900'}`}>
                {dashboard?.totalOptativas || 0} de {dashboard?.maxOptativas || 2} preenchidas
              </span>
            </div>

            <div className="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded text-xs flex items-center gap-1.5">
              <span className={`w-2 h-2 rounded-full ${periodoAberto ? 'bg-emerald-500' : 'bg-red-500'}`}></span>
              <span className={`font-semibold ${periodoAberto ? 'text-emerald-700' : 'text-red-700'}`}>
                {periodoAberto ? 'Período regular: Aberto' : 'Período fechado'}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Alerta de Período Fechado */}
      {!periodoAberto && (
        <div className="p-3.5 bg-red-50 border border-red-200 rounded flex items-center gap-2 text-red-800 text-xs">
          <ShieldAlert className="w-4 h-4 text-red-600 shrink-0" />
          <span>O período de matrículas está encerrado pela Secretaria Acadêmica. Alterações bloqueadas.</span>
        </div>
      )}

      {error && (
        <div className="p-3.5 bg-red-50 border border-red-200 rounded flex items-center justify-between text-red-800 text-xs">
          <div className="flex items-center gap-2">
            <XCircle className="w-4 h-4 text-red-600 shrink-0" />
            <span>{error}</span>
          </div>
          <button onClick={() => setError(null)} className="text-red-700 font-bold ml-2">
            ×
          </button>
        </div>
      )}

      {/* Layout Dividido (Split Layout): Catálogo na Esquerda (68%) e Resumo na Direita (32%) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Coluna Principal: Catálogo e Ofertas Semestrais (68% - 8 cols) */}
        <div className="lg:col-span-8 space-y-4">
          {/* Barra de Controle Superior (Busca + Filtros em Tabs) */}
          <div className="bg-white rounded-md border border-slate-200 p-4 shadow-2xs flex flex-col sm:flex-row items-center justify-between gap-3">
            {/* Campo de Busca em Tempo Real */}
            <div className="relative w-full sm:w-72">
              <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Buscar por disciplina, código ou docente..."
                className="w-full pl-8 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-slate-900 focus:bg-white transition-all"
              />
            </div>

            {/* Filtros Rápidos em Pills */}
            <div className="flex items-center gap-1 w-full sm:w-auto overflow-x-auto pb-1 sm:pb-0">
              <button
                type="button"
                onClick={() => setSelectedFilter('TODAS')}
                className={`px-2.5 py-1 rounded-sm text-[11px] font-medium transition-colors shrink-0 ${
                  selectedFilter === 'TODAS'
                    ? 'bg-slate-900 text-white font-semibold'
                    : 'text-slate-600 hover:bg-slate-100'
                }`}
              >
                Todas ({ofertas.length})
              </button>

              <button
                type="button"
                onClick={() => setSelectedFilter('VAGAS')}
                className={`px-2.5 py-1 rounded-sm text-[11px] font-medium transition-colors shrink-0 ${
                  selectedFilter === 'VAGAS'
                    ? 'bg-slate-900 text-white font-semibold'
                    : 'text-slate-600 hover:bg-slate-100'
                }`}
              >
                Com Vagas
              </button>

              <button
                type="button"
                onClick={() => setSelectedFilter('OBRIGATORIAS')}
                className={`px-2.5 py-1 rounded-sm text-[11px] font-medium transition-colors shrink-0 ${
                  selectedFilter === 'OBRIGATORIAS'
                    ? 'bg-slate-900 text-white font-semibold'
                    : 'text-slate-600 hover:bg-slate-100'
                }`}
              >
                Obrigatórias
              </button>

              <button
                type="button"
                onClick={() => setSelectedFilter('OPTATIVAS')}
                className={`px-2.5 py-1 rounded-sm text-[11px] font-medium transition-colors shrink-0 ${
                  selectedFilter === 'OPTATIVAS'
                    ? 'bg-slate-900 text-white font-semibold'
                    : 'text-slate-600 hover:bg-slate-100'
                }`}
              >
                Eletivas
              </button>
            </div>
          </div>

          {/* Tabela Estruturada de Ofertas */}
          <div className="bg-white rounded-md border border-slate-200 shadow-2xs overflow-hidden">
            <div className="px-5 py-3.5 border-b border-slate-200 flex items-center justify-between bg-slate-50/50">
              <span className="text-xs font-bold text-slate-800">
                Turmas Ofertadas ({filteredOfertas.length})
              </span>
              <span className="text-[11px] text-slate-500">
                Semestre {dashboard?.semestre}
              </span>
            </div>

            {filteredOfertas.length === 0 ? (
              <div className="py-12 text-center text-xs text-slate-500">
                Nenhuma disciplina encontrada com os filtros selecionados.
              </div>
            ) : (
              <div className="divide-y divide-slate-100">
                {filteredOfertas.map((oferta) => {
                  const lotada = oferta.vagasOcupadas >= oferta.maxVagas || oferta.status === 'VAGAS_ENCERRADAS';
                  const percVagas = (oferta.vagasOcupadas / oferta.maxVagas) * 100;

                  return (
                    <div
                      key={oferta.id}
                      className={`p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4 transition-colors ${
                        oferta.jaMatriculado
                          ? 'bg-blue-50/30'
                          : lotada
                          ? 'bg-slate-50/70 opacity-75'
                          : 'hover:bg-slate-50/60'
                      }`}
                    >
                      {/* Dados da Disciplina */}
                      <div className="flex items-start gap-3 min-w-0">
                        <span className="font-mono text-xs font-bold px-2 py-1 bg-slate-100 text-slate-800 rounded border border-slate-200 shrink-0 mt-0.5">
                          {oferta.codigoDisciplina}
                        </span>

                        <div className="min-w-0">
                          <div className="flex items-center gap-2 flex-wrap">
                            <h3 className="text-xs font-bold text-slate-900 leading-tight">
                              {oferta.nomeDisciplina}
                            </h3>

                            {oferta.jaMatriculado && (
                              <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-blue-100 text-blue-800 border border-blue-200">
                                <Check className="w-3 h-3 text-blue-600" />
                                {oferta.tipoMatricula === 'OBRIGATORIA' ? 'Inscrito (Obrigatória)' : 'Inscrito (Eletiva)'}
                              </span>
                            )}

                            {lotada && !oferta.jaMatriculado && (
                              <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-red-100 text-red-800 border border-red-200">
                                Turma Esgotada
                              </span>
                            )}
                          </div>

                          <div className="flex items-center gap-3 text-[11px] text-slate-500 mt-1 flex-wrap">
                            <span className="flex items-center gap-1">
                              <GraduationCap className="w-3 h-3 text-slate-400" />
                              Docente: <strong className="text-slate-700 font-medium">{oferta.professorNome}</strong>
                            </span>
                            <span>•</span>
                            <span>{oferta.cursoNome}</span>
                          </div>

                          {/* Barra de Ocupação Compacta */}
                          <div className="flex items-center gap-3 mt-2">
                            <div className="w-32 bg-slate-200 rounded-xs h-1.5 overflow-hidden">
                              <div
                                className={`h-1.5 rounded-xs transition-all duration-300 ${
                                  lotada ? 'bg-red-500' : percVagas > 80 ? 'bg-amber-500' : 'bg-slate-900'
                                }`}
                                style={{ width: `${Math.min(100, percVagas)}%` }}
                              ></div>
                            </div>
                            <span className="text-[10px] font-mono text-slate-500">
                              {oferta.vagasOcupadas}/{oferta.maxVagas} vagas ({oferta.vagasRestantes} livres)
                            </span>
                          </div>
                        </div>
                      </div>

                      {/* Botão de Ação */}
                      <div className="shrink-0 flex items-center justify-end sm:justify-center">
                        {oferta.jaMatriculado && oferta.matriculaId ? (
                          <button
                            onClick={() => handleCancelarMatricula(oferta.matriculaId!, oferta.nomeDisciplina)}
                            disabled={cancelingId === oferta.matriculaId || !periodoAberto}
                            className="px-3 py-1.5 text-xs font-semibold text-slate-600 hover:text-red-700 hover:bg-red-50 rounded border border-slate-200 hover:border-red-200 transition-colors disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                          >
                            {cancelingId === oferta.matriculaId ? 'Desistindo...' : 'Desistir'}
                          </button>
                        ) : (
                          <button
                            onClick={() => openMatriculaModal(oferta)}
                            disabled={lotada || limiteGeralAtingido || !periodoAberto}
                            className="px-3 py-1.5 text-xs font-semibold text-white bg-slate-900 hover:bg-slate-800 active:bg-slate-950 rounded transition-colors disabled:bg-slate-200 disabled:text-slate-400 disabled:cursor-not-allowed cursor-pointer shadow-2xs"
                          >
                            {!periodoAberto
                              ? 'Período Fechado'
                              : lotada
                              ? 'Turma Esgotada'
                              : limiteGeralAtingido
                              ? 'Limite Atingido'
                              : 'Selecionar Turma'}
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>

        {/* Coluna Lateral Fixa: Painel "Minha Grade / Resumo da Inscrição" (32% - 4 cols) */}
        <div className="lg:col-span-4 sticky top-20 space-y-4">
          <div className="bg-white rounded-md border border-slate-200 p-5 shadow-2xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div>
                <span className="text-xs font-bold text-slate-900 block">
                  Minha Grade Semestral
                </span>
                <span className="text-[11px] text-slate-400">
                  Semestre {dashboard?.semestre}
                </span>
              </div>
              <span className="text-xs font-semibold px-2 py-0.5 rounded bg-slate-100 text-slate-700 font-mono">
                {dashboard?.matriculas.length || 0} ativa(s)
              </span>
            </div>

            {/* Grupo Obrigatórias */}
            <div>
              <div className="flex items-center justify-between text-xs font-semibold text-slate-700 mb-2">
                <span className="flex items-center gap-1.5">
                  <BookOpen className="w-3.5 h-3.5 text-blue-600" />
                  Obrigatórias ({dashboard?.totalObrigatorias || 0}/4)
                </span>
                <span className="text-[10px] text-slate-400">máx. 4</span>
              </div>

              {matriculasObrigatorias.length === 0 ? (
                <div className="p-2.5 bg-slate-50 rounded border border-dashed border-slate-200 text-center text-[11px] text-slate-400">
                  Nenhuma obrigatória selecionada.
                </div>
              ) : (
                <div className="space-y-1.5">
                  {matriculasObrigatorias.map((mat) => (
                    <div
                      key={mat.id}
                      className="p-2 bg-slate-50 hover:bg-slate-100/80 rounded border border-slate-200/80 flex items-center justify-between gap-2 text-xs transition-colors"
                    >
                      <div className="min-w-0">
                        <span className="font-bold text-slate-900 block truncate text-[11px]">
                          {mat.codigoDisciplina} • {mat.nomeDisciplina}
                        </span>
                        <span className="text-[10px] text-slate-500 block truncate">
                          {mat.professorNome}
                        </span>
                      </div>
                      <button
                        onClick={() => handleCancelarMatricula(mat.id, mat.nomeDisciplina)}
                        disabled={cancelingId === mat.id || !periodoAberto}
                        className="p-1 text-slate-400 hover:text-red-600 rounded transition-colors shrink-0"
                        title="Remover"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Grupo Optativas */}
            <div className="pt-2 border-t border-slate-100">
              <div className="flex items-center justify-between text-xs font-semibold text-slate-700 mb-2">
                <span className="flex items-center gap-1.5">
                  <Layers className="w-3.5 h-3.5 text-indigo-600" />
                  Eletivas / Optativas ({dashboard?.totalOptativas || 0}/2)
                </span>
                <span className="text-[10px] text-slate-400">máx. 2</span>
              </div>

              {matriculasOptativas.length === 0 ? (
                <div className="p-2.5 bg-slate-50 rounded border border-dashed border-slate-200 text-center text-[11px] text-slate-400">
                  Nenhuma eletiva selecionada.
                </div>
              ) : (
                <div className="space-y-1.5">
                  {matriculasOptativas.map((mat) => (
                    <div
                      key={mat.id}
                      className="p-2 bg-slate-50 hover:bg-slate-100/80 rounded border border-slate-200/80 flex items-center justify-between gap-2 text-xs transition-colors"
                    >
                      <div className="min-w-0">
                        <span className="font-bold text-slate-900 block truncate text-[11px]">
                          {mat.codigoDisciplina} • {mat.nomeDisciplina}
                        </span>
                        <span className="text-[10px] text-slate-500 block truncate">
                          {mat.professorNome}
                        </span>
                      </div>
                      <button
                        onClick={() => handleCancelarMatricula(mat.id, mat.nomeDisciplina)}
                        disabled={cancelingId === mat.id || !periodoAberto}
                        className="p-1 text-slate-400 hover:text-red-600 rounded transition-colors shrink-0"
                        title="Remover"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Modal de Seleção de Modalidade */}
      {selectedOferta && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-2xs p-4">
          <div className="bg-white rounded-md max-w-md w-full p-5 shadow-xl border border-slate-200 animate-scale-up">
            <div className="flex items-start justify-between pb-3 border-b border-slate-100">
              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                  Confirmação de Inscrição
                </span>
                <h3 className="text-sm font-bold text-slate-900 mt-0.5">
                  {selectedOferta.nomeDisciplina}
                </h3>
              </div>
              <button
                onClick={() => setSelectedOferta(null)}
                className="text-slate-400 hover:text-slate-600 text-lg font-bold"
              >
                ×
              </button>
            </div>

            <div className="py-3 space-y-3">
              <div className="bg-slate-50 p-2.5 rounded border border-slate-200 text-xs text-slate-600 space-y-0.5">
                <div>Código: <strong className="text-slate-800 font-mono">{selectedOferta.codigoDisciplina}</strong></div>
                <div>Docente: <strong className="text-slate-800">{selectedOferta.professorNome}</strong></div>
                <div>Vagas Livres: <strong className="text-slate-800 font-mono">{selectedOferta.vagasRestantes} de {selectedOferta.maxVagas}</strong></div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1.5">
                  Selecione a Modalidade no Currículo:
                </label>

                <div className="grid grid-cols-2 gap-2">
                  <label
                    className={`border rounded p-3 cursor-pointer text-xs ${
                      selectedTipo === 'OBRIGATORIA'
                        ? 'border-blue-600 bg-blue-50/50'
                        : 'border-slate-200'
                    } ${obrigatoriasCheias ? 'opacity-50 cursor-not-allowed bg-slate-50' : ''}`}
                  >
                    <div className="flex items-center justify-between font-bold text-slate-900">
                      <span>Obrigatória</span>
                      <input
                        type="radio"
                        name="tipoMatricula"
                        value="OBRIGATORIA"
                        checked={selectedTipo === 'OBRIGATORIA'}
                        disabled={obrigatoriasCheias}
                        onChange={() => setSelectedTipo('OBRIGATORIA')}
                      />
                    </div>
                    <span className="text-[10px] text-slate-500 block mt-1 font-mono">
                      {dashboard?.totalObrigatorias}/4 inscritas
                    </span>
                  </label>

                  <label
                    className={`border rounded p-3 cursor-pointer text-xs ${
                      selectedTipo === 'OPTATIVA'
                        ? 'border-indigo-600 bg-indigo-50/50'
                        : 'border-slate-200'
                    } ${optativasCheias ? 'opacity-50 cursor-not-allowed bg-slate-50' : ''}`}
                  >
                    <div className="flex items-center justify-between font-bold text-slate-900">
                      <span>Eletiva</span>
                      <input
                        type="radio"
                        name="tipoMatricula"
                        value="OPTATIVA"
                        checked={selectedTipo === 'OPTATIVA'}
                        disabled={optativasCheias}
                        onChange={() => setSelectedTipo('OPTATIVA')}
                      />
                    </div>
                    <span className="text-[10px] text-slate-500 block mt-1 font-mono">
                      {dashboard?.totalOptativas}/2 inscritas
                    </span>
                  </label>
                </div>
              </div>

              <div className="flex items-start gap-1.5 bg-slate-50 p-2.5 rounded text-[11px] text-slate-600 border border-slate-200/80">
                <Info className="w-3.5 h-3.5 text-slate-500 shrink-0 mt-0.5" />
                <span>A confirmação vinculará a disciplina à sua grade do semestre {dashboard?.semestre}.</span>
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setSelectedOferta(null)}
                className="px-3 py-1.5 text-xs font-semibold text-slate-600 hover:text-slate-800 rounded hover:bg-slate-100"
              >
                Cancelar
              </button>

              <button
                type="button"
                disabled={submitting || (selectedTipo === 'OBRIGATORIA' && obrigatoriasCheias) || (selectedTipo === 'OPTATIVA' && optativasCheias)}
                onClick={handleConfirmarMatricula}
                className="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-semibold transition-colors disabled:opacity-50 cursor-pointer"
              >
                {submitting ? 'Confirmando...' : 'Confirmar Matrícula'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
