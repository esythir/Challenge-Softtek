package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.domain.Answer;
import br.com.fiap.challenge_softteck.domain.Option;
import br.com.fiap.challenge_softteck.dto.CheckinItemDTO;
import br.com.fiap.challenge_softteck.dto.DailySummaryDTO;
import br.com.fiap.challenge_softteck.dto.DailyTotalDTO;
import br.com.fiap.challenge_softteck.dto.MonthlyCheckinSummaryDTO;
import br.com.fiap.challenge_softteck.dto.MonthlyCheckinTrendDTO;
import br.com.fiap.challenge_softteck.dto.PredominantOptionCountDTO;
import br.com.fiap.challenge_softteck.dto.PredominantOptionDTO;
import br.com.fiap.challenge_softteck.dto.WeeklyCheckinDTO;
import br.com.fiap.challenge_softteck.dto.WeeklyMoodDTO;
import br.com.fiap.challenge_softteck.dto.WorkloadDTO;
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
import java.util.function.IntFunction;
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
        LocalDateTime fim    = sabado.plusDays(1).atStartOfDay();

        List<LocalDate> respondedDates = respRepo.findCheckinsBetween(uuid, inicio, fim).stream()
                .map(fr -> fr.getAnsweredAt().toLocalDate())
                .distinct()
                .toList();

        List<WeeklyCheckinDTO.DailyCheckinDTO> dias = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    LocalDate dia  = domingo.plusDays(i);
                    boolean exists = respondedDates.contains(dia);
                    return new WeeklyCheckinDTO.DailyCheckinDTO(dia, exists);
                })
                .toList();

        return new WeeklyCheckinDTO(domingo, sabado, dias);
    }

    @Transactional(readOnly = true)
    public MonthlyCheckinTrendDTO monthlyTrend(byte[] uuid, Integer year, Integer month) {
        LocalDate hoje = LocalDate.now();
        int y = year  != null ? year  : hoje.getYear();
        int m = month != null ? month : hoje.getMonthValue();

        LocalDate start = LocalDate.of(y, m, 1);
        LocalDate end   = start.plusMonths(1);

        var responses = respRepo.findCheckinsBetween(
                uuid,
                start.atStartOfDay(),
                end.atStartOfDay()
        );

        Map<DayOfWeek, Long> countByDow = responses.stream()
                .map(r -> r.getAnsweredAt().toLocalDate().getDayOfWeek())
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));

        List<DailyTotalDTO> totals = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    DayOfWeek dow = i == 0 ? DayOfWeek.SUNDAY : DayOfWeek.of(i);
                    return new DailyTotalDTO(i, countByDow.getOrDefault(dow, 0L));
                })
                .toList();

        long max = totals.stream().mapToLong(DailyTotalDTO::getTotal).max().orElse(0);
        long min = totals.stream().mapToLong(DailyTotalDTO::getTotal).min().orElse(0);

        List<Integer> peaks = totals.stream()
                .filter(d -> d.getTotal() == max)
                .map(DailyTotalDTO::getWeekday)
                .toList();
        List<Integer> lows = totals.stream()
                .filter(d -> d.getTotal() == min)
                .map(DailyTotalDTO::getWeekday)
                .toList();

        DailySummaryDTO dailySummary = new DailySummaryDTO(totals, peaks, lows);

        Map<LocalDate, List<br.com.fiap.challenge_softteck.domain.FormResponse>> byWeek = responses.stream()
                .collect(Collectors.groupingBy(fr ->
                        fr.getAnsweredAt().toLocalDate()
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                ));

        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        List<WeeklyMoodDTO> weekly = byWeek.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> {
                    LocalDate wkStart = e.getKey();
                    var weekResp = e.getValue();

                    PredominantOptionDTO emoji = weekResp.stream()
                            .flatMap(fr -> fr.getAnswers().stream())
                            .filter(a -> a.getQuestion().getOrdinal() == 1)
                            .collect(Collectors.groupingBy(Answer::getOption, Collectors.counting()))
                            .entrySet().stream()
                            .max(Comparator.comparingLong(Map.Entry::getValue))
                            .map(en -> new PredominantOptionDTO(en.getKey().getId(), en.getKey().getLabel()))
                            .orElse(null);

                    PredominantOptionDTO senti = weekResp.stream()
                            .flatMap(fr -> fr.getAnswers().stream())
                            .filter(a -> a.getQuestion().getOrdinal() == 2)
                            .collect(Collectors.groupingBy(Answer::getOption, Collectors.counting()))
                            .entrySet().stream()
                            .max(Comparator.comparingLong(Map.Entry::getValue))
                            .map(en -> new PredominantOptionDTO(en.getKey().getId(), en.getKey().getLabel()))
                            .orElse(null);

                    return new WeeklyMoodDTO(wkStart.format(fmt), emoji, senti);
                })
                .toList();

        double[] ordinals = byWeek.values().stream()
                .mapToDouble(list -> list.stream()
                        .flatMap(fr -> fr.getAnswers().stream())
                        .filter(a -> a.getQuestion().getOrdinal() == 2)
                        .mapToInt(a -> a.getOption().getOrdinal())
                        .average().orElse(0))
                .toArray();

        int split = ordinals.length / 2;
        double firstAvg  = Arrays.stream(ordinals, 0, split).average().orElse(0);
        double secondAvg = Arrays.stream(ordinals, split, ordinals.length).average().orElse(0);

        String overallTrend;
        if (secondAvg > firstAvg) overallTrend = "A tendência geral este mês é de aumento de sentimentos negativos";
        else if (secondAvg < firstAvg) overallTrend = "A tendência geral este mês é de aumento de sentimentos positivos";
        else overallTrend = "A tendência geral este mês está estável";

        return new MonthlyCheckinTrendDTO(String.format("%04d-%02d", y, m),
                dailySummary, weekly, overallTrend);
    }

    @Transactional(readOnly = true)
    public MonthlyCheckinSummaryDTO monthlySummary(byte[] uuid, Integer year, Integer month) {
        LocalDate hoje = LocalDate.now();
        int y = year  != null ? year  : hoje.getYear();
        int m = month != null ? month : hoje.getMonthValue();

        LocalDate start = LocalDate.of(y, m, 1);
        LocalDate end   = start.plusMonths(1);

        var checkins = respRepo.findCheckinsBetween(uuid,
                start.atStartOfDay(), end.atStartOfDay());
        long total = checkins.size();

        IntFunction<List<PredominantOptionCountDTO>> topByOrdinal = ord -> {
            Map<Option, Long> freq = checkins.stream()
                    .flatMap(fr -> fr.getAnswers().stream())
                    .filter(a -> a.getQuestion().getOrdinal() == ord)
                    .collect(Collectors.groupingBy(Answer::getOption, Collectors.counting()));

            long maxCount = freq.values().stream().mapToLong(l -> l).max().orElse(0);
            return freq.entrySet().stream()
                    .filter(e -> e.getValue() == maxCount)
                    .map(e -> new PredominantOptionCountDTO(
                            e.getKey().getId(),
                            e.getKey().getLabel(),
                            e.getValue()
                    ))
                    .toList();
        };

        List<PredominantOptionCountDTO> emojiTop = topByOrdinal.apply(1);
        List<PredominantOptionCountDTO> sentiTop = topByOrdinal.apply(2);

        double avgCurrSent = checkins.stream()
                .flatMap(fr -> fr.getAnswers().stream())
                .filter(a -> a.getQuestion().getOrdinal() == 2)
                .mapToInt(a -> a.getOption().getOrdinal())
                .average().orElse(0);

        LocalDate prevStart = start.minusMonths(1);
        LocalDate prevEnd   = start;
        var prevCheckins = respRepo.findCheckinsBetween(uuid,
                prevStart.atStartOfDay(), prevEnd.atStartOfDay());

        double avgPrevSent = prevCheckins.stream()
                .flatMap(fr -> fr.getAnswers().stream())
                .filter(a -> a.getQuestion().getOrdinal() == 2)
                .mapToInt(a -> a.getOption().getOrdinal())
                .average().orElse(0);

        String trend = avgCurrSent > avgPrevSent ? "up"
                : avgCurrSent < avgPrevSent ? "down"
                : "same";

        var currSA = respRepo.findByFormCodeBetween("SELF_ASSESSMENT",
                uuid, start.atStartOfDay(), end.atStartOfDay());
        var prevSA = respRepo.findByFormCodeBetween("SELF_ASSESSMENT",
                uuid, prevStart.atStartOfDay(), prevEnd.atStartOfDay());

        double avgWorkCurr = currSA.stream()
                .flatMap(fr -> fr.getAnswers().stream())
                .filter(a -> a.getQuestion().getOrdinal() == 1)
                .mapToInt(a -> a.getOption().getOrdinal())
                .average().orElse(0);

        double avgWorkPrev = prevSA.stream()
                .flatMap(fr -> fr.getAnswers().stream())
                .filter(a -> a.getQuestion().getOrdinal() == 1)
                .mapToInt(a -> a.getOption().getOrdinal())
                .average().orElse(0);

        double percentChange = avgWorkPrev > 0
                ? ((avgWorkCurr - avgWorkPrev) / avgWorkPrev * 100)
                : 0;
        percentChange = Math.round(percentChange * 10) / 10.0;

        WorkloadDTO workload = new WorkloadDTO(avgWorkCurr, avgWorkPrev, percentChange);

        return new MonthlyCheckinSummaryDTO(
                String.format("%04d-%02d", y, m),
                total,
                emojiTop,
                sentiTop,
                trend,
                workload
        );
    }

}
