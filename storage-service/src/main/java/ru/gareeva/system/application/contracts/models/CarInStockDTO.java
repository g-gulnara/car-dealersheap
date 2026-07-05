package ru.gareeva.system.application.contracts.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CarInStockDTO {
    private UUID id;

    private CarModelDTO modelDTO;

    private boolean reserved;

    private Integer price;
}
