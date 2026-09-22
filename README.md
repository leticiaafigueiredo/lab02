# Sistema de Matrículas Web — Sprint 2

> **Pontifícia Universidade Católica de Minas Gerais (PUC Minas)**  
> **Curso:** Engenharia de Software  
> **Disciplina:** Laboratório de Desenvolvimento de Software  
> **Autores:** Letícia Figueiredo e Murilo Freitas  

---

## 1. Visão Geral

O **Sistema de Matrículas Web** é uma solução para a gestão semestral de matrículas acadêmicas do curso de Engenharia de Software. O projeto evoluiu a partir de um protótipo de terminal (CLI) para uma aplicação Web completa baseada em arquitetura cliente-servidor desacoplada.

A plataforma atende aos três perfis de usuários da universidade: **Secretaria**, **Professores** e **Alunos**, implementando rigorosamente as regras de negócio de matrícula semestral, controle de períodos, limites por modalidade de disciplina, regras de quórum de turmas e auditoria com notificação ao sistema de cobranças.

---

## 2. Tecnologias Utilizadas

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.3.3**
  - **Spring Web** (API RESTful)
  - **Spring Security** com autenticação stateless baseada em **JWT (JSON Web Token - JJWT 0.12.6)**
  - **Spring Data JPA** & **Hibernate** (Mapeamento Objeto-Relacional)
  - **H2 Database** (Banco relacional em memória com Console Web)
  - **Jakarta Bean Validation** (Validação declarativa de requisições)
  - **SLF4J / Logback** (Auditoria de operações e cobranças)
- **Maven** (Gerenciamento de dependências e build)
- **JUnit 5** & **AssertJ** (Testes unitários e de integração)

### Frontend
- **React 19** com **TypeScript**
- **Vite 6** (Bundler rápido)
- **Tailwind CSS v4** (Design system moderno e responsivo)
- **Lucide React** (Ícones vetoriais)
- **Axios** (Cliente HTTP com interceptores automáticos de autenticação JWT)
- **React Router DOM v7** (Roteamento declarativo com proteção de rotas por papel de usuário)

---

## 3. Arquitetura e Design da Aplicação

### Interface do Usuário (Design System Acadêmico)
- **Barra Lateral de Navegação (Sidebar):** Navegação fixa à esquerda com identificação contextual do usuário autenticado e rotas restritas por perfil.
- **Cabeçalho com Breadcrumbs e Status Institucional:** Barra superior com indicação de tela ativa, período letivo (ex: `2026.2`), status da janela de matrícula e situação financeira regular.
- **Painel do Aluno (`/dashboard/aluno`):** Visão consolidada com barras de progresso funcionais para limites de disciplinas (`0/4 Obrigatórias`, `0/2 Eletivas`), tabela densa de matrículas ativas com ações de cancelamento durante o período aberto.
- **Catálogo de Matrículas em Split Layout (`/matricula`):**
  - **Coluna Principal (65%):** Barra de busca em tempo real e abas de filtragem (`Todas`, `Com Vagas`, `Obrigatórias`, `Eletivas`), cards informativos com indicação de vagas, horários, créditos e botão de inscrição.
  - **Painel de Resumo Fixo (35%):** Sidebar contextual com contadores em tempo real, disciplinas selecionadas no semestre e alertas claros de regras de negócio.
- **Painel do Professor (`/dashboard/professor`):** Lista de turmas atribuídas ao docente com quórum atual (meta mínima de 3 alunos) e listagem expansível de alunos inscritos.
- **Painel da Secretaria (`/dashboard/secretaria`):** Painel administrativo para abertura/fechamento do período de matrículas e encerramento oficial do semestre com aplicação automática das regras de quórum.

---

## 4. Regras de Negócio Implementadas

1. **Limites de Inscrição por Aluno:**
   - No máximo **4 disciplinas obrigatórias**.
   - No máximo **2 disciplinas optativas / eletivas**.
   - O sistema bloqueia tentativas de exceder os limites ou matricular-se em duplicidade na mesma oferta.
2. **Capacidade Máxima de Turma:**
   - Cada oferta de disciplina possui limite de **60 alunos**.
   - Ao atingir a capacidade, a disciplina fica com status **Lotada** e impede novas inscrições.
3. **Controle de Período Regular:**
   - Alunos só podem realizar novas matrículas ou cancelar inscrições existentes enquanto o período estiver com status **ABERTO** (`PeriodoMatriculas.aberto == true`).
   - Com o período fechado, as ações são bloqueadas tanto no backend quanto no frontend.
4. **Regra de Quórum e Encerramento do Semestre:**
   - Ao encerrar o período via Secretaria:
     - Ofertas com **3 ou mais alunos inscritos** são confirmadas e marcadas como `ATIVAS`.
     - Ofertas com **menos de 3 alunos** são automaticamente `CANCELADAS`.
5. **Notificação ao Sistema de Cobranças:**
   - Cada inscrição confirmada e cada cancelamento efetuado disparam um evento registrado no log do sistema e persistido em formato de auditoria no arquivo `data/cobrancas.log`.

---

## 5. Como Executar o Projeto

### Pré-requisitos
- **Java JDK 21+** instalado e configurado no `PATH`.
- **Apache Maven 3.8+** instalado (ou uso do wrapper se disponível).
- **Node.js 18+** e **npm** instalados.

