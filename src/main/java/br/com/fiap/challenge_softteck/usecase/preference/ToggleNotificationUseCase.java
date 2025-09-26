package br.com.fiap.challenge_softteck.usecase.preference;

import br.com.fiap.challenge_softteck.domain.entity.UserPreference;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.port.out.firebase.UserPreferenceRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para alternar notificações do usuário.
 * 
 * @param userId              ID do usuário
 * @param enableNotifications true para habilitar, false para desabilitar
 * @return CompletableFuture com as preferências atualizadas
 */
@Service
public class ToggleNotificationUseCase {

    private final UserPreferenceRepositoryPort userPreferenceRepository;

    @Autowired
    public ToggleNotificationUseCase(UserPreferenceRepositoryPort userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public CompletableFuture<UserPreference> execute(UserId userId, boolean enableNotifications) {
        return userPreferenceRepository.findByUserId(userId)
                .thenCompose(optionalPreference -> {
                    // Obter preferências existentes ou criar padrão
                    UserPreference currentPreference = optionalPreference.orElse(UserPreference.createDefault(userId));

                    // Criar nova instância com notificações atualizadas
                    UserPreference updatedPreference = currentPreference.withNotificationsEnabled(enableNotifications);

                    // Salvar preferências atualizadas
                    return userPreferenceRepository.save(updatedPreference);
                });
    }
}
