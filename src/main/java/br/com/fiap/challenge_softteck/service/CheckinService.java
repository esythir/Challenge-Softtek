package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.dto.CheckinItemDTO;
import br.com.fiap.challenge_softteck.repo.FormResponseRepository;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final FormResponseRepository respRepo;

    public Page<CheckinItemDTO> list(byte[] uuid, LocalDate from, LocalDate to, Pageable page) {
        LocalDateTime f = from == null ? null : from.atStartOfDay();
        LocalDateTime t = to == null   ? null : to.plusDays(1).atStartOfDay();
        return respRepo.listCheckins(uuid, f, t, page)
                .map(fr -> new CheckinItemDTO(
                        fr.getId(), fr.getAnsweredAt(),
                        fr.getAnswers().stream()
                                .map(a -> new CheckinItemDTO.Answer(
                                        a.getQuestion().getId(),
                                        a.getOption() != null ? a.getOption().getId() : null,
                                        a.getOption() != null ? a.getOption().getLabel() : null))
                                .toList()));
    }
}
