package br.com.fiap.challenge_softteck.framework.config;

import br.com.fiap.challenge_softteck.interfaceadapter.out.fake.InMemoryFormRepository;
import br.com.fiap.challenge_softteck.interfaceadapter.out.fake.InMemoryFormResponseRepository;
import br.com.fiap.challenge_softteck.interfaceadapter.out.fake.InMemoryUserPreferenceRepository;
import br.com.fiap.challenge_softteck.port.out.firebase.UserPreferenceRepositoryPort;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DevStubConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
    public FormResponseRepositoryPort formResponseRepositoryPort() {
        return new InMemoryFormResponseRepository();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
    public FormRepositoryPort formRepositoryPort() {
        return new InMemoryFormRepository();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
    public UserPreferenceRepositoryPort userPreferenceRepositoryPort() {
        return new InMemoryUserPreferenceRepository();
    }
}
