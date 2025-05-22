package br.com.fiap.challenge_softteck.dto;

import lombok.*;
import java.util.*;

@Getter
@Setter
public class SubmitResponseDTO {
    private List<AnswerDTO> answers;

    @Getter
    @Setter
    public static class AnswerDTO {
        private Long questionId;
        private Long optionId;   // CHOICE
        private Double value;    // SCALE
        private String text;     // TEXT
    }
}