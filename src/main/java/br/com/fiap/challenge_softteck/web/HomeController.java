package br.com.fiap.challenge_softteck.web;

import br.com.fiap.challenge_softteck.auth.UserMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
public class HomeController {

    private final JwtEncoder encoder;
    private final UserMappingService mapper;

    public HomeController(JwtEncoder encoder, UserMappingService mapper) {
        this.encoder = encoder;
        this.mapper  = mapper;
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String,String>> home(@AuthenticationPrincipal OidcUser oidc) {

        UUID uid = mapper.mapSubToUuid(oidc.getSubject());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("challenge-softteck")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .claim("uid", uid.toString())
                .build();

        String jwt = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(Map.of("jwt", jwt));
    }

    @GetMapping("/")
    public String root() { return "OK"; }

    @GetMapping("/debug/claims")
    public Map<String,Object> debug(@AuthenticationPrincipal OidcUser oidc) {
        return oidc.getClaims();
    }

}
