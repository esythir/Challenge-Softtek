package br.com.fiap.challenge_softteck.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_ANSWER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSE_ID")
    private FormResponse response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPTION_ID")
    private Option option;      // nullable for SCALE/TEXT

    @Column(name = "VALUE_NUMERIC")
    private BigDecimal valueNumeric;

    @Column(name = "VALUE_TEXT")
    private String valueText;
}