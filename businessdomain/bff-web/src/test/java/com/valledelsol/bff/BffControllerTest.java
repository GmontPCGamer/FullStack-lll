package com.valledelsol.bff;

import com.valledelsol.bff.controller.BffController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - BFF Controller (Circuit Breaker fallbacks)")
class BffControllerTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private BffController bffController;

    @Test
    @DisplayName("fallbackGetReportes debería retornar 503")
    void testFallbackGetReportes() {
        Mono<ResponseEntity<String>> result = bffController.fallbackGetReportes(new RuntimeException("timeout"));

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("CIRCUIT_OPEN"));
    }

    @Test
    @DisplayName("fallbackCreateReporte debería retornar 503")
    void testFallbackCreateReporte() {
        Mono<ResponseEntity<String>> result = bffController.fallbackCreateReporte("{}", new RuntimeException("error"));

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("CIRCUIT_OPEN"));
    }

    @Test
    @DisplayName("fallbackGetAlertas debería retornar 503")
    void testFallbackGetAlertas() {
        Mono<ResponseEntity<String>> result = bffController.fallbackGetAlertas(new RuntimeException("timeout"));

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("CIRCUIT_OPEN"));
    }

    @Test
    @DisplayName("fallbackCreateAlerta debería retornar 503")
    void testFallbackCreateAlerta() {
        Mono<ResponseEntity<String>> result = bffController.fallbackCreateAlerta("{}", new RuntimeException("error"));

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("CIRCUIT_OPEN"));
    }
}
