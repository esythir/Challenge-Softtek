# 🚀 Challenge Softtek - API de Monitoramento de Bem-estar

API para monitoramento de bem-estar e clima organizacional com arquitetura limpa e integração Firebase.

## 🏗️ Arquitetura

- **Clean Architecture** com separação clara de responsabilidades
- **Spring Boot 3.4.5** com Java 21
- **Firebase Firestore** para persistência de dados
- **JWT Authentication** com Firebase Auth
- **Swagger UI** para documentação da API

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.6+
- Conta Firebase (opcional para desenvolvimento)

### Desenvolvimento (com mocks) - RECOMENDADO
```bash
# Clonar o repositório
git clone <repository-url>
cd Challenge-Softtek

# Executar com mocks (sem Firebase)
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=dev

# OU usar o script
test-mocks.bat
```

### Produção (com Firebase)
```bash
# 1. Configurar Firebase (ver setup-firebase.md)
# 2. Configurar regras do Firestore (ver setup-firebase-rules.md)
# 3. Executar com Firebase real
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod

# OU usar o script
test-firebase.bat
```

### Scripts Disponíveis
- `test-mocks.bat` - Executa com mocks (desenvolvimento)
- `test-firebase.bat` - Executa com Firebase real (produção)

## 📚 Documentação da API

Após executar a aplicação, acesse:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 🔐 Autenticação

### Desenvolvimento
Use tokens no formato: `Bearer test-userId`

Exemplo:
```bash
curl -H "Authorization: Bearer test-user-123" http://localhost:8080/api/forms
```

### Produção
Use tokens Firebase reais obtidos do Firebase Auth.

## 📊 Endpoints Principais

### Formulários
- `GET /api/forms` - Listar formulários disponíveis
- `POST /api/forms/{formCode}/submit` - Submeter resposta

### Check-ins
- `GET /api/checkins` - Listar check-ins do usuário
- `GET /api/checkins/weekly` - Resumo semanal
- `GET /api/checkins/monthly-summary` - Resumo mensal
- `GET /api/checkins/mood-distribution` - Distribuição de humor

### Análises
- `GET /analysis/workload-alerts` - Alertas de carga de trabalho
- `GET /analysis/climate-diagnosis` - Diagnóstico de clima

### Preferências
- `GET /api/preferences` - Obter preferências do usuário
- `PUT /api/preferences` - Atualizar preferências
- `POST /api/preferences/notifications/toggle` - Toggle notificações

## 🗄️ Estrutura do Banco (Firestore)

### Coleções
- `forms` - Formulários disponíveis
- `form_responses` - Respostas dos usuários
- `user_preferences` - Preferências dos usuários

### Tipos de Formulário
- `CHECKIN` - Check-in diário
- `SELF_ASSESSMENT` - Autoavaliação de carga
- `CLIMATE` - Pesquisa de clima
- `REPORT` - Canal de escuta

## 🛠️ Configuração

### Variáveis de Ambiente
```yaml
# application.yaml
firebase:
  enabled: true  # true para produção, false para desenvolvimento

spring:
  profiles:
    active: dev  # dev, prod
```

### Firebase
Para configurar o Firebase, siga as instruções em `setup-firebase.md`.

## 🧪 Testes

```bash
# Executar testes
.\mvnw.cmd test

# Executar com cobertura
.\mvnw.cmd test jacoco:report
```

## 📦 Build

```bash
# Compilar
.\mvnw.cmd clean compile

# Package
.\mvnw.cmd clean package

# Docker
docker build -t challenge-softtek .
```

## 🚀 Deploy

### Docker
```bash
docker run -p 8080:8080 challenge-softtek
```

### Heroku
```bash
# Configurar variáveis de ambiente
heroku config:set FIREBASE_CREDENTIALS="$(cat firebase-service-account.json)"

# Deploy
git push heroku main
```

## 📝 Logs

A aplicação usa logging estruturado com diferentes níveis:

- **DEBUG**: Desenvolvimento
- **INFO**: Produção
- **WARN**: Avisos
- **ERROR**: Erros

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 🆘 Suporte

Para dúvidas ou problemas:
1. Verifique a documentação da API no Swagger UI
2. Consulte os logs da aplicação
3. Abra uma issue no repositório
