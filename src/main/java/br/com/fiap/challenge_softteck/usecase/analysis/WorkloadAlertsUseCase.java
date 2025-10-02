package br.com.fiap.challenge_softteck.usecase.analysis;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.dto.WorkloadAlertsDTO;
import br.com.fiap.challenge_softteck.dto.WorkloadAlertMonthDTO;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Caso de uso para análise de alertas de carga de trabalho.
 */
@Service
public class WorkloadAlertsUseCase {

    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public WorkloadAlertsUseCase(FormResponseRepositoryPort formResponseRepository) {
        this.formResponseRepository = formResponseRepository;
    }

    public CompletableFuture<WorkloadAlertsDTO> execute(UserId userId, Integer months) {
        // Implementação simplificada para desenvolvimento
        return CompletableFuture.completedFuture(createMockWorkloadAlerts(months != null ? months : 3));
    }

    private WorkloadAlertsDTO createMockWorkloadAlerts(int months) {
        List<WorkloadAlertMonthDTO> monthsData = new ArrayList<>();

        for (int i = 0; i < months; i++) {
            String period = LocalDateTime.now().minus(i, ChronoUnit.MONTHS).getMonth().name() + " " +
                    LocalDateTime.now().minus(i, ChronoUnit.MONTHS).getYear();
            double workloadAvg = 3.5 + (Math.random() * 2.0); // Entre 3.5 e 5.5
            long alertCount = (long) (Math.random() * 5); // Entre 0 e 4

            monthsData.add(new WorkloadAlertMonthDTO(period, workloadAvg, alertCount));
        }

        return new WorkloadAlertsDTO(monthsData);
    }

    private WorkloadAlertsDTO analyzeWorkloadAlerts(List<FormResponse> selfAssessments, int monthsToAnalyze) {
        if (selfAssessments.isEmpty()) {
            return new WorkloadAlertsDTO(List.of());
        }

        // Agrupar autoavaliações por mês
        Map<String, List<FormResponse>> assessmentsByMonth = selfAssessments.stream()
                .collect(Collectors.groupingBy(response -> response.getAnsweredAt().getYear() + "-" +
                        String.format("%02d", response.getAnsweredAt().getMonthValue())));

        List<WorkloadAlertMonthDTO> workloadAlertMonths = new ArrayList<>();

        for (Map.Entry<String, List<FormResponse>> entry : assessmentsByMonth.entrySet()) {
            String month = entry.getKey();
            List<FormResponse> monthAssessments = entry.getValue();

            // Calcular métricas de carga de trabalho
            double workloadAvg = calculateWorkloadAverage(monthAssessments);
            long alertCount = calculateAlertCount(monthAssessments);

            workloadAlertMonths.add(new WorkloadAlertMonthDTO(month, workloadAvg, alertCount));
        }

        return new WorkloadAlertsDTO(workloadAlertMonths);
    }

    private double calculateWorkloadAverage(List<FormResponse> assessments) {
        if (assessments.isEmpty()) {
            return 0.0;
        }

        // Analisar respostas específicas sobre carga de trabalho
        double totalWorkloadScore = 0.0;
        int validResponses = 0;

        for (FormResponse response : assessments) {
            // Buscar respostas relacionadas a carga de trabalho
            double workloadScore = analyzeWorkloadFromResponse(response);
            if (workloadScore > 0) {
                totalWorkloadScore += workloadScore;
                validResponses++;
            }
        }

        if (validResponses == 0) {
            // Fallback: usar frequência de autoavaliações como indicador
            return calculateWorkloadFromFrequency(assessments.size());
        }

        return totalWorkloadScore / validResponses;
    }

