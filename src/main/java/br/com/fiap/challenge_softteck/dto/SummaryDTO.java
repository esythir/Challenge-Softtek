package br.com.fiap.challenge_softteck.dto;

import lombok.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class SummaryDTO {
    private int total;
    private List<Item> breakdown;
    private String overallLevel;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Item {
        private String value;
        private long count;
        private double percent;
        private String level;
    }
}