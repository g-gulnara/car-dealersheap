package ru.gareeva.system.domain.entities.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSentForApproval {
    private UUID orderId;
    private UUID carId;
    private String orderType;
    private String traceId;
}
