package ru.gareeva.system.application.services;

import jakarta.ws.rs.HttpMethod;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.gareeva.system.abstractions.repositories.TestDriveRequestsRepository;
import ru.gareeva.system.application.contracts.services.TestDriveService;
import ru.gareeva.system.domain.entities.cars.TestDriveRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TestDriveServiceImpl implements TestDriveService {
    private final TestDriveRequestsRepository testDriveRequestsRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public TestDriveRequest requestTestDrive(UUID carId, UUID clientId, LocalDateTime dateTime) {


        TestDriveRequest request =
                new TestDriveRequest(clientId, carId, dateTime.toLocalDate(), dateTime.toLocalTime());

        testDriveRequestsRepository.save(request);

        return request;
    }

    public void addCarToTestList(UUID car, UUID userId) {

        //carsForTestDriveListRepository.save(car);
        restTemplate.postForObject(
                "http://localhost:8081/cars/test-drive/{car}", null, Void.class, car);
    }

    public List<TestDriveRequest> getTestDriveRequests(UUID userId) {

        return testDriveRequestsRepository.findAll();
    }

    @Override
    public TestDriveRequest getById(UUID requestId) {
        return testDriveRequestsRepository.findById(requestId).get();
    }

    public List<UUID> getCarsForTestDrive(UUID userId) {
        //return null;
        //TODO return carsForTestDriveListRepository.findAll();
        UUID[] carIds = restTemplate.getForObject(
                "http://localhost:8081/cars/test-drive",
                UUID[].class
        );

        return carIds != null ? Arrays.asList(carIds) : List.of();
    }
    public void RemoveCarFromTestDriveList(UUID id, UUID userId) {

        //TODO carsForTestDriveListRepository.deleteById(id);
        restTemplate.delete("http://localhost:8081/cars/test-drive/{car}", id);
    }
}
