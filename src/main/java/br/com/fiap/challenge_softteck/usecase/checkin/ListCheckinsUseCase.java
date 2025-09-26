package br.com.fiap.challenge_softteck.usecase.checkin;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ListCheckinsUseCase {

    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public ListCheckinsUseCase(FormResponseRepositoryPort formResponseRepository) {
        this.formResponseRepository = formResponseRepository;
    }

    public CompletableFuture<List<FormResponse>> execute(UserId userId, LocalDateTime from, LocalDateTime to) {
        return formResponseRepository.findCheckinsByUserAndPeriod(userId, from, to);
    }
}
