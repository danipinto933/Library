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
        kafkaTemplate.send(TOPIC, event);
    }
}
