package br.com.fiap.challenge_softteck.interfaceadapter.in.web.test;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        System.out.println("DEBUG: TestController /ping chamado");
        return "pong";
    }

    @GetMapping("/hello")
    public String hello() {
        System.out.println("DEBUG: TestController /hello chamado");
        return "Hello World!";
    }
}
