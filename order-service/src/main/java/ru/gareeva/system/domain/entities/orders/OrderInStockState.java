package ru.gareeva.system.domain.entities.orders;

public enum OrderInStockState {
    CREATED,
    APPROVED_BY_MANAGER,
    AWAITING_PAYMENT,
    PAID,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED
}
