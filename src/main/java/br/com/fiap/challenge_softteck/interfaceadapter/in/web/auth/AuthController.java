package br.com.fiap.challenge_softteck.interfaceadapter.in.web.auth;

import br.com.fiap.challenge_softteck.framework.auth.FirebaseAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para operações de autenticação.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Auth", description = "Operações de autenticação")
public class AuthController {

    private final FirebaseAuthService firebaseAuthService;

    @Autowired
    public AuthController(@Autowired(required = false) FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @PostMapping("/verify-token")
    @Operation(summary = "Verificar token", description = "Verifica se um token JWT é válido")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (firebaseAuthService != null) {
                var userId = firebaseAuthService.extractUserIdFromToken(authHeader);
                var tokenInfo = firebaseAuthService.getTokenInfo(authHeader);

                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "userId", userId.value(),
                        "email", tokenInfo.getEmail() != null ? tokenInfo.getEmail() : "N/A",
                        "name", tokenInfo.getName() != null ? tokenInfo.getName() : "N/A"));
            } else {
                return ResponseEntity.ok(Map.of(
                        "valid", false,
                        "error", "Firebase Auth Service não disponível"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/test-token")
    @Operation(summary = "Token de teste", description = "Gera um token de teste para desenvolvimento")
    public ResponseEntity<Map<String, String>> getTestToken() {
        // Para desenvolvimento, retornar instruções de como obter um token real
        return ResponseEntity.ok(Map.of(
                "message", "Para obter um token JWT real do Firebase:",
                "instructions", "1. Configure Firebase Authentication no seu projeto",
                "instructions2", "2. Use o Firebase SDK para fazer login e obter o token",
                "instructions3", "3. Use o token no header Authorization: Bearer <token>",
                "testToken", "test-user-123 (apenas para desenvolvimento)"));
    }
}
