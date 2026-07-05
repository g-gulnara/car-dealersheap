package ru.gareeva.system.abstractions.repositories;

import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gareeva.system.domain.entities.cars.TestDriveRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestDriveRequestsRepository extends JpaRepository<TestDriveRequest, UUID> {
}
