# Projeto - Cidades ESGInteligentes | EcoTrack API

API RESTful para gestão de resíduos, coletas e indicadores ambientais, desenvolvida como projeto acadêmico de ESG e evoluída com práticas completas de DevOps.

## Integrantes

- Matheus Campanatti Fabricio — RM563852
- Mateus Felipe Reis Coelho — RM566177
- Otávio Nobrega da Silva — RM566082
- Paulo Mota Budacs — RM564015

## Projeto — Cidades ESG Inteligentes

O EcoTrack apoia o acompanhamento de resíduos e reciclagem por meio de uma API que permite registrar resíduos, gerenciar coletas, calcular o impacto estimado de CO₂ evitado e gerar indicadores ambientais. A solução utiliza autenticação JWT e banco de dados Oracle.

### Funcionalidades principais

- cadastro, consulta, atualização e exclusão de resíduos;
- cadastro, consulta, atualização e exclusão de coletas;
- cálculo de CO₂ evitado;
- consolidação de indicadores ESG;
- geração de relatório ambiental;
- autenticação e autorização com JWT;
- documentação interativa da API com Swagger/OpenAPI;
- healthcheck com Spring Boot Actuator.

## Como executar localmente com Docker

### Pré-requisitos

- Docker;
- Docker Compose.

### 1. Preparar as variáveis de ambiente

Na raiz do projeto, copie o arquivo de exemplo:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

O arquivo `.env.example` contém valores destinados exclusivamente ao ambiente local. Para outros ambientes, substitua senhas e segredos.

### 2. Subir a aplicação

```bash
docker compose up --build -d
```

O Docker Compose cria a rede, o volume persistente do Oracle, o banco e a aplicação. A API só inicia após o healthcheck do banco indicar que o Oracle está disponível.

### 3. Verificar os containers

```bash
docker compose ps
```

### 4. Acessar a aplicação

API:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

Healthcheck:

```text
http://localhost:8080/actuator/health
```

### 5. Encerrar o ambiente

```bash
docker compose down
```

Para remover também o volume do banco:

```bash
docker compose down -v
```

## Autenticação

No ambiente local, o Docker Compose utiliza as configurações do `.env`. Com o arquivo de exemplo, é criado um administrador para facilitar os testes:

```text
E-mail: admin@ecotrack.com
Senha: admin123
```

Login:

```http
POST /auth/login
```

Exemplo:

```json
{
  "email": "admin@ecotrack.com",
  "senha": "admin123"
}
```

Os endpoints de resíduos, coletas, indicadores e relatórios exigem o header:

```text
Authorization: Bearer SEU_TOKEN
```

## Testes automatizados

A suíte utiliza JUnit 5, Mockito e AssertJ.

Execução:

```bash
mvn clean verify
```

Testes implementados:

- `ResiduoServiceTest`;
- `ColetaServiceTest`;
- `IndicadorServiceTest`;
- `AuthServiceTest`.

Os relatórios Maven Surefire são gerados em:

```text
target/surefire-reports/
```

## Pipeline CI/CD

A automação está em:

```text
.github/workflows/ci-cd.yml
```

O pipeline utiliza GitHub Actions e possui quatro jobs principais:

```text
Build e testes
      ↓
Build e publicação da imagem Docker
      ↓
Deploy staging
      ↓
Deploy produção
```

### Build e testes

Em pull requests e pushes para `main`, o pipeline:

1. baixa o código;
2. configura Java 21;
3. executa `mvn clean verify`;
4. publica os relatórios de testes como artefato do workflow.

### Imagem Docker

Em push para `main` ou execução manual do workflow, após os testes:

1. o pipeline autentica no GitHub Container Registry;
2. constrói a imagem com o `Dockerfile` do projeto;
3. publica uma tag com o SHA do commit;
4. atualiza também a tag `latest`.

A promoção entre ambientes utiliza a mesma imagem identificada pelo SHA do commit.

### Deploy staging

Após a publicação da imagem, o job `Deploy staging` inicia um ambiente isolado em um runner do GitHub Actions utilizando `deploy/docker-compose.staging.yml`. O ambiente contém a API e um Oracle XE dedicados ao staging.

A aplicação utiliza:

```text
SPRING_PROFILES_ACTIVE=staging
Porta externa no runner: 8081
```

As credenciais do ambiente são geradas durante a execução do workflow e não ficam versionadas. O pipeline aguarda o healthcheck do container, executa smoke tests no Actuator e no Swagger e registra evidências da execução.

### Deploy produção

O job de produção somente começa após o sucesso do staging. A mesma imagem Docker identificada pelo SHA do commit é promovida para um novo ambiente isolado de produção, garantindo que o artefato validado seja exatamente o artefato implantado.

A aplicação utiliza:

