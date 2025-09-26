package br.com.fiap.challenge_softteck.port.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Porta para operações de repositório de respostas de formulário no Firebase
 * Firestore.
 */
public interface FormResponseRepositoryPort {

    /**
     * Busca uma resposta por ID
     */
    CompletableFuture<Optional<FormResponse>> findById(String id);

    /**
     * Busca respostas de check-in de um usuário em um período
     */
    CompletableFuture<List<FormResponse>> findCheckinsByUserAndPeriod(
            UserId userId, LocalDateTime from, LocalDateTime to);

    /**
     * Busca respostas de um formulário específico por usuário e período
     */
    CompletableFuture<List<FormResponse>> findByFormCodeAndUserAndPeriod(
            String formCode, UserId userId, LocalDateTime from, LocalDateTime to);

    /**
     * Busca a última resposta de um formulário por usuário
     */
    CompletableFuture<Optional<FormResponse>> findLastResponseByFormAndUser(
            String formId, UserId userId);

    /**
     * Busca a última resposta de um formulário por código e usuário
     */
    CompletableFuture<Optional<FormResponse>> findLastResponseByFormCodeAndUser(
            String formCode, UserId userId);

    /**
     * Salva uma resposta
     */
    CompletableFuture<FormResponse> save(FormResponse response);

    /**
     * Atualiza uma resposta
     */
    CompletableFuture<FormResponse> update(FormResponse response);

    /**
     * Remove uma resposta
     */
    CompletableFuture<Void> delete(String id);

    /**
     * Conta respostas de um usuário em um período
     */
    CompletableFuture<Long> countResponsesByUserAndPeriod(
            UserId userId, LocalDateTime from, LocalDateTime to);
}
