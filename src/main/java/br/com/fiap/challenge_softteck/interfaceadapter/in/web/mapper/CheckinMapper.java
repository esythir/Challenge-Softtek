package br.com.fiap.challenge_softteck.interfaceadapter.in.web.mapper;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.CheckinResponseDTO;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.WeeklyCheckinDTO;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.MonthlyCheckinSummaryDTO;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.MoodDistributionDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CheckinMapper {

    public CheckinResponseDTO toCheckinResponseDTO(FormResponse formResponse) {
        return new CheckinResponseDTO(
                formResponse.getId().toString(),
                "CHECKIN", // Assumindo que é sempre check-in
                formResponse.getAnsweredAt(),
                formResponse.getAnswers().stream()
                        .map(this::toAnswerDTO)
                        .collect(Collectors.toList()));
    }

    private CheckinResponseDTO.AnswerDTO toAnswerDTO(FormResponse.Answer answer) {
        return new CheckinResponseDTO.AnswerDTO(
                answer.getQuestionId().toString(),
                null, // TODO: Buscar texto da pergunta
                answer.getOptionId() != null ? answer.getOptionId().toString() : null,
                null, // TODO: Buscar label da opção
                answer.getValue() instanceof br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.TextAnswer
                        ? ((br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.TextAnswer) answer.getValue())
                                .value()
                        : null,
                answer.getValue() instanceof br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.NumericAnswer
                        ? ((br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.NumericAnswer) answer
                                .getValue()).value().doubleValue()
                        : null);
    }

    public WeeklyCheckinDTO toWeeklyCheckinDTO(List<FormResponse> checkins) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        int totalCheckins = checkins.size();
        double averageMood = calculateAverageMood(checkins);

        List<WeeklyCheckinDTO.DailyCheckinDTO> dailyCheckins = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            boolean hasCheckin = checkins.stream()
                    .anyMatch(checkin -> checkin.getAnsweredAt().toLocalDate().equals(date));

            Double moodValue = null;
            String moodLabel = null;
            if (hasCheckin) {
                FormResponse checkin = checkins.stream()
                        .filter(c -> c.getAnsweredAt().toLocalDate().equals(date))
                        .findFirst()
                        .orElse(null);
                if (checkin != null) {
                    moodValue = extractMoodValue(checkin);
                    moodLabel = getMoodLabel(moodValue);
                }
            }

            dailyCheckins.add(new WeeklyCheckinDTO.DailyCheckinDTO(date, hasCheckin, moodValue, moodLabel));
        }

        return new WeeklyCheckinDTO(weekStart, weekEnd, totalCheckins, averageMood, dailyCheckins);
    }

    public MonthlyCheckinSummaryDTO toMonthlyCheckinSummaryDTO(List<FormResponse> checkins, Integer year,
            Integer month) {
        LocalDate targetDate = LocalDate.now();
        if (year != null && month != null) {
            targetDate = LocalDate.of(year, month, 1);
        }

        int totalCheckins = checkins.size();
        double averageMood = calculateAverageMood(checkins);
        int daysWithCheckin = (int) checkins.stream()
                .map(checkin -> checkin.getAnsweredAt().toLocalDate())
                .distinct()
                .count();
        int totalDays = targetDate.lengthOfMonth();
        double completionRate = totalDays > 0 ? (double) daysWithCheckin / totalDays * 100 : 0;

        Map<String, Integer> moodDistribution = calculateMoodDistribution(checkins);

        return new MonthlyCheckinSummaryDTO(
                targetDate.getYear(),
                targetDate.getMonthValue(),
                totalCheckins,
                averageMood,
                daysWithCheckin,
                totalDays,
                completionRate,
                List.of(), // TODO: Implementar resumos semanais
                moodDistribution);
    }

    public MoodDistributionDTO toMoodDistributionDTO(List<FormResponse> checkins) {
        Map<String, Integer> moodCounts = calculateMoodDistribution(checkins);
        int totalResponses = moodCounts.values().stream().mapToInt(Integer::intValue).sum();

        Map<String, Double> moodPercentages = moodCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> totalResponses > 0 ? (double) entry.getValue() / totalResponses * 100 : 0.0));

        String mostCommonMood = moodCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        String leastCommonMood = moodCounts.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        double averageMood = calculateAverageMood(checkins);

        return new MoodDistributionDTO(
                moodCounts,
                moodPercentages,
                mostCommonMood,
                leastCommonMood,
                averageMood,
                totalResponses);
    }

    private double calculateAverageMood(List<FormResponse> checkins) {
        return checkins.stream()
                .mapToDouble(this::extractMoodValue)
                .filter(value -> value > 0)
                .average()
                .orElse(0.0);
    }

    private Double extractMoodValue(FormResponse checkin) {
        // Assumindo que a primeira resposta é sempre o humor
        return checkin.getAnswers().stream()
                .findFirst()
                .map(answer -> {
                    if (answer
                            .getValue() instanceof br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.NumericAnswer) {
                        return ((br.com.fiap.challenge_softteck.domain.valueobject.AnswerValue.NumericAnswer) answer
                                .getValue()).value().doubleValue();
                    }
                    return null;
                })
                .orElse(null);
    }

    private String getMoodLabel(Double moodValue) {
        if (moodValue == null)
            return null;

        if (moodValue <= 1.5)
            return "Muito ruim";
        if (moodValue <= 2.5)
            return "Ruim";
        if (moodValue <= 3.5)
            return "Regular";
        if (moodValue <= 4.5)
            return "Bom";
        return "Muito bom";
    }

    private Map<String, Integer> calculateMoodDistribution(List<FormResponse> checkins) {
        Map<String, Integer> distribution = new HashMap<>();

        for (FormResponse checkin : checkins) {
            String moodLabel = getMoodLabel(extractMoodValue(checkin));
            if (moodLabel != null) {
                distribution.merge(moodLabel, 1, Integer::sum);
            }
        }

        return distribution;
    }
}
