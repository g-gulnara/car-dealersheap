package ru.gareeva.system.abstractions.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gareeva.system.domain.entities.cars.CarInStock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarsForTestDriveListRepository extends JpaRepository<CarInStock, UUID> {
}
