package com.valledelsol.bff.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Backend For Frontend (BFF) Controller — Municipalidad Valle del Sol
 *
 * Patrones aplicados:
 * - BFF Pattern: único punto de entrada para el cliente React, agrega y adapta respuestas de microservicios
 * - Circuit Breaker (Resilience4j): evita cascadas de fallos si un microservicio no responde
 * - Repository Pattern (delegado a los microservicios via WebClient)
 */
@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class BffController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${reportes.service.url}")
    private String reportesUrl;

    @Value("${alertas.service.url}")
    private String alertasUrl;

    // ── Reportes ─────────────────────────────────────────────────────────────

    @GetMapping("/reportes")
    @CircuitBreaker(name = "reportes-service", fallbackMethod = "fallbackGetReportes")
    public Mono<ResponseEntity<String>> getReportes() {
        return webClientBuilder.build()
                .get()
                .uri(reportesUrl)
                .retrieve()
                .toEntity(String.class);
    }

    @PostMapping("/reportes")
    @CircuitBreaker(name = "reportes-service", fallbackMethod = "fallbackCreateReporte")
    public Mono<ResponseEntity<String>> createReporte(@RequestBody String reporte) {
        return webClientBuilder.build()
                .post()
                .uri(reportesUrl)
                .header("Content-Type", "application/json")
                .bodyValue(reporte)
                .retrieve()
                .toEntity(String.class);
    }

    // ── Alertas ───────────────────────────────────────────────────────────────

    @GetMapping("/alertas")
    @CircuitBreaker(name = "alertas-service", fallbackMethod = "fallbackGetAlertas")
    public Mono<ResponseEntity<String>> getAlertas() {
        return webClientBuilder.build()
                .get()
                .uri(alertasUrl)
                .retrieve()
                .toEntity(String.class);
    }

    @PostMapping("/alertas")
    @CircuitBreaker(name = "alertas-service", fallbackMethod = "fallbackCreateAlerta")
    public Mono<ResponseEntity<String>> createAlerta(@RequestBody String alerta) {
        return webClientBuilder.build()
                .post()
                .uri(alertasUrl)
                .header("Content-Type", "application/json")
                .bodyValue(alerta)
                .retrieve()
                .toEntity(String.class);
    }

    // ── Fallback Methods (Circuit Breaker) ────────────────────────────────────

    public Mono<ResponseEntity<String>> fallbackGetReportes(Throwable t) {
        String msg = "{\"error\":\"Servicio de reportes no disponible. Intente más tarde.\",\"status\":\"CIRCUIT_OPEN\"}";
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(msg));
    }

    public Mono<ResponseEntity<String>> fallbackCreateReporte(String body, Throwable t) {
        String msg = "{\"error\":\"No se pudo crear el reporte. Servicio temporalmente no disponible.\",\"status\":\"CIRCUIT_OPEN\"}";
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(msg));
    }

    public Mono<ResponseEntity<String>> fallbackGetAlertas(Throwable t) {
        String msg = "{\"error\":\"Servicio de alertas no disponible. Intente más tarde.\",\"status\":\"CIRCUIT_OPEN\"}";
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(msg));
    }

    public Mono<ResponseEntity<String>> fallbackCreateAlerta(String body, Throwable t) {
        String msg = "{\"error\":\"No se pudo crear la alerta. Servicio temporalmente no disponible.\",\"status\":\"CIRCUIT_OPEN\"}";
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(msg));
    }
}
