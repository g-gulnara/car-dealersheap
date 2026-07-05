package ru.gareeva.system.application.contracts.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.gareeva.system.domain.entities.orders.OrderInStockState;
import ru.gareeva.system.domain.entities.orders.PersonalOrderState;

import java.util.UUID;

@Setter
@Getter
@Builder
public class PersonalOrderDTO {
    private UUID id;

    private UUID clientId;

    private UUID managerId;

    private UUID car;

    private PersonalOrderState status;

    private boolean payed;
}
