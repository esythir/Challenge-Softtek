package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.domain.Answer;
import br.com.fiap.challenge_softteck.domain.FormResponse;
import br.com.fiap.challenge_softteck.domain.Option;
import br.com.fiap.challenge_softteck.domain.Question;
import br.com.fiap.challenge_softteck.dto.*;
import br.com.fiap.challenge_softteck.repo.FormResponseRepository;
import br.com.fiap.challenge_softteck.repo.QuestionRepository;
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
    private final QuestionRepository questionRepo;

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
                                        a.getOption() != null ? a.getOption().getId() : null,
                                        a.getOption() != null ? a.getOption().getLabel() : null
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

        Map<LocalDate, List<FormResponse>> byWeek = responses.stream()
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

        // calcula Emoji e Sentiment
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

        // média atual de self-assessment
        var currSA = respRepo.findByFormCodeBetween(
                "SELF_ASSESS", uuid, start.atStartOfDay(), end.atStartOfDay());
        double avgWorkCurr = currSA.stream()
                .flatMap(fr -> fr.getAnswers().stream())
                .filter(a -> a.getQuestion().getOrdinal() == 1)
                .mapToInt(a -> a.getOption().getOrdinal())
                .average().orElse(0);

        // média anterior
        LocalDate prevStart = start.minusMonths(1);
        LocalDate prevEnd   = start;
        var prevSA = respRepo.findByFormCodeBetween(
                "SELF_ASSESS", uuid, prevStart.atStartOfDay(), prevEnd.atStartOfDay());
        double avgWorkPrev = prevSA.stream()
                .flatMap(fr -> fr.getAnswers().stream())
                .filter(a -> a.getQuestion().getOrdinal() == 1)
                .mapToInt(a -> a.getOption().getOrdinal())
                .average().orElse(0);

        String trend = avgWorkCurr > avgWorkPrev ? "up"
                : avgWorkCurr < avgWorkPrev ? "down"
                : "same";

        double percentChange = avgWorkPrev > 0
                ? Math.round(((avgWorkCurr - avgWorkPrev) / avgWorkPrev * 100) * 10) / 10.0
                : 0;

        WorkloadDTO workload = new WorkloadDTO(avgWorkCurr, avgWorkPrev, percentChange);

        return new MonthlyCheckinSummaryDTO(
                String.format("%04d-%02d", y, m),
                total, emojiTop, sentiTop, trend, workload
        );
    }

    @Transactional(readOnly = true)
    public MoodDistributionDTO moodDistribution(byte[] uuid, Integer year, Integer month) {
        LocalDate today = LocalDate.now();
        int y = year  != null ? year  : today.getYear();
        int m = month != null ? month : today.getMonthValue();
        LocalDate start = LocalDate.of(y, m, 1);
        LocalDateTime startAt = start.atStartOfDay();
        LocalDateTime endAt   = start.plusMonths(1).atStartOfDay();

        List<FormResponse> responses = respRepo.findCheckinsBetween(uuid, startAt, endAt);
        long totalResponses = responses.size();

        List<Integer> ords = List.of(1, 2);
        List<Question> questions = questionRepo.findByFormCodeAndOrdinalIn("CHECKIN", ords);

        var questionDTOs = questions.stream()
                .sorted(Comparator.comparingInt(Question::getOrdinal))
                .map(q -> {
                    Map<Long, Long> freq = responses.stream()
                            .flatMap(fr -> fr.getAnswers().stream())
                            .filter(a -> a.getQuestion().getOrdinal() == q.getOrdinal())
                            .filter(a -> a.getOption() != null)
                            .collect(Collectors.groupingBy(a -> a.getOption().getId(), Collectors.counting()));

                    List<OptionDistributionDTO> opts = q.getOptions().stream()
                            .sorted(Comparator.comparing(o -> o.getOrdinal()))
                            .map(o -> {
                                long cnt = freq.getOrDefault(o.getId(), 0L);
                                double pct = totalResponses > 0
                                        ? Math.round((cnt * 100.0 / totalResponses) * 10) / 10.0
                                        : 0.0;
                                return new OptionDistributionDTO(o.getId(), o.getLabel(), cnt, pct, mapLevel(pct));
                            })
                            .toList();

                    return new QuestionDistributionDTO(q.getOrdinal(), q.getText(), totalResponses, opts);
                })
                .toList();

        return new MoodDistributionDTO(String.format("%04d-%02d", y, m), questionDTOs);
    }

    private String mapLevel(double percent) {
        if (percent <= 33)      return "Baixo";
        else if (percent <= 66) return "Moderado";
        else                    return "Alto";
    }

    @Transactional(readOnly = true)
    public WorkloadAlertsDTO workloadAlerts(byte[] uuid, Integer monthsParam) {
        int months = monthsParam != null ? monthsParam : 3;
        LocalDate today = LocalDate.now();

        var list = IntStream.rangeClosed(1, months)
                .mapToObj(i -> {
                    LocalDate monthStart = today.minusMonths(months - i).withDayOfMonth(1);
                    LocalDateTime from = monthStart.atStartOfDay();
                    LocalDateTime to   = monthStart.plusMonths(1).atStartOfDay();

                    var responses = respRepo.findByFormCodeBetween("SELF_ASSESS", uuid, from, to);
                    double avgWork = responses.stream()
                            .flatMap(fr -> fr.getAnswers().stream())
                            .filter(a -> a.getQuestion().getOrdinal() == 1)
                            .mapToInt(a -> a.getOption().getOrdinal())
                            .average().orElse(0);

                    long alertCount = responses.stream()
                            .filter(fr -> fr.getAnswers().stream()
                                    .filter(a -> a.getQuestion().getOrdinal() != 1)
                                    .anyMatch(a -> a.getOption().getOrdinal() >= 4))
                            .count();

                    return new WorkloadAlertMonthDTO(
                            String.format("%04d-%02d", monthStart.getYear(), monthStart.getMonthValue()),
                            Math.round(avgWork * 10) / 10.0,
                            alertCount
                    );
                })
                .toList();

        return new WorkloadAlertsDTO(list);
    }

    @Transactional(readOnly = true)
    public ClimateDiagnosisDTO climateDiagnosis(byte[] uuid) {
        LocalDateTime last = respRepo.lastAnsweredByCode("CLIMATE", uuid);
        if (last == null) return new ClimateDiagnosisDTO("", List.of());

        LocalDate startMonth = last.toLocalDate().withDayOfMonth(1);
        LocalDateTime from = startMonth.atStartOfDay();
        LocalDateTime to   = startMonth.plusMonths(1).atStartOfDay();

        var responses = respRepo.findByFormCodeBetween("CLIMATE", uuid, from, to);

        Map<String,List<Integer>> dimsMap = Map.of(
                "relacionamento", List.of(1,2,3,4),
                "comunicacao",    List.of(5,6,7,8),
                "lideranca",      List.of(9,10,11,12,13,14,15)
        );

        var dims = dimsMap.entrySet().stream()
                .map(e -> {
                    double avg = responses.stream()
                            .flatMap(fr -> fr.getAnswers().stream())
                            .filter(a -> e.getValue().contains(a.getQuestion().getOrdinal()))
                            .mapToInt(a -> a.getOption().getOrdinal())
                            .average().orElse(0);
                    double score = Math.round(avg * 10) / 10.0;
                    String status = score < 3 ? "Alerta" : score < 4 ? "Atenção" : "Saudável";
                    return new DimensionScoreDTO(e.getKey(), score, status);
                })
                .toList();

        return new ClimateDiagnosisDTO(
                String.format("%04d-%02d", startMonth.getYear(), startMonth.getMonthValue()),
                dims
        );
    }
}
