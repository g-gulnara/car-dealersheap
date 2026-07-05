package ru.gareeva.system.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gareeva.system.abstractions.repositories.OrderInStockRepository;
import ru.gareeva.system.abstractions.repositories.OutboxRepository;
import ru.gareeva.system.abstractions.repositories.PersonalOrderRepository;
import ru.gareeva.system.application.contracts.services.OrderInStockService;
import ru.gareeva.system.domain.entities.events.OrderSentForApproval;
import ru.gareeva.system.domain.entities.orders.OrderInStock;
import ru.gareeva.system.domain.entities.orders.OrderInStockState;
import ru.gareeva.system.infrustructure.config.SecurityUtils;
import ru.gareeva.system.infrustructure.kafka.OutboxEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderInStockServiceImpl implements OrderInStockService {
    private final OrderInStockRepository orderInStockRepository;
    private final PersonalOrderRepository personalOrderRepo;
    private final KeycloakManagerService keycloakManagerService;

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public OrderInStock createStockOrder(UUID carId, UUID clientId) {

        //CarInStock car = carInStockRepository.findById(carId).get();
        //car.reserve();

        List<UUID> managerIds = keycloakManagerService.getAvailableManagers();
        if (managerIds.isEmpty()) {
            throw new RuntimeException("No managers available in Keycloak!");
        }
        UUID managerId = managerIds.get(0);

        OrderInStock order = new OrderInStock(clientId, managerId, carId);
        orderInStockRepository.save(order);

        OutboxEvent event = OutboxEvent.builder()
                .payload(String.format("{\"orderId\":\"%s\", \"carId\":\"%s\"}", order.getId(), carId))
                .topic("order-paid")
                .sent(false)
                .build();

        outboxRepository.save(event);


        return order;
    }

    public List<OrderInStock> viewOrdersInStock(UUID userId) {

        return orderInStockRepository.findAll();
    }


    @Transactional
    public void transitionToNextState(UUID orderId, UUID userId) {
        boolean isAdmin = SecurityUtils.hasRole("admin");
        boolean isManager = SecurityUtils.hasRole("manager");
        boolean isWarehouse = SecurityUtils.hasRole("warehouse_admin");
        OrderInStock order = orderInStockRepository.findById(orderId).get();

        boolean allowed = switch (order.getStatus()) {
            case CREATED, APPROVED_BY_MANAGER -> isManager || isAdmin;
            case PAID -> isWarehouse || isAdmin || isManager;
            case READY_FOR_PICKUP -> isWarehouse || isManager || isAdmin;
            case AWAITING_PAYMENT -> isManager || isAdmin;
            default -> false;
        };

        if (!allowed) {
            throw new RuntimeException("Not enough");
        }
        order.transitionToNextState();
        orderInStockRepository.save(order);
        if (order.getStatus() == OrderInStockState.PAID) {
            try {
                OrderSentForApproval event = new OrderSentForApproval(
                        order.getId(),
                        order.getCarId(),
                        "IN_STOCK",
                        UUID.randomUUID().toString()
                );

                OutboxEvent outbox = new OutboxEvent();
                outbox.setTopic("order-paid");
                outbox.setPayload(objectMapper.writeValueAsString(event));
                outboxRepository.save(outbox);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize event", e);
            }
        }
    }

    public void cancel(UUID orderId) {
        OrderInStock order = orderInStockRepository.findById(orderId).get();
        order.cancel();
        orderInStockRepository.save(order);
    }
}
