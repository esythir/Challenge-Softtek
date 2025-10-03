package br.com.fiap.challenge_softteck.interfaceadapter.in.web.form;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.framework.auth.FirebaseAuthService;
import br.com.fiap.challenge_softteck.usecase.form.ListAvailableFormsUseCase;
import br.com.fiap.challenge_softteck.usecase.form.SubmitFormResponseUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import br.com.fiap.challenge_softteck.domain.exception.BusinessException;
import br.com.fiap.challenge_softteck.domain.exception.InvalidAnswerException;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*")
public class FormController {

    private final ListAvailableFormsUseCase listAvailableFormsUseCase;
    private final SubmitFormResponseUseCase submitFormResponseUseCase;
    private final FirebaseAuthService firebaseAuthService;

    public FormController(ListAvailableFormsUseCase listAvailableFormsUseCase,
            SubmitFormResponseUseCase submitFormResponseUseCase,
            FirebaseAuthService firebaseAuthService) {
        this.listAvailableFormsUseCase = listAvailableFormsUseCase;
        this.submitFormResponseUseCase = submitFormResponseUseCase;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Lista formulários disponíveis para um usuário
     */
    @GetMapping
    public CompletableFuture<ResponseEntity<List<Form>>> listAvailableForms(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String type) {

        try {
            // Extrair UserId do token usando serviços de auth (firebase/mock)
            UserId userId = extractUserIdFromToken(authHeader);

            FormType formType = type != null ? FormType.valueOf(type.toUpperCase()) : null;

            return listAvailableFormsUseCase.execute(userId, formType)
                    .thenApply(forms -> ResponseEntity.ok(forms))
                    .exceptionally(throwable -> {
                        // Em caso de erro, retorna lista vazia
                        return ResponseEntity.status(500).body(List.of());
                    });

        } catch (Exception e) {
            // Em caso de erro de validação, retorna lista vazia
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(List.of()));
        }
    }

    /**
     * Submete uma resposta de formulário
     */
    @PostMapping("/{formCode}/submit")
    public CompletableFuture<ResponseEntity<FormResponse>> submitFormResponse(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String formCode,
            @RequestBody String body) {

        try {
            UserId userId = extractUserIdFromToken(authHeader);

            // Aceitar tanto objeto único quanto array de objetos
            String normalized = body != null && body.stripLeading().startsWith("{")
                    ? ("[" + body + "]")
                    : body;

            ObjectMapper mapper = new ObjectMapper();
            List<SubmitFormResponseUseCase.AnswerData> answers = mapper.readValue(normalized,
                    new TypeReference<List<SubmitFormResponseUseCase.AnswerData>>() {
                    });

            return submitFormResponseUseCase.execute(formCode, userId, answers)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> {
                        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                        if (cause instanceof BusinessException || cause instanceof InvalidAnswerException) {
                            return ResponseEntity.badRequest().body(null);
                        }
                        return ResponseEntity.status(500).body(null);
                    });

        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }
    }

    /**
     * Extrai UserId do token de autorização usando Firebase Auth
     */
    private UserId extractUserIdFromToken(String authHeader) {
        if (firebaseAuthService == null) {
            throw new RuntimeException("Firebase Auth Service não está disponível");
        }
        return firebaseAuthService.extractUserIdFromToken(authHeader);
    }
}