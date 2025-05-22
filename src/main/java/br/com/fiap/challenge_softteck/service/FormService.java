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
        List<FormListDTO> out = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (var f : formRepo.findActiveByType(type)) {
            LocalDateTime last = respRepo.lastAnswered(f.getId(), userUuid);
            LocalDateTime nextAllowed;

            switch (f.getFormType()) {
                case "CHECKIN":
                case "REPORT":
                    nextAllowed = now.minusSeconds(1);
                    break;

                default:
                    if (last == null) {
                        nextAllowed = LocalDateTime.MIN;
                    } else {
                        nextAllowed = last.plusDays(f.getPeriodicityDays());
                    }
            }

            if (!nextAllowed.isAfter(now)) {
                out.add(new FormListDTO(
                        f.getId(),
                        f.getCode(),
                        f.getName(),
                        f.getFormType(),
                        f.getDescription(),
                        nextAllowed,
                        last
                ));
            }
        }

        return out;
    }

    public FormDetailDTO getDetail(Long id) {
        return formRepo.findById(id).map(Mapper::toDetail).orElseThrow();
    }
}
