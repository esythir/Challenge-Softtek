package br.com.fiap.challenge_softteck.dto;

import java.time.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class FormListDTO {
    private Long id;
    private String code;
    private String name;
    private String type;
    private String description;
    private LocalDateTime nextAllowed;
    private LocalDateTime lastAnsweredAt;
}
