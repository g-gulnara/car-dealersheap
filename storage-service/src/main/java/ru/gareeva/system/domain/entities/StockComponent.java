package ru.gareeva.system.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "stock_components")
@Getter
@Setter
@NoArgsConstructor
public class StockComponent extends BaseEntity {

    @Column(name = "component_id", nullable = false)
    private UUID componentId;

    @Column(name = "car_model_id")
    private UUID carId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean reserved = false;

    public void reserve() {
        if (quantity <= 0) throw new RuntimeException("Component out of stock");
        reserved = true;
        quantity--;
    }

    public boolean isAvailable() {
        return quantity > 0 && !reserved;
    }
}