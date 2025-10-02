# 🔥 Configuração do Firebase

## Passos para configurar o Firebase

### 1. Criar Projeto no Firebase Console
1. Acesse: https://console.firebase.google.com/
2. Clique em "Adicionar projeto"
3. Nome do projeto: `challenge-softteck`
4. Siga os passos para criar o projeto

### 2. Configurar Firestore Database
1. No console do Firebase, vá em "Firestore Database"
2. Clique em "Criar banco de dados"
3. Escolha "Modo de produção" (ou teste se preferir)
4. Selecione uma localização (ex: `southamerica-east1`)

### 3. Gerar Chave de Serviço
1. Vá em "Configurações do projeto" (ícone de engrenagem)
2. Aba "Contas de serviço"
3. Clique em "Gerar nova chave privada"
4. Baixe o arquivo JSON

### 4. Substituir arquivo de configuração
Substitua o conteúdo do arquivo `src/main/resources/firebase-service-account.json` com o JSON baixado.

### 5. Executar a aplicação

#### Desenvolvimento (com mocks):
```bash
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=dev
```

#### Produção (com Firebase real):
```bash
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod
```

## Estrutura do Firestore

A aplicação criará automaticamente as seguintes coleções:

- `forms` - Formulários disponíveis
- `form_responses` - Respostas dos usuários
- `user_preferences` - Preferências dos usuários

## Testando a configuração

Após configurar o Firebase, você pode testar:

1. **Listar formulários**: `GET /api/forms`
2. **Criar resposta**: `POST /api/forms/{formCode}/submit`
3. **Ver check-ins**: `GET /api/checkins`

## Troubleshooting

### Erro: "Could not open ServletContext resource"
- Verifique se o arquivo `firebase-service-account.json` está no local correto
- Verifique se o JSON está válido

### Erro: "Firebase project not found"
- Verifique se o `project_id` no JSON corresponde ao projeto no Firebase Console

### Erro: "Permission denied"
- Verifique se a conta de serviço tem permissões no Firestore
- No Firebase Console, vá em "Regras" e configure as permissões
