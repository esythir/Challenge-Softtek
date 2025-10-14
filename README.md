# Projeto - Cidades ESGInteligentes

Sistema de gestão ESG (Environmental, Social, and Governance) desenvolvido em Java Spring Boot com integração Firebase e Google OAuth2.

## 🚀 Como executar localmente com Docker

### Pré-requisitos
- Docker e Docker Compose instalados
- Git

### Passos para execução

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd Challenge-Softtek
   ```

2. **Configure as variáveis de ambiente (opcional)**
   ```bash
   cp env.example .env
   # Edite o arquivo .env com suas credenciais (opcional para desenvolvimento)
   ```

3. **Execute com Docker Compose**
   ```bash
   # Subir todos os serviços
   docker-compose up -d
   
   # Verificar logs
   docker-compose logs -f app
   
   # Parar os serviços
   docker-compose down
   ```

4. **Acesse a aplicação**
   - Aplicação: http://localhost:8080
   - Banco PostgreSQL: localhost:5432

### Comandos úteis

```bash
# Rebuild da aplicação
docker-compose up --build app

# Executar apenas o banco
docker-compose up postgres

# Verificar status dos containers
docker-compose ps

# Acessar logs específicos
docker-compose logs app
docker-compose logs postgres
```

## 🔄 Pipeline CI/CD

### Ferramentas utilizadas
- **GitHub Actions**: Orquestração do pipeline
- **Docker**: Containerização da aplicação
- **GitHub Container Registry**: Armazenamento das imagens
- **PostgreSQL**: Banco de dados para testes

### Etapas do pipeline

1. **Build e Testes** (`build-and-test`)
   - Checkout do código
   - Configuração do Java 21
   - Cache de dependências Maven
   - Execução de testes unitários com PostgreSQL
   - Build da aplicação
   - Upload de artifacts

2. **Build da Imagem Docker** (`build-docker`)
   - Build da imagem Docker
   - Push para GitHub Container Registry
   - Cache de layers Docker

3. **Deploy Staging** (`deploy-staging`)
   - Deploy automático na branch `develop`
   - Health check do ambiente

4. **Deploy Produção** (`deploy-production`)
   - Deploy automático na branch `main`
   - Health check do ambiente

### Funcionamento
- **Push para `develop`**: Deploy automático em staging
- **Push para `main`**: Deploy automático em produção
- **Pull Request**: Execução de testes e build

## 🐳 Containerização

### Dockerfile

```dockerfile
# Multi-stage build para otimizar a imagem
FROM maven:3.9.6-openjdk-21-slim AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de dependências primeiro (para cache de layers)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage de produção
FROM openjdk:21-jre-slim

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR da aplicação do stage de build
COPY --from=build /app/target/challenge-softteck-*.jar app.jar

# Mudar para usuário não-root
USER appuser

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Estratégias adotadas

1. **Multi-stage build**: Reduz o tamanho da imagem final
2. **Cache de layers**: Otimiza o tempo de build
3. **Usuário não-root**: Melhora a segurança
4. **Health check**: Monitora a saúde da aplicação

### Docker Compose

O arquivo `docker-compose.yml` orquestra:
- **PostgreSQL**: Banco de dados com health checks
- **Spring Boot App**: Aplicação principal com dependências

## 📸 Prints do funcionamento

### Pipeline em execução
![Pipeline CI/CD](docs/pipeline-execution.png)
*Pipeline executando com sucesso - Build, Testes e Deploy*

### Deploy em Staging
![Staging Environment](docs/staging-deploy.png)
*Deploy automático para ambiente de staging*

### Deploy em Produção
![Production Environment](docs/production-deploy.png)
*Deploy automático para ambiente de produção*

### Health Checks
![Health Checks](docs/health-checks.png)
*Health checks funcionando corretamente*

### Containerização
![Docker Build](docs/docker-build.png)
*Build da imagem Docker com sucesso*

## 🛠 Tecnologias utilizadas

### Backend
- **Java 21**: Linguagem de programação
- **Spring Boot 3.4.5**: Framework principal
- **Spring Security**: Autenticação e autorização
- **Spring OAuth2**: Integração com Google
- **MapStruct**: Mapeamento entre objetos
- **Lombok**: Redução de boilerplate
- **OpenAPI/Swagger**: Documentação da API

### Banco de Dados
- **PostgreSQL 15**: Banco de dados principal
- **Flyway**: Migração de banco de dados

### Autenticação
- **Firebase Admin SDK**: Autenticação Firebase
- **Google OAuth2**: Login social
- **JWT**: Tokens de autenticação

### DevOps
- **Docker**: Containerização
- **Docker Compose**: Orquestração local
- **GitHub Actions**: CI/CD
- **GitHub Container Registry**: Registry de imagens

### Monitoramento
- **Spring Boot Actuator**: Health checks e métricas
- **Docker Health Checks**: Monitoramento de containers

## 📁 Estrutura do Projeto

```
challenge-softteck/
├── .github/workflows/          # Pipelines CI/CD
├── src/main/java/              # Código fonte Java
├── src/main/resources/         # Recursos da aplicação
├── src/test/                   # Testes
├── Dockerfile                  # Imagem Docker
├── docker-compose.yml          # Orquestração local
├── env.example                 # Variáveis de ambiente
└── README.md                   # Este arquivo
```

## 🔧 Configuração de Ambiente

### Variáveis de Ambiente Necessárias

```bash
# Google OAuth (opcional para desenvolvimento)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Firebase (opcional para desenvolvimento)
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_SERVICE_ACCOUNT_KEY=firebase-service-account.json

# Database
POSTGRES_DB=challenge_softteck
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres123

# Application
SPRING_PROFILES_ACTIVE=dev
```

## 🚀 Deploy

### Staging
- Deploy automático na branch `develop`
- Ambiente: https://staging.challenge-softteck.com

### Produção
- Deploy automático na branch `main`
- Ambiente: https://challenge-softteck.com

## 📊 Monitoramento

### Health Checks
- **Aplicação**: `/actuator/health`
- **Docker**: Health check configurado no Dockerfile
- **Pipeline**: Verificação automática após deploy

### Logs
- **Aplicação**: `docker-compose logs -f app`
- **Pipeline**: GitHub Actions logs

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👥 Autores

- **Seu Nome** - *Desenvolvimento* - [SeuGitHub](https://github.com/seuusuario)

## 📞 Suporte

Para suporte, envie um email para seu-email@exemplo.com ou abra uma issue no GitHub.