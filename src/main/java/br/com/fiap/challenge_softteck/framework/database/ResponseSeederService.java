package br.com.fiap.challenge_softteck.framework.database;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço para popular o banco de dados com respostas de exemplo.
 */
@Service
@ConditionalOnBean(Firestore.class)
public class ResponseSeederService {

        private static final Logger logger = LoggerFactory.getLogger(ResponseSeederService.class);

        private final FormResponseRepositoryPort formResponseRepository;

        @Autowired
        public ResponseSeederService(FormResponseRepositoryPort formResponseRepository) {
                this.formResponseRepository = formResponseRepository;
        }

        @PostConstruct
        public void seedResponses() {
                logger.info("Iniciando seed de respostas...");

                seedFormResponses()
                                .thenRun(() -> logger.info("Seed de respostas concluído com sucesso"))
                                .exceptionally(throwable -> {
                                        logger.error("Erro durante o seed de respostas: {}", throwable.getMessage(),
                                                        throwable);
                                        return null;
                                });
        }

        private CompletableFuture<Void> seedFormResponses() {
                return CompletableFuture.runAsync(() -> {
                        try {
                                // Verificar se já existem respostas
                                var userId = UserId.fromString("user-123");
                                var existingResponses = formResponseRepository.findCheckinsByUserAndPeriod(
                                                userId, LocalDateTime.now().minusDays(30), LocalDateTime.now()).get();
                                if (!existingResponses.isEmpty()) {
                                        logger.info("Respostas já existem, pulando seed");
                                        return;
                                }

                                // Criar respostas de exemplo para diferentes usuários
                                createSampleResponses();

                                logger.info("Respostas de exemplo criadas com sucesso");
                        } catch (Exception e) {
                                logger.error("Erro ao criar respostas de exemplo: {}", e.getMessage(), e);
                        }
                });
        }

        private void createSampleResponses() {
                try {
                        var userId = UserId.fromString("user-123");

                        // Resposta 1: Check-in Diário - Usuário user-123
                        var checkinResponse1 = FormResponse.builder()
                                        .id(1L)
                                        .formId(1L)
                                        .userId(userId)
                                        .answeredAt(LocalDateTime.now().minusDays(1))
                                        .answers(List.of(
                                                        new FormResponse.Answer(1L, 1L, 1L, null,
                                                                        AnswerValue.text("good")),
                                                        new FormResponse.Answer(2L, 1L, 2L, null,
                                                                        AnswerValue.numeric(7)),
                                                        new FormResponse.Answer(3L, 1L, 3L, null,
                                                                        AnswerValue.numeric(8)),
                                                        new FormResponse.Answer(4L, 1L, 4L, null, AnswerValue
                                                                        .text("Dia produtivo, mas um pouco cansado"))))
                                        .build();

                        // Resposta 2: Check-in Diário - Usuário user-123 (hoje)
                        var checkinResponse2 = FormResponse.builder()
                                        .id(2L)
                                        .formId(1L)
                                        .userId(userId)
                                        .answeredAt(LocalDateTime.now())
                                        .answers(List.of(
                                                        new FormResponse.Answer(5L, 2L, 1L, null,
                                                                        AnswerValue.text("excellent")),
                                                        new FormResponse.Answer(6L, 2L, 2L, null,
                                                                        AnswerValue.numeric(9)),
                                                        new FormResponse.Answer(7L, 2L, 3L, null,
                                                                        AnswerValue.numeric(9)),
                                                        new FormResponse.Answer(8L, 2L, 4L, null, AnswerValue
                                                                        .text("Excelente dia! Muito motivado"))))
                                        .build();

                        // Resposta 3: Autoavaliação - Usuário user-123
                        var selfAssessmentResponse = FormResponse.builder()
                                        .id(3L)
                                        .formId(2L)
                                        .userId(userId)
                                        .answeredAt(LocalDateTime.now().minusDays(3))
                                        .answers(List.of(
                                                        new FormResponse.Answer(9L, 3L, 5L, null,
                                                                        AnswerValue.numeric(6)),
                                                        new FormResponse.Answer(10L, 3L, 6L, null,
                                                                        AnswerValue.text("sometimes")),
                                                        new FormResponse.Answer(11L, 3L, 7L, null,
                                                                        AnswerValue.text("1-5"))))
                                        .build();

                        // Resposta 4: Clima Organizacional - Usuário user-123
                        var climateResponse = FormResponse.builder()
                                        .id(4L)
                                        .formId(3L)
                                        .userId(userId)
                                        .answeredAt(LocalDateTime.now().minusDays(7))
                                        .answers(List.of(
                                                        new FormResponse.Answer(12L, 4L, 8L, null,
                                                                        AnswerValue.numeric(8)),
                                                        new FormResponse.Answer(13L, 4L, 9L, null,
                                                                        AnswerValue.numeric(9)),
                                                        new FormResponse.Answer(14L, 4L, 10L, null,
                                                                        AnswerValue.numeric(7)),
                                                        new FormResponse.Answer(15L, 4L, 11L, null,
                                                                        AnswerValue.numeric(6)),
                                                        new FormResponse.Answer(16L, 4L, 12L, null,
                                                                        AnswerValue.numeric(8))))
                                        .build();

                        // Salvar todas as respostas
                        formResponseRepository.save(checkinResponse1).get();
                        formResponseRepository.save(checkinResponse2).get();
                        formResponseRepository.save(selfAssessmentResponse).get();
                        formResponseRepository.save(climateResponse).get();

                        logger.info("Respostas de exemplo criadas para usuário user-123");

                } catch (Exception e) {
                        logger.error("Erro ao criar respostas de exemplo: {}", e.getMessage(), e);
                }
        }
}
