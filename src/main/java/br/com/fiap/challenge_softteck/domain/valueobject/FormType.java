package br.com.fiap.challenge_softteck.domain.valueobject;

/**
 * Value Object que representa os tipos de formulário disponíveis no sistema.
 * Cada tipo tem características específicas de periodicidade e uso.
 */
public enum FormType {
    /**
     * Check-in diário para monitoramento de humor e sentimentos
     */
    CHECKIN,

    /**
     * Autoavaliação de riscos psicossociais (periódica)
     */
    SELF_ASSESSMENT,

    /**
     * Diagnóstico de clima organizacional (periódico)
     */
    CLIMATE,

    /**
     * Canal de escuta para reportes (sempre disponível)
     */
    REPORT;

    /**
     * Verifica se o tipo de formulário é sempre disponível
     */
    public boolean isAlwaysAvailable() {
        return this == REPORT;
    }

    /**
     * Verifica se o tipo de formulário tem periodicidade diária
     */
    public boolean isDaily() {
        return this == CHECKIN;
    }
}
