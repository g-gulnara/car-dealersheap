package ru.gareeva.system.abstractions.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.gareeva.system.domain.entities.cars.CarInStock;

import java.util.UUID;

@Repository
public interface CarInStockRepository extends JpaRepository<CarInStock, UUID>, JpaSpecificationExecutor<CarInStock> {

}
