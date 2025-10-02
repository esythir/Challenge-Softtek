package br.com.fiap.challenge_softteck.framework.auth;

import br.com.fiap.challenge_softteck.domain.exception.BusinessException;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.interfaceadapter.common.error.ErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação Firebase para verificação de tokens JWT.
 */
@Service
@ConditionalOnBean(FirebaseAuth.class)
public class FirebaseAuthService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthService.class);

    private final FirebaseAuth firebaseAuth;

    @Autowired
    public FirebaseAuthService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    /**
     * Verifica e extrai o UserId de um token Firebase.
     * 
     * @param authHeader Header de autorização (Bearer token)
     * @return UserId extraído do token
     * @throws BusinessException se o token for inválido
     */
    public UserId extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Token de autorização inválido: header ausente ou formato incorreto");
            throw new BusinessException(ErrorCode.INVALID_AUTH_TOKEN.getCode(),
                    "Token de autorização inválido");
        }

        String idToken = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            if (uid == null || uid.trim().isEmpty()) {
                logger.warn("UID não encontrado no token Firebase");
                throw new BusinessException(ErrorCode.INVALID_AUTH_TOKEN.getCode(),
                        "UID não encontrado no token");
            }

            logger.debug("Token verificado com sucesso para UID: {}", uid);
            return UserId.fromString(uid);

        } catch (FirebaseAuthException e) {
            logger.warn("Erro na verificação do token Firebase: {}", e.getMessage());
            throw new BusinessException(ErrorCode.GOOGLE_TOKEN_VERIFICATION_ERROR.getCode(),
                    "Erro na verificação do token: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado na verificação do token", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                    "Erro interno na verificação do token");
        }
    }

    /**
     * Verifica se um token é válido sem extrair o UID.
     * 
     * @param authHeader Header de autorização
     * @return true se o token for válido
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
     * Extrai informações adicionais do token Firebase.
     * 
     * @param authHeader Header de autorização
     * @return FirebaseToken com informações do usuário
     * @throws BusinessException se o token for inválido
     */
    public FirebaseToken getTokenInfo(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_TOKEN.getCode(),
                    "Token de autorização inválido");
        }

        String idToken = authHeader.substring(7);

        try {
            return firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            logger.warn("Erro na verificação do token Firebase: {}", e.getMessage());
            throw new BusinessException(ErrorCode.GOOGLE_TOKEN_VERIFICATION_ERROR.getCode(),
                    "Erro na verificação do token: " + e.getMessage());
        }
    }
}
