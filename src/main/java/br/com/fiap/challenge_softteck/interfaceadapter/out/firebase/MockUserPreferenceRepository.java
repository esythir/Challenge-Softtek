package br.com.fiap.challenge_softteck.interfaceadapter.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.UserPreferenceRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
@Primary
public class MockUserPreferenceRepository implements UserPreferenceRepositoryPort {

    @Override
    public CompletableFuture<Optional<UserPreference>> findByUserId(UserId userId) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<UserPreference> save(UserPreference preference) {
        return CompletableFuture.completedFuture(preference);
    }

    @Override
    public CompletableFuture<UserPreference> update(UserPreference preference) {
        return CompletableFuture.completedFuture(preference);
    }

    @Override
    public CompletableFuture<Void> delete(UserId userId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> exists(UserId userId) {
        return CompletableFuture.completedFuture(false);
    }
}
