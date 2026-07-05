package ru.gareeva.system.domain.entities.orders;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.gareeva.system.domain.entities.BaseEntity;
import ru.gareeva.system.domain.exceptions.DomainValidationException;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders_in_stock")
public class OrderInStock extends BaseEntity {

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderInStockState status;

    @Column(nullable = false)
    private boolean isPayed;

    public OrderInStock(UUID clientInp, UUID managerInp, UUID modelInp) {
        clientId = clientInp;
        managerId = managerInp;
        carId = modelInp;
        isPayed = false;
        //concreteCar.reserve();
        status = OrderInStockState.CREATED;
    }


    public void transitionToNextState() {
        this.status = switch (this.status) {
            case CREATED -> OrderInStockState.APPROVED_BY_MANAGER;
            case APPROVED_BY_MANAGER -> OrderInStockState.AWAITING_PAYMENT;
            case AWAITING_PAYMENT -> OrderInStockState.PAID;
            case PAID -> OrderInStockState.READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> OrderInStockState.COMPLETED;
            case COMPLETED -> throw new IllegalStateException("Alreadu completed");
            case CANCELLED -> throw new IllegalStateException("Canceled");
        };
    }


    public void cancel() {
        if (isPayed || status == OrderInStockState.COMPLETED) {
            throw new DomainValidationException(
                    "Order cannot be cancelled in status: " + status
            );
        }

        status = OrderInStockState.CANCELLED;
        //concreteCar.realise();
    }
}
