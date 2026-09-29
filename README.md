# Sistema de Matrículas — Laboratório de Engenharia de Software

> **Pontifícia Universidade Católica de Minas Gerais (PUC Minas)**  
> **Curso:** Engenharia de Software  
> **Disciplina:** Projeto de Software / Laboratório de Desenvolvimento de Software  
> **Docente:** Profa. Milena Menezes Adão  
> **Autores:** Letícia Figueiredo e Murilo Freitas  

---

## 1. Visão Geral e Especificação do Sistema

O **Sistema de Matrículas** é uma solução completa para a gestão e informatização dos processos de matrículas semestrais da universidade. O sistema atende aos três perfis de atores definidos no projeto (**Secretaria**, **Professor** e **Aluno**), além de integrar-se com o **Sistema de Cobranças**.

### Principais Funcionalidades Atendidas:
- **Secretaria Acadêmica:**
  - Gera o currículo e as ofertas para cada semestre letivo.
  - Mantém e cadastra as informações sobre cursos, disciplinas, professores e alunos.
  - Controla o período de matrículas (abertura/fechamento) e executa o encerramento semestral aplicando a **Regra de Quórum** ($\ge 3$ alunos para ativação de turmas).
- **Alunos:**
  - Realizam matrícula em até **4 disciplinas obrigatórias (1ª opção)** e até **2 disciplinas optativas (alternativas)**.
  - Cancelam matrículas previamente efetuadas enquanto o período de matrículas estiver aberto.
  - Consultam sua grade curricular e limites de inscrição em tempo real.
- **Professores:**
  - Acessam o sistema para consultar quais alunos estão matriculados em cada uma das disciplinas que lecionam.
- **Sistema de Cobranças:**
  - Notificado automaticamente a cada inscrição e cancelamento de aluno no semestre, registrando logs de cobrança e auditoria financeira persistida.
- **Persistência em Disco e Docker:**
  - Banco de dados relacional baseado em arquivo (`./data/matriculasdb.mv.db`) com JPA/Hibernate e arquivo de log de auditoria (`./data/cobrancas.log`), preservando o estado entre reinicializações e contêineres Docker.

---

## 2. Modelagem do Sistema (Lab01S01 & Lab01S02)

### 2.1 Diagrama de Casos de Uso (UML)

```mermaid
graph TD
    classDef actorStyle fill:#1e293b,stroke:#0f172a,stroke-width:2px,color:#fff;
    classDef useCaseStyle fill:#f8fafc,stroke:#334155,stroke-width:1.5px,color:#0f172a;
    classDef externalStyle fill:#e2e8f0,stroke:#64748b,stroke-width:1.5px,stroke-dasharray: 4 4,color:#0f172a;

    Aluno(("Aluno")):::actorStyle
    Professor(("Professor")):::actorStyle
    Secretaria(("Secretaria")):::actorStyle
    Cobranca(("Sistema de Cobranças")):::actorStyle

    subgraph Sistema de Matrículas
        UC01(["UC01 - Autenticar-se no Sistema"]):::useCaseStyle
        UC02(["UC02 - Manter Cursos"]):::useCaseStyle
        UC03(["UC03 - Manter Disciplinas"]):::useCaseStyle
        UC04(["UC04 - Manter Professores"]):::useCaseStyle
        UC05(["UC05 - Manter Alunos"]):::useCaseStyle
        UC06(["UC06 - Gerar Currículo e Ofertas do Semestre"]):::useCaseStyle
        UC07(["UC07 - Matricular-se em Disciplinas"]):::useCaseStyle
        UC08(["UC08 - Cancelar Matrícula"]):::useCaseStyle
        UC09(["UC09 - Consultar Próprias Matrículas"]):::useCaseStyle
        UC10(["UC10 - Consultar Alunos por Disciplina"]):::useCaseStyle
        UC11(["UC11 - Encerrar Período e Aplicar Quórum"]):::useCaseStyle
        UC12(["UC12 - Notificar Sistema de Cobranças"]):::externalStyle
        UC13(["UC13 - Validar Janela do Período"]):::externalStyle
        UC14(["UC14 - Controlar Limite de Vagas (Máx 60)"]):::externalStyle
    end

    %% Acessos Aluno
    Aluno --> UC01
    Aluno --> UC07
    Aluno --> UC08
    Aluno --> UC09

    %% Acessos Professor
    Professor --> UC01
    Professor --> UC10

    %% Acessos Secretaria
    Secretaria --> UC01
    Secretaria --> UC02
    Secretaria --> UC03
    Secretaria --> UC04
    Secretaria --> UC05
    Secretaria --> UC06
    Secretaria --> UC11

    %% Relacionamentos de Inclusão
    UC07 -.->|«include»| UC13
    UC07 -.->|«include»| UC14
    UC07 -.->|«include»| UC12
    UC08 -.->|«include»| UC13
    UC08 -.->|«include»| UC12
    UC11 -.->|«include»| UC12

    %% Notificações externas
    UC12 --> Cobranca
```

