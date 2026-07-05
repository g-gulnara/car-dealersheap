package ru.gareeva.system.application.contracts.models;

import lombok.*;
import ru.gareeva.system.domain.entities.orders.OrderInStockState;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInStockDTO {
    private UUID id;

    private UUID clientId;

    private UUID managerId;

    private UUID car;

    private OrderInStockState status;

    private boolean payed;
}
