package br.com.fiap.challenge_softteck.port.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Porta para operações de repositório de preferências de usuário no Firebase
 * Firestore.
 */
public interface UserPreferenceRepositoryPort {

    /**
     * Busca preferências de um usuário
     */
    CompletableFuture<Optional<UserPreference>> findByUserId(UserId userId);

    /**
     * Salva preferências de um usuário
     */
    CompletableFuture<UserPreference> save(UserPreference preference);

    /**
     * Atualiza preferências de um usuário
     */
    CompletableFuture<UserPreference> update(UserPreference preference);

    /**
     * Remove preferências de um usuário
     */
    CompletableFuture<Void> delete(UserId userId);

    /**
     * Verifica se um usuário tem preferências salvas
     */
    CompletableFuture<Boolean> exists(UserId userId);
}
