# Histórias de Usuário — Sistema de Matrículas

Formato: **Como** [ator], **quero** [objetivo], **para** [benefício].  
Critérios de aceite no estilo Given / When / Then. Cada história referencia o caso de uso em [`diagrama-casos-de-uso.md`](./diagrama-casos-de-uso.md).

Regras de negócio transversais (Product Owner):

- Curso possui **nome**, **número de créditos** e é constituído por **disciplinas**.
- Aluno pode matricular-se em **4 disciplinas obrigatórias** (1ª opção) e **2 optativas** (alternativas).
- Matrícula e cancelamento só no **período de matrículas**.
- Disciplina fica **ativa** (ocorre no semestre seguinte) se, ao fim do período, tiver **pelo menos 3** alunos matriculados; caso contrário é **cancelada**.
- Máximo de **60** alunos por disciplina; ao atingir esse número, as inscrições na disciplina **encerram**.
- Após o aluno inscrever-se no semestre, o **sistema de cobranças** é notificado.
- Professores consultam os alunos matriculados em cada disciplina.
- Todos os usuários autenticam-se com **login e senha**.

---

## Épico: Autenticação

### HU-01 — Autenticar-se no sistema (UC01)

**Como** usuário (secretaria, aluno ou professor),  
**quero** autenticar-me com login e senha,  
**para** acessar apenas as funcionalidades do meu perfil.

**Critérios de aceite**

- Dado um usuário cadastrado, quando informo login e senha válidos, então o sistema concede acesso conforme o perfil.
- Dado login ou senha inválidos, quando tento autenticar, então o acesso é negado e nenhuma operação protegida é executada.
- Dado um usuário não autenticado, quando tento uma operação do sistema, então sou impedido até autenticar.

---

## Épico: Cadastros da secretaria

### HU-02 — Manter cursos (UC02)

**Como** secretaria,  
**quero** cadastrar e atualizar cursos (nome, créditos e disciplinas),  
**para** compor a estrutura acadêmica da universidade.

**Critérios de aceite**

- Dado que estou autenticada como secretaria, quando cadastro um curso com nome e créditos, então o curso fica disponível para associação a disciplinas.
- Dado um curso existente, quando associo disciplinas, então o curso passa a ser constituído por essas disciplinas.
- Dado dados incompletos (sem nome ou sem créditos), quando tento salvar, então o sistema rejeita o cadastro.

### HU-03 — Manter disciplinas (UC03)

**Como** secretaria,  
**quero** cadastrar e atualizar disciplinas,  
**para** que possam constar no currículo do semestre e receber matrículas.

**Critérios de aceite**

- Dado que estou autenticada como secretaria, quando cadastro uma disciplina, então ela pode ser incluída no currículo de um semestre.
- Dado uma disciplina existente, quando a atualizo, então as alterações passam a valer para a oferta vigente (sem apagar matrículas já feitas, salvo regra explícita de cancelamento).

### HU-04 — Manter professores (UC04)

**Como** secretaria,  
**quero** cadastrar e atualizar professores,  
**para** que possam acessar o sistema e consultar turmas.

**Critérios de aceite**

- Dado que estou autenticada como secretaria, quando cadastro um professor com credenciais, então ele consegue autenticar-se.
- Dado um professor cadastrado, quando atualizo seus dados, então o perfil usado no login e nas consultas reflete a alteração.

### HU-05 — Manter alunos (UC05)

**Como** secretaria,  
**quero** cadastrar e atualizar alunos,  
**para** que possam matricular-se no período correspondente.

**Critérios de aceite**

- Dado que estou autenticada como secretaria, quando cadastro um aluno com credenciais, então ele consegue autenticar-se e matricular-se no período aberto.
- Dado um aluno cadastrado, quando atualizo seus dados, então o cadastro usado nas matrículas e na cobrança permanece consistente.

### HU-06 — Gerar currículo do semestre (UC06)

**Como** secretaria,  
**quero** gerar o currículo (oferta) de cada semestre,  
**para** definir quais disciplinas estarão disponíveis para matrícula.

**Critérios de aceite**

- Dado o período acadêmico, quando gero o currículo do semestre com um conjunto de disciplinas, então apenas essas disciplinas ficam abertas para inscrição naquele semestre.
- Dado um currículo já gerado, quando o período de matrículas está aberto, então alunos veem as disciplinas daquela oferta.

---

## Épico: Matrícula do aluno

### HU-07 — Matricular-se em disciplinas obrigatórias e optativas (UC07, UC13, UC14)

**Como** aluno,  
**quero** matricular-me em até 4 disciplinas obrigatórias e 2 optativas no período de matrículas,  
**para** compor minha grade do semestre.

**Critérios de aceite**

