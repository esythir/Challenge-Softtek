package br.com.fiap.challenge_softteck.security;

import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        String privPem = Files.readString(Path.of("src/main/resources/keys/jwt.key"));
        String pubPem  = Files.readString(Path.of("src/main/resources/keys/jwt.pub"));

        RSAKey rsa = new RSAKey.Builder((RSAPublicKey) PemUtils.parsePublicKey(pubPem))
                .privateKey(PemUtils.parsePrivateKey(privPem))
                .keyID("softtek-jwt")
                .build();

        return new NimbusJwtEncoder(new ImmutableJWKSet<SecurityContext>(new JWKSet(rsa)));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
                .jwt(jwt -> { /* usa configuração padrão ou custom JwtDecoder */ })
            )
            .logout(l -> l.logoutSuccessUrl("/"));

        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
//                .build();
//    }


}
