package ru.gareeva.system.application.services;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("Toma")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin")
                .clientSecret("secret-key")
                .build();
    }
}
