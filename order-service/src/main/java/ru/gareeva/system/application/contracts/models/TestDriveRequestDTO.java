package ru.gareeva.system.application.contracts.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class TestDriveRequestDTO {
    private UUID id;

    private UUID client;

    private UUID car;

    private LocalDate date;

    private LocalTime time;
}
