package br.com.fiap.challenge_softteck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WeeklyCheckinDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyCheckinDTO> days;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DailyCheckinDTO {
        private LocalDate date;
        private boolean hasCheckin;
    }
}
