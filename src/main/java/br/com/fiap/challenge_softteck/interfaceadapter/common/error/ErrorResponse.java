package br.com.fiap.challenge_softteck.interfaceadapter.common.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe que representa uma resposta de erro padronizada.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") LocalDateTime timestamp,
        String path,
        List<String> details) {

    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now(), null, null);
    }

    public ErrorResponse(String code, String message, String path) {
        this(code, message, LocalDateTime.now(), path, null);
    }

    public ErrorResponse(String code, String message, String path, List<String> details) {
        this(code, message, LocalDateTime.now(), path, details);
    }

    /**
     * Cria uma resposta de erro a partir de um ErrorCode
     */
    public static ErrorResponse fromErrorCode(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getDefaultMessage());
    }

    /**
     * Cria uma resposta de erro a partir de um ErrorCode com path
     */
    public static ErrorResponse fromErrorCode(ErrorCode errorCode, String path) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getDefaultMessage(), path);
    }
}
