package br.com.fiap.challenge_softteck.framework.config;

import br.com.fiap.challenge_softteck.port.out.external.FirebaseRemoteConfigPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço para gerenciar configurações remotas do Firebase.
 */
@Service
@ConditionalOnBean(FirebaseRemoteConfigPort.class)
public class RemoteConfigService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteConfigService.class);

    private final FirebaseRemoteConfigPort firebaseRemoteConfigPort;
    private Map<String, String> errorDictionary;
    private Map<String, Object> appConfig;

    @Autowired
    public RemoteConfigService(FirebaseRemoteConfigPort firebaseRemoteConfigPort) {
        this.firebaseRemoteConfigPort = firebaseRemoteConfigPort;
    }

    @PostConstruct
    public void initialize() {
        logger.info("Inicializando Remote Config Service...");

        // Carregar configurações iniciais
        loadErrorDictionary();
        loadAppConfig();

        // Verificar se Remote Config está disponível
        firebaseRemoteConfigPort.isAvailable()
                .thenAccept(available -> {
                    if (available) {
                        logger.info("Firebase Remote Config está disponível");
                    } else {
                        logger.warn("Firebase Remote Config não está disponível, usando configurações padrão");
                    }
                });
    }

    /**
     * Obtém uma mensagem de erro por código
     */
    public String getErrorMessage(String errorCode) {
        if (errorDictionary != null && errorDictionary.containsKey(errorCode)) {
            return errorDictionary.get(errorCode);
        }
        return "Erro desconhecido";
    }

    /**
     * Obtém uma configuração da aplicação
     */
    public Object getAppConfig(String key) {
        if (appConfig != null && appConfig.containsKey(key)) {
            return appConfig.get(key);
        }
        return null;
    }

    /**
     * Verifica se a aplicação está em modo de manutenção
     */
    public boolean isMaintenanceMode() {
        Object maintenanceMode = getAppConfig("maintenance_mode");
        return maintenanceMode instanceof Boolean && (Boolean) maintenanceMode;
    }

    /**
     * Obtém o número máximo de formulários por usuário
     */
    public long getMaxFormsPerUser() {
        Object maxForms = getAppConfig("max_forms_per_user");
        if (maxForms instanceof Number) {
            return ((Number) maxForms).longValue();
        }
        return 10L; // Valor padrão
    }

    /**
     * Força a atualização das configurações remotas
     */
    public CompletableFuture<Void> refreshConfig() {
        return firebaseRemoteConfigPort.forceRefresh()
                .thenRun(() -> {
                    loadErrorDictionary();
                    loadAppConfig();
                    logger.info("Configurações remotas atualizadas");
                });
    }

    private void loadErrorDictionary() {
        firebaseRemoteConfigPort.getErrorDictionary()
                .thenAccept(dictionary -> {
                    this.errorDictionary = dictionary;
                    logger.info("Dicionário de erros carregado com {} entradas", dictionary.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Erro ao carregar dicionário de erros: {}", throwable.getMessage());
                    return null;
                });
    }

    private void loadAppConfig() {
        firebaseRemoteConfigPort.getAppConfig()
                .thenAccept(config -> {
                    this.appConfig = config;
                    logger.info("Configurações da aplicação carregadas");
                })
                .exceptionally(throwable -> {
                    logger.error("Erro ao carregar configurações da aplicação: {}", throwable.getMessage());
                    return null;
                });
    }
}
