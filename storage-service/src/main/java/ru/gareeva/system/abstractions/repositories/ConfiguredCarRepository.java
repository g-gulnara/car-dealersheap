package ru.gareeva.system.abstractions.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gareeva.system.domain.entities.cars.ConfiguredCar;

import java.util.UUID;

@Repository
public interface ConfiguredCarRepository extends JpaRepository<ConfiguredCar, UUID> {
}
