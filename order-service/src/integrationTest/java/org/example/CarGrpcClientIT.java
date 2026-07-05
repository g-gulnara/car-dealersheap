package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.gareeva.system.ApplicationMain;
import ru.gareeva.system.application.services.KeycloakManagerService;
import ru.gareeva.system.domain.exceptions.EntityNotFoundException;
import ru.gareeva.system.domain.exceptions.StorageUnavailableException;
import ru.gareeva.system.grpc.CarGrpcClient;
import ru.gareeva.system.grpc.CarResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApplicationMain.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class CarGrpcClientIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("car dealership")
            .withUsername("user")
            .withPassword("user");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> "http://localhost:8180/realms/car-dealership-realm");
    }

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CarGrpcClient carGrpcClient;

    @MockBean
    KeycloakManagerService keycloakManagerService;

    @Test
    void shouldReturnCarsList() throws Exception {
        CarResponse car = CarResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setBrand("Tesla")
                .setModelName("Model 3")
                .build();

        Mockito.when(carGrpcClient.getAvailableCars()).thenReturn(List.of(car));

        mockMvc.perform(get("/api/v1/cars")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnCarById() throws Exception {
        String id = UUID.randomUUID().toString();
        CarResponse car = CarResponse.newBuilder()
                .setId(id)
                .setBrand("Tesla")
                .build();

        Mockito.when(carGrpcClient.getCarById(id)).thenReturn(car);

        mockMvc.perform(get("/api/v1/cars/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn503WhenStorageUnavailable() throws Exception {
        Mockito.when(carGrpcClient.getAvailableCars())
                .thenThrow(new StorageUnavailableException("StorageService unavailable"));

        mockMvc.perform(get("/api/v1/cars")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void shouldReturn503OnTimeout() throws Exception {
        Mockito.when(carGrpcClient.getAvailableCars())
                .thenThrow(new StorageUnavailableException("StorageService timeout"));

        mockMvc.perform(get("/api/v1/cars")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void shouldReturn404WhenCarNotFound() throws Exception {
        String id = UUID.randomUUID().toString();
        Mockito.when(carGrpcClient.getCarById(id))
                .thenThrow(new EntityNotFoundException("Car not found: " + id));

        mockMvc.perform(get("/api/v1/cars/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        Mockito.when(carGrpcClient.getAvailableCars()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/cars")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}