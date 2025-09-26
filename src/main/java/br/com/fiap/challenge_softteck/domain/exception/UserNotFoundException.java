package br.com.fiap.challenge_softteck.domain.exception;

/**
 * Exceção lançada quando um usuário não é encontrado.
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super("USER0001");
    }

    public UserNotFoundException(String userId) {
        super("USER0001", "Usuário com ID " + userId + " não encontrado");
    }
}
