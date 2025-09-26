package br.com.fiap.challenge_softteck.domain.entity;

import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.Periodicity;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Entidade de domínio que representa um formulário.
 * Contém as regras de negócio relacionadas aos formulários.
 */
@Getter
@Builder
public class Form {
    private final Long id;
    private final String code;
    private final String name;
    private final FormType formType;
    private final String description;
    private final Periodicity periodicity;
    private final Integer reminderDays;
    private final boolean active;
    private final List<Question> questions;

    public Form(Long id, String code, String name, FormType formType, String description,
            Periodicity periodicity, Integer reminderDays, boolean active, List<Question> questions) {
        this.id = id;
        this.code = validateCode(code);
        this.name = validateName(name);
        this.formType = Objects.requireNonNull(formType, "Form type cannot be null");
        this.description = description;
        this.periodicity = Objects.requireNonNull(periodicity, "Periodicity cannot be null");
        this.reminderDays = reminderDays;
        this.active = active;
        this.questions = questions != null ? new ArrayList<>(questions) : new ArrayList<>();
    }

    /**
     * Verifica se o formulário está disponível para resposta
     */
    public boolean isAvailableForResponse(UserId userId, LocalDateTime lastResponseDate, LocalDateTime now) {
        if (!active) {
            return false;
        }
        return periodicity.isAvailableNow(lastResponseDate, now);
    }

    /**
     * Adiciona uma pergunta ao formulário
     */
    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        questions.add(question);
    }

    /**
     * Retorna as perguntas em ordem de ordinal
     */
    public List<Question> getQuestionsInOrder() {
        return questions.stream()
                .sorted((q1, q2) -> Integer.compare(q1.getOrdinal(), q2.getOrdinal()))
                .toList();
    }

    /**
     * Verifica se o formulário tem perguntas
     */
    public boolean hasQuestions() {
        return !questions.isEmpty();
    }

    /**
     * Retorna o número de perguntas
     */
    public int getQuestionCount() {
        return questions.size();
    }

    /**
     * Busca uma pergunta por ID
     */
    public Optional<Question> getQuestionById(Long questionId) {
        return questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst();
    }

    /**
     * Verifica se o formulário está ativo
     */
    public boolean isActive() {
        return active;
    }

    private String validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Form code cannot be null or empty");
        }
        return code.trim();
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Form name cannot be null or empty");
        }
        return name.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Form form = (Form) obj;
        return Objects.equals(id, form.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
