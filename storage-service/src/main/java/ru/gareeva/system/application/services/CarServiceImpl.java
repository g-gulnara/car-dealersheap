package ru.gareeva.system.application.services;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.gareeva.system.abstractions.repositories.CarInStockRepository;
import ru.gareeva.system.abstractions.repositories.CarsForTestDriveListRepository;
import ru.gareeva.system.abstractions.repositories.specifications.CarSpecification;
import ru.gareeva.system.application.contracts.services.CarService;
import ru.gareeva.system.domain.entities.cars.CarFilter;
import ru.gareeva.system.domain.entities.cars.CarInStock;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor

public class CarServiceImpl implements CarService {
    private final CarInStockRepository carInStockRepository;

    private final CarsForTestDriveListRepository carsForTestDriveRepository;

    public List<CarInStock> getCarsWithFilter(CarFilter filter) {
        Specification<CarInStock> spec = CarSpecification.build(filter);

        return carInStockRepository.findAll(spec);
    }

    public CarInStock getCarInfo(UUID carId) {
        return carInStockRepository.findById(carId).get();
    }

    public void addNewCarToStock(CarInStock car, UUID userId) {

        carInStockRepository.save(car);
    }

    public List<UUID> getCarsForTestDrive() {
        return carsForTestDriveRepository.findAll()
                .stream()
                .map(CarInStock::getId)
                .toList();
    }

    public void addCarToTestDrive(CarInStock car) {
        carsForTestDriveRepository.save(car);
    }

    public void removeCarFromTestDrive(UUID carId) {
        carsForTestDriveRepository.deleteById(carId);
    }
}
