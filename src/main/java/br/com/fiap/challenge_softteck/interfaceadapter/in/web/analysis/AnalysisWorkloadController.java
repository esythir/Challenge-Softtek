package br.com.fiap.challenge_softteck.interfaceadapter.in.web.analysis;

import br.com.fiap.challenge_softteck.dto.WorkloadAlertsDTO;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.usecase.analysis.WorkloadAlertsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * Controller para análise de carga de trabalho.
 * Refatorado para usar a nova arquitetura Firebase + NoSQL.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisWorkloadController {

    private final WorkloadAlertsUseCase workloadAlertsUseCase;

    @GetMapping("/workload-alerts")
    public CompletableFuture<ResponseEntity<WorkloadAlertsDTO>> getWorkloadAlerts(
            @RequestParam(defaultValue = "3") Integer months,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            UserId userId = extractUserIdFromJwt(jwt);

            return workloadAlertsUseCase.execute(userId, months)
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
