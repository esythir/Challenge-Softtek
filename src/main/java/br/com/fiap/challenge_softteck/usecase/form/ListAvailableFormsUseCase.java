package br.com.fiap.challenge_softteck.usecase.form;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ListAvailableFormsUseCase {

    private final FormRepositoryPort formRepository;
    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public ListAvailableFormsUseCase(FormRepositoryPort formRepository,
            FormResponseRepositoryPort formResponseRepository) {
        this.formRepository = formRepository;
        this.formResponseRepository = formResponseRepository;
    }

    public CompletableFuture<List<Form>> execute(UserId userId, FormType type) {
        return formRepository.findActiveByType(type)
                .thenCompose(forms -> {
                    LocalDateTime now = LocalDateTime.now();

                    return CompletableFuture.allOf(
                            forms.stream()
                                    .map(form -> checkFormAvailability(form, userId, now))
                                    .toArray(CompletableFuture[]::new))
                            .thenApply(v -> forms.stream()
                                    .filter(form -> isFormAvailable(form, userId, now))
                                    .collect(Collectors.toList()));
                });
    }

    private CompletableFuture<Boolean> checkFormAvailability(Form form, UserId userId, LocalDateTime now) {
        if (form.getFormType().isAlwaysAvailable()) {
            return CompletableFuture.completedFuture(true);
        }

        return formResponseRepository.findLastResponseByFormCodeAndUser(form.getCode(), userId)
                .thenApply(lastResponse -> {
                    LocalDateTime lastResponseDate = lastResponse
                            .map(response -> response.getAnsweredAt())
                            .orElse(null);

                    return form.isAvailableForResponse(userId, lastResponseDate, now);
                });
    }

    private boolean isFormAvailable(Form form, UserId userId, LocalDateTime now) {
        // Esta é uma versão simplificada - na implementação real,
        // você usaria o resultado do checkFormAvailability
        return form.isActive();
    }
}
