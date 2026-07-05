package ru.gareeva.system.abstractions.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gareeva.system.domain.entities.StockComponent;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockComponentRepository extends JpaRepository<StockComponent, UUID> {
    List<StockComponent> findAllByComponentIdIn(List<UUID> componentIds);
    List<StockComponent> findAllByCarId(UUID carId);

}