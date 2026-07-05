package ru.gareeva.system.application.services;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
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


    public List<UUID> getAvailableManagers() {
        return keycloak.realm("Toma").roles().get("manager").getUserMembers()
                .stream()
                .map(user -> UUID.fromString(user.getId()))
                .toList();
    }
}
