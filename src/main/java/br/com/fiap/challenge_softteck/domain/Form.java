package br.com.fiap.challenge_softteck.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "TB_FORM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Form {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "FORM_TYPE")
    private String formType;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PERIODICITY_DAYS")
    private Integer periodicityDays;

    @Column(name = "REMINDER_DAYS")
    private Integer reminderDays;

    @Column(name="IS_ACTIVE",
            columnDefinition="CHAR(1)")
    private Boolean active; // 'Y' / 'N'

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "form")
    private List<Question> questions = new ArrayList<>();

}
