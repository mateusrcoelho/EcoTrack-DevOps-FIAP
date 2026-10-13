# EcoTrack API — Documentação Técnica DevOps

## Integrantes

- Matheus Campanatti Fabricio — RM563852
- Mateus Felipe Reis Coelho — RM566177
- Otávio Nobrega da Silva — RM566082
- Paulo Mota Budacs — RM564015

## 1. Visão geral do projeto

O EcoTrack é uma API REST com foco ESG para gestão de resíduos, coletas e indicadores ambientais. A solução registra materiais, acompanha coletas, calcula o impacto estimado de CO₂ evitado e gera indicadores e relatórios ambientais.

## 2. Pipeline CI/CD

O pipeline foi implementado com GitHub Actions e é acionado em pull requests, pushes para a branch `main` e também por execução manual.

Fluxo principal:

1. Checkout do código.
2. Configuração do Java 21.
3. Build Maven e execução dos testes automatizados.
4. Geração da imagem Docker.
5. Publicação da imagem no GitHub Container Registry.
6. Deploy automatizado no ambiente de staging.
7. Healthcheck e smoke tests de staging.
8. Promoção da mesma imagem para produção.
9. Healthcheck e smoke tests de produção.
10. Publicação das evidências como artefatos do workflow.

Em pull requests são executadas as etapas de build e testes. Os deploys ocorrem em `main`, garantindo que somente uma versão validada seja promovida.

## 3. Containerização e orquestração

A aplicação utiliza Docker com build multi-stage. A etapa de build utiliza Maven e Java 21; a imagem final contém Java 21 JRE, o JAR da aplicação e o utilitário usado pelo healthcheck.

O Docker Compose orquestra dois serviços:

- `app`: EcoTrack API.
- `oracle`: Oracle Database XE 21.

A configuração inclui:

- volumes persistentes para o banco durante a execução do ambiente;
- rede Docker dedicada;
- variáveis de ambiente;
- healthchecks;
- dependência da aplicação em relação à saúde do banco;
- composições separadas para staging e produção.

## 4. Testes automatizados

A suíte utiliza JUnit 5, Mockito e AssertJ. Foram implementados testes unitários para:

- `ResiduoService`;
- `ColetaService`;
- `IndicadorService`;
- `AuthService`.

A execução ocorre com `mvn clean verify`. Os relatórios Surefire são publicados como artefato do workflow.

## 5. Estratégia de ambientes

A mesma imagem imutável, identificada pelo SHA do commit, é promovida entre os ambientes.

- Staging: profile `staging`, porta 8081.
- Produção: profile `production`, porta 8080.

Cada ambiente é iniciado em um runner isolado do GitHub Actions com banco, volume e rede próprios. As credenciais temporárias são geradas no momento da execução e descartadas após o encerramento do job.

Essa abordagem mantém a separação entre staging e produção e permite reproduzir o processo de implantação sem depender de credenciais ou servidores externos para a avaliação.

## 6. Evidências

O pipeline gera e publica evidências reais dos ambientes em execução.

Staging:

- `04-staging-funcionando.png`;
- `staging-health.json`;
- `staging-containers.txt`.

Produção:

- `06-production-funcionando.png`;
- `production-health.json`;
- `production-containers.txt`.

Também são utilizados prints da interface do GitHub Actions para registrar o sucesso das etapas de build, testes, publicação da imagem, staging e produção.

## 7. Desafios e soluções adotadas

### Inicialização do banco

O Oracle necessita de um tempo de inicialização superior ao da aplicação. Foi configurado healthcheck no banco e `depends_on` com condição de serviço saudável para impedir que a API tente se conectar antes de o Oracle estar pronto.

### Segurança de configurações

As credenciais deixaram de ser mantidas no código. No ambiente local são utilizadas variáveis do arquivo `.env`, enquanto nos ambientes automatizados o workflow gera credenciais temporárias em tempo de execução.

### Consistência entre ambientes

A imagem Docker é construída uma única vez e publicada no GitHub Container Registry. Staging e produção utilizam exatamente a mesma tag por SHA do commit; somente profiles, portas, redes, volumes e credenciais mudam entre os ambientes.

### Validação do deploy

O Spring Boot Actuator disponibiliza `/actuator/health`. O script `scripts/wait-for-health.sh` aguarda o container atingir o estado `healthy`. Em seguida, o pipeline executa smoke tests e captura a interface Swagger com Playwright, produzindo evidências reais da aplicação em funcionamento.
