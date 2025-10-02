package br.com.fiap.challenge_softteck.interfaceadapter.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.UserPreferenceRepositoryPort;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementação real do repositório de preferências de usuário usando Firebase
 * Firestore.
 * Versão simplificada para desenvolvimento.
 */
@Repository
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseUserPreferenceRepository implements UserPreferenceRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseUserPreferenceRepository.class);

    private final Firestore firestore;

    @Autowired
    public FirebaseUserPreferenceRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public CompletableFuture<Optional<UserPreference>> findByUserId(UserId userId) {
        logger.info("Buscando preferências do usuário: {}", userId);
        // Implementação simplificada - retorna vazio por enquanto
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<UserPreference> save(UserPreference preference) {
        logger.info("Salvando preferências do usuário: {}", preference.getUserId());

        return CompletableFuture.supplyAsync(() -> {
            try {
                var docRef = firestore.collection("user_preferences").document(preference.getUserId().value());

                var data = new java.util.HashMap<String, Object>();
                data.put("userId", preference.getUserId().value());
                data.put("notificationsEnabled", preference.isNotificationsEnabled());
                data.put("lastCheckinReminder", preference.getLastCheckinReminder());
                data.put("lastSelfReminder", preference.getLastSelfReminder());
                data.put("lastClimateReminder", preference.getLastClimateReminder());

                docRef.set(data).get();
                logger.info("Preferências salvas com sucesso no Firestore: {}", preference.getUserId());
                return preference;
            } catch (Exception e) {
                logger.error("Erro ao salvar preferências no Firestore: {}", e.getMessage(), e);
                throw new RuntimeException("Erro ao salvar preferências", e);
            }
        });
    }

    @Override
    public CompletableFuture<UserPreference> update(UserPreference preference) {
        logger.info("Atualizando preferências do usuário: {}", preference.getUserId());
        // Implementação simplificada - retorna a preferência como está
        return CompletableFuture.completedFuture(preference);
    }

    @Override
    public CompletableFuture<Void> delete(UserId userId) {
        logger.info("Deletando preferências do usuário: {}", userId);
        // Implementação simplificada - não faz nada
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> exists(UserId userId) {
        logger.info("Verificando existência das preferências do usuário: {}", userId);
        // Implementação simplificada - retorna false
        return CompletableFuture.completedFuture(false);
    }
}
