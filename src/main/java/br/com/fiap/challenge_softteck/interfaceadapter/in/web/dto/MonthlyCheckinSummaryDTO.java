package br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO para resumo mensal de check-ins
 */
public record MonthlyCheckinSummaryDTO(
        int year,
        int month,
        int totalCheckins,
        double averageMood,
        int daysWithCheckin,
        int totalDays,
        double completionRate,
        List<WeeklySummaryDTO> weeklySummaries,
        Map<String, Integer> moodDistribution) {

    /**
     * DTO para resumo semanal
     */
    public record WeeklySummaryDTO(
            int weekNumber,
            LocalDate weekStart,
            LocalDate weekEnd,
            int checkins,
            double averageMood) {
    }
}