---

### Passo 1: Executar o Backend (Spring Boot)

Na raiz do projeto (`lab02/`):

```bash
mvn spring-boot:run
```

- A API REST estará disponível em: `http://localhost:8080`
- O console do banco H2 estará acessível em: `http://localhost:8080/h2-console`
  - **JDBC URL:** `jdbc:h2:mem:matriculasdb`
  - **User:** `sa`
  - **Password:** *(em branco)*

---

### Passo 2: Executar o Frontend (React + Vite)

Em um novo terminal, entre na pasta `frontend/`:

```bash
cd frontend
npm install
npm run dev
```

- A aplicação Web estará disponível no navegador em: `http://localhost:5173`

---

### Passo 3: Executar os Testes Automatizados

Na raiz do projeto:

```bash
mvn test
```

> Foram implementados testes de unidade e integração cobrindo regras de limites de matrícula, quórum, duplicidade, lotação de turmas, autenticação e dashboard.

---

## 6. Credenciais de Teste (Usuários Padrão)

A base de dados é inicializada automaticamente pelo `DatabaseSeeder` com as seguintes contas:

| Perfil | Login | Senha | Nome do Usuário | Detalhes |
|---|---|---|---|---|
| **Secretaria** | `secretaria` | `123` | Coordenação / Secretaria | Gestão de período e quórum |
| **Professor** | `prof` | `123` | Profa. Dra. Ana Lima | Disciplinas de Engenharia de Software |
| **Professor** | `prof2` | `123` | Prof. Me. Carlos Eduardo | Disciplinas de Banco de Dados e Redes |
| **Aluno** | `aluno1` | `123` | João Silva | RA: 1001 (Engenharia de Software) |
| **Aluno** | `aluno2` | `123` | Maria Souza | RA: 1002 (Engenharia de Software) |
| **Aluno** | `aluno3` | `123` | Pedro Alves | RA: 1003 (Engenharia de Software) |
| **Aluno** | `aluno4` | `123` | Beatriz Costa | RA: 1004 (Engenharia de Software) |

> Na tela de login, você pode clicar nos botões de preenchimento rápido para alternar facilmente entre perfis.

---

## 7. Principais Endpoints da API REST

### Autenticação (`/api/auth`)
- `POST /api/auth/login` — Autentica o usuário e retorna o token JWT com perfil.
- `GET /api/auth/me` — Retorna dados do usuário autenticado no momento.

### Matrículas e Ofertas (`/api/matriculas` e `/api/ofertas`)
- `GET /api/matriculas/dashboard` — Resumo das matrículas do aluno autenticado e contagem de limites.
- `GET /api/matriculas/minhas` — Lista de todas as matrículas ativas do aluno no semestre.
- `POST /api/matriculas` — Realiza matrícula em uma oferta (`{ ofertaId, tipo: "OBRIGATORIA" | "OPTATIVA" }`).
- `DELETE /api/matriculas/{id}` — Cancela matrícula em uma disciplina durante período aberto.
- `GET /api/ofertas/disponiveis` — Catálogo de ofertas disponíveis no semestre com contagem de vagas.

### Área do Docente (`/api/professor`)
- `GET /api/professor/turmas` — Lista turmas associadas ao professor autenticado e respectivos alunos matriculados.

### Secretaria e Períodos (`/api/periodo` e `/api/secretaria`)
- `GET /api/periodo/atual` — Consulta o período letivo ativo e status de abertura.
- `POST /api/periodo/toggle` — Alterna status do período (Aberto / Fechado).
- `POST /api/secretaria/encerrar-periodo` — Encerra o período aplicando a regra de quórum (mínimo 3 alunos).

---

## 8. Estrutura do Repositório

```
lab02/
├── pom.xml                               # Configurações do Maven e dependências Spring Boot
├── README.md                             # Documentação do projeto
├── data/
│   └── cobrancas.log                     # Arquivo de auditoria do sistema de cobranças
├── src/
│   ├── main/
│   │   ├── java/br/pucminas/matriculas/
│   │   │   ├── config/                   # Segurança (JWT, SecurityConfig, DatabaseSeeder)
│   │   │   ├── controller/               # Controllers REST (Auth, Matricula, Oferta, Periodo, etc.)
│   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   ├── modelo/                   # Entidades JPA (Usuario, Aluno, Professor, Oferta, etc.)
│   │   │   ├── repository/               # Interfaces Spring Data JPA
│   │   │   └── servico/                  # Camada de regras de negócio e serviços
│   │   └── resources/
│   │       └── application.properties    # Configurações de banco, JWT e logs
│   └── test/                             # Testes automatizados (JUnit 5 + AssertJ)
└── frontend/
    ├── package.json                      # Dependências Node.js
    ├── vite.config.ts                    # Configurações do Vite
    └── src/
        ├── components/                   # Sidebar, Header, AppLayout, ProtectedRoute
        ├── context/                      # Contexto de Autenticação (AuthContext)
        ├── pages/                        # Telas (Login, DashboardAluno, Matricula, Professor, Secretaria)
        ├── services/                     # Configuração Axios e cliente de API
        └── types/                        # Definições de tipos TypeScript
```

