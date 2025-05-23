package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeeklyMoodDTO {
    private String weekStart;
    private PredominantOptionDTO predominantEmoji;
    private PredominantOptionDTO predominantSentiment;
}
