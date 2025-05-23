package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.dto.CheckinItemDTO;
import br.com.fiap.challenge_softteck.repo.FormResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final FormResponseRepository respRepo;

    @Transactional(readOnly = true)
    public Page<CheckinItemDTO> list(byte[] uuid, LocalDate from, LocalDate to, Pageable page) {
        LocalDateTime f = (from == null ? null : from.atStartOfDay());
        LocalDateTime t = (to   == null ? null : to.plusDays(1).atStartOfDay());
        return respRepo.listCheckins(uuid, f, t, page)
                .map(fr -> new CheckinItemDTO(
                        fr.getId(),
                        fr.getAnsweredAt(),
                        fr.getAnswers().stream()
                                .map(a -> new CheckinItemDTO.Answer(
                                        a.getQuestion().getId(),
                                        a.getOption() != null ? a.getOption().getId() : null,
                                        a.getOption() != null ? a.getOption().getLabel() : null
                                ))
                                .toList()
                ));
    }
}
