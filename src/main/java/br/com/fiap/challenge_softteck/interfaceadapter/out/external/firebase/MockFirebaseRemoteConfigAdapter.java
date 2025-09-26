package br.com.fiap.challenge_softteck.interfaceadapter.out.external.firebase;

import br.com.fiap.challenge_softteck.port.out.external.FirebaseRemoteConfigPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Primary
public class MockFirebaseRemoteConfigAdapter implements FirebaseRemoteConfigPort {

    @Override
    public CompletableFuture<Map<String, String>> getErrorDictionary() {
        Map<String, String> defaultErrors = new HashMap<>();

        // Formulários
        defaultErrors.put("FORM0001", "Formulário não encontrado");
        defaultErrors.put("FORM0002", "Formulário não está disponível para resposta no momento");
        defaultErrors.put("FORM0003", "Formulário já foi respondido hoje");
        defaultErrors.put("FORM0004", "Pergunta não encontrada no formulário");
        defaultErrors.put("FORM0005", "Resposta inválida para o tipo de pergunta");

        // Respostas
        defaultErrors.put("RESP0001", "Resposta não encontrada");
        defaultErrors.put("RESP0002", "Erro ao salvar resposta do formulário");
        defaultErrors.put("RESP0003", "Resposta incompleta - faltam perguntas obrigatórias");

        // Usuários
        defaultErrors.put("USER0001", "Usuário não encontrado");
        defaultErrors.put("USER0002", "Preferências do usuário não encontradas");
        defaultErrors.put("USER0003", "Erro ao atualizar preferências do usuário");

        // Autenticação
        defaultErrors.put("AUTH0001", "Token de autenticação inválido");
        defaultErrors.put("AUTH0002", "Usuário não autenticado");
        defaultErrors.put("AUTH0003", "Erro na verificação do token Google");

        // Análises
        defaultErrors.put("ANAL0001", "Dados insuficientes para gerar análise");
        defaultErrors.put("ANAL0002", "Erro ao processar análise de check-in");
        defaultErrors.put("ANAL0003", "Erro ao gerar relatório de clima organizacional");

        // Sistema
        defaultErrors.put("CORE0001", "Erro interno do servidor");
        defaultErrors.put("CORE0002", "Serviço temporariamente indisponível");
        defaultErrors.put("CORE0003", "Erro de validação de dados");
        defaultErrors.put("CORE0004", "Erro ao conectar com Firebase");
        defaultErrors.put("CORE0005", "Erro ao carregar configurações remotas");

        return CompletableFuture.completedFuture(defaultErrors);
    }

    @Override
    public CompletableFuture<String> getErrorMessage(String errorCode) {
        return getErrorDictionary().thenApply(errorDict -> errorDict.getOrDefault(errorCode, "Erro desconhecido"));
    }

    @Override
    public CompletableFuture<Map<String, Object>> getAppConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("app_version", "1.0.0");
        config.put("maintenance_mode", false);
        config.put("max_forms_per_user", 10L);
        config.put("notification_enabled", true);

        return CompletableFuture.completedFuture(config);
    }

    @Override
    public CompletableFuture<Void> forceRefresh() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> isAvailable() {
        return CompletableFuture.completedFuture(true);
    }
}