- Dado que o período de matrículas está aberto e estou autenticado, quando me inscrevo em uma disciplina com vagas, então a matrícula é registrada.
- Dado que já possuo 4 disciplinas obrigatórias, quando tento uma quinta obrigatória, então o sistema recusa.
- Dado que já possuo 2 disciplinas optativas, quando tento uma terceira optativa, então o sistema recusa.
- Dado que a disciplina já possui 60 alunos, quando tento matricular-me nela, então a inscrição é recusada (vagas encerradas).
- Dado que o período de matrículas está fechado, quando tento matricular-me, então a operação é recusada.

### HU-08 — Cancelar matrícula (UC08, UC13)

**Como** aluno,  
**quero** cancelar matrículas feitas anteriormente durante o período de matrículas,  
**para** ajustar minha grade enquanto a janela estiver aberta.

**Critérios de aceite**

- Dado que estou matriculado em uma disciplina e o período está aberto, quando cancelo essa matrícula, então deixo de constar na lista da disciplina e libero uma vaga (se a disciplina ainda não tiver sido cancelada pela regra de oferta).
- Dado que o período de matrículas está fechado, quando tento cancelar, então a operação é recusada.

### HU-09 — Consultar próprias matrículas (UC09)

**Como** aluno,  
**quero** consultar as disciplinas em que estou matriculado,  
**para** acompanhar minha inscrição no semestre.

**Critérios de aceite**

- Dado que estou autenticado como aluno, quando consulto minhas matrículas, então vejo apenas as disciplinas em que eu estou inscrito no semestre corrente.
- Dado que uma disciplina da minha lista foi cancelada ao fim do período (menos de 3 alunos), quando consulto, então essa disciplina aparece como cancelada / não ofertada.

---

## Épico: Regras de oferta e cobrança

### HU-10 — Encerrar vagas ao atingir 60 alunos (UC14)

**Como** sistema,  
**quero** encerrar inscrições de uma disciplina ao atingir 60 alunos,  
**para** respeitar o limite máximo de turma.

**Critérios de aceite**

- Dado uma disciplina com 59 matriculados, quando um 60º aluno se matricula, então a matrícula é aceita e novas inscrições nessa disciplina são encerradas.
- Dado uma disciplina com 60 matriculados, quando outro aluno tenta inscrever-se, então o sistema informa que as matrículas daquela disciplina estão encerradas.

### HU-11 — Ativar ou cancelar disciplina ao fim do período (UC11)

**Como** secretaria,  
**quero** encerrar o período de matrículas e definir quais disciplinas ocorrerão,  
**para** que só sigam ativas as turmas com demanda mínima.

**Critérios de aceite**

- Dado o fim do período, quando uma disciplina tem **3 ou mais** alunos matriculados, então ela permanece **ativa** para o semestre seguinte.
- Dado o fim do período, quando uma disciplina tem **menos de 3** alunos, então ela é **cancelada** e não ocorre no semestre seguinte.
- Dado o período ainda aberto, quando um aluno consulta ofertas, então disciplinas abaixo de 3 inscritos ainda aceitam matrícula (não são canceladas antecipadamente).

### HU-12 — Notificar o sistema de cobranças (UC12)

**Como** sistema de matrículas,  
**quero** notificar o sistema de cobranças após o aluno inscrever-se no semestre,  
**para** que ele seja cobrado pelas disciplinas daquele semestre.

**Critérios de aceite**

- Dado um aluno que conclui a inscrição no semestre (matrícula registrada), quando a operação é confirmada, então o sistema de cobranças recebe notificação com o aluno e as disciplinas daquele semestre.
- Dado cancelamento de disciplina por falta de quórum ao fim do período, quando a oferta é fechada, então a cobrança correspondente deixa de considerar essa disciplina (notificação de ajuste).

---

## Épico: Consulta do professor

### HU-13 — Consultar alunos matriculados por disciplina (UC10)

**Como** professor,  
**quero** saber quais alunos estão matriculados em cada disciplina,  
**para** acompanhar as turmas.

**Critérios de aceite**

- Dado que estou autenticado como professor, quando seleciono uma disciplina, então vejo a lista de alunos matriculados nela.
- Dado uma disciplina cancelada ao fim do período, quando consulto, então o sistema indica que a turma não ocorrerá.
- Dado um usuário que não é professor (nem secretaria, se aplicável à consulta administrativa), quando tenta essa listagem, então o acesso é negado.

---

## Mapeamento história × caso de uso

| História | Casos de uso |
|----------|----------------|
| HU-01 | UC01 |
| HU-02 | UC02 |
| HU-03 | UC03 |
| HU-04 | UC04 |
| HU-05 | UC05 |
| HU-06 | UC06 |
| HU-07 | UC07, UC13, UC14 |
| HU-08 | UC08, UC13 |
| HU-09 | UC09 |
| HU-10 | UC14 |
| HU-11 | UC11 |
| HU-12 | UC12 |
| HU-13 | UC10 |
