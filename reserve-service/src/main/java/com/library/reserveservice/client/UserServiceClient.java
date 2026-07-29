package com.library.reserveservice.client;

import com.library.reserveservice.dto.UserExternalDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "fallbackGetUser")
    public UserExternalDto getUserById(Long userId) {
        return restTemplate.getForObject("http://USER-SERVICE/api/v1/users/id/" + userId, UserExternalDto.class);
    }

    public UserExternalDto fallbackGetUser(Long userId, Throwable t) {
        UserExternalDto fallback = new UserExternalDto();
        fallback.setId(userId);
        fallback.setName("Desconocido (User-Service no disponible)");
        return fallback;
    }
}
