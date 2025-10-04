package br.com.fiap.challenge_softteck.framework.config;

import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.framework.auth.FirebaseAuthService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DevAuthConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
    public FirebaseAuthService firebaseAuthService() {
        return new FirebaseAuthService(null) {

            @Override
            public UserId extractUserIdFromToken(String authHeader) {
                return UserId.fromString("dev-user");
            }

            @Override
            public boolean isValidToken(String authHeader) {
                return true;
            }

            @Override
            public FirebaseToken getTokenInfo(String authHeader) {
                return null;
            }
        };
    }
}
