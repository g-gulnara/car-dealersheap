package ru.gareeva.system.presentation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull
    private UUID car;
}
