package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.dto.CheckinItemDTO;
import br.com.fiap.challenge_softteck.dto.WeeklyCheckinDTO;
import br.com.fiap.challenge_softteck.dto.DailyTotalDTO;
import br.com.fiap.challenge_softteck.dto.DailySummaryDTO;
import br.com.fiap.challenge_softteck.dto.PredominantOptionDTO;
import br.com.fiap.challenge_softteck.dto.WeeklyMoodDTO;
import br.com.fiap.challenge_softteck.dto.MonthlyCheckinTrendDTO;
import br.com.fiap.challenge_softteck.repo.FormResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final FormResponseRepository respRepo;

    @Transactional(readOnly = true)
    public Page<CheckinItemDTO> list(byte[] uuid, LocalDate from, LocalDate to, Pageable page) {
        LocalDateTime f = from == null ? null : from.atStartOfDay();
        LocalDateTime t = to   == null ? null : to.plusDays(1).atStartOfDay();
        return respRepo.listCheckins(uuid, f, t, page)
                .map(fr -> new CheckinItemDTO(
                        fr.getId(),
                        fr.getAnsweredAt(),
                        fr.getAnswers().stream()
                                .map(a -> new CheckinItemDTO.Answer(
                                        a.getQuestion().getId(),
                                        a.getOption()   != null ? a.getOption().getId()   : null,
                                        a.getOption()   != null ? a.getOption().getLabel(): null
                                ))
                                .toList()
                ));
    }

    @Transactional(readOnly = true)
    public WeeklyCheckinDTO weeklySummary(byte[] uuid) {
        LocalDate hoje    = LocalDate.now();
        LocalDate domingo = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate sabado  = domingo.plusDays(6);

        LocalDateTime inicio = domingo.atStartOfDay();
        LocalDateTime fim    = sabado.plusDays(1).atStartOfDay(); // exclusivo

        var respondedDates = respRepo.findCheckinsBetween(uuid, inicio, fim).stream()
                .map(fr -> fr.getAnsweredAt().toLocalDate())
                .distinct()
                .toList();

        var dias = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    LocalDate dia    = domingo.plusDays(i);
                    boolean exists   = respondedDates.contains(dia);
                    return new WeeklyCheckinDTO.DailyCheckinDTO(dia, exists);
                })
                .toList();

        return new WeeklyCheckinDTO(domingo, sabado, dias);
    }

    @Transactional(readOnly = true)
    public MonthlyCheckinTrendDTO monthlyTrend(byte[] uuid, Integer ano, Integer mes) {
        var hoje = LocalDate.now();
        int y = ano   != null ? ano   : hoje.getYear();
        int m = mes   != null ? mes   : hoje.getMonthValue();

        LocalDate mesStart = LocalDate.of(y, m, 1);
        LocalDate mesEnd   = mesStart.plusMonths(1);

        var all = respRepo.findCheckinsBetween(
                uuid,
                mesStart.atStartOfDay(),
                mesEnd.atStartOfDay()
        );

        var cnt = all.stream()
                .map(fr -> fr.getAnsweredAt().toLocalDate().getDayOfWeek())
                .collect(Collectors.groupingBy(d->d, Collectors.counting()));

        var totals = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    DayOfWeek dow = (i == 0 ? DayOfWeek.SUNDAY : DayOfWeek.of(i));
                    return new DailyTotalDTO(i, cnt.getOrDefault(dow, 0L));
                })
                .toList();

        long max = totals.stream().mapToLong(DailyTotalDTO::getTotal).max().orElse(0);
        long min = totals.stream().mapToLong(DailyTotalDTO::getTotal).min().orElse(0);

        var peaks = totals.stream()
                .filter(d -> d.getTotal() == max)
                .map(DailyTotalDTO::getWeekday)
                .toList();

        var lows = totals.stream()
                .filter(d -> d.getTotal() == min)
                .map(DailyTotalDTO::getWeekday)
                .toList();

        var dailySummary = new DailySummaryDTO(totals, peaks, lows);

        var byWeek = all.stream()
                .collect(Collectors.groupingBy(fr ->
                        fr.getAnsweredAt().toLocalDate()
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)))
                );

        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        var weekly = byWeek.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> {
                    LocalDate wk = e.getKey();
                    var responses = e.getValue();

                    var emoji = responses.stream()
                            .flatMap(fr -> fr.getAnswers().stream())
                            .filter(a -> a.getQuestion().getOrdinal() == 1)
                            .collect(Collectors.groupingBy(a->a.getOption(), Collectors.counting()))
                            .entrySet().stream()
                            .max(Comparator.comparingLong(Map.Entry::getValue))
                            .map(en -> new PredominantOptionDTO(
                                    en.getKey().getId(),
                                    en.getKey().getLabel()))
                            .orElse(null);

                    var senti = responses.stream()
                            .flatMap(fr -> fr.getAnswers().stream())
                            .filter(a -> a.getQuestion().getOrdinal() == 2)
                            .collect(Collectors.groupingBy(a->a.getOption(), Collectors.counting()))
                            .entrySet().stream()
                            .max(Comparator.comparingLong(Map.Entry::getValue))
                            .map(en -> new PredominantOptionDTO(
                                    en.getKey().getId(),
                                    en.getKey().getLabel()))
                            .orElse(null);

                    return new WeeklyMoodDTO(
                            wk.format(fmt),
                            emoji,
                            senti
                    );
                })
                .toList();

        var ordinals = byWeek.values().stream()
                .map(list -> list.stream()
                        .flatMap(fr -> fr.getAnswers().stream())
                        .filter(a -> a.getQuestion().getOrdinal() == 2)
                        .map(a -> a.getOption().getOrdinal())
                        .mapToInt(i->i)
                        .average()
                        .orElse(0))
                .mapToDouble(d->d)
                .toArray();

        double mid = ordinals.length / 2.0;
        double firstAvg  = Arrays.stream(ordinals, 0, (int)Math.floor(mid)).average().orElse(0);
        double secondAvg = Arrays.stream(ordinals, (int)Math.floor(mid), ordinals.length).average().orElse(0);

        String trend;
        if (secondAvg > firstAvg) {
            trend = "A tendência geral este mês é de aumento de sentimentos negativos";
        } else if (secondAvg < firstAvg) {
            trend = "A tendência geral este mês é de aumento de sentimentos positivos";
        } else {
            trend = "A tendência geral este mês está estável";
        }

        return new MonthlyCheckinTrendDTO(
                String.format("%04d-%02d", y, m),
                dailySummary,
                weekly,
                trend
        );
    }
}
