package ru.gareeva.system.infrustructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gareeva.system.abstractions.repositories.OutboxRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxSender {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 3000)
    public void sendEvents() {

        List<OutboxEvent> events = outboxRepository.findBySentFalse();
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getPayload());
                event.setSent(true);
                outboxRepository.save(event);
                log.info("Sent event to topic: {}", event.getTopic());
            } catch (Exception e) {
                log.error("Failed to send event: {}", event.getId(), e);
            }
        }
    }
}