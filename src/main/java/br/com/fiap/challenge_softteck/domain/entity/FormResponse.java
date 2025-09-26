package br.com.fiap.challenge_softteck.domain.entity;

import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade de domínio que representa uma resposta completa a um formulário.
 * Contém as regras de negócio relacionadas às respostas de formulário.
 */
@Getter
@Builder
public class FormResponse {
    private final Long id;
    private final Long formId;
    private final UserId userId;
    private final LocalDateTime answeredAt;
    private final List<Answer> answers;

    public FormResponse(Long id, Long formId, UserId userId, LocalDateTime answeredAt, List<Answer> answers) {
        this.id = id;
        this.formId = Objects.requireNonNull(formId, "Form ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.answeredAt = Objects.requireNonNull(answeredAt, "Answered at cannot be null");
        this.answers = answers != null ? new ArrayList<>(answers) : new ArrayList<>();
    }

    /**
     * Adiciona uma resposta à resposta do formulário
     */
    public void addAnswer(Answer answer) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        answers.add(answer);
    }

    /**
     * Verifica se a resposta está completa
     */
    public boolean isComplete(Form form) {
        if (form == null) {
            return false;
        }
        return answers.size() == form.getQuestionCount();
    }

    /**
     * Retorna a resposta para uma pergunta específica
     */
    public Answer getAnswerForQuestion(Long questionId) {
        return answers.stream()
                .filter(answer -> Objects.equals(answer.getQuestionId(), questionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica se a resposta tem respostas para todas as perguntas obrigatórias
     */
    public boolean hasAllRequiredAnswers(Form form) {
        if (form == null) {
            return false;
        }

        return form.getQuestionsInOrder().stream()
                .filter(Question::isRequired)
                .allMatch(question -> getAnswerForQuestion(question.getId()) != null);
    }

    /**
     * Retorna o número de respostas
     */
    public int getAnswerCount() {
        return answers.size();
    }

    /**
     * Verifica se a resposta está vazia
     */
    public boolean isEmpty() {
        return answers.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FormResponse that = (FormResponse) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Classe interna que representa uma resposta individual a uma pergunta
     */
    @Getter
    @Builder
    public static class Answer {
        private final Long id;
        private final Long responseId;
        private final Long questionId;
        private final Long optionId;
        private final AnswerValue value;

        public Answer(Long id, Long responseId, Long questionId, Long optionId, AnswerValue value) {
            this.id = id;
            this.responseId = Objects.requireNonNull(responseId, "Response ID cannot be null");
            this.questionId = Objects.requireNonNull(questionId, "Question ID cannot be null");
            this.optionId = optionId;
            this.value = Objects.requireNonNull(value, "Answer value cannot be null");
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Answer answer = (Answer) obj;
            return Objects.equals(id, answer.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}