---

### 2.2 Diagrama de Classes Estrutural (UML)

```mermaid
classDiagram
    direction TB

    class Perfil {
        <<enumeration>>
        SECRETARIA
        PROFESSOR
        ALUNO
    }

    class StatusOferta {
        <<enumeration>>
        ABERTA
        VAGAS_ENCERRADAS
        ATIVA
        CANCELADA
    }

    class TipoMatricula {
        <<enumeration>>
        OBRIGATORIA
        OPTATIVA
    }

    class Usuario {
        -String id
        -String login
        -String senha
        -String nome
        -Perfil perfil
        +autenticar(senha) boolean
        +getId() String
        +getLogin() String
        +getNome() String
        +getPerfil() Perfil
    }

    class Aluno {
        -String ra
        +getRa() String
        +setRa(ra) void
    }

    class Professor {
        -String departamento
        -String titulacao
        +getDepartamento() String
        +getTitulacao() String
    }

    class Curso {
        -String id
        -String nome
        -int creditos
        -List~Disciplina~ disciplinas
        +associarDisciplina(d) void
        +removerDisciplina(d) void
    }

    class Disciplina {
        -String id
        -String codigo
        -String nome
        -Curso curso
        -Professor professor
        +int MINIMO_ALUNOS = 3
        +int MAXIMO_ALUNOS = 60
        +getCodigo() String
        +getNome() String
    }

    class OfertaDisciplina {
        -String id
        -String semestre
        -Disciplina disciplina
        -StatusOferta status
        -int maxVagas = 60
        -List~Matricula~ matriculas
        +getVagasOcupadas() int
        +temVagas() boolean
        +estaAberta() boolean
        +adicionarMatricula(m) void
        +removerMatricula(m) void
    }

    class Matricula {
        -String id
        -Aluno aluno
        -OfertaDisciplina oferta
        -TipoMatricula tipo
        -LocalDateTime dataHora
        +getAluno() Aluno
        +getOferta() OfertaDisciplina
        +getTipo() TipoMatricula
        +getDataHora() LocalDateTime
    }

    class PeriodoMatriculas {
        -String id
        -String semestre
        -boolean aberto
        +isAberto() boolean
        +setAberto(aberto) void
        +getSemestre() String
    }

    Usuario <|-- Aluno : herança
    Usuario <|-- Professor : herança
    Usuario --> Perfil : possui

    Curso "1" *-- "0..*" Disciplina : constituído por
    Professor "1" <-- "0..*" Disciplina : leciona
    Disciplina "1" <-- "0..*" OfertaDisciplina : ofertada em
    OfertaDisciplina --> StatusOferta : estado

    Aluno "1" <-- "0..*" Matricula : realiza
    OfertaDisciplina "1" <-- "0..*" Matricula : contém
    Matricula --> TipoMatricula : modalidade
```

---

### 2.3 Histórias de Usuário (User Stories)

#### **US01 — Autenticação de Usuários**
- **Como** Usuário do sistema (Secretaria, Professor ou Aluno),
- **Quero** autenticar-me com login e senha,
- **Para que** eu tenha acesso às funcionalidades e dados restritos ao meu perfil de acesso.
- **Critérios de Aceitação:**
  - O sistema valida as credenciais informadas contra o banco de dados.
  - Ao autenticar com sucesso, gera um token de sessão seguro (JWT).
  - Bloqueia acessos não autorizados a rotas restritas.

