package br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de check-in
 */
public record CheckinResponseDTO(
        String id,
        String formCode,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime answeredAt,
        List<AnswerDTO> answers) {

    /**
     * DTO para resposta individual
     */
    public record AnswerDTO(
            String questionId,
            String questionText,
            String optionId,
            String optionLabel,
            String valueText,
            Double valueNumeric) {
    }
}
