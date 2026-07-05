package ru.gareeva.system.application.contracts.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CarComponentDTO {
    private UUID id;
    private String name;
    private BigDecimal extraPrice;
}
