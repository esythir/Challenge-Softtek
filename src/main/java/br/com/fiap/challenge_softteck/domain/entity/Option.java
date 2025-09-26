package br.com.fiap.challenge_softteck.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * Entidade de domínio que representa uma opção de resposta para uma pergunta.
 * Contém as regras de negócio relacionadas às opções.
 */
@Getter
@Builder
public class Option {
    private final Long id;
    private final Long questionId;
    private final int ordinal;
    private final String value;
    private final String label;

    public Option(Long id, Long questionId, int ordinal, String value, String label) {
        this.id = id;
        this.questionId = Objects.requireNonNull(questionId, "Question ID cannot be null");
        this.ordinal = validateOrdinal(ordinal);
        this.value = validateValue(value);
        this.label = validateLabel(label);
    }

    /**
     * Verifica se a opção é válida para uma pergunta
     */
    public boolean isValidForQuestion(Question question) {
        return Objects.equals(this.questionId, question.getId());
    }

    /**
     * Retorna o valor da opção para exibição
     */
    public String getDisplayValue() {
        return label != null ? label : value;
    }

    private int validateOrdinal(int ordinal) {
        if (ordinal <= 0) {
            throw new IllegalArgumentException("Ordinal must be positive");
        }
        return ordinal;
    }

    private String validateValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Option value cannot be null or empty");
        }
        return value.trim();
    }

    private String validateLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Option label cannot be null or empty");
        }
        return label.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Option option = (Option) obj;
        return Objects.equals(id, option.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
