package br.com.fiap.challenge_softteck.interfaceadapter.out.fake;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.UserPreferenceRepositoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stub in-memory para DEV. Carrega quando firebase.enabled != true.
 */
@Repository
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
@ConditionalOnMissingBean(UserPreferenceRepositoryPort.class)
public class InMemoryUserPreferenceRepository implements UserPreferenceRepositoryPort {

    private final ConcurrentMap<String, UserPreference> store = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Optional<UserPreference>> findByUserId(UserId userId) {
        return CompletableFuture.completedFuture(Optional.ofNullable(store.get(userId.value())));
    }

    @Override
    public CompletableFuture<UserPreference> save(UserPreference preference) {
        store.put(preference.getUserId().value(), preference);
        return CompletableFuture.completedFuture(preference);
    }

    @Override
    public CompletableFuture<UserPreference> update(UserPreference preference) {
        store.put(preference.getUserId().value(), preference);
        return CompletableFuture.completedFuture(preference);
    }

    @Override
    public CompletableFuture<Void> delete(UserId userId) {
        store.remove(userId.value());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> exists(UserId userId) {
        return CompletableFuture.completedFuture(store.containsKey(userId.value()));
    }
}
