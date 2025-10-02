package br.com.fiap.challenge_softteck.interfaceadapter.out.firebase;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.Periodicity;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementação real do repositório de formulários usando Firebase Firestore.
 * Versão simplificada para desenvolvimento.
 */
@Repository
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseFormRepository implements FormRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseFormRepository.class);

    private final Firestore firestore;

    @Autowired
    public FirebaseFormRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public CompletableFuture<List<Form>> findActiveByType(FormType type) {
        logger.info("Buscando formulários ativos por tipo: {}", type);

        return CompletableFuture.supplyAsync(() -> {
            try {
                var query = firestore.collection("forms").whereEqualTo("active", true);

                if (type != null) {
                    query = query.whereEqualTo("formType", type.name());
                }

                var querySnapshot = query.get().get();
                logger.info("Query executada, encontrados {} documentos", querySnapshot.size());

                var forms = querySnapshot.getDocuments().stream()
                        .map(doc -> {
                            // Converter dados do Firestore para Form
                            var periodicityDays = doc.getLong("periodicity");
                            var periodicity = periodicityDays != null ? new Periodicity(periodicityDays.intValue())
                                    : Periodicity.daily();

                            var reminderDays = doc.getLong("reminderDays");
                            var active = doc.getBoolean("active");

                            return Form.builder()
                                    .id(Long.valueOf(doc.getId()))
                                    .code(doc.getString("code"))
                                    .name(doc.getString("name"))
                                    .formType(FormType.valueOf(doc.getString("formType")))
                                    .description(doc.getString("description"))
                                    .periodicity(periodicity)
                                    .reminderDays(reminderDays != null ? reminderDays.intValue() : 1)
                                    .active(active != null ? active : false)
                                    .build();
                        })
                        .collect(java.util.stream.Collectors.toList());

                logger.info("Encontrados {} formulários", forms.size());
                return forms;
            } catch (Exception e) {
                logger.error("Erro ao buscar formulários: {}", e.getMessage(), e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Form>> findById(String id) {
        logger.info("Buscando formulário por ID: {}", id);
        // Implementação simplificada - retorna vazio por enquanto
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<Form>> findByCode(String code) {
        logger.info("Buscando formulário por código: {}", code);
        // Implementação simplificada - retorna vazio por enquanto
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<Form>> findAllActive() {
        logger.info("Buscando todos os formulários ativos");

        return CompletableFuture.supplyAsync(() -> {
            try {
                var querySnapshot = firestore.collection("forms")
                        .whereEqualTo("active", true)
                        .get()
                        .get();

                logger.info("Query executada, encontrados {} documentos", querySnapshot.size());

                var forms = querySnapshot.getDocuments().stream()
                        .map(doc -> {
                            // Converter dados do Firestore para Form
                            var periodicityDays = doc.getLong("periodicity");
                            var periodicity = periodicityDays != null ? new Periodicity(periodicityDays.intValue())
                                    : Periodicity.daily();

                            var reminderDays = doc.getLong("reminderDays");
                            var active = doc.getBoolean("active");

                            return Form.builder()
                                    .id(Long.valueOf(doc.getId()))
                                    .code(doc.getString("code"))
                                    .name(doc.getString("name"))
                                    .formType(FormType.valueOf(doc.getString("formType")))
                                    .description(doc.getString("description"))
                                    .periodicity(periodicity)
                                    .reminderDays(reminderDays != null ? reminderDays.intValue() : 1)
                                    .active(active != null ? active : false)
                                    .build();
                        })
                        .collect(java.util.stream.Collectors.toList());

                logger.info("Encontrados {} formulários ativos", forms.size());
                return forms;
            } catch (Exception e) {
                logger.error("Erro ao buscar formulários ativos: {}", e.getMessage(), e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Form> save(Form form) {
        logger.info("Salvando formulário: {}", form.getName());

        return CompletableFuture.supplyAsync(() -> {
            try {
                var docRef = firestore.collection("forms").document(String.valueOf(form.getId()));

                var data = new java.util.HashMap<String, Object>();
                data.put("id", form.getId());
                data.put("code", form.getCode());
                data.put("name", form.getName());
                data.put("formType", form.getFormType().name());
                data.put("description", form.getDescription());
                data.put("periodicity", form.getPeriodicity().days());
                data.put("reminderDays", form.getReminderDays());
                data.put("active", form.isActive());

                // Salvar perguntas se existirem
                if (form.getQuestions() != null && !form.getQuestions().isEmpty()) {
                    var questionsData = form.getQuestions().stream()
                            .map(question -> {
                                var questionData = new java.util.HashMap<String, Object>();
                                questionData.put("id", question.getId());
                                questionData.put("formId", question.getFormId());
                                questionData.put("ordinal", question.getOrdinal());
                                questionData.put("text", question.getText());
                                questionData.put("questionType", question.getQuestionType().name());

                                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                                    var optionsData = question.getOptions().stream()
                                            .map(option -> {
                                                var optionData = new java.util.HashMap<String, Object>();
                                                optionData.put("id", option.getId());
                                                optionData.put("ordinal", option.getOrdinal());
                                                optionData.put("value", option.getValue());
                                                optionData.put("label", option.getLabel());
                                                return optionData;
                                            })
                                            .collect(java.util.stream.Collectors.toList());
                                    questionData.put("options", optionsData);
                                }

                                return questionData;
                            })
                            .collect(java.util.stream.Collectors.toList());
                    data.put("questions", questionsData);
                }

                logger.info("Tentando salvar documento no Firestore...");
                logger.info("Dados que serão salvos: {}", data);
                logger.info("Collection: forms, Document ID: {}", docRef.getId());
                logger.info("Firestore instance: {}", firestore);
                logger.info("Firestore project ID: {}", firestore.getOptions().getProjectId());

                // Teste: tentar salvar um documento simples primeiro
                try {
                    var testDoc = firestore.collection("test").document("test-doc");
                    testDoc.set(Map.of("test", "value", "timestamp", System.currentTimeMillis())).get();
                    logger.info("TESTE: Documento de teste salvo com sucesso");

                    // Verificar se o documento de teste foi realmente salvo
                    var testDocRead = testDoc.get().get();
                    logger.info("TESTE: Documento de teste existe: {}", testDocRead.exists());
                    if (testDocRead.exists()) {
                        logger.info("TESTE: Dados do documento de teste: {}", testDocRead.getData());
                    }
                } catch (Exception testError) {
                    logger.error("TESTE: Erro ao salvar documento de teste: {}", testError.getMessage(), testError);
                }

                try {
                    logger.info("Executando docRef.set(data)...");
                    var result = docRef.set(data).get();
                    logger.info("docRef.set() executado com sucesso. Resultado: {}", result);

                    logger.info("Formulário salvo com sucesso no Firestore: {} - Document ID: {}", form.getName(),
                            docRef.getId());

                    // Verificar se o documento foi realmente salvo
                    logger.info("Verificando se documento foi salvo...");
                    var savedDoc = docRef.get().get();
                    logger.info("Documento verificado após salvamento: {}", savedDoc.exists());
                    if (savedDoc.exists()) {
                        logger.info("Dados do documento salvo: {}", savedDoc.getData());
                    } else {
                        logger.error("ERRO: Documento não foi salvo no Firestore!");
                        logger.error("Possível causa: Regras de segurança do Firestore bloqueando escrita");
                        logger.error("Verifique as regras de segurança no console do Firebase");
                    }
                } catch (Exception saveError) {
                    logger.error("ERRO ao salvar no Firestore: {}", saveError.getMessage(), saveError);
                    if (saveError.getMessage().contains("permission")
                            || saveError.getMessage().contains("PERMISSION_DENIED")) {
                        logger.error(
                                "ERRO DE PERMISSÃO: As regras de segurança do Firestore estão bloqueando a escrita!");
                        logger.error("Configure as regras para permitir escrita com service account");
                    }
                    throw saveError;
                }

                return form;
            } catch (Exception e) {
                logger.error("Erro ao salvar formulário no Firestore: {}", e.getMessage(), e);
                throw new RuntimeException("Erro ao salvar formulário", e);
            }
        });
    }

    @Override
    public CompletableFuture<Form> update(Form form) {
        logger.info("Atualizando formulário: {}", form.getName());
        // Implementação simplificada - retorna o formulário como está
        return CompletableFuture.completedFuture(form);
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        logger.info("Deletando formulário com ID: {}", id);
        // Implementação simplificada - não faz nada
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> exists(String id) {
        logger.info("Verificando existência do formulário com ID: {}", id);
        // Implementação simplificada - retorna false
        return CompletableFuture.completedFuture(false);
    }
}
