package br.com.fiap.challenge_softteck.interfaceadapter.common.error;

import br.com.fiap.challenge_softteck.domain.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata exceções de negócio
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        logger.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        ErrorCode errorCode = ErrorCode.fromCode(ex.getErrorCode());
        ErrorResponse errorResponse = errorCode != null
                ? ErrorResponse.fromErrorCode(errorCode, request.getDescription(false))
                : new ErrorResponse(ex.getErrorCode(), ex.getMessage(), request.getDescription(false));

        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Trata exceções de validação
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
            WebRequest request) {
        logger.warn("Validation exception: {}", ex.getMessage());

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                ErrorCode.VALIDATION_ERROR.getDefaultMessage(),
                request.getDescription(false),
                details);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Trata exceções não tratadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
                request.getDescription(false));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Determina o status HTTP baseado no código de erro
     */
    private HttpStatus determineHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "FORM0001", "RESP0001", "USER0001", "USER0002", "FORM0004" ->
                HttpStatus.NOT_FOUND;
            case "FORM0002", "FORM0003", "FORM0005", "RESP0003" ->
                HttpStatus.BAD_REQUEST;
            case "AUTH0001", "AUTH0002", "AUTH0003" ->
                HttpStatus.UNAUTHORIZED;
            case "CORE0002", "CORE0004", "CORE0005" ->
                HttpStatus.SERVICE_UNAVAILABLE;
            case "CORE0003" ->
                HttpStatus.BAD_REQUEST;
            default ->
                HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
