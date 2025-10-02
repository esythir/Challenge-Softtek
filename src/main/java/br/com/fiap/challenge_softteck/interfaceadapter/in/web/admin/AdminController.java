package br.com.fiap.challenge_softteck.interfaceadapter.in.web.admin;

import br.com.fiap.challenge_softteck.framework.database.DataSeederService;
import br.com.fiap.challenge_softteck.framework.database.ResponseSeederService;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller para operações administrativas.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Admin", description = "Operações administrativas")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired(required = false)
    private DataSeederService dataSeederService;

    @Autowired(required = false)
    private ResponseSeederService responseSeederService;

    @Autowired(required = false)
    private FormRepositoryPort formRepository;

    @PostMapping("/seed-forms")
    @Operation(summary = "Popular formulários", description = "Cria formulários de exemplo no banco de dados")
    public ResponseEntity<Map<String, Object>> seedForms() {
        logger.info("Requisição para seed de formulários recebida");
        try {
            if (dataSeederService != null) {
                dataSeederService.seedData();
                logger.info("Seed de formulários executado com sucesso");

                // Listar formulários criados para confirmar
                if (formRepository != null) {
                    try {
                        var forms = formRepository.findAllActive().get();
                        logger.info("Formulários encontrados após seed: {}", forms.size());

                        var formList = forms.stream()
                                .map(form -> Map.of(
                                        "id", form.getId(),
                                        "code", form.getCode(),
                                        "name", form.getName(),
                                        "type", form.getFormType().name(),
                                        "active", form.isActive()))
                                .collect(java.util.stream.Collectors.toList());

                        return ResponseEntity.ok(Map.of(
                                "message", "Formulários criados com sucesso",
                                "count", forms.size(),
                                "forms", formList,
                                "debug", "Firestore query executada com sucesso"));
                    } catch (Exception e) {
                        logger.error("Erro ao listar formulários: {}", e.getMessage(), e);
                        return ResponseEntity.ok(Map.of(
                                "message", "Formulários criados com sucesso",
                                "error", "Erro ao listar formulários: " + e.getMessage(),
                                "debug", "Erro na consulta Firestore"));
                    }
                } else {
                    return ResponseEntity.ok(Map.of(
                            "message", "Formulários criados com sucesso",
                            "warning", "FormRepository não disponível para listar formulários"));
                }
            } else {
                logger.warn("DataSeederService não disponível - Firebase não configurado");
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "DataSeederService não disponível - Firebase não configurado"));
            }
        } catch (Exception e) {
            logger.error("Erro ao executar seed de formulários: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/seed-responses")
    @Operation(summary = "Popular respostas", description = "Cria respostas de exemplo no banco de dados")
    public ResponseEntity<Map<String, String>> seedResponses() {
        logger.info("Requisição para seed de respostas recebida");
        try {
            if (responseSeederService != null) {
                responseSeederService.seedResponses();
                logger.info("Seed de respostas executado com sucesso");
                return ResponseEntity.ok(Map.of("message", "Respostas criadas com sucesso"));
            } else {
                logger.warn("ResponseSeederService não disponível - Firebase não configurado");
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "ResponseSeederService não disponível - Firebase não configurado"));
            }
        } catch (Exception e) {
            logger.error("Erro ao executar seed de respostas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/seed-all")
    @Operation(summary = "Popular tudo", description = "Cria formulários e respostas de exemplo no banco de dados")
    public ResponseEntity<Map<String, String>> seedAll() {
        logger.info("Requisição para seed completo recebida");
        try {
            if (dataSeederService != null && responseSeederService != null) {
                dataSeederService.seedData();
                responseSeederService.seedResponses();
                logger.info("Seed completo executado com sucesso");
                return ResponseEntity.ok(Map.of("message", "Dados criados com sucesso"));
            } else {
                logger.warn("Serviços de seed não disponíveis - Firebase não configurado");
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Serviços de seed não disponíveis - Firebase não configurado"));
            }
        } catch (Exception e) {
            logger.error("Erro ao executar seed completo: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
