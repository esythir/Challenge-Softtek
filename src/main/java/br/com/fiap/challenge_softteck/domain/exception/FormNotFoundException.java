package br.com.fiap.challenge_softteck.domain.exception;

/**
 * Exceção lançada quando um formulário não é encontrado.
 */
public class FormNotFoundException extends BusinessException {

    public FormNotFoundException() {
        super("FORM0001");
    }

    public FormNotFoundException(String formId) {
        super("FORM0001", "Formulário com ID " + formId + " não encontrado");
    }
}
