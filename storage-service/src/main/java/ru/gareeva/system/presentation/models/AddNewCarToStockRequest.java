package ru.gareeva.system.presentation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.gareeva.system.domain.entities.cars.CarInStock;

import java.util.UUID;

@Data
public class AddNewCarToStockRequest {
    @NotNull
    private final CarInStock car;
}
