package br.com.fiap.challenge_softteck.controller;

import br.com.fiap.challenge_softteck.dto.MonthlyCheckinTrendDTO;
import br.com.fiap.challenge_softteck.service.CheckinService;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis/checkins")
public class AnalysisCheckinController {

    private final CheckinService service;

    @GetMapping("/monthly-trend")
    public MonthlyCheckinTrendDTO getMonthlyTrend(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal Jwt jwt
    ) {
        byte[] uuid = UuidUtil.uuidToBytes(
                UUID.fromString(jwt.getClaimAsString("uid"))
        );
        return service.monthlyTrend(uuid, year, month);
    }
}
