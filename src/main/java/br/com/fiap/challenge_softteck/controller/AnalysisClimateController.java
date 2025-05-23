package br.com.fiap.challenge_softteck.controller;

import br.com.fiap.challenge_softteck.dto.ClimateDiagnosisDTO;
import br.com.fiap.challenge_softteck.service.CheckinService;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Endpoint de diagnóstico de clima organizacional.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisClimateController {

    private final CheckinService service;

    @GetMapping("/climate-diagnosis")
    public ClimateDiagnosisDTO getClimateDiagnosis(
            @AuthenticationPrincipal Jwt jwt
    ) {
        byte[] uuid = UuidUtil.uuidToBytes(
                UUID.fromString(jwt.getClaimAsString("uid"))
        );
        return service.climateDiagnosis(uuid);
    }
}
