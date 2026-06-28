package com.valledelsol.bff;

import com.valledelsol.bff.controller.BffController;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests E2E para el BFF.
 * Simula el flujo completo: frontend → BFF → microservicios.
 * Usa mocks de WebClient para simular respuestas exitosas y fallos
 * de los microservicios downstream (reportes-ms, alertas-ms).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests E2E - BFF: Flujo Frontend → API REST → Microservicios")
class BffE2ETest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private BffController bffController;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        org.springframework.test.util.ReflectionTestUtils.setField(bffController, "reportesUrl", "/api/reportes");
        org.springframework.test.util.ReflectionTestUtils.setField(bffController, "alertasUrl", "/api/alertas");
    }

    @Test
    @DisplayName("E2E: GET reportes → BFF → reportes-ms (éxito)")
    void testGetReportesSuccess() {
        String expectedBody = "[{\"id\":1,\"descripcion\":\"Foco de incendio\"}]";
        configureGetMock("/api/reportes", expectedBody);

        Mono<ResponseEntity<String>> result = bffController.getReportes();

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    @DisplayName("E2E: GET alertas → BFF → alertas-ms (éxito)")
    void testGetAlertasSuccess() {
        String expectedBody = "[{\"id\":1,\"titulo\":\"Alerta de prueba\",\"nivelGravedad\":\"ALTO\"}]";
        configureGetMock("/api/alertas", expectedBody);

        Mono<ResponseEntity<String>> result = bffController.getAlertas();

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    @DisplayName("E2E: POST reporte → BFF → reportes-ms (éxito)")
    void testPostReporteSuccess() {
        String requestBody = "{\"ubicacionLatitud\":\"-33.4569\",\"descripcion\":\"Incendio\"}";
        String responseBody = "{\"id\":1,\"estado\":\"REPORTADO\"}";

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(requestBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(Mono.just(ResponseEntity.ok(responseBody)));

        Mono<ResponseEntity<String>> result = bffController.createReporte(requestBody);

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("REPORTADO"));
    }

    @Test
    @DisplayName("E2E: POST alerta → BFF → alertas-ms (éxito)")
    void testPostAlertaSuccess() {
        String requestBody = "{\"titulo\":\"Alarma\",\"nivelGravedad\":\"ALTO\"}";
        String responseBody = "{\"id\":1,\"nivelGravedad\":\"ALTO\"}";

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(requestBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(Mono.just(ResponseEntity.ok(responseBody)));

        Mono<ResponseEntity<String>> result = bffController.createAlerta(requestBody);

        ResponseEntity<String> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("ALTO"));
    }

    private void configureGetMock(String path, String responseBody) {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(contains(path))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(Mono.just(ResponseEntity.ok(responseBody)));
    }
}
