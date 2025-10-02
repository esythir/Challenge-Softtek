# 🔥 Configuração das Regras do Firestore

## Regras para Desenvolvimento (Recomendado para começar)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Formulários - leitura livre
    match /forms/{formId} {
      allow read: if true;
      allow write: if false;
    }
    
    // Respostas - acesso total para desenvolvimento
    match /formResponses/{responseId} {
      allow read, write: if true;
    }
    
    // Preferências do usuário - acesso total para desenvolvimento
    match /userPreferences/{userId} {
      allow read, write: if true;
    }
    
    // Check-ins - acesso total para desenvolvimento
    match /checkins/{checkinId} {
      allow read, write: if true;
    }
    
    // Bloquear outras coleções
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

## Regras para Produção (Mais Seguras)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Formulários - apenas leitura para usuários autenticados
    match /forms/{formId} {
      allow read: if request.auth != null;
      allow write: if false;
    }
    
    // Respostas - usuário só acessa suas próprias respostas
    match /formResponses/{responseId} {
      allow read, write: if request.auth != null 
        && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null 
        && request.auth.uid == request.resource.data.userId;
    }
    
    // Preferências - usuário só acessa suas próprias preferências
    match /userPreferences/{userId} {
      allow read, write: if request.auth != null 
        && request.auth.uid == userId;
    }
    
    // Check-ins - usuário só acessa seus próprios check-ins
    match /checkins/{checkinId} {
      allow read, write: if request.auth != null 
        && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null 
        && request.auth.uid == request.resource.data.userId;
    }
  }
}
```

## Como Aplicar

1. **No Firebase Console:**
   - Acesse: https://console.firebase.google.com/
   - Selecione seu projeto
   - Vá em "Firestore Database"
   - Clique na aba "Regras"
   - Cole uma das configurações acima
   - Clique em "Publicar"

2. **Para Testar:**
   - Use as regras de desenvolvimento primeiro
   - Teste a aplicação
   - Depois mude para as regras de produção

## Estrutura das Coleções

A aplicação espera as seguintes coleções no Firestore:

- `forms` - Formulários disponíveis
- `formResponses` - Respostas dos usuários
- `userPreferences` - Preferências dos usuários
- `checkins` - Check-ins dos usuários

## Próximos Passos

1. Configure as regras no Firebase Console
2. Execute a aplicação com perfil de produção:
   ```bash
   .\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod
   ```
3. Teste os endpoints para verificar se estão funcionando com Firebase real
