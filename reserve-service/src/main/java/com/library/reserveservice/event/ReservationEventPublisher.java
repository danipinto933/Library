package com.library.reserveservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationEventPublisher {

    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;
    private static final String TOPIC = "book-reservations";

    public void publishReservationCreatedEvent(ReservationEvent event) {
        log.info("Publishing ReservationCreated event for reserveId: {}", event.getReserveId());
        try {
            kafkaTemplate.send(TOPIC, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Fallo asíncrono al publicar evento en Kafka para reserveId {}: {}", 
                              event.getReserveId(), ex.getMessage(), ex);
                } else {
                    log.info("Evento publicado exitosamente en Kafka en el tópico '{}' para reserveId: {}", 
                             TOPIC, event.getReserveId());
                }
            });
        } catch (Exception e) {
            log.error("Fallo síncrono al intentar enviar evento a Kafka para reserveId {}: {}", 
                      event.getReserveId(), e.getMessage(), e);
        }
    }
}
