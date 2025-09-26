package br.com.fiap.challenge_softteck.usecase.analysis;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.dto.MonthlyCheckinTrendDTO;
import br.com.fiap.challenge_softteck.dto.DailySummaryDTO;
import br.com.fiap.challenge_softteck.dto.DailyTotalDTO;
import br.com.fiap.challenge_softteck.dto.WeeklyMoodDTO;
import br.com.fiap.challenge_softteck.dto.PredominantOptionDTO;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Caso de uso para análise de tendência mensal de check-ins.
 */
@Service
public class MonthlyCheckinTrendUseCase {

    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public MonthlyCheckinTrendUseCase(FormResponseRepositoryPort formResponseRepository) {
        this.formResponseRepository = formResponseRepository;
    }

    public CompletableFuture<MonthlyCheckinTrendDTO> execute(UserId userId, Integer year, Integer month) {
        YearMonth targetMonth = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        LocalDate startOfMonth = targetMonth.atDay(1);
        LocalDate endOfMonth = targetMonth.atEndOfMonth();

        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);

        return formResponseRepository.findByFormCodeAndUserAndPeriod(
                FormType.CHECKIN.name(), userId, startDateTime, endDateTime)
                .thenApply(checkins -> {
                    // Implementar lógica real de análise de tendência
                    return analyzeMonthlyTrend(checkins, targetMonth);
                });
    }

    private MonthlyCheckinTrendDTO analyzeMonthlyTrend(List<FormResponse> checkins, YearMonth targetMonth) {
        // Agrupar check-ins por dia
        Map<LocalDate, List<FormResponse>> checkinsByDay = checkins.stream()
                .collect(Collectors.groupingBy(response -> response.getAnsweredAt().toLocalDate()));

        // Calcular resumo diário
        List<DailyTotalDTO> weekdayTotals = new ArrayList<>();
        List<Integer> peakWeekdays = new ArrayList<>();
        List<Integer> lowWeekdays = new ArrayList<>();

        // Agrupar por dia da semana (1=Segunda, 7=Domingo)
        Map<Integer, Long> weekdayCounts = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            weekdayCounts.put(i, 0L);
        }

        for (Map.Entry<LocalDate, List<FormResponse>> entry : checkinsByDay.entrySet()) {
            int weekday = entry.getKey().getDayOfWeek().getValue();
            long count = entry.getValue().size();
            weekdayCounts.put(weekday, weekdayCounts.get(weekday) + count);
        }

        // Criar DailyTotalDTO para cada dia da semana
        for (Map.Entry<Integer, Long> entry : weekdayCounts.entrySet()) {
            weekdayTotals.add(new DailyTotalDTO(entry.getKey(), entry.getValue()));
        }

        // Identificar picos e baixas (simplificado)
        long avgCount = weekdayCounts.values().stream().mapToLong(Long::longValue).sum() / 7;
        for (Map.Entry<Integer, Long> entry : weekdayCounts.entrySet()) {
            if (entry.getValue() > avgCount * 1.2) {
                peakWeekdays.add(entry.getKey());
            } else if (entry.getValue() < avgCount * 0.8) {
                lowWeekdays.add(entry.getKey());
            }
        }

        DailySummaryDTO dailySummary = new DailySummaryDTO(weekdayTotals, peakWeekdays, lowWeekdays);

        // Calcular dados semanais
        List<WeeklyMoodDTO> weeklyMoodData = calculateWeeklyMoodData(checkinsByDay, targetMonth);

        // Determinar tendência geral
        String overallTrend = calculateOverallTrend(new ArrayList<>(weekdayCounts.values()));

        return new MonthlyCheckinTrendDTO(
                targetMonth.toString(),
                dailySummary,
                weeklyMoodData,
                overallTrend);
    }

    private List<WeeklyMoodDTO> calculateWeeklyMoodData(Map<LocalDate, List<FormResponse>> checkinsByDay,
            YearMonth targetMonth) {
        List<WeeklyMoodDTO> weeklyData = new ArrayList<>();

        // Dividir o mês em semanas
        LocalDate startOfMonth = targetMonth.atDay(1);
        LocalDate endOfMonth = targetMonth.atEndOfMonth();

        LocalDate currentWeekStart = startOfMonth;

        while (!currentWeekStart.isAfter(endOfMonth)) {
            final LocalDate weekStart = currentWeekStart;
            final LocalDate weekEnd = weekStart.plusDays(6);
            final LocalDate finalWeekEnd = weekEnd.isAfter(endOfMonth) ? endOfMonth : weekEnd;

            // Contar check-ins da semana (para uso futuro)
            // long weekCheckins = checkinsByDay.entrySet().stream()
            // .filter(entry -> !entry.getKey().isBefore(weekStart)
            // && !entry.getKey().isAfter(finalWeekEnd))
            // .mapToLong(entry -> entry.getValue().size())
            // .sum();

            // Criar dados de humor predominante (simplificado)
            PredominantOptionDTO predominantEmoji = new PredominantOptionDTO(1L, "Neutro");
            PredominantOptionDTO predominantSentiment = new PredominantOptionDTO(1L, "Neutro");

            weeklyData.add(new WeeklyMoodDTO(weekStart.toString(), predominantEmoji, predominantSentiment));

            currentWeekStart = finalWeekEnd.plusDays(1);
        }

        return weeklyData;
    }

    private String calculateOverallTrend(List<Long> dailyCounts) {
        if (dailyCounts.size() < 2) {
            return "Dados insuficientes";
        }

        // Calcular tendência baseada na variação dos check-ins
        long firstHalf = dailyCounts.subList(0, dailyCounts.size() / 2).stream().mapToLong(Long::longValue).sum();
        long secondHalf = dailyCounts.subList(dailyCounts.size() / 2, dailyCounts.size()).stream()
                .mapToLong(Long::longValue).sum();

        double change = (double) (secondHalf - firstHalf) / firstHalf;

        if (change > 0.1) {
            return "Crescimento";
        } else if (change < -0.1) {
            return "Declínio";
        } else {
            return "Estável";
        }
    }
}