    private double analyzeWorkloadFromResponse(FormResponse response) {
        // Implementação real: analisar respostas específicas sobre carga de trabalho
        // Por exemplo, buscar perguntas sobre:
        // - "Como você avalia sua carga de trabalho hoje?"
        // - "Você se sente sobrecarregado?"
        // - "Quantas horas extras você trabalhou esta semana?"

        // Por enquanto, simular análise baseada em padrões de resposta
        // Em uma implementação real, você buscaria por questionId específicos

        // Simular análise baseada no número de respostas e padrões
        int responseCount = response.getAnswers() != null ? response.getAnswers().size() : 0;

        if (responseCount == 0) {
            return 0.0;
        }

        // Simular pontuação baseada em padrões de resposta
        // Valores mais altos indicam maior carga de trabalho
        double baseScore = 2.0; // Score base

        // Ajustar baseado no número de respostas (mais respostas = mais complexidade)
        double complexityFactor = Math.min(responseCount / 5.0, 2.0);

        // Ajustar baseado na frequência de autoavaliações
        double frequencyFactor = Math.min(responseCount / 3.0, 1.5);

        return Math.min(baseScore + complexityFactor + frequencyFactor, 5.0);
    }

    private double calculateWorkloadFromFrequency(int assessmentCount) {
        // Fallback: calcular baseado na frequência de autoavaliações
        if (assessmentCount > 15) {
            return 4.5; // Muitas autoavaliações = alta carga
        } else if (assessmentCount > 8) {
            return 3.5; // Média-alta carga
        } else if (assessmentCount > 4) {
            return 2.5; // Média carga
        } else {
            return 1.5; // Baixa carga
        }
    }

    private long calculateAlertCount(List<FormResponse> assessments) {
        if (assessments.isEmpty()) {
            return 0;
        }

        long alertCount = 0;

        for (FormResponse response : assessments) {
            // Analisar cada resposta para identificar sinais de alerta
            long responseAlerts = analyzeAlertsFromResponse(response);
            alertCount += responseAlerts;
        }

        // Adicionar alertas baseados em padrões gerais
        alertCount += analyzePatternAlerts(assessments);

        return alertCount;
    }

    private long analyzeAlertsFromResponse(FormResponse response) {
        long alerts = 0;

        // Simular análise de respostas específicas
        // Em uma implementação real, você buscaria por:
        // - Respostas indicando estresse alto
        // - Respostas sobre sobrecarga de trabalho
        // - Respostas sobre dificuldades de concentração
        // - Respostas sobre problemas de sono

        if (response.getAnswers() != null) {
            int responseCount = response.getAnswers().size();

            // Simular alertas baseados em padrões de resposta
            if (responseCount > 8) {
                alerts += 2; // Muitas respostas podem indicar complexidade excessiva
            } else if (responseCount > 5) {
                alerts += 1; // Respostas moderadas
            }

            // Simular alertas baseados em horário de resposta
            // Respostas muito tarde ou muito cedo podem indicar estresse
            int hour = response.getAnsweredAt().getHour();
            if (hour < 6 || hour > 22) {
                alerts += 1; // Resposta fora do horário normal
            }
        }

        return alerts;
    }

    private long analyzePatternAlerts(List<FormResponse> assessments) {
        long patternAlerts = 0;

        // Analisar padrões temporais
        int size = assessments.size();

        if (size > 20) {
            patternAlerts += 3; // Muitas autoavaliações = possível sobrecarga
        } else if (size > 15) {
            patternAlerts += 2; // Alto número de autoavaliações
        } else if (size > 10) {
            patternAlerts += 1; // Número moderado-alto
        }

        // Analisar frequência de respostas
        if (size > 0) {
            // Calcular dias entre respostas
            var sortedResponses = assessments.stream()
                    .sorted((a, b) -> a.getAnsweredAt().compareTo(b.getAnsweredAt()))
                    .toList();

            long consecutiveDays = 1;
            for (int i = 1; i < sortedResponses.size(); i++) {
                var prev = sortedResponses.get(i - 1).getAnsweredAt().toLocalDate();
                var curr = sortedResponses.get(i).getAnsweredAt().toLocalDate();

                if (prev.plusDays(1).equals(curr)) {
                    consecutiveDays++;
                } else {
                    consecutiveDays = 1;
                }
            }

            // Muitos dias consecutivos podem indicar estresse
            if (consecutiveDays > 7) {
                patternAlerts += 2;
            } else if (consecutiveDays > 5) {
                patternAlerts += 1;
            }
        }

        return patternAlerts;
    }
}