```text
SPRING_PROFILES_ACTIVE=production
Porta externa no runner: 8080
```

O ambiente de produção também executa healthcheck, smoke test e coleta de evidências antes de ser encerrado.

### Ambientes e evidências automatizadas

O workflow referencia dois GitHub Environments:

```text
staging
production
```

Os ambientes são executados de forma efêmera e isolada nos runners do GitHub Actions. Essa estratégia permite reproduzir o deploy completo sem armazenar credenciais de infraestrutura externa.

Para cada ambiente, o pipeline produz artefatos com:

- resposta do endpoint `/actuator/health`;
- estado dos containers do Docker Compose;
- captura real da página Swagger da aplicação em execução.

A promoção para produção ocorre somente se todas as etapas de staging forem concluídas com sucesso.

## Containerização

O projeto utiliza build multi-stage. A primeira imagem contém Maven e compila o projeto; a imagem final utiliza apenas Java 21 JRE, reduzindo dependências em runtime.

Conteúdo do `Dockerfile`:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 1001 --user-group ecotrack

COPY --from=build /workspace/target/*.jar app.jar
RUN chown ecotrack:ecotrack /app/app.jar

USER ecotrack
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD curl --fail --silent --show-error http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Orquestração

O `docker-compose.yml` local contém:

- aplicação Spring Boot;
- Oracle XE 21;
- volume persistente para `/opt/oracle/oradata`;
- rede `ecotrack-network`;
- variáveis de ambiente;
- healthcheck do banco;
- healthcheck da aplicação.

Os ambientes utilizados pelo pipeline são definidos em:

```text
deploy/docker-compose.staging.yml
deploy/docker-compose.production.yml
```

Cada ambiente possui banco, volume, rede e container próprios. Durante o CI/CD, cada composição é executada em um runner isolado do GitHub Actions.

## Prints do funcionamento

As evidências da execução devem ser adicionadas à pasta:

```text
docs/evidencias/
```

Padrão de arquivos:

```text
01-build-testes.png
02-docker-image.png
03-deploy-staging.png
04-staging-funcionando.png
05-deploy-production.png
06-production-funcionando.png
```

A documentação técnica que acompanha essas evidências está em:

```text
docs/Documentacao_Tecnica.md
```

## Collection Postman

Arquivo:

```text
postman/EcoTrack-API-FIAP.postman_collection.json
```

A collection contém autenticação, resíduos, coletas, indicadores e relatório ambiental. A variável `baseUrl` está configurada inicialmente para `http://localhost:8080`.

## Principais endpoints

### Autenticação

- `POST /auth/register`
- `POST /auth/login`

### Resíduos

- `GET /residuos`
- `GET /residuos/{id}`
- `POST /residuos`
- `PUT /residuos/{id}`
- `DELETE /residuos/{id}`

### Coletas

- `GET /coletas`
- `GET /coletas/{id}`
- `POST /coletas`
- `PUT /coletas/{id}`
- `DELETE /coletas/{id}`

### Indicadores e relatórios

- `GET /indicadores`
- `POST /relatorios/ambiental`

### Observabilidade

- `GET /actuator/health`

## Tecnologias utilizadas

- Java 21;
- Spring Boot 3.3.5;
- Spring Web;
- Spring Data JPA;
- Spring Security;
- JWT;
- Spring Boot Actuator;
- Oracle Database XE 21;
- Flyway;
- Maven;
- JUnit 5;
- Mockito;
- AssertJ;
- Docker;
- Docker Compose;
- GitHub Actions;
- GitHub Container Registry;
- Swagger/OpenAPI;
- Postman.

## Estrutura do projeto

```text
EcoTrack-DevOps-FIAP/
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── deploy/
│   ├── docker-compose.staging.yml
│   └── docker-compose.production.yml
├── docs/
│   ├── Documentacao_Tecnica.md
│   └── evidencias/
├── postman/
│   └── EcoTrack-API-FIAP.postman_collection.json
├── scripts/
│   └── wait-for-health.sh
├── src/
│   ├── main/
│   └── test/
├── .dockerignore
├── .env.example
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Checklist de entrega

| Item | OK |
|---|:---:|
| Projeto compactado em `.ZIP` com estrutura organizada | ☑ |
| Dockerfile funcional | ☑ |
| `docker-compose.yml` ou arquivos Kubernetes | ☑ |
| Pipeline com etapas de build, teste e deploy | ☑ |
| README.md com instruções | ☑ |
| Testes automatizados integrados ao pipeline | ☑ |
| Volumes, variáveis de ambiente e redes configurados | ☑ |
| Configuração separada de staging e produção | ☑ |
| Documentação técnica preparada | ☑ |
| Prints reais do pipeline e dos ambientes anexados | ☐ |
| Deploy executado em staging e produção | ☐ |
