package com.library.bookservice.event;

import com.library.bookservice.model.Book;
import com.library.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {

    private final BookRepository bookRepository;

    @KafkaListener(topics = "book-reservations", groupId = "book-service-group")
    public void onReservationEvent(ReservationEvent event) {
        try {
            log.info("Received ReservationEvent for reserveId: {} with status: {}", 
                     event != null ? event.getReserveId() : "null", 
                     event != null ? event.getStatus() : "null");

            if (event == null || event.getBookIds() == null || event.getBookIds().isEmpty()) {
                return;
            }

            List<Book> books = bookRepository.findAllById(event.getBookIds());
            
            boolean newAvailabilityStatus = !"CREATED".equals(event.getStatus());

            for (Book book : books) {
                book.setAvailable(newAvailabilityStatus);
            }

            bookRepository.saveAll(books);
            log.info("Updated availability for {} books to {}", books.size(), newAvailabilityStatus);
        } catch (Exception e) {
            log.error("Error al procesar evento de reserva en book-service para reserveId {}: {}", 
                      event != null ? event.getReserveId() : "null", e.getMessage(), e);
        }
    }
}
