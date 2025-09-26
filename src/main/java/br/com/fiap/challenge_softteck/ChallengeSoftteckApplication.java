package br.com.fiap.challenge_softteck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"br.com.fiap.challenge_softteck.domain",
		"br.com.fiap.challenge_softteck.dto",
		"br.com.fiap.challenge_softteck.framework",
		"br.com.fiap.challenge_softteck.interfaceadapter",
		"br.com.fiap.challenge_softteck.port",
		"br.com.fiap.challenge_softteck.usecase"
})
public class ChallengeSoftteckApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeSoftteckApplication.class, args);
	}

}
