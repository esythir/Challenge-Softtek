package br.com.fiap.challenge_softteck.domain.valueobject;

/**
 * Value Object que representa os tipos de pergunta disponíveis nos formulários.
 * Cada tipo determina como a resposta deve ser coletada e armazenada.
 */
public enum QuestionType {
    /**
     * Pergunta de múltipla escolha com opções pré-definidas
     */
    CHOICE,

    /**
     * Pergunta de escala numérica (ex: 1-5, 1-10)
     */
    SCALE,

    /**
     * Pergunta de texto livre
     */
    TEXT;

    /**
     * Verifica se o tipo de pergunta requer opções pré-definidas
     */
    public boolean requiresOptions() {
        return this == CHOICE;
    }

    /**
     * Verifica se o tipo de pergunta aceita valores numéricos
     */
    public boolean acceptsNumericValue() {
        return this == SCALE;
    }

    /**
     * Verifica se o tipo de pergunta aceita texto livre
     */
    public boolean acceptsTextValue() {
        return this == TEXT;
    }
}
