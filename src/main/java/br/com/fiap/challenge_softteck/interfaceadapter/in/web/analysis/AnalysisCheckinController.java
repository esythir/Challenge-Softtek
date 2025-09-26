package br.com.fiap.challenge_softteck.interfaceadapter.in.web.analysis;

import br.com.fiap.challenge_softteck.dto.MonthlyCheckinTrendDTO;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.usecase.analysis.MonthlyCheckinTrendUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.concurrent.CompletableFuture;

/**
 * Controller para análise de check-ins.
 * Refatorado para usar a nova arquitetura Firebase + NoSQL.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis/checkins")
public class AnalysisCheckinController {

    private final MonthlyCheckinTrendUseCase monthlyCheckinTrendUseCase;

    @GetMapping("/monthly-trend")
    public CompletableFuture<ResponseEntity<MonthlyCheckinTrendDTO>> getMonthlyTrend(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            UserId userId = extractUserIdFromJwt(jwt);

            return monthlyCheckinTrendUseCase.execute(userId, year, month)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(500).body(null));

        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }
    }

    /**
     * Extrai o UserId do JWT token
     */
    private UserId extractUserIdFromJwt(Jwt jwt) {
        String uid = jwt.getClaimAsString("uid");
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException("UID não encontrado no token JWT");
        }
        return UserId.fromString(uid);
    }
}
