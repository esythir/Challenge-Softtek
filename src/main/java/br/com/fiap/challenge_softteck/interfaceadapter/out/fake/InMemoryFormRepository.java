package br.com.fiap.challenge_softteck.interfaceadapter.out.fake;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Stub in-memory para desenvolvimento/local.
 * Carrega quando firebase.enabled != true (ou ausente).
 */
@Repository
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
@ConditionalOnMissingBean(FormRepositoryPort.class)
public class InMemoryFormRepository implements FormRepositoryPort {

    @Override
    public CompletableFuture<Optional<Form>> findById(String id) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<Form>> findByCode(String code) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<Form>> findActiveByType(FormType type) {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<List<Form>> findAllActive() {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Form> save(Form form) {
        return CompletableFuture.completedFuture(form);
    }

    @Override
    public CompletableFuture<Form> update(Form form) {
        return CompletableFuture.completedFuture(form);
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> exists(String id) {
        return CompletableFuture.completedFuture(false);
    }
}
