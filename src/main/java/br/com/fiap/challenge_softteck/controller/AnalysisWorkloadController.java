package br.com.fiap.challenge_softteck.controller;

import br.com.fiap.challenge_softteck.dto.WorkloadAlertsDTO;
import br.com.fiap.challenge_softteck.service.CheckinService;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Endpoints de análise transversal.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisWorkloadController {

    private final CheckinService service;

    @GetMapping("/workload-alerts")
    public WorkloadAlertsDTO getWorkloadAlerts(
            @RequestParam(defaultValue = "3") Integer months,
            @AuthenticationPrincipal Jwt jwt
    ) {
        byte[] uuid = UuidUtil.uuidToBytes(
                UUID.fromString(jwt.getClaimAsString("uid"))
        );
        return service.workloadAlerts(uuid, months);
    }
}
