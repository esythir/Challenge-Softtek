package br.com.fiap.challenge_softteck.framework.auth;

import br.com.fiap.challenge_softteck.domain.exception.BusinessException;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.interfaceadapter.common.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Mock do serviço de autenticação Firebase para desenvolvimento.
 * Usado quando Firebase não está configurado.
 */
@Service
@ConditionalOnProperty(name = "firebase.mock", havingValue = "true", matchIfMissing = true)
public class MockFirebaseAuthService {

    private static final Logger logger = LoggerFactory.getLogger(MockFirebaseAuthService.class);

    /**
     * Mock da verificação de token - sempre retorna um usuário de teste.
     */
    public UserId extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Token de autorização inválido: header ausente ou formato incorreto");
            throw new BusinessException(ErrorCode.INVALID_AUTH_TOKEN.getCode(),
                    "Token de autorização inválido");
        }

        String idToken = authHeader.substring(7);

        // Mock: aceita qualquer token que comece com "test-"
        if (idToken.startsWith("test-")) {
            String userId = idToken.substring(5); // Remove "test-" prefix
            logger.debug("Mock: Token aceito para usuário: {}", userId);
            return UserId.fromString(userId);
        }

        // Mock: se não começar com "test-", retorna usuário padrão
        logger.debug("Mock: Token não reconhecido, usando usuário padrão");
        return UserId.fromString("test-user-123");
    }

    /**
     * Mock da verificação de token.
     */
    public boolean isValidToken(String authHeader) {
        try {
            extractUserIdFromToken(authHeader);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * Mock do token info.
     */
    public Object getTokenInfo(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_TOKEN.getCode(),
                    "Token de autorização inválido");
        }

        // Mock: retorna um objeto simples
        return new Object() {
            public String getUid() {
                return "test-user-123";
            }
        };
    }
}
