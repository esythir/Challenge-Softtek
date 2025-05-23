package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.dto.CheckinItemDTO;
import br.com.fiap.challenge_softteck.dto.WeeklyCheckinDTO;
import br.com.fiap.challenge_softteck.repo.FormResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.*;

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

    @Transactional(readOnly = true)
    public WeeklyCheckinDTO weeklySummary(byte[] uuid) {
        LocalDate hoje = LocalDate.now();
        LocalDate domingo = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate sabado  = domingo.plusDays(6);

        LocalDateTime inicio = domingo.atStartOfDay();
        LocalDateTime fim    = sabado.plusDays(1).atStartOfDay(); // exclusivo

        List<LocalDate> respondedDates = respRepo
                .findCheckinsBetween(uuid, inicio, fim)
                .stream()
                .map(fr -> fr.getAnsweredAt().toLocalDate())
                .distinct()
                .toList();

        List<WeeklyCheckinDTO.DailyCheckinDTO> dias = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    LocalDate dia = domingo.plusDays(i);
                    boolean exists = respondedDates.contains(dia);
                    return new WeeklyCheckinDTO.DailyCheckinDTO(dia, exists);
                })
                .toList();

        return new WeeklyCheckinDTO(domingo, sabado, dias);
    }
}
