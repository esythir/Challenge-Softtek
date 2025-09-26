package br.com.fiap.challenge_softteck.domain.entity;

import br.com.fiap.challenge_softteck.domain.valueobject.QuestionType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade de domínio que representa uma pergunta de um formulário.
 * Contém as regras de negócio relacionadas às perguntas.
 */
@Getter
@Builder
public class Question {
    private final Long id;
    private final Long formId;
    private final int ordinal;
    private final String text;
    private final QuestionType questionType;
    private final List<Option> options;

    public Question(Long id, Long formId, int ordinal, String text, QuestionType questionType, List<Option> options) {
        this.id = id;
        this.formId = Objects.requireNonNull(formId, "Form ID cannot be null");
        this.ordinal = validateOrdinal(ordinal);
        this.text = validateText(text);
        this.questionType = Objects.requireNonNull(questionType, "Question type cannot be null");
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
        
        validateQuestionTypeAndOptions();
    }

    /**
     * Verifica se a pergunta tem opções
     */
    public boolean hasOptions() {
        return !options.isEmpty();
    }

    /**
     * Adiciona uma opção à pergunta
     */
    public void addOption(Option option) {
        if (option == null) {
            throw new IllegalArgumentException("Option cannot be null");
        }
        if (!questionType.requiresOptions()) {
            throw new IllegalStateException("Question type " + questionType + " does not support options");
        }
        options.add(option);
    }

    /**
     * Retorna as opções em ordem de ordinal
     */
    public List<Option> getOptionsInOrder() {
        return options.stream()
                .sorted((o1, o2) -> Integer.compare(o1.getOrdinal(), o2.getOrdinal()))
                .toList();
    }

    /**
     * Verifica se a pergunta é obrigatória
     */
    public boolean isRequired() {
        return true; // Por enquanto, todas as perguntas são obrigatórias
    }

    private int validateOrdinal(int ordinal) {
        if (ordinal <= 0) {
            throw new IllegalArgumentException("Ordinal must be positive");
        }
        return ordinal;
    }

    private String validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be null or empty");
        }
        return text.trim();
    }

    private void validateQuestionTypeAndOptions() {
        if (questionType.requiresOptions() && options.isEmpty()) {
            throw new IllegalStateException("Question type " + questionType + " requires options");
        }
        if (!questionType.requiresOptions() && !options.isEmpty()) {
            throw new IllegalStateException("Question type " + questionType + " does not support options");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question question = (Question) obj;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
