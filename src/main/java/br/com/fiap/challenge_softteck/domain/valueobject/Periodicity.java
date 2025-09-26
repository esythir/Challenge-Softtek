package br.com.fiap.challenge_softteck.domain.valueobject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Value Object que representa a periodicidade de um formulário.
 * Define com que frequência um formulário pode ser respondido.
 */
public record Periodicity(int days) {

    public Periodicity {
        if (days < 0) {
            throw new IllegalArgumentException("Periodicity days cannot be negative");
        }
    }

    /**
     * Cria uma periodicidade diária
     */
    public static Periodicity daily() {
        return new Periodicity(1);
    }

    /**
     * Cria uma periodicidade semanal
     */
    public static Periodicity weekly() {
        return new Periodicity(7);
    }

    /**
     * Cria uma periodicidade mensal
     */
    public static Periodicity monthly() {
        return new Periodicity(30);
    }

    /**
     * Cria uma periodicidade trimestral
     */
    public static Periodicity quarterly() {
        return new Periodicity(90);
    }

    /**
     * Cria uma periodicidade que permite resposta sempre
     */
    public static Periodicity always() {
        return new Periodicity(0);
    }

    /**
     * Verifica se o formulário pode ser respondido sempre
     */
    public boolean isAlwaysAvailable() {
        return days == 0;
    }

    /**
     * Calcula a próxima data permitida para resposta baseada na última resposta
     */
    public LocalDateTime calculateNextAllowedDate(LocalDateTime lastResponseDate) {
        if (isAlwaysAvailable()) {
            return LocalDateTime.MIN; // Sempre disponível
        }
        return lastResponseDate.plus(days, ChronoUnit.DAYS);
    }

    /**
     * Verifica se o formulário está disponível para resposta na data atual
     */
    public boolean isAvailableNow(LocalDateTime lastResponseDate, LocalDateTime now) {
        if (isAlwaysAvailable()) {
            return true;
        }
        LocalDateTime nextAllowed = calculateNextAllowedDate(lastResponseDate);
        return !nextAllowed.isAfter(now);
    }
}
