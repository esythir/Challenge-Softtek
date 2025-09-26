package br.com.fiap.challenge_softteck.usecase.preference;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.UserPreferenceRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para buscar preferências do usuário.
 */
@Service
public class GetUserPreferencesUseCase {

    private final UserPreferenceRepositoryPort userPreferenceRepository;

    @Autowired
    public GetUserPreferencesUseCase(UserPreferenceRepositoryPort userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public CompletableFuture<Optional<UserPreference>> execute(UserId userId) {
        return userPreferenceRepository.findByUserId(userId);
    }
}
