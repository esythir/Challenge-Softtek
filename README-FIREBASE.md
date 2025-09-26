# Challenge Softtek - Firebase + NoSQL Architecture

## 🏗️ Arquitetura Implementada

Este projeto foi refatorado para usar **Clean Architecture + Ports & Adapters** com **Firebase Firestore** como banco de dados NoSQL.

### Estrutura da Arquitetura

```
src/main/java/br/com/fiap/challenge_softteck/
├── domain/                    # Camada de Domínio (Pure)
│   ├── entity/               # Entidades de negócio
│   ├── valueobject/          # Value Objects
│   └── exception/            # Exceções de domínio
├── usecase/                  # Casos de Uso (Application)
│   └── form/                 # Casos de uso de formulários
├── port/                     # Portas (Interfaces)
│   ├── in/                   # Portas de entrada
│   └── out/                  # Portas de saída
│       ├── firebase/         # Portas para Firebase
│       └── external/         # Portas para serviços externos
├── interfaceadapter/         # Adaptadores de Interface
│   ├── in/web/              # Controllers REST
│   ├── out/firebase/        # Repositórios Firestore
│   ├── out/external/        # Adaptadores externos
│   └── common/error/        # Tratamento de erros
└── framework/               # Framework (Infrastructure)
    ├── config/              # Configurações
    ├── firebase/            # Serviços Firebase
    └── security/            # Segurança
```

## 🔧 Configuração do Firebase

### 1. Criar Projeto Firebase

1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Crie um novo projeto chamado `challenge-softteck`
3. Ative o **Firestore Database**
4. Ative o **Authentication**
5. Ative o **Remote Config**

### 2. Configurar Service Account

1. Vá para **Project Settings** > **Service Accounts**
2. Clique em **Generate New Private Key**
3. Baixe o arquivo JSON
4. Substitua o conteúdo de `src/main/resources/firebase-service-account.json`

### 3. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```bash
FIREBASE_PROJECT_ID=challenge-softteck
FIREBASE_SERVICE_ACCOUNT_KEY=firebase-service-account.json
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### 4. Configurar Remote Config

No Firebase Console, vá para **Remote Config** e adicione:

```json
{
  "api_erros_dictionary": {
    "FORM0001": "Formulário não encontrado",
    "FORM0002": "Formulário não está disponível para resposta no momento",
    "FORM0003": "Formulário já foi respondido hoje",
    "RESP0001": "Resposta não encontrada",
    "RESP0002": "Erro ao salvar resposta do formulário",
    "USER0001": "Usuário não encontrado",
    "AUTH0001": "Token de autenticação inválido",
    "CORE0001": "Erro interno do servidor"
  },
  "app_version": "1.0.0",
  "maintenance_mode": false,
  "max_forms_per_user": 10,
  "notification_enabled": true
}
```

## 🚀 Executando o Projeto

### 1. Instalar Dependências

```bash
mvn clean install
```

### 2. Executar Aplicação

```bash
mvn spring-boot:run
```

### 3. Testar Endpoints

```bash
# Listar formulários disponíveis
curl -X GET "http://localhost:8080/api/forms/available?type=CHECKIN" \
  -H "Authorization: Bearer your-firebase-token"

# Submeter resposta
curl -X POST "http://localhost:8080/api/forms/CHECKIN/submit" \
  -H "Authorization: Bearer your-firebase-token" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "questionId": 1,
      "optionId": 1,
      "valueNumeric": null,
      "valueText": null
    }
  ]'
```

## 📊 Estrutura do Firestore

### Coleções

- **forms**: Formulários disponíveis
- **formResponses**: Respostas dos usuários
- **userPreferences**: Preferências dos usuários

### Exemplo de Documento (forms)

```json
{
  "id": "form-001",
  "code": "CHECKIN",
  "name": "Check-in Diário",
  "formType": "CHECKIN",
  "description": "Formulário de check-in diário",
  "active": true,
  "periodicityDays": 1,
  "reminderDays": 1,
  "questions": [
    {
      "id": "q-001",
      "ordinal": 1,
      "text": "Como está seu humor hoje?",
      "questionType": "SCALE",
      "options": [
        {"id": "opt-001", "ordinal": 1, "value": "1", "label": "Muito ruim"},
        {"id": "opt-002", "ordinal": 2, "value": "2", "label": "Ruim"},
        {"id": "opt-003", "ordinal": 3, "value": "3", "label": "Regular"},
        {"id": "opt-004", "ordinal": 4, "value": "4", "label": "Bom"},
        {"id": "opt-005", "ordinal": 5, "value": "5", "label": "Muito bom"}
      ]
    }
  ]
}
```

## 🔒 Autenticação

O projeto usa **Firebase Authentication** com tokens JWT. Para testar:

1. Configure o Firebase Auth no console
2. Gere um token de teste
3. Use o token no header `Authorization: Bearer <token>`

## 🧪 Testes

```bash
# Executar testes unitários
mvn test

# Executar testes de integração
mvn verify
```

## 📝 Logs

Os logs estão configurados para mostrar:
- DEBUG: Aplicação
- INFO: Firebase e Firestore
- WARN: Erros de negócio
- ERROR: Erros críticos

## 🚨 Tratamento de Erros

O sistema usa códigos de erro padronizados:

- **FORM0001-FORM0005**: Erros de formulário
- **RESP0001-RESP0003**: Erros de resposta
- **USER0001-USER0003**: Erros de usuário
- **AUTH0001-AUTH0003**: Erros de autenticação
- **CORE0001-CORE0005**: Erros do sistema

## 🔄 Migração

Para migrar dados existentes:

1. Exporte dados do Oracle
2. Transforme para formato Firestore
3. Importe usando scripts de migração
4. Valide integridade dos dados

## 📚 Documentação da API

Acesse: `http://localhost:8080/swagger-ui.html`

## 🆘 Suporte

Para dúvidas ou problemas:
1. Verifique os logs da aplicação
2. Consulte a documentação do Firebase
3. Verifique as configurações do Remote Config
4. Teste a conectividade com o Firebase
