package br.com.fiap.challenge_softteck.usecase.analysis;

import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.dto.ClimateDiagnosisDTO;
import br.com.fiap.challenge_softteck.dto.DimensionScoreDTO;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para diagnóstico de clima organizacional.
 */
@Service
public class ClimateDiagnosisUseCase {

    private final FormResponseRepositoryPort formResponseRepository;

    @Autowired
    public ClimateDiagnosisUseCase(FormResponseRepositoryPort formResponseRepository) {
        this.formResponseRepository = formResponseRepository;
    }

    public CompletableFuture<ClimateDiagnosisDTO> execute(UserId userId) {
        // Buscar respostas de clima dos últimos 6 meses
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minus(6, ChronoUnit.MONTHS);
        LocalDateTime now = LocalDateTime.now();

        return formResponseRepository.findByFormCodeAndUserAndPeriod(
                FormType.CLIMATE.name(), userId, sixMonthsAgo, now)
                .thenApply(climateResponses -> {
                    // Implementar lógica real de diagnóstico de clima
                    return analyzeClimateDiagnosis(climateResponses, "Últimos 6 meses");
                });
    }

    private ClimateDiagnosisDTO analyzeClimateDiagnosis(List<FormResponse> climateResponses, String period) {
        if (climateResponses.isEmpty()) {
            return new ClimateDiagnosisDTO(period, List.of());
        }

        // Agrupar respostas por dimensão do clima organizacional
        List<DimensionScoreDTO> dimensionScores = new ArrayList<>();

        // Dimensões do clima organizacional baseadas no seed data
        String[] dimensions = {
                "Relacionamento com Liderança",
                "Relacionamento com Colegas",
                "Comunicação",
                "Reconhecimento",
                "Ambiente de Trabalho"
        };

        for (String dimension : dimensions) {
            double score = calculateDimensionScore(climateResponses, dimension);
            String status = determineStatus(score);

            dimensionScores.add(new DimensionScoreDTO(dimension, score, status));
        }

        return new ClimateDiagnosisDTO(period, dimensionScores);
    }

    private double calculateDimensionScore(List<FormResponse> responses, String dimension) {
        // Lógica simplificada para calcular score da dimensão
        // Em uma implementação real, você analisaria as respostas específicas de cada
        // pergunta
        return 3.5; // Placeholder - score médio
    }

    private String determineStatus(double score) {
        if (score < 3.0) {
            return "Crítico";
        } else if (score < 4.0) {
            return "Atenção";
        } else {
            return "Bom";
        }
    }
}
