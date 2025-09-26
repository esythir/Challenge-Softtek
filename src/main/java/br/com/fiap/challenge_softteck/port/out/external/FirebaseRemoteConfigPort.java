package br.com.fiap.challenge_softteck.port.out.external;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Porta para operações com Firebase Remote Config.
 */
public interface FirebaseRemoteConfigPort {

    /**
     * Busca o dicionário de códigos de erro do Remote Config
     */
    CompletableFuture<Map<String, String>> getErrorDictionary();

    /**
     * Busca uma mensagem de erro específica por código
     */
    CompletableFuture<String> getErrorMessage(String errorCode);

    /**
     * Busca configurações gerais da aplicação
     */
    CompletableFuture<Map<String, Object>> getAppConfig();

    /**
     * Força a atualização das configurações remotas
     */
    CompletableFuture<Void> forceRefresh();

    /**
     * Verifica se o Remote Config está disponível
     */
    CompletableFuture<Boolean> isAvailable();
}
