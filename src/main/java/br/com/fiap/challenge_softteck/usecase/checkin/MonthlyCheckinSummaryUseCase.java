package br.com.fiap.challenge_softteck.usecase.checkin;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MonthlyCheckinSummaryUseCase {

    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public MonthlyCheckinSummaryUseCase(FormResponseRepositoryPort formResponseRepository) {
        this.formResponseRepository = formResponseRepository;
    }

    public CompletableFuture<List<FormResponse>> execute(UserId userId, Integer year, Integer month) {
        LocalDate targetDate = LocalDate.now();

        if (year != null && month != null) {
            targetDate = LocalDate.of(year, month, 1);
        }

        LocalDate monthStart = targetDate.withDayOfMonth(1);
        LocalDate monthEnd = targetDate.withDayOfMonth(targetDate.lengthOfMonth());

        LocalDateTime from = monthStart.atStartOfDay();
        LocalDateTime to = monthEnd.atTime(23, 59, 59);

        return formResponseRepository.findCheckinsByUserAndPeriod(userId, from, to);
    }
}
