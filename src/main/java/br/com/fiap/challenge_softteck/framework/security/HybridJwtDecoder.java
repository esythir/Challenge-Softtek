package br.com.fiap.challenge_softteck.framework.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Map;

/**
 * JWT Decoder híbrido que funciona tanto com Firebase real quanto com tokens de
 * desenvolvimento.
 * 
 * Para DESENVOLVIMENTO: aceita tokens no formato "test-userId"
 * Para PRODUÇÃO: valida tokens Firebase reais
 */
public class HybridJwtDecoder implements JwtDecoder {

    private static final Logger logger = LoggerFactory.getLogger(HybridJwtDecoder.class);

    private final FirebaseAuth firebaseAuth;

    public HybridJwtDecoder() {
        this.firebaseAuth = null;
    }

    @Autowired(required = false)
    public HybridJwtDecoder(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        logger.debug("Decodificando token JWT: {}", token);

        // Para desenvolvimento, aceitar apenas tokens no formato "test-userId"
        if (token.startsWith("test-")) {
            return decodeDevelopmentToken(token);
        }

        // Para produção, se Firebase estiver disponível
        if (firebaseAuth != null) {
            return decodeFirebaseToken(token);
        }

        // Fallback: rejeitar token
        throw new JwtException("Token não reconhecido. Use tokens no formato 'test-userId' para desenvolvimento.");
    }

    /**
     * Decodifica token de desenvolvimento (test-userId)
     */
    private Jwt decodeDevelopmentToken(String token) {
        String userId = token.substring(5); // Remove "test-" prefix

        logger.debug("Token de desenvolvimento aceito para usuário: {}", userId);

        return Jwt.withTokenValue(token)
                .header("alg", "HS256")
                .header("typ", "JWT")
                .claim("sub", userId)
                .claim("iss", "development-issuer")
                .claim("aud", "development-audience")
                .claim("exp", Instant.now().plusSeconds(3600))
                .claim("iat", Instant.now())
                .claim("roles", "USER")
                .claim("firebase", Map.of(
                        "identities", Map.of(),
                        "sign_in_provider", "development"))
                .build();
    }

    /**
     * Decodifica e valida token Firebase real
     */
    private Jwt decodeFirebaseToken(String token) {
        try {
            logger.debug("Validando token Firebase real");

            // Validar token com Firebase
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);

            // Converter para JWT do Spring Security (versão simplificada)
            return Jwt.withTokenValue(token)
                    .header("alg", "RS256")
                    .header("typ", "JWT")
                    .claim("sub", firebaseToken.getUid())
                    .claim("iss", firebaseToken.getIssuer())
                    .claim("aud", "firebase")
                    .claim("exp", System.currentTimeMillis() / 1000 + 3600) // 1 hora
                    .claim("iat", System.currentTimeMillis() / 1000)
                    .claim("auth_time", System.currentTimeMillis() / 1000)
                    .claim("firebase", firebaseToken.getClaims())
                    .claim("roles", "USER")
                    .build();

        } catch (FirebaseAuthException e) {
            logger.warn("Erro na validação do token Firebase: {}", e.getMessage());
            throw new JwtException("Token Firebase inválido: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erro inesperado na validação do token", e);
            throw new JwtException("Erro interno na validação do token", e);
        }
    }
}
