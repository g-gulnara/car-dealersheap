package ru.gareeva.system.presentation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateComponentPriceRequest {
    @NotNull
    private UUID componentId;

    @NotNull
    private Integer newPrice;
}
