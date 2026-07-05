package org.example;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.gareeva.system.ApplicationMain;
import ru.gareeva.system.abstractions.repositories.CarInStockRepository;
import ru.gareeva.system.abstractions.repositories.CarModelRepository;
import ru.gareeva.system.domain.entities.cars.CarInStock;
import ru.gareeva.system.domain.entities.cars.CarModel;
import ru.gareeva.system.domain.entities.cars.specifications.CarBodyStyle;
import ru.gareeva.system.grpc.*;

import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest(classes = ApplicationMain.class)
@Testcontainers
class CarGrpcServerIT {

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
    CarInStockRepository carRepo;

    @Autowired
    CarModelRepository carModelRepository;

    @GrpcClient("local-test")
    CarServiceGrpc.CarServiceBlockingStub stub;

    @Test
    void shouldReturnAvailableCars() {
        CarModel model = carModelRepository.save(
                CarModel.builder()
                        .brand("Toyota")
                        .modelName("Camry")
                        .carBody(CarBodyStyle.SEDAN)
                        .build()
        );
        carRepo.save(CarInStock.builder().model(model).isReserved(false).build());

        CarListResponse response = stub.getAvailableCars(Empty.getDefaultInstance());

        assertFalse(response.getCarsList().isEmpty());
        assertEquals("Toyota", response.getCars(0).getBrand());
    }

    @Test
    void shouldReturnCarById() {
        CarModel model = carModelRepository.save(
                CarModel.builder()
                        .brand("Honda")
                        .modelName("Civic")
                        .carBody(CarBodyStyle.SEDAN)
                        .build()
        );
        CarInStock car = carRepo.save(
                CarInStock.builder().model(model).isReserved(false).build()
        );

        CarResponse response = stub.getCarById(
                CarIdRequest.newBuilder().setId(car.getId().toString()).build()
        );

        assertEquals(car.getId().toString(), response.getId());
    }

    @Test
    void shouldReturnNotFoundForUnknownId() {
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                stub.getCarById(CarIdRequest.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .build())
        );
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
    }

    @Test
    void shouldReturnEmptyListWhenNoCars() {
        carRepo.deleteAll();
        CarListResponse response = stub.getAvailableCars(Empty.getDefaultInstance());
        assertTrue(response.getCarsList().isEmpty());
    }
}