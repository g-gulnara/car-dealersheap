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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.gareeva.system.ApplicationMain;
import ru.gareeva.system.abstractions.repositories.AssemblyOrderRepository;
import ru.gareeva.system.abstractions.repositories.CarModelRepository;
import ru.gareeva.system.abstractions.repositories.CarInStockRepository;
import ru.gareeva.system.abstractions.repositories.ConcreteCarComponentRepository;
import ru.gareeva.system.domain.entities.AssemblyOrder;
import ru.gareeva.system.domain.entities.CarComponent;
import ru.gareeva.system.domain.entities.cars.CarInStock;
import ru.gareeva.system.domain.entities.cars.CarModel;
import ru.gareeva.system.domain.entities.cars.specifications.CarBodyStyle;
import ru.gareeva.system.domain.vo.Money;

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

    @Autowired
    AssemblyOrderRepository assemblyRepo;
    @Autowired CarInStockRepository carRepo;
    @Autowired CarModelRepository carModelRepository;

    @Test
    void shouldCreateAssemblyOrder() {

        CarModel model = carModelRepository.save(
                CarModel.builder()
                        .brand("Toyota")
                        .modelName("Camry")
                        .carBody(CarBodyStyle.SEDAN)
                        .build()
        );

        CarInStock car = carRepo.save(
                CarInStock.builder()
                        .model(model)
                        .isReserved(false)
                        .build()
        );

        AssemblyOrder order = new AssemblyOrder();
        order.setCarId(car.getId());
        order.setSourceOrderId(UUID.randomUUID());
        order.setStatus("CREATED");
        order.setSourceOrderType("PERSONAL");
        order.setStatus("CREATED");

        assemblyRepo.save(order);

        assertFalse(assemblyRepo.findAll().isEmpty());
    }
}

