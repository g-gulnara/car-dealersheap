package ru.gareeva.system.application.contracts.services;

import ru.gareeva.system.domain.entities.orders.OrderInStock;

import java.util.List;
import java.util.UUID;

public interface OrderInStockService {
    OrderInStock createStockOrder(UUID carId, UUID client);

    List<OrderInStock> viewOrdersInStock(UUID role);

    void transitionToNextState(UUID orderId, UUID user);

    void cancel(UUID orderId);
}
