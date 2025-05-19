package br.com.fiap.challenge_softteck.auth;

import br.com.fiap.challenge_softteck.config.GoogleProps;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Valida um id_token gerado pelo Google Sign-In (Android / Web-One-Tap).
 */
@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(GoogleProps props)
            throws GeneralSecurityException, IOException {

        var transport   = GoogleNetHttpTransport.newTrustedTransport();
        var gsonFactory = GsonFactory.getDefaultInstance();

        this.verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                .setAudience(props.getAcceptedClientIds())
                .build();
    }

    public GoogleIdToken verify(String idTokenString) {
        try { return verifier.verify(idTokenString); }
        catch (Exception ex) { return null; }
    }
}

