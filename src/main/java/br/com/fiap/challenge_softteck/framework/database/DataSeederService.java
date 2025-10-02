package br.com.fiap.challenge_softteck.framework.database;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.entity.Question;
import br.com.fiap.challenge_softteck.domain.entity.Option;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.Periodicity;
import br.com.fiap.challenge_softteck.domain.valueobject.QuestionType;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço para popular o banco de dados com dados iniciais.
 */
@Service
@ConditionalOnBean(Firestore.class)
public class DataSeederService {

    private static final Logger logger = LoggerFactory.getLogger(DataSeederService.class);

    private final FormRepositoryPort formRepository;

    @Autowired
    public DataSeederService(FormRepositoryPort formRepository) {
        this.formRepository = formRepository;
    }

    @PostConstruct
    public void seedData() {
        logger.info("Iniciando seed de dados...");

        seedForms()
                .thenRun(() -> logger.info("Seed de dados concluído com sucesso"))
                .exceptionally(throwable -> {
                    logger.error("Erro durante o seed de dados: {}", throwable.getMessage(), throwable);
                    return null;
                });
    }

    private CompletableFuture<Void> seedForms() {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Criando formulários no Firestore...");

                // Criar formulário de Check-in Diário
                createDailyCheckinForm();

                // Criar formulário de Autoavaliação
                createSelfAssessmentForm();

                // Criar formulário de Clima Organizacional
                createClimateForm();

                logger.info("Formulários criados com sucesso no Firestore");
            } catch (Exception e) {
                logger.error("Erro ao criar formulários: {}", e.getMessage(), e);
            }
        });
    }

    private void createDailyCheckinForm() {
        try {
            var questions = List.of(
                    Question.builder()
                            .id(1L)
                            .formId(1L)
                            .ordinal(1)
                            .text("Como você está se sentindo hoje?")
                            .questionType(QuestionType.CHOICE)
                            .options(List.of(
                                    new Option(1L, 1L, 1, "excellent", "Excelente"),
                                    new Option(2L, 1L, 2, "good", "Bom"),
                                    new Option(3L, 1L, 3, "regular", "Regular"),
                                    new Option(4L, 1L, 4, "bad", "Ruim"),
                                    new Option(5L, 1L, 5, "terrible", "Péssimo")))
                            .build(),
                    Question.builder()
                            .id(2L)
                            .formId(1L)
                            .ordinal(2)
                            .text("Qual seu nível de energia hoje?")
                            .questionType(QuestionType.SCALE)
                            .build(),
                    Question.builder()
                            .id(3L)
                            .formId(1L)
                            .ordinal(3)
                            .text("Como está sua produtividade hoje?")
                            .questionType(QuestionType.SCALE)
                            .build(),
                    Question.builder()
                            .id(4L)
                            .formId(1L)
                            .ordinal(4)
                            .text("Alguma observação adicional?")
                            .questionType(QuestionType.TEXT)
                            .build());

            var form = Form.builder()
                    .id(1L)
                    .code("DAILY_CHECKIN")
                    .name("Check-in Diário")
                    .formType(FormType.CHECKIN)
                    .description("Formulário diário para acompanhamento do bem-estar")
                    .periodicity(Periodicity.daily())
                    .reminderDays(1)
                    .active(true)
                    .questions(questions)
                    .build();

            logger.info("Salvando formulário de Check-in Diário no Firestore...");
            try {
                var savedForm = formRepository.save(form).get();
                logger.info("Formulário de Check-in Diário criado com ID: {}", savedForm.getId());

                // Teste imediato: tentar buscar o formulário que acabou de salvar
                var testForms = formRepository.findAllActive().get();
                logger.info("TESTE: Formulários encontrados imediatamente após salvar: {}", testForms.size());
                if (!testForms.isEmpty()) {
                    logger.info("TESTE: Primeiro formulário encontrado: {}", testForms.get(0).getName());
                } else {
                    logger.error("ERRO: Nenhum formulário encontrado após salvar!");
                    logger.error("Isso indica que os dados não estão sendo persistidos no Firestore");
                }
            } catch (Exception e) {
                logger.error("ERRO ao salvar formulário de Check-in Diário: {}", e.getMessage(), e);
                throw e;
            }
        } catch (Exception e) {
            logger.error("Erro ao criar formulário de Check-in Diário: {}", e.getMessage(), e);
        }
    }

    private void createSelfAssessmentForm() {
        try {
            var questions = List.of(
                    Question.builder()
                            .id(5L)
                            .formId(2L)
                            .ordinal(1)
                            .text("Como você avalia sua carga de trabalho atual?")
                            .questionType(QuestionType.SCALE)
                            .build(),
                    Question.builder()
                            .id(6L)
                            .formId(2L)
                            .ordinal(2)
                            .text("Você se sente sobrecarregado?")
                            .questionType(QuestionType.CHOICE)
                            .options(List.of(
                                    new Option(6L, 6L, 1, "never", "Nunca"),
                                    new Option(7L, 6L, 2, "rarely", "Raramente"),
                                    new Option(8L, 6L, 3, "sometimes", "Às vezes"),
                                    new Option(9L, 6L, 4, "often", "Frequentemente"),
                                    new Option(10L, 6L, 5, "always", "Sempre")))
                            .build(),
                    Question.builder()
                            .id(7L)
                            .formId(2L)
                            .ordinal(3)
                            .text("Quantas horas extras você trabalhou esta semana?")
                            .questionType(QuestionType.CHOICE)
                            .options(List.of(
                                    new Option(11L, 7L, 1, "0", "Nenhuma"),
                                    new Option(12L, 7L, 2, "1-5", "1-5 horas"),
                                    new Option(13L, 7L, 3, "6-10", "6-10 horas"),
                                    new Option(14L, 7L, 4, "11-15", "11-15 horas"),
                                    new Option(15L, 7L, 5, "16+", "16+ horas")))
                            .build());

            var form = Form.builder()
                    .id(2L)
                    .code("SELF_ASSESSMENT")
                    .name("Autoavaliação de Carga de Trabalho")
                    .formType(FormType.SELF_ASSESSMENT)
                    .description("Avaliação semanal da carga de trabalho")
                    .periodicity(Periodicity.weekly())
                    .reminderDays(7)
                    .active(true)
                    .questions(questions)
                    .build();

            formRepository.save(form).get();
            logger.info("Formulário de Autoavaliação criado");
        } catch (Exception e) {
            logger.error("Erro ao criar formulário de Autoavaliação: {}", e.getMessage(), e);
        }
    }

    private void createClimateForm() {
        try {
            var questions = List.of(
                    Question.builder()
                            .id(8L)
                            .formId(3L)
                            .ordinal(1)
                            .text("Como você avalia o relacionamento com sua liderança?")
                            .questionType(QuestionType.SCALE)
                            .build(),
                    Question.builder()
                            .id(9L)
                            .formId(3L)
                            .ordinal(2)
                            .text("Como você avalia o relacionamento com seus colegas?")
                            .questionType(QuestionType.SCALE)
                            .build(),
                    Question.builder()
                            .id(10L)
                            .formId(3L)
                            .ordinal(3)
                            .text("Como você avalia a comunicação na empresa?")
                            .questionType(QuestionType.SCALE)
                            .build(),
                    Question.builder()
                            .id(11L)
                            .formId(3L)
                            .ordinal(4)
                            .text("Como você avalia o reconhecimento que recebe?")
                            .questionType(QuestionType.SCALE)
                            .build(),
                    Question.builder()
                            .id(12L)
                            .formId(3L)
                            .ordinal(5)
                            .text("Como você avalia o ambiente de trabalho?")
                            .questionType(QuestionType.SCALE)
                            .build());

            var form = Form.builder()
                    .id(3L)
                    .code("CLIMATE_SURVEY")
                    .name("Pesquisa de Clima Organizacional")
                    .formType(FormType.CLIMATE)
                    .description("Avaliação trimestral do clima organizacional")
                    .periodicity(Periodicity.quarterly())
                    .reminderDays(90)
                    .active(true)
                    .questions(questions)
                    .build();

            formRepository.save(form).get();
            logger.info("Formulário de Clima Organizacional criado");
        } catch (Exception e) {
            logger.error("Erro ao criar formulário de Clima Organizacional: {}", e.getMessage(), e);
        }
    }
}