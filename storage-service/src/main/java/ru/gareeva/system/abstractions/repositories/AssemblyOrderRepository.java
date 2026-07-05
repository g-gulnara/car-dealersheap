package ru.gareeva.system.abstractions.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gareeva.system.domain.entities.AssemblyOrder;

import java.util.UUID;

@Repository
public interface AssemblyOrderRepository extends JpaRepository<AssemblyOrder, UUID> {
    boolean existsBySourceOrderId(UUID sourceOrderId);
}