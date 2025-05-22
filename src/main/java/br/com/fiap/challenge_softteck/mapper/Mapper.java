package br.com.fiap.challenge_softteck.mapper;

import br.com.fiap.challenge_softteck.domain.*;
import br.com.fiap.challenge_softteck.dto.*;
import java.util.*;
import java.util.stream.*;

public class Mapper {
    public static FormDetailDTO toDetail(Form f) {
        var qlist = f.getQuestions().stream()
                .sorted(Comparator.comparing(Question::getOrdinal))
                .map(q -> new FormDetailDTO.QuestionDTO(
                        q.getId(), q.getOrdinal(), q.getQType(), q.getText(),
                        q.getOptions().stream()
                                .sorted(Comparator.comparing(Option::getOrdinal))
                                .map(o -> new FormDetailDTO.OptionDTO(o.getId(), o.getValue(), o.getLabel()))
                                .toList()))
                .toList();
        return new FormDetailDTO(f.getId(), f.getCode(), f.getName(), qlist);
    }
}