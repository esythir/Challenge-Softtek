package br.com.fiap.challenge_softteck.domain.exception;

/**
 * Exceção lançada quando uma resposta é inválida.
 */
public class InvalidAnswerException extends BusinessException {

    public InvalidAnswerException() {
        super("FORM0005");
    }

    public InvalidAnswerException(String message) {
        super("FORM0005", message);
    }

    public InvalidAnswerException(String message, Throwable cause) {
        super("FORM0005", message, cause);
    }
}
