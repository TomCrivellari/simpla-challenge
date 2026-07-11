# Simpla Meta

Aplicação web de gestão financeira pessoal baseada em microsserviços. O projeto permite criar uma conta, registrar receitas e despesas, acompanhar o resumo financeiro, criar metas com aportes e conversar com um assistente de IA que utiliza o contexto financeiro do usuário.

## Funcionalidades

- Cadastro, login e consulta do usuário autenticado;
- autenticação stateless com JWT;
- cadastro, edição, consulta e exclusão de receitas e despesas;
- dashboard com saldo, totais e dados consolidados;
- criação e acompanhamento de metas financeiras;
- registro de aportes em metas, integrado ao histórico de transações;
- projeção de progresso das metas;
- assistente financeiro integrado à API Gemini;
- rotas privadas no frontend;
- documentação OpenAPI/Swagger individual e agregada pelo gateway.

## Arquitetura

```text
Frontend React (Vite) :5173
          |
          v
API Gateway :8084
    |          |          |          |
    v          v          v          v
 Auth       Finance      Meta        AI
 :8081      :8082        :8083       :8085
    |          |            |          |
 Postgres   Postgres        |          +--> Gemini API
 :5433      :5434           |          |
                            +----------+--> Finance Service
                            |
                         Postgres
                         :5435
```

O frontend acessa somente o gateway pelo prefixo `/api/v1`. O gateway encaminha cada rota ao serviço responsável e valida os tokens JWT. Os serviços de Finance, Meta e AI também validam o token. O Meta Service cria e remove transações relacionadas aos aportes, enquanto o AI Service consulta o dashboard e as metas para montar o contexto do assistente.

## Serviços e portas

| Módulo | Responsabilidade | Porta |
| --- | --- | ---: |
| `simpla-meta-frontend` | Interface React | `5173` |
| `gateway-service` | Ponto de entrada, roteamento e Swagger agregado | `8084` |
| `auth-service` | Usuários, login e emissão de JWT | `8081` |
| `finance-service` | Transações e dashboard | `8082` |
| `meta-service` | Metas, aportes e projeções | `8083` |
| `ai-service` | Assistente financeiro com Gemini | `8085` |
| PostgreSQL Auth | Banco `auth_db` | `5433` |
| PostgreSQL Finance | Banco `finance_db` | `5434` |
| PostgreSQL Meta | Banco `meta_db` | `5435` |

## Tecnologias

### Backend

- Java 21;
- Spring Boot 4.0.6;
- Spring Security e OAuth2 Resource Server;
- Spring Data JPA;
- Spring Cloud Gateway MVC;
- OpenFeign para comunicação entre serviços;
- PostgreSQL 17;
- JWT com HMAC;
- Bean Validation;
- Springdoc OpenAPI 3;
- Maven Wrapper;
- JUnit e Mockito.

### Frontend

- React 19;
- Vite 6;
- React Router 7;
- Tailwind CSS 4;
- Bootstrap e React Bootstrap;
- React Google Charts;
- ESLint 9.

## Estrutura do repositório

```text
.
├── ai-service/                 # Integração com Gemini e contexto financeiro
├── auth-service/               # Cadastro, login, usuários e JWT
├── finance-service/            # Transações e dashboard
├── gateway-service/            # API Gateway e Swagger agregado
├── meta-service/               # Metas e aportes
└── simpla-meta-frontend/       # SPA React/Vite
```

Os serviços backend seguem uma separação por `controller`, `service`, `repository`, `model`, `dto`, `config`, `client` e `exception`, conforme a necessidade de cada módulo.

## Pré-requisitos

Para a execução recomendada de toda a aplicação, instale apenas:

- Docker com Docker Compose.

Para executar os módulos manualmente durante o desenvolvimento, também são necessários:

- Java JDK 21;
- Node.js 20 ou superior;
- npm 10 ou superior;
- uma chave da API Gemini, caso queira utilizar o assistente de IA.

Não é necessário instalar Maven globalmente, pois cada serviço contém Maven Wrapper (`mvnw`).

## Como executar localmente

### Início rápido: aplicação completa com Docker

Este é o fluxo recomendado para avaliar o projeto. Ele inicia o frontend, os cinco serviços backend e os três bancos de dados com um único comando.

Clone e acesse o projeto:

```bash
git clone <URL_DO_REPOSITORIO>
cd simpla-challenge
```

Opcionalmente, crie o arquivo de configuração para habilitar o assistente de IA:

```bash
cp .env.example .env
```

Edite o `.env` e preencha `GEMINI_API_KEY`. Sem essa chave, toda a aplicação continua funcionando, exceto as respostas do assistente de IA.

Inicie todo o ambiente:

```bash
docker compose up --build
```

