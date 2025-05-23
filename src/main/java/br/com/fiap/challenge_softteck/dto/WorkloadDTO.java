package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkloadDTO {
    private double currentAvg;
    private double previousAvg;
    private double percentChange;
}
