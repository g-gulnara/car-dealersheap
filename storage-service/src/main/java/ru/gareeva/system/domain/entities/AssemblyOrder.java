package ru.gareeva.system.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "assembly_orders")
@Getter
@Setter
@NoArgsConstructor
public class AssemblyOrder extends BaseEntity {

    @Column(name = "source_order_id", nullable = false)
    private UUID sourceOrderId;

    @Column(name = "source_order_type", nullable = false)
    private String sourceOrderType;

    @Column(name = "car_id")
    private UUID carId;

    @Column(name = "warehouse_admin_id")
    private String warehouseAdminId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssemblyStatus status;
}