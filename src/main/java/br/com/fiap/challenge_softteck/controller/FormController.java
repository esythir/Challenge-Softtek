package br.com.fiap.challenge_softteck.controller;

import br.com.fiap.challenge_softteck.dto.*;
import br.com.fiap.challenge_softteck.service.*;
import br.com.fiap.challenge_softteck.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/forms")
@RequiredArgsConstructor
public class FormController {
    private final FormService formService;
    private final ResponseService responseService;

    @GetMapping
    public List<FormListDTO> list(@RequestHeader("Authorization") String auth,
                                  @RequestParam(required=false) String type,
                                  @AuthenticationPrincipal Jwt jwt) {
        byte[] uuid = UuidUtil.uuidToBytes(UUID.fromString(jwt.getClaimAsString("uid")));
        return formService.listAvailable(uuid, type);
    }

    @GetMapping("/{id}")
    public FormDetailDTO detail(@PathVariable Long id) {
        return formService.getDetail(id);
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<Void> submit(@PathVariable Long id,
                                       @RequestBody SubmitResponseDTO body,
                                       @AuthenticationPrincipal Jwt jwt) {
        byte[] uuid = UuidUtil.uuidToBytes(UUID.fromString(jwt.getClaimAsString("uid")));
        Long respId = responseService.saveResponse(id, uuid, body);
        return ResponseEntity.created(URI.create("/forms/"+id+"/responses/"+respId)).build();
    }

}