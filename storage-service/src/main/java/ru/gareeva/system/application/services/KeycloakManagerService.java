package ru.gareeva.system.application.services;

import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KeycloakManagerService {
    private final Keycloak keycloak;
    private final String realm = "Toma";

    public KeycloakManagerService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

//    @Bean
//    public Keycloak keycloak() {
//        return KeycloakBuilder.builder()
//                .serverUrl("http://localhost:8080")
//                .realm("Toma")
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                .clientId("admin")
//                .clientSecret("secret-key")
//                .build();
//    }

    public List<UUID> getAvailableManagers() {

        return keycloak.realm(realm).roles().get("manager").getUserMembers()
                .stream()
                .map(user -> UUID.fromString(user.getId()))
                .toList();
    }
}
