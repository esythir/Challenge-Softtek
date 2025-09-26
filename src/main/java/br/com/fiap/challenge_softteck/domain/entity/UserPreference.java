package br.com.fiap.challenge_softteck.domain.entity;

import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio que representa as preferências de um usuário.
 * Contém as regras de negócio relacionadas às preferências do usuário.
 */
@Getter
@Builder
public class UserPreference {
    private final UserId userId;
    private final boolean notificationsEnabled;
    private final LocalDateTime lastCheckinReminder;
    private final LocalDateTime lastSelfReminder;
    private final LocalDateTime lastClimateReminder;

    public UserPreference(UserId userId, boolean notificationsEnabled,
            LocalDateTime lastCheckinReminder, LocalDateTime lastSelfReminder,
            LocalDateTime lastClimateReminder) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.notificationsEnabled = notificationsEnabled;
        this.lastCheckinReminder = lastCheckinReminder;
        this.lastSelfReminder = lastSelfReminder;
        this.lastClimateReminder = lastClimateReminder;
    }

    /**
     * Cria uma preferência padrão para um usuário
     */
    public static UserPreference createDefault(UserId userId) {
        return new UserPreference(userId, true, null, null, null);
    }

    /**
     * Atualiza o status das notificações
     */
    public UserPreference withNotificationsEnabled(boolean enabled) {
        return new UserPreference(userId, enabled, lastCheckinReminder, lastSelfReminder, lastClimateReminder);
    }

    /**
     * Atualiza o último lembrete de check-in
     */
    public UserPreference withLastCheckinReminder(LocalDateTime reminder) {
        return new UserPreference(userId, notificationsEnabled, reminder, lastSelfReminder, lastClimateReminder);
    }

    /**
     * Atualiza o último lembrete de autoavaliação
     */
    public UserPreference withLastSelfReminder(LocalDateTime reminder) {
        return new UserPreference(userId, notificationsEnabled, lastCheckinReminder, reminder, lastClimateReminder);
    }

    /**
     * Atualiza o último lembrete de clima
     */
    public UserPreference withLastClimateReminder(LocalDateTime reminder) {
        return new UserPreference(userId, notificationsEnabled, lastCheckinReminder, lastSelfReminder, reminder);
    }

    /**
     * Verifica se o usuário tem notificações habilitadas
     */
    public boolean hasNotificationsEnabled() {
        return notificationsEnabled;
    }

    /**
     * Verifica se o usuário nunca recebeu um lembrete de check-in
     */
    public boolean neverReceivedCheckinReminder() {
        return lastCheckinReminder == null;
    }

    /**
     * Verifica se o usuário nunca recebeu um lembrete de autoavaliação
     */
    public boolean neverReceivedSelfReminder() {
        return lastSelfReminder == null;
    }

    /**
     * Verifica se o usuário nunca recebeu um lembrete de clima
     */
    public boolean neverReceivedClimateReminder() {
        return lastClimateReminder == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UserPreference that = (UserPreference) obj;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
