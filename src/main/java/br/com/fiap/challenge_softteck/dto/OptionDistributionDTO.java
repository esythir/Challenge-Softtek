package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OptionDistributionDTO {
    private Long optionId;
    private String label;
    private long count;
    private double percent;
    private String level;
}
