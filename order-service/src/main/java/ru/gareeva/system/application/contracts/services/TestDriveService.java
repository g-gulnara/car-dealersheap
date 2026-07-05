package ru.gareeva.system.application.contracts.services;

import ru.gareeva.system.domain.entities.cars.TestDriveRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TestDriveService {
    TestDriveRequest requestTestDrive(UUID carId, UUID client, LocalDateTime dateTime);

    void addCarToTestList(UUID car, UUID user);

    List<TestDriveRequest> getTestDriveRequests(UUID user);

    TestDriveRequest getById(UUID requestId);

    List<UUID> getCarsForTestDrive(UUID user);

    void RemoveCarFromTestDriveList(UUID id, UUID user);
}
