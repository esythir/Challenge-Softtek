package br.com.fiap.challenge_softteck.dto;

import lombok.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class FormDetailDTO {
    private Long id;
    private String code;
    private String name;
    private List<QuestionDTO> questions;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QuestionDTO {
        private Long id;
        private Integer ordinal;
        private String type;
        private String text;
        private List<OptionDTO> options;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OptionDTO {
        private Long id;
        private String value;
        private String label;
    }
}