package br.com.fiap.challenge_softteck.interfaceadapter.common.mapper;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.entity.Question;
import br.com.fiap.challenge_softteck.domain.entity.Option;
import br.com.fiap.challenge_softteck.dto.FormDetailDTO;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Mapper para converter entidades de domínio em DTOs de formulário.
 * Parte da camada Interface Adapter da Clean Architecture.
 */
@Component
public class FormMapper {

    /**
     * Converte uma entidade Form em FormDetailDTO
     */
    public FormDetailDTO toDetail(Form form) {
        if (form == null) {
            return null;
        }

        var questionList = form.getQuestions().stream()
                .sorted(Comparator.comparing(Question::getOrdinal))
                .map(this::toQuestionDTO)
                .toList();

        return new FormDetailDTO(
                form.getId(),
                form.getCode(),
                form.getName(),
                questionList);
    }

    /**
     * Converte uma entidade Question em QuestionDTO
     */
    private FormDetailDTO.QuestionDTO toQuestionDTO(Question question) {
        if (question == null) {
            return null;
        }

        var optionList = question.getOptions().stream()
                .sorted(Comparator.comparing(Option::getOrdinal))
                .map(this::toOptionDTO)
                .toList();

        return new FormDetailDTO.QuestionDTO(
                question.getId(),
                question.getOrdinal(),
                question.getQuestionType().name(), // Convert enum to string
                question.getText(),
                optionList);
    }

    /**
     * Converte uma entidade Option em OptionDTO
     */
    private FormDetailDTO.OptionDTO toOptionDTO(Option option) {
        if (option == null) {
            return null;
        }

        return new FormDetailDTO.OptionDTO(
                option.getId(),
                option.getValue(),
                option.getLabel());
    }
}