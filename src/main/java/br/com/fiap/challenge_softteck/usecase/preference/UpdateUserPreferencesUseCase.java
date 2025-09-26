package br.com.fiap.challenge_softteck.usecase.preference;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.UserPreferenceRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para atualizar preferências do usuário.
 */
@Service
public class UpdateUserPreferencesUseCase {

    private final UserPreferenceRepositoryPort userPreferenceRepository;

    @Autowired
    public UpdateUserPreferencesUseCase(UserPreferenceRepositoryPort userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public CompletableFuture<UserPreference> execute(UserId userId, UserPreference preferences) {
        // Criar nova instância com o userId correto
        UserPreference updatedPreferences = new UserPreference(
                userId,
                preferences.isNotificationsEnabled(),
                preferences.getLastCheckinReminder(),
                preferences.getLastSelfReminder(),
                preferences.getLastClimateReminder());

        // Verificar se já existe
        return userPreferenceRepository.exists(userId)
                .thenCompose(exists -> {
                    if (exists) {
                        return userPreferenceRepository.update(updatedPreferences);
                    } else {
                        return userPreferenceRepository.save(updatedPreferences);
                    }
                });
    }
}
