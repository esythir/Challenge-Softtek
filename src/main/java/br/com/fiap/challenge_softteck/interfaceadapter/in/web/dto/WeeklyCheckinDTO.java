package br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para resumo semanal de check-ins
 */
public record WeeklyCheckinDTO(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate weekStart,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate weekEnd,
        int totalCheckins,
        double averageMood,
        List<DailyCheckinDTO> dailyCheckins) {

    /**
     * DTO para check-in diário
     */
    public record DailyCheckinDTO(
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
            boolean hasCheckin,
            Double moodValue,
            String moodLabel) {
    }
}
