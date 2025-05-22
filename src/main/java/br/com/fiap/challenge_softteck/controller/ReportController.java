package br.com.fiap.challenge_softteck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Object body) {
        return ResponseEntity.created(URI.create("/reports/1")).build();
    }
}