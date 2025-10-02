package br.com.fiap.challenge_softteck.interfaceadapter.in.web.preference;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.usecase.preference.GetUserPreferencesUseCase;
import br.com.fiap.challenge_softteck.usecase.preference.ToggleNotificationUseCase;
import br.com.fiap.challenge_softteck.usecase.preference.UpdateUserPreferencesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Controller para gerenciar preferências do usuário.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final GetUserPreferencesUseCase getUserPreferencesUseCase;
    private final UpdateUserPreferencesUseCase updateUserPreferencesUseCase;
    private final ToggleNotificationUseCase toggleNotificationUseCase;

    @GetMapping
    public CompletableFuture<ResponseEntity<UserPreference>> getPreferences(
            @RequestHeader("Authorization") String authHeader) {
        try {
            UserId userId = extractUserIdFromToken(authHeader);

            return getUserPreferencesUseCase.execute(userId)
                    .thenApply(optionalPreference -> {
                        UserPreference preference = optionalPreference.orElse(UserPreference.createDefault(userId));
                        return ResponseEntity.ok(preference);
                    })
                    .exceptionally(throwable -> ResponseEntity.status(500).body(null));

        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }
    }

    @PutMapping
    public CompletableFuture<ResponseEntity<UserPreference>> updatePreferences(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserPreference preferences) {
        try {
            UserId userId = extractUserIdFromToken(authHeader);

            return updateUserPreferencesUseCase.execute(userId, preferences)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(500).body(null));

        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }
    }

    @PutMapping("/notifications")
    public CompletableFuture<ResponseEntity<UserPreference>> toggleNotifications(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam boolean enabled) {
        try {
            UserId userId = extractUserIdFromToken(authHeader);

            return toggleNotificationUseCase.execute(userId, enabled)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(500).body(null));

        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }
    }

    /**
     * Extrai UserId do token de autorização (implementação simplificada)
     */
    private UserId extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorização inválido");
        }
        return UserId.fromString("test-user-123");
    }
}