Na primeira execução, o Docker fará o download das imagens e compilará os serviços, portanto o processo pode levar alguns minutos. Quando os containers estiverem ativos, acesse:

- Aplicação: [http://localhost:5173](http://localhost:5173)
- Swagger agregado: [http://localhost:5173/swagger-ui.html](http://localhost:5173/swagger-ui.html)

Para executar em segundo plano:

```bash
docker compose up --build -d
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para encerrar todos os serviços:

```bash
docker compose down
```

Os dados permanecem nos volumes Docker. Para encerrar e apagar também os bancos locais:

```bash
docker compose down --volumes
```

> `docker compose down --volumes` apaga definitivamente os dados persistidos nos três bancos.

### Execução manual para desenvolvimento

Use esta alternativa caso queira executar ou depurar cada módulo separadamente.

#### 1. Inicie os bancos de dados

Na raiz do projeto, execute:

```bash
docker compose -f auth-service/docker-compose.yml up -d
docker compose -f finance-service/docker-compose.yml up -d
docker compose -f meta-service/docker-compose.yml up -d
```

Os arquivos Compose criam bancos e volumes persistentes com as credenciais locais padrão `postgres` / `postgres`.

Confira se os três containers estão ativos:

```bash
docker compose -f auth-service/docker-compose.yml ps
docker compose -f finance-service/docker-compose.yml ps
docker compose -f meta-service/docker-compose.yml ps
```

#### 2. Configure o serviço de IA

Crie o arquivo local a partir do exemplo:

```bash
cp ai-service/.env.example ai-service/.env
```

Edite `ai-service/.env` e informe sua chave:

```dotenv
GEMINI_API_KEY=sua-chave-da-api-gemini
GEMINI_MODEL=gemini-3-flash-preview
```

O restante da aplicação pode funcionar sem a chave, mas as requisições ao assistente de IA não serão concluídas.

#### 3. Inicie os serviços backend

Abra um terminal para cada comando, a partir da raiz do repositório:

```bash
cd auth-service
./mvnw spring-boot:run
```

```bash
cd finance-service
./mvnw spring-boot:run
```

```bash
cd meta-service
./mvnw spring-boot:run
```

```bash
cd ai-service
./mvnw spring-boot:run
```

```bash
cd gateway-service
./mvnw spring-boot:run
```

No Windows, substitua `./mvnw` por `mvnw.cmd`.

> Inicie os serviços a partir das próprias pastas. Isso é especialmente importante para o AI Service, que carrega o arquivo `.env` do diretório atual.

#### 4. Inicie o frontend

Em outro terminal:

```bash
cd simpla-meta-frontend
npm install
npm run dev
```

Acesse [http://localhost:5173](http://localhost:5173). Durante o desenvolvimento, o Vite encaminha chamadas iniciadas por `/api` para o gateway em `http://localhost:8084`.

## Variáveis de ambiente

Todos os valores possuem padrões adequados ao ambiente local, exceto a chave Gemini.

| Variável | Serviço(s) | Padrão/finalidade |
| --- | --- | --- |
| `SERVER_PORT` | Todos os backends | Sobrescreve a porta do serviço |
| `JWT_SECRET` | Todos os backends | Segredo compartilhado para assinar e validar JWT |
| `JWT_ACCESS_TOKEN_TTL` | Auth | `PT1H` (duração ISO-8601) |
| `DB_URL` | Auth, Finance, Meta | URL JDBC do banco correspondente |
| `DB_USERNAME` | Auth, Finance, Meta | `postgres` |
| `DB_PASSWORD` | Auth, Finance, Meta | `postgres` |
| `AUTH_SERVICE_URL` | Gateway | `http://localhost:8081` |
| `FINANCE_SERVICE_URL` | Gateway, Meta, AI | `http://localhost:8082` |
| `META_SERVICE_URL` | Gateway, AI | `http://localhost:8083` |
| `AI_SERVICE_URL` | Gateway | `http://localhost:8085` |
| `GEMINI_API_KEY` | AI | Chave da API Gemini |
| `GEMINI_API_URL` | AI | Endpoint base da API Gemini |
| `GEMINI_MODEL` | AI | `gemini-3-flash-preview` |
| `VITE_API_URL` | Frontend | `/api/v1` |

> O valor de `JWT_SECRET` precisa ser idêntico no Auth, Gateway, Finance, Meta e AI. O padrão existente serve apenas para desenvolvimento; use um segredo longo e seguro em outros ambientes.

Na execução com Docker Compose, copie o arquivo `.env.example` da raiz para `.env`. O Compose distribui automaticamente `JWT_SECRET`, `GEMINI_API_KEY` e `GEMINI_MODEL` aos containers e configura as URLs internas e os bancos. O arquivo `.env` não é versionado.

Exemplo de execução com configurações personalizadas:

```bash
JWT_SECRET='um-segredo-seguro-compartilhado' SERVER_PORT=8081 ./mvnw spring-boot:run
```

## API

Todas as rotas utilizadas pelo frontend passam pelo gateway em `http://localhost:8084`.

| Método | Endpoint | Descrição | Autenticação |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Cadastra usuário | Não |
| `POST` | `/api/v1/auth/login` | Autentica e retorna JWT | Não |
| `GET` | `/api/v1/auth/me` | Retorna usuário autenticado | JWT |
| `GET` | `/api/v1/dashboard` | Retorna resumo financeiro | JWT |
| `GET` | `/api/v1/transactions` | Lista transações | JWT |
| `GET` | `/api/v1/transactions/{id}` | Consulta uma transação | JWT |
| `POST` | `/api/v1/transactions` | Cria transação | JWT |
| `PUT` | `/api/v1/transactions/{id}` | Atualiza transação | JWT |
| `DELETE` | `/api/v1/transactions/{id}` | Exclui transação | JWT |
| `GET` | `/api/v1/goals` | Lista metas | JWT |
| `GET` | `/api/v1/goals/{id}` | Consulta uma meta | JWT |
| `POST` | `/api/v1/goals` | Cria meta | JWT |
| `PUT` | `/api/v1/goals/{id}` | Atualiza meta | JWT |
| `DELETE` | `/api/v1/goals/{id}` | Exclui meta | JWT |
| `GET` | `/api/v1/goals/{id}/contributions` | Lista aportes | JWT |
| `POST` | `/api/v1/goals/{id}/contributions` | Registra aporte | JWT |
| `DELETE` | `/api/v1/goals/{id}/contributions/{contributionId}` | Remove aporte | JWT |
| `POST` | `/api/v1/ai/chat` | Envia conversa ao assistente | JWT |

Envie o token nas rotas privadas:

```http
Authorization: Bearer <token>
```

### Exemplo de cadastro e login

```bash
curl -X POST http://localhost:8084/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"fullName":"Maria Silva","email":"maria@example.com","password":"senha123"}'
```

```bash
curl -X POST http://localhost:8084/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"maria@example.com","password":"senha123"}'
```

## Documentação Swagger

Com todos os serviços ativos, a documentação agregada fica disponível em:

- Com Docker Compose: [http://localhost:5173/swagger-ui.html](http://localhost:5173/swagger-ui.html)
- Na execução manual: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)

Na execução manual, as documentações individuais também ficam disponíveis:

- Auth: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Finance: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- Meta: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- AI: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

## Rotas do frontend

| Rota | Tela | Acesso |
| --- | --- | --- |
| `/` | Página inicial | Público |
| `/login` | Login | Público |
| `/cadastro` | Cadastro | Público |
| `/principal` | Dashboard | Protegido |
| `/transacoes` | Gestão de transações | Protegido |
| `/metas` | Gestão de metas | Protegido |
| `/assistente` | Assistente financeiro | Protegido |

## Testes e qualidade

Execute os testes de cada serviço a partir da raiz:

```bash
./auth-service/mvnw -f auth-service/pom.xml test
./finance-service/mvnw -f finance-service/pom.xml test
./meta-service/mvnw -f meta-service/pom.xml test
./ai-service/mvnw -f ai-service/pom.xml test
./gateway-service/mvnw -f gateway-service/pom.xml test
```

No frontend:

```bash
cd simpla-meta-frontend
npm run lint
npm run build
```

## Encerrando o ambiente

Se estiver usando a execução completa recomendada:

```bash
docker compose down
```

Se estiver usando a execução manual, interrompa os processos Spring Boot e Vite com `Ctrl+C`. Depois, desligue os bancos:

```bash
docker compose -f auth-service/docker-compose.yml down
docker compose -f finance-service/docker-compose.yml down
docker compose -f meta-service/docker-compose.yml down
```

Os dados permanecem nos volumes Docker. Para também removê-los, acrescente `--volumes` ao respectivo comando `down`; essa operação apaga os dados daquele banco.

## Observações para produção

- Troque obrigatoriamente o `JWT_SECRET` padrão e mantenha-o fora do versionamento;
- não utilize as credenciais padrão dos bancos;
- configure URLs internas dos serviços pelas variáveis de ambiente;
- proteja a chave Gemini em um gerenciador de segredos;
- avalie substituir `spring.jpa.hibernate.ddl-auto=update` por migrações versionadas;
- configure CORS, HTTPS, logs, métricas e políticas de backup conforme o ambiente;
- gere o frontend com `npm run build` e publique o conteúdo de `dist/` em um servidor web.
