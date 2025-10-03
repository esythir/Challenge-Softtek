package br.com.fiap.challenge_softteck.framework.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
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
    public void initializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // Tentar carregar o arquivo do classpath
                InputStream serviceAccount = this.getClass().getClassLoader()
                        .getResourceAsStream("firebase-service-account.json");

                if (serviceAccount == null) {
                    throw new FileNotFoundException("firebase-service-account.json not found in classpath");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId(projectId)
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully for project: " + projectId);
            } catch (Exception e) {
                // Log error but don't fail startup for development
                System.err.println("Warning: Could not initialize Firebase: " + e.getMessage());
                System.err.println("Application will run in development mode without Firebase");
                // Não falhar a inicialização - apenas logar o erro
            }
        }
    }

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
    public FirebaseRemoteConfig firebaseRemoteConfig() {
        return FirebaseRemoteConfig.getInstance();
    }
}