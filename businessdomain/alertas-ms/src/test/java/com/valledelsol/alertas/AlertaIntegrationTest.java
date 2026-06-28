package com.valledelsol.alertas;

import com.valledelsol.alertas.entities.Alerta;
import com.valledelsol.alertas.repository.AlertaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Tests de Integración - Microservicio Alertas")
class AlertaIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private String baseUrl;

    @Autowired
    private AlertaRepository alertaRepository;

    @BeforeEach
    void setUp() {
        alertaRepository.deleteAll();
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new org.springframework.web.client.NoOpResponseErrorHandler());
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("POST /api/alertas → crear alerta y GET /api/alertas → listar")
    void testCrearYListarAlertas() {
        Alerta nueva = new Alerta();
        nueva.setTitulo("Riesgo de Incendio");
        nueva.setMensaje("Evacúe el sector.");
        nueva.setNivelGravedad("ALTO");

        ResponseEntity<Alerta> created = restTemplate.postForEntity(baseUrl + "/api/alertas", nueva, Alerta.class);
        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().getId());
        assertEquals("ALTO", created.getBody().getNivelGravedad());

        ResponseEntity<List> list = restTemplate.getForEntity(baseUrl + "/api/alertas", List.class);
        assertEquals(HttpStatus.OK, list.getStatusCode());
        assertTrue(list.getBody().size() > 0);
    }

    @Test
    @DisplayName("POST /api/alertas → nivel MEDIO por defecto")
    void testCrearConNivelPorDefecto() {
        Alerta sinNivel = new Alerta();
        sinNivel.setTitulo("Humo detectado");
        sinNivel.setMensaje("Precaución");

        ResponseEntity<Alerta> created = restTemplate.postForEntity(baseUrl + "/api/alertas", sinNivel, Alerta.class);
        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertEquals("MEDIO", created.getBody().getNivelGravedad());
    }

    @Test
    @DisplayName("DELETE /api/alertas/{id} → eliminar alerta existente")
    void testEliminarAlerta() {
        Alerta alerta = new Alerta();
        alerta.setTitulo("Prueba");
        alerta.setMensaje("Eliminar");
        alerta.setNivelGravedad("BAJO");
        Alerta saved = alertaRepository.save(alerta);

        restTemplate.delete(baseUrl + "/api/alertas/" + saved.getId());

        assertFalse(alertaRepository.existsById(saved.getId()));
    }
}
