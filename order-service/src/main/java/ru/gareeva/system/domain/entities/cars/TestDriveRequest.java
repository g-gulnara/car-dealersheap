package ru.gareeva.system.domain.entities.cars;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.gareeva.system.domain.entities.BaseEntity;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="test_drive_requests")
public class TestDriveRequest extends BaseEntity {

    @Column(name = "client_id", nullable = false)
    private UUID client;

    @Column(name = "car_id", nullable = false)
    private UUID car;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;
}
