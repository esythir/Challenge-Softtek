package br.com.fiap.challenge_softteck.interfaceadapter.out.fake;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementação in-memory (stub) para desenvolvimento/local.
 * Carrega por padrão quando firebase.enabled != true.
 */
@Repository
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
@ConditionalOnMissingBean(FormResponseRepositoryPort.class)
public class InMemoryFormResponseRepository implements FormResponseRepositoryPort {

    @Override
    public CompletableFuture<Optional<FormResponse>> findById(String id) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<FormResponse>> findLastResponseByFormAndUser(String formId, UserId userId) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<FormResponse>> findLastResponseByFormCodeAndUser(String formCode, UserId userId) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<FormResponse> save(FormResponse response) {
        return CompletableFuture.completedFuture(response);
    }

    @Override
    public CompletableFuture<FormResponse> update(FormResponse response) {
        return CompletableFuture.completedFuture(response);
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Long> countResponsesByUserAndPeriod(UserId userId, LocalDateTime from, LocalDateTime to) {
        return CompletableFuture.completedFuture(0L);
    }

    @Override
    public CompletableFuture<List<FormResponse>> findCheckinsByUserAndPeriod(UserId userId, LocalDateTime from, LocalDateTime to) {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<List<FormResponse>> findByFormCodeAndUserAndPeriod(
            String formCode, UserId userId, LocalDateTime from, LocalDateTime to) {
        return CompletableFuture.completedFuture(List.of());
    }
}
