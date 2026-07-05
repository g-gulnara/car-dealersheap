package ru.gareeva.system.application.contracts.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gareeva.system.domain.entities.AssemblyStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyOrderDto {

    private UUID id;
    private UUID sourceOrderId;
    private String sourceOrderType;
    private UUID carId;
    private UUID warehouseAdminId;
    private AssemblyStatus status;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
}