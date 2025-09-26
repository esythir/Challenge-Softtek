package br.com.fiap.challenge_softteck.interfaceadapter.in.web.checkin;

import br.com.fiap.challenge_softteck.domain.exception.BusinessException;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.framework.auth.FirebaseAuthService;
import br.com.fiap.challenge_softteck.interfaceadapter.common.error.ErrorCode;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.CheckinResponseDTO;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.WeeklyCheckinDTO;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.MonthlyCheckinSummaryDTO;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.dto.MoodDistributionDTO;
import br.com.fiap.challenge_softteck.interfaceadapter.in.web.mapper.CheckinMapper;
import br.com.fiap.challenge_softteck.usecase.checkin.ListCheckinsUseCase;
import br.com.fiap.challenge_softteck.usecase.checkin.WeeklyCheckinSummaryUseCase;
import br.com.fiap.challenge_softteck.usecase.checkin.MonthlyCheckinSummaryUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/checkins")
@CrossOrigin(origins = "*")
public class CheckinController {

    private static final Logger logger = LoggerFactory.getLogger(CheckinController.class);

    private final ListCheckinsUseCase listCheckinsUseCase;
    private final WeeklyCheckinSummaryUseCase weeklyCheckinSummaryUseCase;
    private final MonthlyCheckinSummaryUseCase monthlyCheckinSummaryUseCase;
    private final CheckinMapper checkinMapper;
    private final FirebaseAuthService firebaseAuthService;

    @Autowired
    public CheckinController(ListCheckinsUseCase listCheckinsUseCase,
            WeeklyCheckinSummaryUseCase weeklyCheckinSummaryUseCase,
            MonthlyCheckinSummaryUseCase monthlyCheckinSummaryUseCase,
            CheckinMapper checkinMapper,
            FirebaseAuthService firebaseAuthService) {
        this.listCheckinsUseCase = listCheckinsUseCase;
        this.weeklyCheckinSummaryUseCase = weeklyCheckinSummaryUseCase;
        this.monthlyCheckinSummaryUseCase = monthlyCheckinSummaryUseCase;
        this.checkinMapper = checkinMapper;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Lista check-ins do usuário com paginação
     */
    @GetMapping
    public CompletableFuture<ResponseEntity<List<CheckinResponseDTO>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Validação de parâmetros
            if (page < 0) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(),
                        "Página deve ser maior ou igual a zero");
            }
            if (size <= 0 || size > 100) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(),
                        "Tamanho deve estar entre 1 e 100");
            }

            UserId userId = firebaseAuthService.extractUserIdFromToken(authHeader);

            LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
            LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;

            return listCheckinsUseCase.execute(userId, fromDateTime, toDateTime)
                    .thenApply(checkins -> {
                        List<CheckinResponseDTO> dtos = checkins.stream()
                                .map(checkinMapper::toCheckinResponseDTO)
                                .collect(Collectors.toList());

                        // Aplicar paginação simples (em produção, implementar no repositório)
                        int start = page * size;
                        int end = Math.min(start + size, dtos.size());

                        if (start >= dtos.size()) {
                            return ResponseEntity.ok(List.<CheckinResponseDTO>of());
                        }

                        return ResponseEntity.ok(dtos.subList(start, end));
                    })
                    .exceptionally(throwable -> {
                        logger.error("Erro ao listar check-ins", throwable);
                        throw new RuntimeException("Erro interno ao listar check-ins", throwable);
                    });

        } catch (BusinessException e) {
            logger.warn("Erro de negócio ao listar check-ins: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar check-ins", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                    "Erro interno ao listar check-ins");
        }
    }

    /**
     * Resumo semanal de check-ins
     */
    @GetMapping("/weekly")
    public CompletableFuture<ResponseEntity<WeeklyCheckinDTO>> weekly(
            @RequestHeader("Authorization") String authHeader) {

        try {
            UserId userId = firebaseAuthService.extractUserIdFromToken(authHeader);

            return weeklyCheckinSummaryUseCase.execute(userId)
                    .thenApply(checkins -> {
                        WeeklyCheckinDTO dto = checkinMapper.toWeeklyCheckinDTO(checkins);
                        return ResponseEntity.ok(dto);
                    })
                    .exceptionally(throwable -> {
                        logger.error("Erro ao gerar resumo semanal", throwable);
                        throw new RuntimeException("Erro interno ao gerar resumo semanal", throwable);
                    });

        } catch (BusinessException e) {
            logger.warn("Erro de negócio ao gerar resumo semanal: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar resumo semanal", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                    "Erro interno ao gerar resumo semanal");
        }
    }

    /**
     * Resumo mensal de check-ins
     */
    @GetMapping("/monthly-summary")
    public CompletableFuture<ResponseEntity<MonthlyCheckinSummaryDTO>> monthlySummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Validação de parâmetros
            if (year != null && (year < 2020 || year > 2030)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(),
                        "Ano deve estar entre 2020 e 2030");
            }
            if (month != null && (month < 1 || month > 12)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(),
                        "Mês deve estar entre 1 e 12");
            }

            UserId userId = firebaseAuthService.extractUserIdFromToken(authHeader);

            return monthlyCheckinSummaryUseCase.execute(userId, year, month)
                    .thenApply(checkins -> {
                        MonthlyCheckinSummaryDTO dto = checkinMapper.toMonthlyCheckinSummaryDTO(checkins, year, month);
                        return ResponseEntity.ok(dto);
                    })
                    .exceptionally(throwable -> {
                        logger.error("Erro ao gerar resumo mensal", throwable);
                        throw new RuntimeException("Erro interno ao gerar resumo mensal", throwable);
                    });

        } catch (BusinessException e) {
            logger.warn("Erro de negócio ao gerar resumo mensal: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar resumo mensal", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                    "Erro interno ao gerar resumo mensal");
        }
    }

    /**
     * Distribuição de humor
     */
    @GetMapping("/mood-distribution")
    public CompletableFuture<ResponseEntity<MoodDistributionDTO>> getMoodDistribution(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Validação de parâmetros
            if (year != null && (year < 2020 || year > 2030)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(),
                        "Ano deve estar entre 2020 e 2030");
            }
            if (month != null && (month < 1 || month > 12)) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(),
                        "Mês deve estar entre 1 e 12");
            }

            UserId userId = firebaseAuthService.extractUserIdFromToken(authHeader);

            return monthlyCheckinSummaryUseCase.execute(userId, year, month)
                    .thenApply(checkins -> {
                        MoodDistributionDTO dto = checkinMapper.toMoodDistributionDTO(checkins);
                        return ResponseEntity.ok(dto);
                    })
                    .exceptionally(throwable -> {
                        logger.error("Erro ao gerar distribuição de humor", throwable);
                        throw new RuntimeException("Erro interno ao gerar distribuição de humor", throwable);
                    });

        } catch (BusinessException e) {
            logger.warn("Erro de negócio ao gerar distribuição de humor: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar distribuição de humor", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                    "Erro interno ao gerar distribuição de humor");
        }
    }

}