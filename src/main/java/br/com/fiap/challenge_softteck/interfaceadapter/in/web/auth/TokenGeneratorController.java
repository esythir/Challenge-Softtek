package br.com.fiap.challenge_softteck.interfaceadapter.in.web.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller para gerar tokens JWT reais do Firebase.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Token Generator", description = "Geração de tokens JWT reais")
@ConditionalOnBean(FirebaseAuth.class)
public class TokenGeneratorController {

    private final FirebaseAuth firebaseAuth;

    @Autowired
    public TokenGeneratorController(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @PostMapping("/generate-token")
    @Operation(summary = "Gerar JWT real", description = "Gera um JWT real do Firebase para um usuário")
    public ResponseEntity<Map<String, Object>> generateToken(@RequestParam String uid) {
        try {
            // Verificar se o usuário existe
            UserRecord userRecord = firebaseAuth.getUser(uid);

            // Gerar token customizado (simulado - Firebase Admin SDK não gera ID tokens)
            // Para produção real, você precisaria usar o Firebase Client SDK
            String customToken = firebaseAuth.createCustomToken(uid);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "uid", uid,
                    "email", userRecord.getEmail(),
                    "customToken", customToken,
                    "message", "Token customizado gerado. Use este token para obter um ID token real.",
                    "instructions", "Use o customToken com Firebase Client SDK para obter o ID token real"));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Erro ao gerar token: " + e.getMessage()));
        }
    }

    @GetMapping("/list-users")
    @Operation(summary = "Listar usuários", description = "Lista usuários cadastrados no Firebase")
    public ResponseEntity<Map<String, Object>> listUsers() {
        try {
            // Listar usuários (limitado a 10 para exemplo)
            var listUsersResult = firebaseAuth.listUsers(null);
            List<Map<String, String>> users = new ArrayList<>();

            for (var user : listUsersResult.getValues()) {
                users.add(Map.of(
                        "uid", user.getUid(),
                        "email", user.getEmail() != null ? user.getEmail() : "N/A",
                        "displayName", user.getDisplayName() != null ? user.getDisplayName() : "N/A"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "users", users,
                    "count", users.size()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Erro ao listar usuários: " + e.getMessage()));
        }
    }
}
