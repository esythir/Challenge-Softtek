package br.com.fiap.challenge_softteck.interfaceadapter.in.web.form;

import br.com.fiap.challenge_softteck.domain.entity.Form;
import br.com.fiap.challenge_softteck.domain.entity.FormResponse;
import br.com.fiap.challenge_softteck.domain.valueobject.FormType;
import br.com.fiap.challenge_softteck.domain.valueobject.UserId;
import br.com.fiap.challenge_softteck.usecase.form.ListAvailableFormsUseCase;
import br.com.fiap.challenge_softteck.usecase.form.SubmitFormResponseUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*")
public class FormController {

    private final ListAvailableFormsUseCase listAvailableFormsUseCase;
    private final SubmitFormResponseUseCase submitFormResponseUseCase;

    @Autowired
    public FormController(ListAvailableFormsUseCase listAvailableFormsUseCase,
            SubmitFormResponseUseCase submitFormResponseUseCase) {
        this.listAvailableFormsUseCase = listAvailableFormsUseCase;
        this.submitFormResponseUseCase = submitFormResponseUseCase;
    }

    /**
     * Lista formulários disponíveis para um usuário
     */
    @GetMapping("/available")
    public CompletableFuture<ResponseEntity<List<Form>>> listAvailableForms(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String type) {

        try {
            // Extrair UserId do token (implementação simplificada)
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
            @RequestBody List<SubmitFormResponseUseCase.AnswerData> answers) {

        try {
            // Extrair UserId do token (implementação simplificada)
            UserId userId = extractUserIdFromToken(authHeader);

            return submitFormResponseUseCase.execute(formCode, userId, answers)
                    .thenApply(response -> ResponseEntity.ok(response))
                    .exceptionally(throwable -> {
                        // Em caso de erro, retorna null (será tratado pelo GlobalExceptionHandler)
                        return ResponseEntity.status(500).body(null);
                    });

        } catch (Exception e) {
            // Em caso de erro de validação, retorna null
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }
    }

    /**
     * Extrai UserId do token de autorização (implementação simplificada)
     * Em produção, isso seria feito pelo FirebaseAuthService
     */
    private UserId extractUserIdFromToken(String authHeader) {
        // Implementação simplificada - em produção, usar FirebaseAuthService
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorização inválido");
        }

        // Por enquanto, retornar um UserId fixo para testes
        return UserId.fromString("test-user-id");
    }
}