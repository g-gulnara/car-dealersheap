package ru.gareeva.system.application.contracts.models;

import lombok.*;
import ru.gareeva.system.domain.entities.orders.OrderInStockState;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDTO {
    private String id;
    private String brand;
    private String modelName;
}