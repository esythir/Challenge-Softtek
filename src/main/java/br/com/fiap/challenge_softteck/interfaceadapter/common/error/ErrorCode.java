package br.com.fiap.challenge_softteck.interfaceadapter.common.error;

/**
 * Enum que define os códigos de erro do sistema.
 * Estes códigos são sincronizados com o Firebase Remote Config.
 */
public enum ErrorCode {
    
    // Formulários
    FORM_NOT_FOUND("FORM0001", "Formulário não encontrado"),
    FORM_NOT_AVAILABLE("FORM0002", "Formulário não está disponível para resposta no momento"),
    FORM_ALREADY_ANSWERED_TODAY("FORM0003", "Formulário já foi respondido hoje"),
    QUESTION_NOT_FOUND("FORM0004", "Pergunta não encontrada no formulário"),
    INVALID_ANSWER_TYPE("FORM0005", "Resposta inválida para o tipo de pergunta"),
    
    // Respostas
    RESPONSE_NOT_FOUND("RESP0001", "Resposta não encontrada"),
    ERROR_SAVING_RESPONSE("RESP0002", "Erro ao salvar resposta do formulário"),
    INCOMPLETE_RESPONSE("RESP0003", "Resposta incompleta - faltam perguntas obrigatórias"),
    
    // Usuários
    USER_NOT_FOUND("USER0001", "Usuário não encontrado"),
    USER_PREFERENCES_NOT_FOUND("USER0002", "Preferências do usuário não encontradas"),
    ERROR_UPDATING_PREFERENCES("USER0003", "Erro ao atualizar preferências do usuário"),
    
    // Autenticação
    INVALID_AUTH_TOKEN("AUTH0001", "Token de autenticação inválido"),
    USER_NOT_AUTHENTICATED("AUTH0002", "Usuário não autenticado"),
    GOOGLE_TOKEN_VERIFICATION_ERROR("AUTH0003", "Erro na verificação do token Google"),
    
    // Análises
    INSUFFICIENT_DATA_FOR_ANALYSIS("ANAL0001", "Dados insuficientes para gerar análise"),
    ERROR_PROCESSING_CHECKIN_ANALYSIS("ANAL0002", "Erro ao processar análise de check-in"),
    ERROR_GENERATING_CLIMATE_REPORT("ANAL0003", "Erro ao gerar relatório de clima organizacional"),
    
    // Sistema
    INTERNAL_SERVER_ERROR("CORE0001", "Erro interno do servidor"),
    SERVICE_UNAVAILABLE("CORE0002", "Serviço temporariamente indisponível"),
    VALIDATION_ERROR("CORE0003", "Erro de validação de dados"),
    FIREBASE_CONNECTION_ERROR("CORE0004", "Erro ao conectar com Firebase"),
    REMOTE_CONFIG_ERROR("CORE0005", "Erro ao carregar configurações remotas");
    
    private final String code;
    private final String defaultMessage;
    
    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    /**
     * Busca um ErrorCode pelo código
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return null;
    }
}
