package ru.gareeva.system.presentation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateTestDriveRequest {
    @NotNull
    private UUID carId;

    @NotNull
    private LocalDateTime time;
}
