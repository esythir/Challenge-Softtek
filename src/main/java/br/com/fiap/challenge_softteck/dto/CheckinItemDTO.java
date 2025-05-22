package br.com.fiap.challenge_softteck.dto;

import lombok.*;
import java.time.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class CheckinItemDTO {
    private Long checkinId;
    private LocalDateTime timestamp;
    private List<Answer> answers;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Answer {
        private Long questionId;
        private Long optionId;
        private String value;
    }
}