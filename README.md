# Sistema de Matrículas

> Feito por Letícia Figueiredo e Murilo Freitas

Protótipo Java (Lab01S03) do Sistema de Matrículas da universidade: interface em **linha de comando** e persistência em **arquivos**.

## Documentação

- [Diagrama de Casos de Uso](docs/diagrama-casos-de-uso.md)
- [Histórias de Usuário](docs/historias-de-usuario.md)
- [Diagrama de Classes](docs/diagrama-de-classes.md)

## Requisitos cobertos

- Secretaria mantém cursos (nome, créditos e disciplinas), disciplinas, professores e alunos, e gera o currículo do semestre.
- Aluno autentica-se e, **somente no período de matrículas**, inscreve-se em até **4 obrigatórias** e **2 optativas**, ou cancela matrícula.
- Disciplina com **60** inscritos encerra novas vagas; ao **fim do período**, oferta com **≥ 3** alunos fica **ativa**, senão é **cancelada**.
- Após inscrição no semestre, o **sistema de cobranças** é notificado (`data/cobrancas.log`).
- Professor consulta alunos matriculados em cada disciplina.
- Login e senha para todos os perfis.

## Como executar

Requer Java 21 e Maven.

```bash
mvn -q compile exec:java
```

Ou:

```bash
mvn -q package
java -jar target/sistema-matriculas-1.0.0.jar
```

Dados ficam em `data/` (CSV + `cobrancas.log`). Na primeira execução o sistema cria usuários e um currículo de demonstração (semestre **2026.2**, período **aberto**).

### Usuários iniciais

| Perfil | Login | Senha |
|--------|--------|--------|
| Secretaria | `secretaria` | `123` |
| Professor | `prof` | `123` |
| Aluno | `aluno1`, `aluno2`, `aluno3` | `123` |

Para recomeçar do zero, apague a pasta `data/`.
