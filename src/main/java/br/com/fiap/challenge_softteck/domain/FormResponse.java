package br.com.fiap.challenge_softteck.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "TB_FORM_RESPONSE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormResponse {
    @Id @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FORM_ID")
    private Form form;

    @Column(name = "USER_UUID")
    private byte[] userUuid;

    @Column(name = "ANSWERED_AT")
    private LocalDateTime answeredAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "response", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();
}