package br.com.fiap.challenge_softteck.port.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Porta para operações de repositório de formulários no Firebase Firestore.
 */
public interface FormRepositoryPort {

    /**
     * Busca todos os formulários ativos por tipo
     */
    CompletableFuture<List<Form>> findActiveByType(FormType type);

    /**
     * Busca um formulário por ID
     */
    CompletableFuture<Optional<Form>> findById(String id);

    /**
     * Busca um formulário por código
     */
    CompletableFuture<Optional<Form>> findByCode(String code);

    /**
     * Busca todos os formulários ativos
     */
    CompletableFuture<List<Form>> findAllActive();

    /**
     * Salva um formulário
     */
    CompletableFuture<Form> save(Form form);

    /**
     * Atualiza um formulário
     */
    CompletableFuture<Form> update(Form form);

    /**
     * Remove um formulário
     */
    CompletableFuture<Void> delete(String id);

    /**
     * Verifica se um formulário existe
     */
    CompletableFuture<Boolean> exists(String id);
}
