package br.com.fiap.challenge_softteck.usecase.checkin;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class WeeklyCheckinSummaryUseCase {

    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public WeeklyCheckinSummaryUseCase(FormResponseRepositoryPort formResponseRepository) {
        this.formResponseRepository = formResponseRepository;
    }

    public CompletableFuture<List<FormResponse>> execute(UserId userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        LocalDateTime from = weekStart.atStartOfDay();
        LocalDateTime to = weekEnd.atTime(23, 59, 59);

        return formResponseRepository.findCheckinsByUserAndPeriod(userId, from, to);
    }
}
