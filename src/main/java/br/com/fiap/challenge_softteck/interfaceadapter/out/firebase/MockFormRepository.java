package br.com.fiap.challenge_softteck.interfaceadapter.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
@Primary
public class MockFormRepository implements FormRepositoryPort {

    @Override
    public CompletableFuture<List<Form>> findActiveByType(FormType type) {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Optional<Form>> findById(String id) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<Form>> findByCode(String code) {
        return CompletableFuture.completedFuture(Optional.empty());
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
