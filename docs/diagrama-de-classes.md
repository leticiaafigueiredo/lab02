# Diagrama de Classes — Sistema de Matrículas

Alinhado à implementação Java em `src/main/java`.

```mermaid
classDiagram
    class Usuario {
        - String id
        - String login
        - String senha
        - String nome
        - Perfil perfil
        + autenticar(senha) boolean
    }
    class Aluno {
        - String ra
    }
    class Professor
    class Curso {
        - String id
        - String nome
        - int creditos
        - List~String~ disciplinaIds
        + associarDisciplina(id)
    }
    class Disciplina {
        + int MINIMO_ALUNOS
        + int MAXIMO_ALUNOS
        - String codigo
        - String nome
        - String cursoId
        - String professorId
    }
    class OfertaDisciplina {
        - String semestre
        - StatusOferta status
        + aceitaMatricula() boolean
    }
    class Matricula {
        - TipoMatricula tipo
    }
    class PeriodoMatriculas {
        - String semestre
        - boolean aberto
    }
    Usuario <|-- Aluno
    Usuario <|-- Professor
    Curso "1" o-- "*" Disciplina
    Professor "0..1" --> "*" Disciplina
    OfertaDisciplina "*" --> "1" Disciplina
    Matricula "*" --> "1" Aluno
    Matricula "*" --> "1" OfertaDisciplina

    class AutenticacaoServico
    class SecretariaServico
    class MatriculaServico
    class ProfessorServico
    class CobrancaServico
    class RepositorioArquivo
    SecretariaServico --> RepositorioArquivo
    MatriculaServico --> RepositorioArquivo
    MatriculaServico --> CobrancaServico
    SecretariaServico --> CobrancaServico
```

Camadas: `modelo` (domínio) → `servico` (regras) → `ui` (terminal) → `persistencia` (arquivos CSV em `data/`).
