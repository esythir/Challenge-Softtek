package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.dto.SubmitResponseDTO;
import br.com.fiap.challenge_softteck.domain.*;
import br.com.fiap.challenge_softteck.repo.*;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;

@Service
@RequiredArgsConstructor
public class ResponseService {

    private final FormRepository formRepo;
    private final QuestionRepository qRepo;
    private final OptionRepository optionRepo;
    private final FormResponseRepository respRepo;

    @Transactional
    public Long saveResponse(Long formId, byte[] uuid, SubmitResponseDTO dto) {
        Form form = formRepo.findById(formId).orElseThrow();
        FormResponse resp = new FormResponse();
        resp.setId(null);
        resp.setForm(form);
        resp.setUserUuid(uuid);
        resp.setAnsweredAt(LocalDateTime.now());
        respRepo.save(resp);

        for (var ans : dto.getAnswers()) {
            Question q = qRepo.findById(ans.getQuestionId()).orElseThrow();
            Answer a = new Answer();
            a.setId(null);
            a.setResponse(resp);
            a.setQuestion(q);
            if (ans.getOptionId() != null) {
                a.setOption(optionRepo.findById(ans.getOptionId()).orElseThrow());
            } else if (ans.getValue() != null) {
                a.setValueNumeric(BigDecimal.valueOf(ans.getValue()));
            } else if (ans.getText() != null) {
                a.setValueText(ans.getText());
            }
            resp.getAnswers().add(a);
        }
        respRepo.save(resp);
        return resp.getId();
    }
}
