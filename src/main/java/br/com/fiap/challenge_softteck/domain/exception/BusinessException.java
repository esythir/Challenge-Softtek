package br.com.fiap.challenge_softteck.domain.exception;

/**
 * Exceção base para erros de negócio.
 * Todas as exceções de domínio devem estender esta classe.
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String errorCode) {
        super("Business error: " + errorCode);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
