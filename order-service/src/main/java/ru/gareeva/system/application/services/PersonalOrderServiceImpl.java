package ru.gareeva.system.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gareeva.system.abstractions.repositories.*;
import ru.gareeva.system.application.contracts.services.PersonalOrderService;
import ru.gareeva.system.domain.entities.events.OrderSentForApproval;
import ru.gareeva.system.domain.entities.orders.OrderInStockState;
import ru.gareeva.system.domain.entities.orders.PersonalOrder;
import ru.gareeva.system.domain.entities.orders.PersonalOrderState;
import ru.gareeva.system.infrustructure.config.SecurityUtils;
import ru.gareeva.system.infrustructure.kafka.OutboxEvent;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonalOrderServiceImpl implements PersonalOrderService {
    private final PersonalOrderRepository personalOrderRepo;
    private final KeycloakManagerService keycloakManagerService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PersonalOrder createPersonalOrder(UUID carId, UUID clientId) {
        //ConfiguredCar car = carRepository.findById(carId).get();

        List<UUID> managerIds = keycloakManagerService.getAvailableManagers();
        if (managerIds.isEmpty()) {
            throw new RuntimeException("No managers available in Keycloak!");
        }
        UUID managerId = managerIds.get(0);

        var order = new PersonalOrder(clientId, managerId, carId);

        personalOrderRepo.save(order);

        return order;
    }
    public List<PersonalOrder> viewPersonalOrders(UUID userId) {
        return personalOrderRepo.findAll();
    }

    public void transitionToNextState(UUID orderId, UUID userId) {
        PersonalOrder order = personalOrderRepo.findById(orderId).get();
        boolean isAdmin = SecurityUtils.hasRole("admin");
        boolean isManager = SecurityUtils.hasRole("manager");
        boolean isWarehouse = SecurityUtils.hasRole("warehouse_admin");

        PersonalOrderState current = order.getStatus();

        boolean isAllowed = switch (current) {
            case CREATED -> isWarehouse || isAdmin;
            case APPROVED_BY_WAREHOUSE, AWAITING_PAYMENT -> isManager ||isAdmin;
            case PAID -> isWarehouse || isAdmin;
            case WAITING_DELIVERY, READY_FOR_PICKUP -> isManager || isAdmin;
            default -> false;
        };

        if (!isAllowed) {
            throw new RuntimeException("is not allowed");
        }

        order.transitionToNextState();
        personalOrderRepo.save(order);
        if (order.getStatus() == PersonalOrderState.PAID) {
            try {
                OrderSentForApproval event = new OrderSentForApproval(
                        order.getId(),
                        order.getCarId(),
                            "PERSONAL",
                        UUID.randomUUID().toString()
                );

                OutboxEvent outbox = new OutboxEvent();
                outbox.setTopic("personal-order-paid");
                outbox.setPayload(objectMapper.writeValueAsString(event));
                outboxRepository.save(outbox);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize event", e);
            }
        }
    }

    public void cancel(UUID orderId, UUID userId) {
        PersonalOrder order = personalOrderRepo.findById(orderId).get();
        order.cancel();
        personalOrderRepo.save(order);
    }
}
