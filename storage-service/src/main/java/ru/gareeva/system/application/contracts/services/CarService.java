package ru.gareeva.system.application.contracts.services;

import ru.gareeva.system.domain.entities.cars.CarFilter;
import ru.gareeva.system.domain.entities.cars.CarInStock;

import java.util.List;
import java.util.UUID;

public interface CarService {
    List<CarInStock> getCarsWithFilter(CarFilter filter);

    CarInStock getCarInfo(UUID carId);

    void addNewCarToStock(CarInStock car, UUID user);

    List<UUID> getCarsForTestDrive();

    void addCarToTestDrive(CarInStock carId);

    void removeCarFromTestDrive(UUID carId);
}
