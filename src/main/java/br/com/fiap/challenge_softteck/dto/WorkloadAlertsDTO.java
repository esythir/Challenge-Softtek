package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WorkloadAlertsDTO {
    private List<WorkloadAlertMonthDTO> months;
}
