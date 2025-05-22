package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.dto.*;
import br.com.fiap.challenge_softteck.repo.*;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import br.com.fiap.challenge_softteck.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepo;
    private final FormResponseRepository respRepo;

    public List<FormListDTO> listAvailable(byte[] userUuid, String type) {
        var list = formRepo.findActiveByType(type);
        List<FormListDTO> out = new ArrayList<>();
        for (var f : list) {
            LocalDateTime last = respRepo.lastAnswered(f.getId(), userUuid);
            LocalDateTime nextAllowed = last == null ? LocalDateTime.MIN
                    : last.plusDays(f.getPeriodicityDays());
            if (nextAllowed.isBefore(LocalDateTime.now())) {
                out.add(new FormListDTO(
                        f.getId(), f.getCode(), f.getName(), f.getFormType(),
                        f.getDescription(), nextAllowed, last));
            }
        }
        return out;
    }

    public FormDetailDTO getDetail(Long id) {
        return formRepo.findById(id).map(Mapper::toDetail).orElseThrow();
    }
}
