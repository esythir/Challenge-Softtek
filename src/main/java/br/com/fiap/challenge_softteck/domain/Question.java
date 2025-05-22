package br.com.fiap.challenge_softteck.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "TB_QUESTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FORM_ID")
    private Form form;

    @Column(name = "ORDINAL")
    private Integer ordinal;

    @Column(name = "TEXT")
    private String text;

    @Column(name = "QTYPE")
    private String qType; // CHOICE|SCALE|TEXT

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<Option> options = new ArrayList<>();
}