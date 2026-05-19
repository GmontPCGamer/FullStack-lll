package com.valledelsol.bff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "http://localhost:5173") // React default port
public class BffController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${reportes.service.url}")
    private String reportesUrl;

    @Value("${alertas.service.url}")
    private String alertasUrl;

    // --- Reportes ---
    @GetMapping("/reportes")
    public Mono<ResponseEntity<String>> getReportes() {
        return webClientBuilder.build()
                .get()
                .uri(reportesUrl)
                .retrieve()
                .toEntity(String.class);
    }

    @PostMapping("/reportes")
    public Mono<ResponseEntity<String>> createReporte(@RequestBody Object reporte) {
        return webClientBuilder.build()
                .post()
                .uri(reportesUrl)
                .bodyValue(reporte)
                .retrieve()
                .toEntity(String.class);
    }

    // --- Alertas ---
    @GetMapping("/alertas")
    public Mono<ResponseEntity<String>> getAlertas() {
        return webClientBuilder.build()
                .get()
                .uri(alertasUrl)
                .retrieve()
                .toEntity(String.class);
    }
}
