package ru.gareeva.system.application.contracts.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ConfiguredCarDTO {
    private UUID id;

    private CarModelDTO model;

    private String wheels;

    private String transmission;

    private String steeringWheel;

    private String interior;

    private Integer price;
}
