package br.com.fiap.challenge_softteck.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "TB_USER_PREF")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPref {
    @Id
    @Column(name = "USER_UUID")
    private byte[] userUuid;

    @Column(name="NOTIF_ENABLED",
            columnDefinition="CHAR(1)")
    private Boolean notifEnabled; // 'Y'/'N'

    @Column(name = "LAST_CHECKIN_REMINDER")
    private LocalDateTime lastCheckinReminder;

    @Column(name = "LAST_SELF_REMINDER")
    private LocalDateTime lastSelfReminder;

    @Column(name = "LAST_CLIMATE_REMINDER")
    private LocalDateTime lastClimateReminder;
}