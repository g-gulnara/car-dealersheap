package ru.gareeva.system.infrustructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.gareeva.system.abstractions.repositories.AssemblyOrderRepository;
import ru.gareeva.system.abstractions.repositories.CarInStockRepository;
import ru.gareeva.system.abstractions.repositories.StockComponentRepository;
import ru.gareeva.system.domain.entities.AssemblyOrder;
import ru.gareeva.system.domain.entities.AssemblyStatus;
import ru.gareeva.system.domain.entities.StockComponent;
import ru.gareeva.system.domain.entities.cars.CarInStock;
import ru.gareeva.system.domain.entities.events.OrderApprovedEvent;
import ru.gareeva.system.domain.entities.events.OrderRejectedEvent;
import ru.gareeva.system.domain.entities.events.OrderSentForApproval;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final AssemblyOrderRepository assemblyOrderRepository;
    private final CarInStockRepository carRepository;
    private final StockComponentRepository stockComponentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-paid", groupId = "storage-service")
    public void handleOrderPaid(String message) {

        OrderSentForApproval event;

        try {
            event = objectMapper.readValue(message, OrderSentForApproval.class);
        } catch (Exception e) {
            log.error("Invalid message format: {}", message, e);
            return;
        }

        if (assemblyOrderRepository.existsBySourceOrderId(event.getOrderId())) {
            log.info("Event already processed: {}", event.getOrderId());
            return;
        }

        AssemblyOrder assembly = new AssemblyOrder();
        assembly.setSourceOrderId(event.getOrderId());
        assembly.setCarId(event.getCarId());
        assembly.setSourceOrderType(event.getOrderType());
        assembly.setStatus(AssemblyStatus.CREATED);
        assemblyOrderRepository.save(assembly);

        try {
            switch (event.getOrderType()) {
                case "IN_STOCK" -> handleInStock(event);
                case "CONFIGURATION" -> handleConfiguration(event);
                default -> throw new RuntimeException("Unknown order type: " + event.getOrderType());
            }

            assembly.setStatus(AssemblyStatus.ASSEMBLED);
            assemblyOrderRepository.save(assembly);

            kafkaTemplate.send("order-approved",
                    objectMapper.writeValueAsString(new OrderApprovedEvent(
                            event.getOrderId(),
                            event.getTraceId(),
                            event.getOrderType()
                    )));

            log.info("Order approved: {}", event.getOrderId());

        } catch (Exception e) {

            assembly.setStatus(AssemblyStatus.FAILED);
            assemblyOrderRepository.save(assembly);

            log.error("Failed to process order: {}", event.getOrderId(), e);

            try {
                kafkaTemplate.send("order-rejected",
                        objectMapper.writeValueAsString(new OrderRejectedEvent(
                                event.getOrderType(),
                                event.getOrderId(),
                                e.getMessage(),
                                event.getTraceId()
                        )));
            } catch (JsonProcessingException ex) {
                log.error("Failed to send rejection", ex);
            }
        }
    }

    private void handleInStock(OrderSentForApproval event) {
        CarInStock car = carRepository.findById(event.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));
        car.reserve();
        carRepository.save(car);
    }

    private void handleConfiguration(OrderSentForApproval event) {
        List<StockComponent> components =
                stockComponentRepository.findAllByCarId(event.getCarId());

        if (components.isEmpty()) {
            throw new RuntimeException("No components found for car: " + event.getCarId());
        }

        for (StockComponent component : components) {
            if (!component.isAvailable()) {
                throw new RuntimeException("Component not available: " + component.getComponentId());
            }
            component.reserve();
        }

        stockComponentRepository.saveAll(components);
        log.info("Components reserved for car {} in order {}", event.getCarId(), event.getOrderId());
    }
}