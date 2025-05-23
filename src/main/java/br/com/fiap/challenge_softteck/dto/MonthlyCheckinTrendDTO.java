package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyCheckinTrendDTO {
    private String period;              // "YYYY-MM"
    private DailySummaryDTO dailySummary;
    private List<WeeklyMoodDTO> weeklyMood;
    private String overallTrend;
}