#### **US02 — Matrícula em Disciplinas Obrigatórias e Optativas (Aluno)**
- **Como** Aluno regularmente cadastrado,
- **Quero** me inscrever em até 4 disciplinas obrigatórias e até 2 optativas durante o período aberto,
- **Para que** eu possa compor minha grade de estudos para o semestre letivo.
- **Critérios de Aceitação:**
  - O aluno pode selecionar no máximo 4 disciplinas obrigatórias e 2 optativas.
  - O sistema impede a matrícula caso a turma atinja a capacidade máxima de 60 alunos.
  - O sistema impede a matrícula em duplicidade na mesma disciplina.
  - O sistema só permite a operação se o período de matrículas estiver aberto.

#### **US03 — Cancelamento de Matrícula (Aluno)**
- **Como** Aluno matriculado em uma disciplina,
- **Quero** cancelar minha inscrição enquanto o período de matrículas estiver aberto,
- **Para que** eu possa ajustar minha grade semestral conforme minha disponibilidade.
- **Critérios de Aceitação:**
  - O cancelamento libera a vaga imediatamente para outros alunos.
  - A operação é bloqueada caso o período esteja fechado.
  - O evento de cancelamento é registrado no log de auditoria do sistema.

#### **US04 — Notificação ao Sistema de Cobranças**
- **Como** Sistema Acadêmico Integrado,
- **Quero** notificar o Sistema de Cobranças a cada matrícula confirmada de um aluno,
- **Para que** o aluno seja cobrado pelas disciplinas vinculadas ao seu semestre letivo.
- **Critérios de Aceitação:**
  - Cada matrícula gera um registro estruturado no arquivo de persistência `./data/cobrancas.log`.
  - Contém data/hora, dados do aluno (nome, RA, login), disciplina, semestre e modalidade.

#### **US05 — Consulta de Turmas e Alunos (Professor)**
- **Como** Professor da universidade,
- **Quero** visualizar minhas disciplinas atribuídas e a lista dos alunos matriculados,
- **Para que** eu possa acompanhar o preenchimento das turmas e preparar o diário de classe.
- **Critérios de Aceitação:**
  - O professor visualiza apenas as turmas sob sua responsabilidade.
  - Exibe o total de inscritos, indicação de alcance de quórum ($\ge 3$) e a lista de alunos (nome, RA, modalidade).

#### **US06 — Manutenção de Cadastros (Secretaria)**
- **Como** Membro da Secretaria Acadêmica,
- **Quero** cadastrar e manter informações de cursos, disciplinas, professores e alunos,
- **Para que** a universidade mantenha sua estrutura acadêmica atualizada.
- **Critérios de Aceitação:**
  - Permite criar novos cursos especificando nome e créditos.
  - Permite criar disciplinas associando-as ao curso e professor responsável.
  - Permite cadastrar professores e alunos com suas respectivas credenciais.

#### **US07 — Geração do Currículo Semestral (Secretaria)**
- **Como** Secretaria Acadêmica,
- **Quero** gerar ofertas de disciplinas para o semestre letivo,
- **Para que** os alunos possam selecionar as turmas durante o período de matrículas.
- **Critérios de Aceitação:**
  - Cria ofertas para o semestre vigente com limite padrão de 60 vagas.
  - Impede a duplicação da mesma disciplina no mesmo semestre.

#### **US08 — Controle de Períodos e Aplicação da Regra de Quórum (Secretaria)**
- **Como** Secretaria Acadêmica,
- **Quero** abrir, fechar e encerrar o período de matrículas aplicando a regra de quórum,
- **Para que** apenas disciplinas viáveis sejam ministradas no semestre seguinte.
- **Critérios de Aceitação:**
  - Ao alternar o período para fechado, nenhuma nova matrícula ou cancelamento é aceito.
  - Ao encerrar o período: disciplinas com $\ge 3$ alunos tornam-se `ATIVAS`; disciplinas com $< 3$ alunos tornam-se `CANCELADAS`.

---

## 3. Tecnologias Utilizadas

