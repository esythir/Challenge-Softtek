package br.com.fiap.challenge_softteck.mobile;

import br.com.fiap.challenge_softteck.auth.GoogleTokenVerifierService;
import br.com.fiap.challenge_softteck.auth.UserMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/mobile")
public class MobileAuthController {

    private final GoogleTokenVerifierService verifier;
    private final UserMappingService mapper;
    private final JwtEncoder encoder;

    public MobileAuthController(GoogleTokenVerifierService verifier,
                                UserMappingService mapper,
                                JwtEncoder encoder) {
        this.verifier = verifier;
        this.mapper   = mapper;
        this.encoder  = encoder;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("id_token");
        if (idTokenString == null)
            return ResponseEntity.badRequest().body(Map.of("error", "missing id_token"));

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null)
            return ResponseEntity.status(401).body(Map.of("error", "invalid id_token"));

        UUID uid = mapper.mapSubToUuid(idToken.getPayload().getSubject());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("challenge-softteck")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .claim("uid", uid.toString())
                .build();

        String jwt = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(Map.of("jwt", jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("status", "logged-out"));
    }
}
