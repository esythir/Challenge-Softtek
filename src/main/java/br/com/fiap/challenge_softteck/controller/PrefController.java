package br.com.fiap.challenge_softteck.controller;

import br.com.fiap.challenge_softteck.dto.PrefDTO;
import br.com.fiap.challenge_softteck.service.PrefService;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PrefController {
    private final PrefService service;

    @GetMapping
    public PrefDTO get(@AuthenticationPrincipal Jwt jwt) {
        byte[] uuid = UuidUtil.uuidToBytes(UUID.fromString(jwt.getClaimAsString("uid")));
        return service.get(uuid);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void put(@RequestBody PrefDTO dto, @AuthenticationPrincipal Jwt jwt) {
        byte[] uuid = UuidUtil.uuidToBytes(UUID.fromString(jwt.getClaimAsString("uid")));
        service.save(uuid, dto);
    }
}