### Backend
- **Java 21 (LTS)**
- **Spring Boot 3.3.3**
  - **Spring Web:** API RESTful desacoplada.
  - **Spring Security:** Autenticação stateless via Bearer Token JWT (JJWT 0.12.6).
  - **Spring Data JPA & Hibernate:** ORM relacional com mapeamento completo de entidades.
  - **H2 Database em Arquivo:** Persistência relacional em disco em `./data/matriculasdb.mv.db`.
  - **Jakarta Bean Validation:** Validação declarativa de entrada de dados.
  - **SLF4J / Logback:** Auditoria financeira em `./data/cobrancas.log`.
- **Maven 3.8+**
- **JUnit 5 & AssertJ:** Testes unitários e de integração automatizados.

### Frontend
- **React 19** com **TypeScript**
- **Vite 6**
- **Tailwind CSS v4**
- **Lucide React**
- **Axios** (com interceptor JWT)
- **React Router DOM v7**

### DevOps & Contêineres
- **Docker & Docker Compose**
- **Nginx (Alpine):** Servidor web para o frontend com reverse proxy para o backend.
- **Eclipse Temurin 21 JRE (Alpine):** Runtime enxuto para o container Spring Boot.

---

## 4. Persistência de Dados e Auditoria

O sistema adota persistência contínua em disco em duas frentes:

1. **Banco de Dados Relacional (`./data/matriculasdb.mv.db`):**
   - Configurado via `jdbc:h2:file:./data/matriculasdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE`.
   - As alterações feitas na aplicação (novos cadastros de cursos, professores, alunos, matrículas e períodos) persistem no arquivo local mesmo após o encerramento ou reinício da aplicação e dos contêineres.
   - O `DatabaseSeeder` verifica a existência prévia de registros antes de popular a base inicial.
2. **Log de Cobrança e Auditoria (`./data/cobrancas.log`):**
   - Registro em formato estruturado a cada matrícula confirmada e cancelada, contendo timestamp, RA, login, nome, disciplina e modalidade.

---

## 5. Como Executar o Projeto

### Opção A: Execução com Docker (Recomendado)

O projeto conta com ambiente completo em contêineres via Docker Compose.

```bash
docker compose up --build
```

- **Frontend (Web):** `http://localhost:3000`
- **Backend (API REST):** `http://localhost:8080`
- **H2 Console:** `http://localhost:8080/h2-console`
- **Volume de Dados:** Os dados do banco e logs são montados em `./data` na raiz do projeto.

---

### Opção B: Execução Manual (Local)

#### Pré-requisitos
- **Java JDK 21+** e **Maven 3.8+**
- **Node.js 18+** e **npm**

#### Passo 1: Iniciar o Backend
```bash
mvn spring-boot:run
```
Disponível em `http://localhost:8080`.

#### Passo 2: Iniciar o Frontend
Em outro terminal:
```bash
cd frontend
npm install
npm run dev
```
Disponível em `http://localhost:5173`.

#### Passo 3: Executar Testes Automatizados
```bash
mvn test
```
Executa 15 testes unitários e de integração cobrindo regras de negócio, limites, quórum e cadastros.

---

## 6. Credenciais de Demonstração (Seed Inicial)

O banco é inicializado automaticamente com as seguintes contas:

| Perfil | Login | Senha | Nome do Usuário | Detalhes / Papel |
|---|---|---|---|---|
| **Secretaria** | `secretaria` | `123` | Secretaria Acadêmica | Manutenção de cadastros, controle de período e quórum |
| **Professor** | `prof` | `123` | Profa. Dra. Ana Lima | Docente (Engenharia de Software) |
| **Professor** | `prof2` | `123` | Prof. Me. Carlos Eduardo | Docente (Ciência da Computação) |
| **Aluno** | `aluno1` | `123` | João Silva | RA: 1001 (Engenharia de Software) |
| **Aluno** | `aluno2` | `123` | Maria Souza | RA: 1002 (Engenharia de Software) |
| **Aluno** | `aluno3` | `123` | Pedro Alves | RA: 1003 (Engenharia de Software) |
| **Aluno** | `aluno4` | `123` | Beatriz Costa | RA: 1004 (Engenharia de Software) |

> Na tela de login há botões de preenchimento rápido para testar cada perfil instantaneamente.

---

## 7. Catálogo de Endpoints REST

