package ru.gareeva.system.application.contracts.services;

import ru.gareeva.system.domain.entities.orders.PersonalOrder;

import java.util.List;
import java.util.UUID;

public interface PersonalOrderService {
    List<PersonalOrder> viewPersonalOrders(UUID user);

    PersonalOrder createPersonalOrder(UUID car, UUID client);

    void transitionToNextState(UUID orderId, UUID user);

    void cancel(UUID orderId, UUID user);
}
