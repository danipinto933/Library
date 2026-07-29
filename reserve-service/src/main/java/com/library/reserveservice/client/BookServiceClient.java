package com.library.reserveservice.client;

import com.library.reserveservice.dto.BookExternalDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookServiceClient {

    private final RestTemplate restTemplate;

    public BookServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "bookServiceCB", fallbackMethod = "fallbackGetBook")
    public BookExternalDto getBookById(Long bookId) {
        return restTemplate.getForObject("http://BOOK-SERVICE/api/v1/books/id/" + bookId, BookExternalDto.class);
    }

    public BookExternalDto fallbackGetBook(Long bookId, Throwable t) {
        BookExternalDto fallback = new BookExternalDto();
        fallback.setId(bookId);
        fallback.setTitle("Libro no disponible (Book-Service caído)");
        fallback.setAvailable(false);
        return fallback;
    }
}
