package ru.gareeva.system.domain.entities.orders;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.gareeva.system.domain.entities.BaseEntity;
import ru.gareeva.system.domain.exceptions.DomainValidationException;

import java.util.UUID;

@Entity
@Table(name = "personal_orders")
@Getter
@NoArgsConstructor
public class PersonalOrder extends BaseEntity {
    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonalOrderState status;

    @Column(nullable = false)
    private boolean isPayed;

    public PersonalOrder(UUID client, UUID manager, UUID carId) {
        this.clientId = client;
        this.managerId = manager;
        this.carId = carId;
        status = PersonalOrderState.CREATED;
        isPayed = false;
    }

    public void transitionToNextState() {
        this.status = switch (this.status) {
            case CREATED ->
                    PersonalOrderState.APPROVED_BY_WAREHOUSE;

            case APPROVED_BY_WAREHOUSE ->
                    PersonalOrderState.AWAITING_PAYMENT;

            case AWAITING_PAYMENT -> {
                this.isPayed = true;
                yield PersonalOrderState.PAID;
            }

            case PAID ->
                    PersonalOrderState.WAITING_DELIVERY;

            case WAITING_DELIVERY ->
                    PersonalOrderState.READY_FOR_PICKUP;

            case READY_FOR_PICKUP ->
                    PersonalOrderState.COMPLETED;

            case COMPLETED ->
                    throw new IllegalStateException("Already completed");

            case CANCELLED ->
                    throw new IllegalStateException("Order cancelled");
        };
    }


    public void cancel() {
        if (isPayed) {
            throw new DomainValidationException(
                    "Order cannot be cancelled in status: " + status
            );
        }

        status = PersonalOrderState.CANCELLED;
    }
}
