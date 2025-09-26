package br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto;

import java.util.Map;

/**
 * DTO para distribuição de humor
 */
public record MoodDistributionDTO(
        Map<String, Integer> moodCounts,
        Map<String, Double> moodPercentages,
        String mostCommonMood,
        String leastCommonMood,
        double averageMood,
        int totalResponses) {
}
