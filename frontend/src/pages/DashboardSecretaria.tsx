import React, { useState, useEffect } from 'react';
import api from '../services/api';
import type { Periodo, Curso, Disciplina, ProfessorItem, AlunoItem } from '../types';
import {
  Calendar,
  ToggleLeft,
  ToggleRight,
  AlertTriangle,
  CheckCircle2,
  Check,
  Plus,
  Sparkles
} from 'lucide-react';

export const DashboardSecretaria: React.FC = () => {
  const [resumo, setResumo] = useState<any>(null);
  const [periodo, setPeriodo] = useState<Periodo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  // Guias de Gestão
  const [activeTab, setActiveTab] = useState<'VISAO_GERAL' | 'CURSOS' | 'DISCIPLINAS' | 'PROFESSORES' | 'ALUNOS' | 'OFERTAS'>('VISAO_GERAL');

  // Listas de Entidades
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [disciplinas, setDisciplinas] = useState<Disciplina[]>([]);
  const [professores, setProfessores] = useState<ProfessorItem[]>([]);
  const [alunos, setAlunos] = useState<AlunoItem[]>([]);

  // Formulários Modais / Criação
  const [showModal, setShowModal] = useState<string | null>(null);

  // Form States
  const [novoCurso, setNovoCurso] = useState({ nome: '', creditos: 240 });
  const [novaDisciplina, setNovaDisciplina] = useState({ codigo: '', nome: '', cursoId: '', professorId: '' });
  const [novoProfessor, setNovoProfessor] = useState({ login: '', senha: '123', nome: '', departamento: 'Engenharia de Software', titulacao: 'Doutor(a)' });
  const [novoAluno, setNovoAluno] = useState({ login: '', senha: '123', nome: '', ra: '' });
  const [novaOferta, setNovaOferta] = useState({ semestre: '2026.2', disciplinaId: '' });

  const loadSecretariaData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [resumoRes, periodoRes, cursosRes, discRes, profRes, alunosRes] = await Promise.all([
        api.get('/secretaria/resumo'),
        api.get<Periodo>('/periodo/atual'),
        api.get<Curso[]>('/secretaria/cursos'),
        api.get<Disciplina[]>('/secretaria/disciplinas'),
        api.get<ProfessorItem[]>('/secretaria/professores'),
        api.get<AlunoItem[]>('/secretaria/alunos'),
      ]);
      setResumo(resumoRes.data);
      setPeriodo(periodoRes.data);
      setCursos(cursosRes.data);
      setDisciplinas(discRes.data);
      setProfessores(profRes.data);
      setAlunos(alunosRes.data);
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

  // Cadastros
  const handleCadastrarCurso = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      await api.post('/secretaria/cursos', novoCurso);
      setSuccessMsg(`Curso "${novoCurso.nome}" cadastrado com sucesso!`);
      setShowModal(null);
      setNovoCurso({ nome: '', creditos: 240 });
      await loadSecretariaData();
    } catch (err: any) {
      setError(err.message || 'Erro ao cadastrar curso.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCadastrarDisciplina = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      await api.post('/secretaria/disciplinas', novaDisciplina);
      setSuccessMsg(`Disciplina "${novaDisciplina.nome}" cadastrada com sucesso!`);
      setShowModal(null);
      setNovaDisciplina({ codigo: '', nome: '', cursoId: '', professorId: '' });
      await loadSecretariaData();
    } catch (err: any) {
      setError(err.message || 'Erro ao cadastrar disciplina.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCadastrarProfessor = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      await api.post('/secretaria/professores', novoProfessor);
      setSuccessMsg(`Docente "${novoProfessor.nome}" cadastrado com sucesso!`);
      setShowModal(null);
      setNovoProfessor({ login: '', senha: '123', nome: '', departamento: 'Engenharia de Software', titulacao: 'Doutor(a)' });
      await loadSecretariaData();
    } catch (err: any) {
      setError(err.message || 'Erro ao cadastrar professor.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCadastrarAluno = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      await api.post('/secretaria/alunos', novoAluno);
      setSuccessMsg(`Estudante "${novoAluno.nome}" (RA: ${novoAluno.ra}) cadastrado com sucesso!`);
      setShowModal(null);
      setNovoAluno({ login: '', senha: '123', nome: '', ra: '' });
      await loadSecretariaData();
    } catch (err: any) {
      setError(err.message || 'Erro ao cadastrar aluno.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleGerarOferta = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      await api.post('/secretaria/ofertas', novaOferta);
      setSuccessMsg(`Oferta curricular gerada com sucesso para o semestre ${novaOferta.semestre}!`);
      setShowModal(null);
      setNovaOferta({ semestre: '2026.2', disciplinaId: '' });
      await loadSecretariaData();
    } catch (err: any) {
      setError(err.message || 'Erro ao gerar oferta semestral.');
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
    <div className="space-y-6">
      <div className="pb-4 border-b border-slate-200 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-purple-100 text-purple-800 border border-purple-200 uppercase tracking-wide">
            Secretaria Acadêmica
          </span>
          <h1 className="text-xl font-bold text-slate-900 mt-1">
            Gestão Acadêmica e Controle Semestral
          </h1>
          <p className="text-slate-500 text-xs mt-0.5">
            Manutenção de cursos, disciplinas, docentes, estudantes e currículo semestral.
          </p>
        </div>
      </div>

      {successMsg && (
        <div className="p-3.5 bg-emerald-50 border border-emerald-200 rounded flex items-center justify-between text-emerald-800 text-xs animate-fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
            <span>{successMsg}</span>
          </div>
          <button onClick={() => setSuccessMsg(null)} className="text-emerald-700 font-bold ml-2 text-sm">
            ×
          </button>
        </div>
      )}

      {error && (
        <div className="p-3.5 bg-red-50 border border-red-200 rounded flex items-center justify-between text-red-800 text-xs animate-fade-in">
          <div className="flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 text-red-600 flex-shrink-0" />
            <span>{error}</span>
          </div>
          <button onClick={() => setError(null)} className="text-red-700 font-bold ml-2 text-sm">
            ×
          </button>
        </div>
      )}

      {/* Navegação por Guias de Gestão */}
      <div className="flex items-center gap-1 border-b border-slate-200 overflow-x-auto pb-px">
        <button
          type="button"
          onClick={() => setActiveTab('VISAO_GERAL')}
          className={`px-3.5 py-2 text-xs font-semibold border-b-2 transition-colors shrink-0 ${
            activeTab === 'VISAO_GERAL'
              ? 'border-purple-600 text-purple-700'
              : 'border-transparent text-slate-500 hover:text-slate-900'
          }`}
        >
          Visão Geral & Período
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('CURSOS')}
          className={`px-3.5 py-2 text-xs font-semibold border-b-2 transition-colors shrink-0 ${
            activeTab === 'CURSOS'
              ? 'border-purple-600 text-purple-700'
              : 'border-transparent text-slate-500 hover:text-slate-900'
          }`}
        >
          Cursos ({cursos.length})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('DISCIPLINAS')}
          className={`px-3.5 py-2 text-xs font-semibold border-b-2 transition-colors shrink-0 ${
            activeTab === 'DISCIPLINAS'
              ? 'border-purple-600 text-purple-700'
              : 'border-transparent text-slate-500 hover:text-slate-900'
          }`}
        >
          Disciplinas ({disciplinas.length})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('PROFESSORES')}
          className={`px-3.5 py-2 text-xs font-semibold border-b-2 transition-colors shrink-0 ${
            activeTab === 'PROFESSORES'
              ? 'border-purple-600 text-purple-700'
              : 'border-transparent text-slate-500 hover:text-slate-900'
          }`}
        >
          Professores ({professores.length})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('ALUNOS')}
          className={`px-3.5 py-2 text-xs font-semibold border-b-2 transition-colors shrink-0 ${
            activeTab === 'ALUNOS'
              ? 'border-purple-600 text-purple-700'
              : 'border-transparent text-slate-500 hover:text-slate-900'
          }`}
        >
          Alunos ({alunos.length})
        </button>
      </div>

      {/* Conteúdo da Guia: VISÃO GERAL */}
      {activeTab === 'VISAO_GERAL' && (
        <div className="space-y-6">
          {/* Controle de Período */}
          <div className="bg-slate-900 rounded-md p-5 text-white shadow-md">
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

                <button
                  onClick={() => setShowModal('OFERTA')}
                  className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-purple-600 hover:bg-purple-700 rounded font-semibold text-xs text-white transition-colors cursor-pointer"
                >
                  <Sparkles className="w-4 h-4" />
                  <span>Gerar Oferta no Currículo</span>
                </button>
              </div>
            </div>
          </div>

          {/* Métricas do Sistema */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
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
      )}

      {/* Conteúdo da Guia: CURSOS */}
      {activeTab === 'CURSOS' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-bold text-slate-900">Cursos de Graduação</h2>
            <button
              onClick={() => setShowModal('CURSO')}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-semibold transition-colors cursor-pointer"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Novo Curso</span>
            </button>
          </div>

          <div className="bg-white rounded-md border border-slate-200 shadow-2xs overflow-hidden">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="bg-slate-50 text-[10px] font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200">
                  <th className="py-2.5 px-4">Nome do Curso</th>
                  <th className="py-2.5 px-4">Total de Créditos</th>
                  <th className="py-2.5 px-4">ID</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {cursos.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3 px-4 font-semibold text-slate-900">{c.nome}</td>
                    <td className="py-3 px-4 font-mono text-slate-700">{c.creditos} créditos</td>
                    <td className="py-3 px-4 font-mono text-[10px] text-slate-400">{c.id}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Conteúdo da Guia: DISCIPLINAS */}
      {activeTab === 'DISCIPLINAS' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-bold text-slate-900">Catálogo Geral de Disciplinas</h2>
            <button
              onClick={() => setShowModal('DISCIPLINA')}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-semibold transition-colors cursor-pointer"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Nova Disciplina</span>
            </button>
          </div>

          <div className="bg-white rounded-md border border-slate-200 shadow-2xs overflow-hidden">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="bg-slate-50 text-[10px] font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200">
                  <th className="py-2.5 px-4">Código</th>
                  <th className="py-2.5 px-4">Nome</th>
                  <th className="py-2.5 px-4">Curso Vinculado</th>
                  <th className="py-2.5 px-4">Docente Responsável</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {disciplinas.map((d) => (
                  <tr key={d.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3 px-4 font-mono font-bold text-slate-900">{d.codigo}</td>
                    <td className="py-3 px-4 font-semibold text-slate-800">{d.nome}</td>
                    <td className="py-3 px-4 text-slate-600">{d.cursoNome || '-'}</td>
                    <td className="py-3 px-4 text-slate-600">{d.professorNome || 'Não atribuído'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Conteúdo da Guia: PROFESSORES */}
      {activeTab === 'PROFESSORES' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-bold text-slate-900">Corpo Docente</h2>
            <button
              onClick={() => setShowModal('PROFESSOR')}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-semibold transition-colors cursor-pointer"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Novo Professor</span>
            </button>
          </div>

          <div className="bg-white rounded-md border border-slate-200 shadow-2xs overflow-hidden">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="bg-slate-50 text-[10px] font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200">
                  <th className="py-2.5 px-4">Nome do Docente</th>
                  <th className="py-2.5 px-4">Login de Acesso</th>
                  <th className="py-2.5 px-4">Departamento</th>
                  <th className="py-2.5 px-4">Titulação</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {professores.map((p) => (
                  <tr key={p.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3 px-4 font-semibold text-slate-900">{p.nome}</td>
                    <td className="py-3 px-4 font-mono text-slate-700">{p.login}</td>
                    <td className="py-3 px-4 text-slate-600">{p.departamento || '-'}</td>
                    <td className="py-3 px-4 text-slate-600">{p.titulacao || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Conteúdo da Guia: ALUNOS */}
      {activeTab === 'ALUNOS' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-bold text-slate-900">Alunos Cadastrados</h2>
            <button
              onClick={() => setShowModal('ALUNO')}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded text-xs font-semibold transition-colors cursor-pointer"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Novo Aluno</span>
            </button>
          </div>

          <div className="bg-white rounded-md border border-slate-200 shadow-2xs overflow-hidden">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="bg-slate-50 text-[10px] font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200">
                  <th className="py-2.5 px-4">Nome do Estudante</th>
                  <th className="py-2.5 px-4">RA</th>
                  <th className="py-2.5 px-4">Login de Acesso</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {alunos.map((a) => (
                  <tr key={a.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3 px-4 font-semibold text-slate-900">{a.nome}</td>
                    <td className="py-3 px-4 font-mono font-bold text-slate-700">{a.ra}</td>
                    <td className="py-3 px-4 font-mono text-slate-600">{a.login}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* --- MODAIS DE CADASTRO --- */}

      {/* Modal Curso */}
      {showModal === 'CURSO' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-2xs p-4">
          <div className="bg-white rounded-md max-w-md w-full p-5 shadow-xl border border-slate-200">
            <h3 className="text-sm font-bold text-slate-900 pb-2 border-b border-slate-100">Cadastrar Novo Curso</h3>
            <form onSubmit={handleCadastrarCurso} className="space-y-3 mt-3 text-xs">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Nome do Curso</label>
                <input
                  type="text"
                  required
                  value={novoCurso.nome}
                  onChange={(e) => setNovoCurso({ ...novoCurso, nome: e.target.value })}
                  placeholder="Ex: Engenharia de Software"
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Total de Créditos</label>
                <input
                  type="number"
                  required
                  min={1}
                  value={novoCurso.creditos}
                  onChange={(e) => setNovoCurso({ ...novoCurso, creditos: parseInt(e.target.value) || 0 })}
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                />
              </div>
              <div className="flex justify-end gap-2 pt-2 border-t border-slate-100">
                <button type="button" onClick={() => setShowModal(null)} className="px-3 py-1.5 rounded text-slate-600 hover:bg-slate-100">Cancelar</button>
                <button type="submit" disabled={actionLoading} className="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded font-semibold">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Disciplina */}
      {showModal === 'DISCIPLINA' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-2xs p-4">
          <div className="bg-white rounded-md max-w-md w-full p-5 shadow-xl border border-slate-200">
            <h3 className="text-sm font-bold text-slate-900 pb-2 border-b border-slate-100">Cadastrar Nova Disciplina</h3>
            <form onSubmit={handleCadastrarDisciplina} className="space-y-3 mt-3 text-xs">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Código da Disciplina</label>
                <input
                  type="text"
                  required
                  value={novaDisciplina.codigo}
                  onChange={(e) => setNovaDisciplina({ ...novaDisciplina, codigo: e.target.value.toUpperCase() })}
                  placeholder="Ex: ES-301"
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Nome da Disciplina</label>
                <input
                  type="text"
                  required
                  value={novaDisciplina.nome}
                  onChange={(e) => setNovaDisciplina({ ...novaDisciplina, nome: e.target.value })}
                  placeholder="Ex: Sistemas Distribuídos"
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Curso Vinculado</label>
                <select
                  required
                  value={novaDisciplina.cursoId}
                  onChange={(e) => setNovaDisciplina({ ...novaDisciplina, cursoId: e.target.value })}
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                >
                  <option value="">Selecione o curso...</option>
                  {cursos.map((c) => (
                    <option key={c.id} value={c.id}>{c.nome}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Professor Responsável</label>
                <select
                  value={novaDisciplina.professorId}
                  onChange={(e) => setNovaDisciplina({ ...novaDisciplina, professorId: e.target.value })}
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                >
                  <option value="">Selecione o professor (opcional)...</option>
                  {professores.map((p) => (
                    <option key={p.id} value={p.id}>{p.nome} ({p.departamento})</option>
                  ))}
                </select>
              </div>
              <div className="flex justify-end gap-2 pt-2 border-t border-slate-100">
                <button type="button" onClick={() => setShowModal(null)} className="px-3 py-1.5 rounded text-slate-600 hover:bg-slate-100">Cancelar</button>
                <button type="submit" disabled={actionLoading} className="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded font-semibold">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Professor */}
      {showModal === 'PROFESSOR' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-2xs p-4">
          <div className="bg-white rounded-md max-w-md w-full p-5 shadow-xl border border-slate-200">
            <h3 className="text-sm font-bold text-slate-900 pb-2 border-b border-slate-100">Cadastrar Novo Docente</h3>
            <form onSubmit={handleCadastrarProfessor} className="space-y-3 mt-3 text-xs">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Nome Completo</label>
                <input
                  type="text"
                  required
                  value={novoProfessor.nome}
                  onChange={(e) => setNovoProfessor({ ...novoProfessor, nome: e.target.value })}
                  placeholder="Ex: Prof. Dr. Roberto Almeida"
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block font-medium text-slate-700 mb-1">Login</label>
                  <input
                    type="text"
                    required
                    value={novoProfessor.login}
                    onChange={(e) => setNovoProfessor({ ...novoProfessor, login: e.target.value })}
                    placeholder="prof_roberto"
                    className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                  />
                </div>
                <div>
                  <label className="block font-medium text-slate-700 mb-1">Senha</label>
                  <input
                    type="password"
                    required
                    value={novoProfessor.senha}
                    onChange={(e) => setNovoProfessor({ ...novoProfessor, senha: e.target.value })}
                    className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block font-medium text-slate-700 mb-1">Departamento</label>
                  <input
                    type="text"
                    value={novoProfessor.departamento}
                    onChange={(e) => setNovoProfessor({ ...novoProfessor, departamento: e.target.value })}
                    className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                  />
                </div>
                <div>
                  <label className="block font-medium text-slate-700 mb-1">Titulação</label>
                  <input
                    type="text"
                    value={novoProfessor.titulacao}
                    onChange={(e) => setNovoProfessor({ ...novoProfessor, titulacao: e.target.value })}
                    className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                  />
                </div>
              </div>
              <div className="flex justify-end gap-2 pt-2 border-t border-slate-100">
                <button type="button" onClick={() => setShowModal(null)} className="px-3 py-1.5 rounded text-slate-600 hover:bg-slate-100">Cancelar</button>
                <button type="submit" disabled={actionLoading} className="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded font-semibold">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Aluno */}
      {showModal === 'ALUNO' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-2xs p-4">
          <div className="bg-white rounded-md max-w-md w-full p-5 shadow-xl border border-slate-200">
            <h3 className="text-sm font-bold text-slate-900 pb-2 border-b border-slate-100">Cadastrar Novo Aluno</h3>
            <form onSubmit={handleCadastrarAluno} className="space-y-3 mt-3 text-xs">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Nome Completo</label>
                <input
                  type="text"
                  required
                  value={novoAluno.nome}
                  onChange={(e) => setNovoAluno({ ...novoAluno, nome: e.target.value })}
                  placeholder="Ex: Carlos Santana"
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block font-medium text-slate-700 mb-1">RA (Registro Acadêmico)</label>
                  <input
                    type="text"
                    required
                    value={novoAluno.ra}
                    onChange={(e) => setNovoAluno({ ...novoAluno, ra: e.target.value })}
                    placeholder="1005"
                    className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                  />
                </div>
                <div>
                  <label className="block font-medium text-slate-700 mb-1">Login</label>
                  <input
                    type="text"
                    required
                    value={novoAluno.login}
                    onChange={(e) => setNovoAluno({ ...novoAluno, login: e.target.value })}
                    placeholder="aluno5"
                    className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                  />
                </div>
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Senha de Acesso</label>
                <input
                  type="password"
                  required
                  value={novoAluno.senha}
                  onChange={(e) => setNovoAluno({ ...novoAluno, senha: e.target.value })}
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                />
              </div>
              <div className="flex justify-end gap-2 pt-2 border-t border-slate-100">
                <button type="button" onClick={() => setShowModal(null)} className="px-3 py-1.5 rounded text-slate-600 hover:bg-slate-100">Cancelar</button>
                <button type="submit" disabled={actionLoading} className="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded font-semibold">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Gerar Oferta */}
      {showModal === 'OFERTA' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-2xs p-4">
          <div className="bg-white rounded-md max-w-md w-full p-5 shadow-xl border border-slate-200">
            <h3 className="text-sm font-bold text-slate-900 pb-2 border-b border-slate-100">Gerar Oferta no Currículo Semestral</h3>
            <form onSubmit={handleGerarOferta} className="space-y-3 mt-3 text-xs">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Semestre Letivo</label>
                <input
                  type="text"
                  required
                  value={novaOferta.semestre}
                  onChange={(e) => setNovaOferta({ ...novaOferta, semestre: e.target.value })}
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600 font-mono font-bold"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Disciplina a Ofertar</label>
                <select
                  required
                  value={novaOferta.disciplinaId}
                  onChange={(e) => setNovaOferta({ ...novaOferta, disciplinaId: e.target.value })}
                  className="w-full p-2 border border-slate-200 rounded text-xs bg-slate-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-purple-600"
                >
                  <option value="">Selecione a disciplina...</option>
                  {disciplinas.map((d) => (
                    <option key={d.id} value={d.id}>{d.codigo} - {d.nome}</option>
                  ))}
                </select>
              </div>
              <div className="flex justify-end gap-2 pt-2 border-t border-slate-100">
                <button type="button" onClick={() => setShowModal(null)} className="px-3 py-1.5 rounded text-slate-600 hover:bg-slate-100">Cancelar</button>
                <button type="submit" disabled={actionLoading} className="px-4 py-1.5 bg-purple-600 hover:bg-purple-700 text-white rounded font-semibold">Gerar Oferta</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
