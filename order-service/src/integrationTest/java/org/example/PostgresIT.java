package org.example;

import jakarta.transaction.Transactional;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.gareeva.system.ApplicationMain;
import ru.gareeva.system.abstractions.repositories.OrderInStockRepository;
import ru.gareeva.system.abstractions.repositories.OutboxRepository;
import ru.gareeva.system.application.services.KeycloakManagerService;
import ru.gareeva.system.domain.entities.orders.OrderInStock;
import ru.gareeva.system.domain.vo.Money;
import ru.gareeva.system.infrustructure.kafka.OutboxEvent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = ApplicationMain.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@AutoConfigureMockMvc
class PostgresIT {

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

    @Autowired MockMvc mockMvc;

    @Autowired OrderInStockRepository orderRepo;

    @MockBean
    KeycloakManagerService keycloakManagerService;


    @Autowired
    private OutboxRepository outboxRepository;


    @Test
    void shouldProcessOrderFromApiToKafkaOutbox() throws Exception {
        UUID carId = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        UUID fakeManagerId = UUID.randomUUID();
        Mockito.when(keycloakManagerService.getAvailableManagers())
                .thenReturn(List.of(fakeManagerId));

        mockMvc.perform(post("/in-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"car\": \"%s\"}", carId))
                        // 1. Добавляем CSRF (без него будет 403 на POST запросах)
                        .with(csrf())
                        .with(jwt()
                                .jwt(j -> j
                                        .subject(userUuid.toString())
                                        .claim("sub", userUuid.toString())
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))
                        ))
                .andExpect(status().isOk());

        List<OutboxEvent> events = outboxRepository.findAll();
        assertFalse(events.isEmpty());

        Thread.sleep(3000);

        OutboxEvent processedEvent = outboxRepository.findById(events.get(0).getId()).orElseThrow();
        assertTrue(processedEvent.isSent());
    }
}
