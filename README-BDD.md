# FIAP — Testes Automatizados BDD da EcoTrack API

Esta branch mantém o backend original (Java 21 / Spring Boot / Oracle) e acrescenta **20 cenários Gherkin** para as 14 operações REST dos controllers, além do healthcheck.

**Importante:** os testes foram implementados e revisados estaticamente, mas **ainda precisam ser executados localmente**. Não apresentar estes testes como aprovados antes de obter `BUILD SUCCESS` e o relatório Cucumber.

## Requisitos
- Java JDK 21
- Apache Maven 3.9+ (comando `mvn -version`)
- Docker Desktop com Docker Compose e recursos suficientes para Oracle XE
- Portas 8080 disponíveis
- Git (para clonar esta branch)

## Execução local (Windows PowerShell)

```powershell
git clone --branch testes-automatizados-fiap https://github.com/mateusrcoelho/EcoTrack-DevOps-FIAP.git
cd EcoTrack-DevOps-FIAP
Copy-Item .env.example .env
notepad .env
docker info
docker compose up --build -d
docker compose ps
Invoke-RestMethod http://localhost:8080/actuator/health
mvn verify -Pbdd-api -Decotrack.baseUrl=http://localhost:8080
Start-Process .\target\bdd-report.html
```

Configure um arquivo `.env` **somente local**, com credenciais adequadas, e não o envie ao GitHub ou à FIAP. O Oracle XE pode precisar de tempo para iniciar; só execute os testes após o healthcheck retornar `UP`.

Caso o Maven não esteja no PATH e você tenha localizado `mvn.cmd`, use a chamada direta, substituindo o caminho abaixo pelo real:
```powershell
& "C:\Tools\apache-maven-3.10.0\bin\mvn.cmd" verify -Pbdd-api -Decotrack.baseUrl=http://localhost:8080
```

Os resultados são gravados em `target/bdd-report.html`, `target/bdd-report.json`, `target/bdd-report.xml` e relatórios Failsafe/Surefire em `target`.

## Cobertura de API
- `POST /auth/register` — válido e duplicado
- `POST /auth/login` — válido e inválido
- `GET/POST /residuos` e `GET/PUT/DELETE /residuos/{id}` — CRUD e negativos
- `GET/POST /coletas` e `GET/PUT/DELETE /coletas/{id}` — CRUD e negativo
- `GET /indicadores` — indicadores ambientais
- `POST /relatorios/ambiental` — relatório ambiental
- `GET /actuator/health` — pré-condição de disponibilidade e contrato
- Todas as respostas JSON de sucesso ou erro previstas nos cenários possuem validação JSON Schema; listas verificam cada item.
- A exclusão de resíduos retorna texto e a de coletas retorna 204 sem corpo, por isso não exigem JSON.

## Evidências necessárias para a entrega
1. Screenshot de `docker compose ps`, com API e Oracle saudáveis;
2. Screenshot de `/actuator/health` com status `UP`;
3. Screenshot de `mvn verify -Pbdd-api`, incluindo resumo e código de sucesso;
4. Screenshot do relatório HTML com os cenários e resultados;
5. Incluir as evidências reais no PDF final da atividade.

## Cuidados
Os cenários de escrita criam registros no banco local de teste. Evite rodar contra produção. Os registros usam e-mails aleatórios para isolar testes de autenticação. Caso algum cenário falhe, anexe também as mensagens e logs de falha ao relatório.

Para diagnóstico: `docker compose logs --tail=100 oracle app`.

Os testes BDD são ativados **apenas** com o perfil Maven `bdd-api` para não interferir na suíte unitária e no pipeline existente.


## Execução automatizada pelo GitHub Actions

A branch `testes-automatizados-fiap` contém o workflow independente `.github/workflows/fiap-bdd.yml`. Ele é iniciado em cada push nessa branch e **não executa deploy** de staging ou produção.

O workflow realiza: checkout, Java 21, build e testes unitários, Docker Compose com Oracle XE e aplicação, espera `/actuator/health` ficar disponível, executa a suíte BDD e publica o artefato `fiap-bdd-relatorios` com HTML/JSON/XML, relatórios e logs.

Para acompanhar, abra [Actions da EcoTrack](https://github.com/mateusrcoelho/EcoTrack-DevOps-FIAP/actions), localize **FIAP BDD API - validação**, abra a execução da branch e baixe `fiap-bdd-relatorios` ao final. Capturas da tela da execução, do relatório HTML e do healthcheck devem ser anexadas ao PDF de entrega **somente após sucesso real**.

A branch também possui um [pull request em rascunho](https://github.com/mateusrcoelho/EcoTrack-DevOps-FIAP/pull/1), que não deve ser mesclado até a validação dos testes.
