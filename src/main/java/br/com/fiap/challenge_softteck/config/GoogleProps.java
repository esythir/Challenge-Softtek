package br.com.fiap.challenge_softteck.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "google")
public class GoogleProps {
    private List<String> acceptedClientIds;

    public List<String> getAcceptedClientIds() { return acceptedClientIds; }
    public void setAcceptedClientIds(List<String> ids) { this.acceptedClientIds = ids; }
}
