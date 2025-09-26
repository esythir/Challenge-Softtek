package br.com.fiap.challenge_softteck.domain.entity;

import br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue;
import br.com.fiap.challenge_softteck.domain.valueobject.QuestionType;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * Entidade de domínio que representa uma resposta individual a uma pergunta.
 * Contém as regras de negócio relacionadas às respostas.
 */
@Getter
@Builder
public class Answer {
    private final Long id;
    private final Long responseId;
    private final Long questionId;
    private final Long optionId;
    private final AnswerValue answerValue;

    public Answer(Long id, Long responseId, Long questionId, Long optionId, AnswerValue answerValue) {
        this.id = id;
        this.responseId = Objects.requireNonNull(responseId, "Response ID cannot be null");
        this.questionId = Objects.requireNonNull(questionId, "Question ID cannot be null");
        this.optionId = optionId;
        this.answerValue = Objects.requireNonNull(answerValue, "Answer value cannot be null");
    }

    /**
     * Verifica se a resposta é válida para o tipo de pergunta
     */
    public boolean isValidForQuestionType(QuestionType questionType) {
        return switch (questionType) {
            case CHOICE -> answerValue instanceof AnswerValue.OptionAnswer;
            case SCALE -> answerValue instanceof AnswerValue.NumericAnswer;
            case TEXT -> answerValue instanceof AnswerValue.TextAnswer;
        };
    }

    /**
     * Retorna o valor da resposta como string para exibição
     */
    public String getDisplayValue() {
        return switch (answerValue) {
            case AnswerValue.OptionAnswer optionAnswer -> String.valueOf(optionAnswer.optionId());
            case AnswerValue.NumericAnswer numericAnswer -> numericAnswer.value().toString();
            case AnswerValue.TextAnswer textAnswer -> textAnswer.value();
        };
    }

    /**
     * Verifica se a resposta tem valor
     */
    public boolean hasValue() {
        return answerValue != null;
    }

    /**
     * Retorna o valor numérico da resposta (se aplicável)
     */
    public java.math.BigDecimal getNumericValue() {
        if (answerValue instanceof AnswerValue.NumericAnswer numericAnswer) {
            return numericAnswer.value();
        }
        return null;
    }

    /**
     * Retorna o valor de texto da resposta (se aplicável)
     */
    public String getTextValue() {
        if (answerValue instanceof AnswerValue.TextAnswer textAnswer) {
            return textAnswer.value();
        }
        return null;
    }

    /**
     * Retorna o ID da opção selecionada (se aplicável)
     */
    public Long getSelectedOptionId() {
        if (answerValue instanceof AnswerValue.OptionAnswer optionAnswer) {
            return optionAnswer.optionId();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Answer answer = (Answer) obj;
        return Objects.equals(id, answer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
