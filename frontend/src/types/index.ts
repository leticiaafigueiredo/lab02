export type Perfil = 'SECRETARIA' | 'PROFESSOR' | 'ALUNO';

export type TipoMatricula = 'OBRIGATORIA' | 'OPTATIVA';

export type StatusOferta = 'ABERTA' | 'VAGAS_ENCERRADAS' | 'ATIVA' | 'CANCELADA';

export interface Usuario {
  id: string;
  nome: string;
  login: string;
  perfil: Perfil;
  ra?: string;
  departamento?: string;
}

export interface AuthResponse {
  token: string;
  id: string;
  nome: string;
  login: string;
  perfil: Perfil;
  ra?: string;
  departamento?: string;
}

export interface Matricula {
  id: string;
  ofertaId: string;
  codigoDisciplina: string;
  nomeDisciplina: string;
  professorNome: string;
  cursoNome: string;
  semestre: string;
  tipo: TipoMatricula;
  dataHora: string;
}

export interface OfertaDisponivel {
  id: string;
  semestre: string;
  codigoDisciplina: string;
  nomeDisciplina: string;
  cursoNome: string;
  professorNome: string;
  status: StatusOferta;
  vagasOcupadas: number;
  vagasRestantes: number;
  maxVagas: number;
  jaMatriculado: boolean;
  matriculaId?: string;
  tipoMatricula?: TipoMatricula;
}

export interface AlunoDashboard {
  alunoId: string;
  alunoNome: string;
  ra: string;
  semestre: string;
  periodoAberto: boolean;
  totalObrigatorias: number;
  maxObrigatorias: number;
  totalOptativas: number;
  maxOptativas: number;
  matriculas: Matricula[];
}

export interface AlunoInscrito {
  matriculaId: string;
  alunoId: string;
  nome: string;
  login: string;
  ra: string;
  tipo: string;
  dataHora: string;
}

export interface TurmaProfessor {
  ofertaId: string;
  codigoDisciplina: string;
  nomeDisciplina: string;
  cursoNome: string;
  semestre: string;
  status: StatusOferta;
  totalInscritos: number;
  alunos: AlunoInscrito[];
}

export interface Periodo {
  id: string;
  semestre: string;
  aberto: boolean;
}
