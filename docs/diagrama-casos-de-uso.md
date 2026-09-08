# Diagrama de Casos de Uso — Sistema de Matrículas

Universidade informatiza o processo de matrículas. A secretaria gera o currículo do semestre e mantém cursos, disciplinas, professores e alunos. Alunos matriculam-se (até 4 obrigatórias e 2 optativas) e podem cancelar matrículas **somente no período de matrículas**. Disciplinas só ocorrem no semestre seguinte se tiverem **pelo menos 3** inscritos; o teto é **60** alunos. Após a inscrição do aluno no semestre, o **sistema de cobranças** é notificado. Professores consultam a lista de alunos por disciplina. Todo acesso exige login com senha.

## Atores

| Ator | Tipo | Responsabilidade |
|------|------|------------------|
| Secretaria | Primário | Gera o currículo do semestre e mantém informações de cursos, disciplinas, professores e alunos |
| Aluno | Primário | Matricula-se e cancela matrículas no período permitido |
| Professor | Primário | Consulta alunos matriculados em cada disciplina |
| Sistema de Cobranças | Secundário (externo) | Recebe notificação para cobrar o aluno pelas disciplinas do semestre |

## Diagrama (Mermaid)

```mermaid
flowchart LR
  Secretaria((Secretaria))
  Aluno((Aluno))
  Professor((Professor))
  Cobranca(("Sistema de<br/>Cobranças"))

  subgraph Sistema["Sistema de Matrículas"]
    UC01["UC01 Autenticar-se"]
    UC02["UC02 Manter cursos"]
    UC03["UC03 Manter disciplinas"]
    UC04["UC04 Manter professores"]
    UC05["UC05 Manter alunos"]
    UC06["UC06 Gerar currículo do semestre"]
    UC07["UC07 Matricular-se em disciplinas"]
    UC08["UC08 Cancelar matrícula"]
    UC09["UC09 Consultar próprias matrículas"]
    UC10["UC10 Consultar alunos por disciplina"]
    UC11["UC11 Encerrar período e definir ofertas"]
    UC12["UC12 Notificar sistema de cobranças"]
    UC13["UC13 Validar período de matrículas"]
    UC14["UC14 Encerrar vagas da disciplina"]
  end

  Secretaria --> UC01
  Secretaria --> UC02
  Secretaria --> UC03
  Secretaria --> UC04
  Secretaria --> UC05
  Secretaria --> UC06
  Secretaria --> UC11

  Aluno --> UC01
  Aluno --> UC07
  Aluno --> UC08
  Aluno --> UC09

  Professor --> UC01
  Professor --> UC10

  UC07 -.include.-> UC13
  UC08 -.include.-> UC13
  UC07 -.include.-> UC14
  UC07 -.include.-> UC12
  UC11 -.include.-> UC12

  UC12 --> Cobranca
```

## Diagrama UML (PlantUML)

Arquivo equivalente: [`diagrama-casos-de-uso.puml`](./diagrama-casos-de-uso.puml). Pode ser renderizado em [PlantUML](https://www.plantuml.com/plantuml/uml/) ou em editores com extensão PlantUML.

```plantuml
@startuml Sistema de Matriculas - Casos de Uso
left to right direction
skinparam actorStyle awesome
skinparam packageStyle rectangle

actor Secretaria
actor Aluno
actor Professor
actor "Sistema de Cobrancas" as Cobranca

rectangle "Sistema de Matriculas" {
  usecase "UC01 Autenticar-se" as UC01
  usecase "UC02 Manter cursos" as UC02
  usecase "UC03 Manter disciplinas" as UC03
  usecase "UC04 Manter professores" as UC04
  usecase "UC05 Manter alunos" as UC05
  usecase "UC06 Gerar curriculo do semestre" as UC06
  usecase "UC07 Matricular-se em disciplinas" as UC07
  usecase "UC08 Cancelar matricula" as UC08
  usecase "UC09 Consultar proprias matriculas" as UC09
  usecase "UC10 Consultar alunos por disciplina" as UC10
  usecase "UC11 Encerrar periodo e definir ofertas" as UC11
  usecase "UC12 Notificar sistema de cobrancas" as UC12
  usecase "UC13 Validar periodo de matriculas" as UC13
  usecase "UC14 Encerrar vagas da disciplina" as UC14
}

Secretaria --> UC01
Secretaria --> UC02
Secretaria --> UC03
Secretaria --> UC04
Secretaria --> UC05
Secretaria --> UC06
Secretaria --> UC11

Aluno --> UC01
Aluno --> UC07
Aluno --> UC08
Aluno --> UC09

Professor --> UC01
Professor --> UC10

UC07 ..> UC13 : <<include>>
UC08 ..> UC13 : <<include>>
UC07 ..> UC14 : <<include>>
UC07 ..> UC12 : <<include>>
UC11 ..> UC12 : <<include>>

UC12 --> Cobranca
@enduml
```

## Relacionamentos

- **Include UC13** em UC07 e UC08: matrícula e cancelamento só ocorrem no período de matrículas.
- **Include UC14** em UC07: ao atingir 60 alunos, novas inscrições naquela disciplina são recusadas.
- **Include UC12** em UC07: após o aluno inscrever-se no semestre, o sistema de cobranças é notificado.
- **Include UC12** em UC11: ao fechar o período, cobranças podem ser alinhadas às disciplinas que permaneceram ativas (mínimo de 3 alunos) e às que foram canceladas.

## Catálogo de casos de uso

| ID | Caso de uso | Ator principal | Resumo |
|----|-------------|----------------|--------|
| UC01 | Autenticar-se | Secretaria, Aluno, Professor | Validar login e senha antes de qualquer operação |
| UC02 | Manter cursos | Secretaria | Cadastrar e atualizar curso (nome, créditos e disciplinas) |
| UC03 | Manter disciplinas | Secretaria | Cadastrar e atualizar disciplinas do currículo |
| UC04 | Manter professores | Secretaria | Cadastrar e atualizar professores |
| UC05 | Manter alunos | Secretaria | Cadastrar e atualizar alunos |
| UC06 | Gerar currículo do semestre | Secretaria | Montar a oferta de disciplinas do semestre |
| UC07 | Matricular-se em disciplinas | Aluno | Inscrever-se em até 4 obrigatórias e 2 optativas |
| UC08 | Cancelar matrícula | Aluno | Desfazer inscrição feita no período vigente |
| UC09 | Consultar próprias matrículas | Aluno | Ver disciplinas em que está inscrito |
| UC10 | Consultar alunos por disciplina | Professor | Listar alunos matriculados em cada disciplina |
| UC11 | Encerrar período e definir ofertas | Secretaria | Fechar matrículas; ativar disciplina se ≥ 3 inscritos, senão cancelar |
| UC12 | Notificar sistema de cobranças | Sistema | Informar cobranças das disciplinas do semestre do aluno |
| UC13 | Validar período de matrículas | Sistema | Impedir matrícula/cancelamento fora da janela |
| UC14 | Encerrar vagas da disciplina | Sistema | Encerrar inscrições da disciplina ao atingir 60 alunos |

As histórias de usuário correspondentes estão em [`historias-de-usuario.md`](./historias-de-usuario.md).
