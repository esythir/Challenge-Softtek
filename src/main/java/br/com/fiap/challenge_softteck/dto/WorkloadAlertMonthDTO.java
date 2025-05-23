package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkloadAlertMonthDTO {
    private String period;
    private double workloadAvg;
    private long alertCount;
}
