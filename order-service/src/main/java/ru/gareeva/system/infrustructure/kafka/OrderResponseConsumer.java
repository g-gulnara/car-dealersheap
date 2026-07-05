package ru.gareeva.system.infrustructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.gareeva.system.abstractions.repositories.OrderInStockRepository;
import ru.gareeva.system.abstractions.repositories.PersonalOrderRepository;
import ru.gareeva.system.domain.entities.events.OrderApprovedEvent;
import ru.gareeva.system.domain.entities.events.OrderRejectedEvent;
import ru.gareeva.system.domain.entities.orders.OrderInStock;
import ru.gareeva.system.domain.entities.orders.PersonalOrder;

@Service
@AllArgsConstructor
@Slf4j
public class OrderResponseConsumer {

    private final OrderInStockRepository orderInStockRepository;
    private final PersonalOrderRepository personalOrderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-approved", groupId = "order-service")
    public void handleOrderApproved(String message) {
        try {
            OrderApprovedEvent event = objectMapper.readValue(message, OrderApprovedEvent.class);

//            OrderInStock order = orderRepository.findById(event.getOrderId()).orElseThrow();
//            order.transitionToNextState();
//            orderRepository.save(order);

            if ("IN_STOCK".equals(event.getOrderType())) {
                OrderInStock order = orderInStockRepository.findById(event.getOrderId()).orElseThrow();
                order.transitionToNextState();
                orderInStockRepository.save(order);
            } else if ("PERSONAL".equals(event.getOrderType())) {
                PersonalOrder order = personalOrderRepository.findById(event.getOrderId()).orElseThrow();
                order.transitionToNextState();
                personalOrderRepository.save(order);
            }

            log.info("Order ready for pickup: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process order-approved event", e);
        }
    }

    @KafkaListener(topics = "order-rejected", groupId = "order-service")
    public void handleOrderRejected(String message) {
        try {
            OrderRejectedEvent event = objectMapper.readValue(message, OrderRejectedEvent.class);


            if ("IN_STOCK".equals(event.getOrderType())) {
                OrderInStock order = orderInStockRepository.findById(event.getOrderId()).orElseThrow();
                order.cancel();
                orderInStockRepository.save(order);
            } else if ("PERSONAL".equals(event.getOrderType())) {
                PersonalOrder order = personalOrderRepository.findById(event.getOrderId()).orElseThrow();
                order.cancel();
                personalOrderRepository.save(order);
            }

            log.info("Order cancelled due to rejection: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process order-rejected event", e);
        }
    }
}