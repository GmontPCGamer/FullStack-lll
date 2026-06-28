package com.valledelsol.bff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Tests de Integración - BFF Web")
class BffWebIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    @DisplayName("GET /api/bff/reportes → retorna fallback (503) si reportes-ms no está disponible")
    void testGetReportesFallback() {
        webTestClient.get()
                .uri("/api/bff/reportes")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Servicio de reportes no disponible. Intente más tarde.")
                .jsonPath("$.status").isEqualTo("CIRCUIT_OPEN");
    }

    @Test
    @DisplayName("GET /api/bff/alertas → retorna fallback (503) si alertas-ms no está disponible")
    void testGetAlertasFallback() {
        webTestClient.get()
                .uri("/api/bff/alertas")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Servicio de alertas no disponible. Intente más tarde.")
                .jsonPath("$.status").isEqualTo("CIRCUIT_OPEN");
    }
}
