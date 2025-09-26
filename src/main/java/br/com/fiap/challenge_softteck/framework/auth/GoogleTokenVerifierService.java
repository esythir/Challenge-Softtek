package br.com.fiap.challenge_softteck.framework.auth;

import br.com.fiap.challenge_softteck.framework.config.GoogleOAuth2Config;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Serviço para verificação de tokens Google OAuth2.
 * Refatorado para usar a nova arquitetura Firebase + NoSQL.
 */
@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(GoogleOAuth2Config config)
            throws GeneralSecurityException, IOException {

        var transport = GoogleNetHttpTransport.newTrustedTransport();
        var gsonFactory = GsonFactory.getDefaultInstance();

        this.verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                .setAudience(config.getAcceptedClientIds())
                .build();
    }

    /**
     * Verifica um token ID do Google OAuth2.
     * 
     * @param idTokenString O token ID como string
     * @return O token verificado ou null se inválido
     */
    public GoogleIdToken verify(String idTokenString) {
        try {
            return verifier.verify(idTokenString);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Verifica se um token é válido.
     * 
     * @param idTokenString O token ID como string
     * @return true se o token for válido, false caso contrário
     */
    public boolean isValidToken(String idTokenString) {
        return verify(idTokenString) != null;
    }

    /**
     * Extrai o UID do usuário de um token válido.
     * 
     * @param idTokenString O token ID como string
     * @return O UID do usuário ou null se o token for inválido
     */
    public String extractUserId(String idTokenString) {
        GoogleIdToken idToken = verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload().getSubject();
        }
        return null;
    }
}
