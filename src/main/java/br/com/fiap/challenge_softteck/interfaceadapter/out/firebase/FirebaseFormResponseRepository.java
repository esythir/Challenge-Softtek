package br.com.fiap.challenge_softteck.interfaceadapter.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementação real do repositório de respostas de formulário usando Firebase
 * Firestore.
 * Versão simplificada para desenvolvimento.
 */
@Repository
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseFormResponseRepository implements FormResponseRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseFormResponseRepository.class);

    private final Firestore firestore;

    @Autowired
    public FirebaseFormResponseRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public CompletableFuture<Optional<FormResponse>> findById(String id) {
        logger.info("Buscando resposta por ID: {}", id);
        // Implementação simplificada - retorna vazio por enquanto
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<FormResponse>> findCheckinsByUserAndPeriod(
            UserId userId, LocalDateTime from, LocalDateTime to) {
        logger.info("Buscando check-ins do usuário {} no período {} a {}", userId, from, to);
        // Implementação simplificada - retorna lista vazia por enquanto
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<List<FormResponse>> findByFormCodeAndUserAndPeriod(
            String formCode, UserId userId, LocalDateTime from, LocalDateTime to) {
        logger.info("Buscando respostas do formulário {} do usuário {} no período {} a {}",
                formCode, userId, from, to);
        // Implementação simplificada - retorna lista vazia por enquanto
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Optional<FormResponse>> findLastResponseByFormAndUser(
            String formId, UserId userId) {
        logger.info("Buscando última resposta do formulário {} do usuário {}", formId, userId);
        // Implementação simplificada - retorna vazio por enquanto
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<FormResponse>> findLastResponseByFormCodeAndUser(
            String formCode, UserId userId) {
        logger.info("Buscando última resposta do formulário {} do usuário {}", formCode, userId);
        // Implementação simplificada - retorna vazio por enquanto
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<FormResponse> save(FormResponse response) {
        logger.info("Salvando resposta do formulário {} do usuário {}",
                response.getFormId(), response.getUserId());

        return CompletableFuture.supplyAsync(() -> {
            try {
                var docRef = firestore.collection("form_responses").document(String.valueOf(response.getId()));

                var data = new java.util.HashMap<String, Object>();
                data.put("id", response.getId());
                data.put("formId", response.getFormId());
                data.put("userId", response.getUserId().value());
                data.put("answeredAt", response.getAnsweredAt());
                data.put("answers", response.getAnswers().stream()
                        .map(answer -> {
                            var answerData = new java.util.HashMap<String, Object>();
                            answerData.put("questionId", answer.getQuestionId());

                            String valueStr = switch (answer.getValue()) {
                                case AnswerValue.OptionAnswer option -> "option:" + option.optionId();
                                case AnswerValue.NumericAnswer numeric -> "numeric:" + numeric.value();
                                case AnswerValue.TextAnswer text -> "text:" + text.value();
                                default -> "unknown";
                            };
                            answerData.put("value", valueStr);

                            return answerData;
                        })
                        .collect(java.util.stream.Collectors.toList()));

                docRef.set(data).get();
                logger.info("Resposta salva com sucesso no Firestore: {}", response.getId());
                return response;
            } catch (Exception e) {
                logger.error("Erro ao salvar resposta no Firestore: {}", e.getMessage(), e);
                throw new RuntimeException("Erro ao salvar resposta", e);
            }
        });
    }

    @Override
    public CompletableFuture<FormResponse> update(FormResponse response) {
        logger.info("Atualizando resposta do formulário {} do usuário {}",
                response.getFormId(), response.getUserId());
        // Implementação simplificada - retorna a resposta como está
        return CompletableFuture.completedFuture(response);
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        logger.info("Deletando resposta com ID: {}", id);
        // Implementação simplificada - não faz nada
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Long> countResponsesByUserAndPeriod(
            UserId userId, LocalDateTime from, LocalDateTime to) {
        logger.info("Contando respostas do usuário {} no período {} a {}", userId, from, to);
        // Implementação simplificada - retorna 0
        return CompletableFuture.completedFuture(0L);
    }
}
