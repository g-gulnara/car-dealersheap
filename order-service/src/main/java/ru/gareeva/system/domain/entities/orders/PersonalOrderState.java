package ru.gareeva.system.domain.entities.orders;

public enum PersonalOrderState {
    CREATED,
    APPROVED_BY_WAREHOUSE,
    AWAITING_PAYMENT,
    PAID,
    WAITING_DELIVERY,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED
}
