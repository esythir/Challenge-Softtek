package br.com.fiap.challenge_softteck.domain.valueobject;

import java.math.BigDecimal;

/**
 * Value Object que representa o valor de uma resposta.
 * Pode ser uma opção (ID), um valor numérico ou texto.
 */
public sealed interface AnswerValue {

    /**
     * Resposta do tipo opção (múltipla escolha)
     */
    record OptionAnswer(Long optionId) implements AnswerValue {
        public OptionAnswer {
            if (optionId == null || optionId <= 0) {
                throw new IllegalArgumentException("Option ID must be positive");
            }
        }
    }

    /**
     * Resposta do tipo numérico (escala)
     */
    record NumericAnswer(BigDecimal value) implements AnswerValue {
        public NumericAnswer {
            if (value == null) {
                throw new IllegalArgumentException("Numeric value cannot be null");
            }
        }
    }

    /**
     * Resposta do tipo texto
     */
    record TextAnswer(String value) implements AnswerValue {
        public TextAnswer {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Text value cannot be null or empty");
            }
        }
    }

    /**
     * Factory method para criar resposta de opção
     */
    static AnswerValue option(Long optionId) {
        return new OptionAnswer(optionId);
    }

    /**
     * Factory method para criar resposta numérica
     */
    static AnswerValue numeric(BigDecimal value) {
        return new NumericAnswer(value);
    }

    /**
     * Factory method para criar resposta numérica a partir de double
     */
    static AnswerValue numeric(double value) {
        return new NumericAnswer(BigDecimal.valueOf(value));
    }

    /**
     * Factory method para criar resposta de texto
     */
    static AnswerValue text(String value) {
        return new TextAnswer(value);
    }
}
