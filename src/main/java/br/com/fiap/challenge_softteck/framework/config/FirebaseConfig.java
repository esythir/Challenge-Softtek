package br.com.fiap.challenge_softteck.framework.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuração do Firebase para a aplicação.
 * Configura Firestore, Authentication e Remote Config.
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-key:classpath:firebase-service-account.json}")
    private Resource serviceAccountKey;

    @Value("${firebase.project-id:challenge-softteck}")
    private String projectId;

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream serviceAccount = serviceAccountKey.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId(projectId)
                        .build();

                FirebaseApp.initializeApp(options);
            }
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Bean
    public FirebaseRemoteConfig firebaseRemoteConfig() {
        return FirebaseRemoteConfig.getInstance();
    }
}