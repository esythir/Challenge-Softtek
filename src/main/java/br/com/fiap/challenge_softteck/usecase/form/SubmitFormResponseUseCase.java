package br.com.fiap.challenge_softteck.usecase.form;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.entity.Question;
import br.com.fiap.challenge_softteck.domain.exception.BusinessException;
import br.com.fiap.challenge_softteck.domain.exception.FormNotFoundException;
import br.com.fiap.challenge_softteck.domain.exception.InvalidAnswerException;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Caso de uso para submissão de respostas de formulário.
 * Implementa as regras de negócio para validação e salvamento de respostas.
 */
@Service
public class SubmitFormResponseUseCase {

    private final FormRepositoryPort formRepository;
    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public SubmitFormResponseUseCase(FormRepositoryPort formRepository,
            FormResponseRepositoryPort formResponseRepository) {
        this.formRepository = formRepository;
        this.formResponseRepository = formResponseRepository;
    }

    /**
     * Executa a submissão de uma resposta de formulário.
     * 
     * @param formCode Código do formulário
     * @param userId   ID do usuário
     * @param answers  Lista de respostas
     * @return CompletableFuture com a resposta salva
     * @throws FormNotFoundException  se o formulário não for encontrado
     * @throws BusinessException      se o formulário não estiver disponível
     * @throws InvalidAnswerException se as respostas forem inválidas
     */
    public CompletableFuture<FormResponse> execute(String formCode, UserId userId, List<AnswerData> answers) {
        return formRepository.findByCode(formCode)
                .thenCompose(formOpt -> {
                    if (formOpt.isEmpty()) {
                        throw new FormNotFoundException(formCode);
                    }

                    Form form = formOpt.get();

                    // Verificar se o formulário está disponível
                    return checkFormAvailability(form, userId)
                            .thenCompose(isAvailable -> {
                                if (!isAvailable) {
                                    throw new BusinessException("FORM0002",
                                            "Formulário não está disponível para resposta no momento");
                                }

                                // Validar respostas
                                validateAnswers(form, answers);

                                // Criar resposta
                                FormResponse response = createFormResponse(form, userId, answers);

                                // Salvar resposta
                                return formResponseRepository.save(response);
                            });
                });
    }

    private CompletableFuture<Boolean> checkFormAvailability(Form form, UserId userId) {
        if (form.getFormType().isAlwaysAvailable()) {
            return CompletableFuture.completedFuture(true);
        }

        return formResponseRepository.findLastResponseByFormCodeAndUser(form.getCode(), userId)
                .thenApply(lastResponse -> {
                    LocalDateTime lastResponseDate = lastResponse
                            .map(response -> response.getAnsweredAt())
                            .orElse(null);

                    return form.isAvailableForResponse(userId, lastResponseDate, LocalDateTime.now());
                });
    }

    private void validateAnswers(Form form, List<AnswerData> answers) {
        // Validar se todas as perguntas obrigatórias foram respondidas
        for (var question : form.getQuestions()) {
            boolean hasAnswer = answers.stream()
                    .anyMatch(answer -> answer.questionId().equals(question.getId()));

            if (!hasAnswer) {
                throw new InvalidAnswerException("Pergunta obrigatória não respondida: " + question.getId());
            }
        }

        // Validar tipos de resposta
        for (var answer : answers) {
            var question = form.getQuestionById(answer.questionId())
                    .orElseThrow(() -> new InvalidAnswerException("Pergunta não encontrada: " + answer.questionId()));

            validateAnswerType(question, answer);
        }
    }

    private void validateAnswerType(Question question, AnswerData answer) {
        switch (question.getQuestionType()) {
            case CHOICE, SCALE -> {
                if (answer.optionId() == null) {
                    throw new InvalidAnswerException(
                            "Resposta deve conter optionId para pergunta: " + question.getId());
                }
            }
            case TEXT -> {
                if (answer.valueText() == null || answer.valueText().trim().isEmpty()) {
                    throw new InvalidAnswerException(
                            "Resposta de texto não pode estar vazia para pergunta: " + question.getId());
                }
            }
        }
    }

    private FormResponse createFormResponse(Form form, UserId userId, List<AnswerData> answers) {
        return FormResponse.builder()
                .id(null) // Será gerado pelo repositório
                .formId(form.getId())
                .userId(userId)
                .answeredAt(LocalDateTime.now())
                .answers(answers.stream()
                        .map(answer -> createAnswer(form.getId(), answer))
                        .collect(Collectors.toList()))
                .build();
    }

    private FormResponse.Answer createAnswer(Long formId, AnswerData answerData) {
        return FormResponse.Answer.builder()
                .id(null) // Será gerado pelo repositório
                .responseId(null) // Será definido após salvar a resposta
                .questionId(answerData.questionId())
                .optionId(answerData.optionId())
                .value(createAnswerValue(answerData))
                .build();
    }

    private br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue createAnswerValue(AnswerData answerData) {
        if (answerData.optionId() != null) {
            return br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.option(answerData.optionId());
        } else if (answerData.valueNumeric() != null) {
            return br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.numeric(answerData.valueNumeric());
        } else {
            return br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.text(answerData.valueText());
        }
    }

    /**
     * DTO para dados de resposta
     */
    public record AnswerData(
            Long questionId,
            Long optionId,
            java.math.BigDecimal valueNumeric,
            String valueText) {
    }
}
