package br.com.fiap.challenge_softteck.controller;

import br.com.fiap.challenge_softteck.dto.MonthlyCheckinSummaryDTO;
import br.com.fiap.challenge_softteck.dto.WeeklyCheckinDTO;
import br.com.fiap.challenge_softteck.service.CheckinService;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService service;

    @GetMapping
    public Page<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @AuthenticationPrincipal Jwt jwt
    ) {
        byte[] uuid = UuidUtil.uuidToBytes(UUID.fromString(jwt.getClaimAsString("uid")));
        Pageable pg = PageRequest.of(page, size, Sort.by("answeredAt").descending());
        return service.list(uuid, from, to, pg);
    }

    @GetMapping("/weekly")
    public WeeklyCheckinDTO weekly(
            @AuthenticationPrincipal Jwt jwt
    ) {
        byte[] uuid = UuidUtil.uuidToBytes(UUID.fromString(jwt.getClaimAsString("uid")));
        return service.weeklySummary(uuid);
    }

    @GetMapping("/monthly-summary")
    public MonthlyCheckinSummaryDTO monthlySummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal Jwt jwt
    ) {
        byte[] uuid = UuidUtil.uuidToBytes(UUID.fromString(jwt.getClaimAsString("uid")));
        return service.monthlySummary(uuid, year, month);
    }
}