### Autenticação (`/api/auth`)
- `POST /api/auth/login` — Autenticação de credenciais e geração de JWT.
- `GET /api/auth/me` — Dados cadastrais do usuário autenticado.

### Matrículas e Catálogo (`/api/matriculas` e `/api/ofertas`)
- `GET /api/matriculas/dashboard` — Resumo acadêmico do aluno e contagem de limites.
- `GET /api/matriculas/minhas` — Lista de matrículas ativas do aluno no semestre.
- `POST /api/matriculas` — Realizar inscrição em oferta (`{ ofertaId, tipo }`).
- `DELETE /api/matriculas/{id}` — Cancelar inscrição em disciplina.
- `GET /api/ofertas/disponiveis` — Catálogo de disciplinas com vagas livres e ocupadas.

### Docentes (`/api/professor`)
- `GET /api/professor/turmas` — Lista turmas sob responsabilidade do docente e alunos inscritos.

### Secretaria (`/api/secretaria` e `/api/periodo`)
- `GET /api/secretaria/resumo` — Métricas gerais do sistema acadêmico.
- `GET /api/periodo/atual` — Período letivo ativo e status de abertura.
- `POST /api/periodo/toggle` — Abrir ou fechar o período de matrículas.
- `POST /api/secretaria/encerrar-periodo` — Encerrar o período aplicando quórum ($\ge 3$).
- `GET /api/secretaria/cursos` / `POST /api/secretaria/cursos` — Listar e cadastrar cursos.
- `GET /api/secretaria/disciplinas` / `POST /api/secretaria/disciplinas` — Listar e cadastrar disciplinas.
- `GET /api/secretaria/professores` / `POST /api/secretaria/professores` — Listar e cadastrar professores.
- `GET /api/secretaria/alunos` / `POST /api/secretaria/alunos` — Listar e cadastrar alunos.
- `POST /api/secretaria/ofertas` — Gerar oferta curricular para um semestre.

---

## 8. Estrutura do Repositório

```
lab02/
├── Dockerfile                            # Dockerfile multi-stage do Backend Spring Boot
├── docker-compose.yml                    # Orquestração de containers (backend + frontend)
├── pom.xml                               # Configurações do Maven e dependências Spring Boot
├── README.md                             # Documentação do projeto, diagramas e histórias de usuário
├── data/
│   ├── matriculasdb.mv.db                # Banco de dados relacional H2 persistido em arquivo
│   └── cobrancas.log                     # Arquivo de auditoria do sistema de cobranças
├── docs/                                 # Documentação de modelagem UML (Casos de Uso, Classes, User Stories)
│   ├── diagrama-casos-de-uso.md
│   ├── diagrama-de-classes.md
│   └── historias-de-usuario.md
├── src/
│   ├── main/
│   │   ├── java/br/pucminas/matriculas/
│   │   │   ├── config/                   # Segurança (JWT, SecurityConfig, DatabaseSeeder)
│   │   │   ├── controller/               # Controllers REST (Auth, Matricula, Oferta, Periodo, Secretaria, etc.)
│   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   ├── modelo/                   # Entidades JPA (Usuario, Aluno, Professor, Curso, Oferta, etc.)
│   │   │   ├── repository/               # Interfaces Spring Data JPA
│   │   │   └── servico/                  # Camada de regras de negócio e serviços
│   │   └── resources/
│   │       └── application.properties    # Configurações de banco em arquivo, JWT e logs
│   └── test/
│       ├── java/br/pucminas/matriculas/  # Testes automatizados (JUnit 5 + AssertJ)
│       └── resources/                    # application.properties isolado para testes
└── frontend/
    ├── Dockerfile                        # Dockerfile multi-stage do Frontend React
    ├── nginx.conf                        # Configuração do Nginx com reverse proxy para /api
    ├── package.json                      # Dependências Node.js
    ├── vite.config.ts                    # Configurações do Vite
    └── src/
        ├── components/                   # Componentes de UI (Sidebar, Header, AppLayout, ProtectedRoute)
        ├── context/                      # Contexto de Autenticação (AuthContext)
        ├── pages/                        # Telas (Login, DashboardAluno, Matricula, Professor, Secretaria)
        ├── services/                     # Configuração Axios e cliente de API
        └── types/                        # Definições de tipos TypeScript
```
