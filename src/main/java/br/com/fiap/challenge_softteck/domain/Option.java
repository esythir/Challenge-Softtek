package br.com.fiap.challenge_softteck.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_OPTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @Column(name = "ORDINAL")
    private Integer ordinal;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "LABEL")
    private String label;
}