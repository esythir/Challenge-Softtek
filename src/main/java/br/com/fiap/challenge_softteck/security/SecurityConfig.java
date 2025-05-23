package br.com.fiap.challenge_softteck.security;

import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtEncoder jwtEncoder() throws IOException {
        // Load PEM files from classpath
        Resource privResource = new ClassPathResource("keys/jwt.key");
        Resource pubResource  = new ClassPathResource("keys/jwt.pub");

        String privPem = new String(privResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String pubPem  = new String(pubResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        RSAKey rsa = new RSAKey.Builder((RSAPublicKey) PemUtils.parsePublicKey(pubPem))
                .privateKey(PemUtils.parsePrivateKey(privPem))
                .keyID("softtek-jwt")
                .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsa)));
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        Resource pubResource  = new ClassPathResource("keys/jwt.pub");
        String pubPem = new String(pubResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        RSAPublicKey publicKey = (RSAPublicKey) PemUtils.parsePublicKey(pubPem);

        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/auth/mobile", "/auth/mobile/logout", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/home", true)
                        .userInfoEndpoint(u -> u.oidcUserService(new OidcUserService()))
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder))
                )
                .logout(l -> l.logoutSuccessUrl("/"));

        return http.build();
    }
}
