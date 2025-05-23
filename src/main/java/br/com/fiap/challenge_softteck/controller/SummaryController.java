package br.com.fiap.challenge_softteck.controller;

import br.com.fiap.challenge_softteck.dto.SummaryDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

@RestController
@RequestMapping("/summary")
public class SummaryController {

    @GetMapping("/checkin")
    public SummaryDTO summary(@RequestParam String month, @AuthenticationPrincipal Jwt jwt) {
        return new SummaryDTO(0, List.of(), "Neutro");
    }
}