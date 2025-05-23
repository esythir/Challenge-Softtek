package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyCheckinSummaryDTO {
    private String period;                       // \"YYYY-MM\"
    private long totalCheckins;
    private List<PredominantOptionCountDTO> predominantEmoji;
    private List<PredominantOptionCountDTO> predominantSentiment;
    private String trend;                        // up/down/same
    private WorkloadDTO workload;
}
