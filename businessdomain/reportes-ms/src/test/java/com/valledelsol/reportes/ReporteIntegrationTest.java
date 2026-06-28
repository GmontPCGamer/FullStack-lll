package com.valledelsol.reportes;

import com.valledelsol.reportes.entities.Reporte;
import com.valledelsol.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Tests de Integración - Microservicio Reportes")
class ReporteIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private String baseUrl;

    @Autowired
    private ReporteRepository reporteRepository;

    @BeforeEach
    void setUp() {
        reporteRepository.deleteAll();
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new org.springframework.web.client.NoOpResponseErrorHandler());
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("POST /api/reportes → crear reporte y GET /api/reportes → listar")
    void testCrearYListarReportes() {
        Reporte nuevo = new Reporte();
        nuevo.setUbicacionLatitud("-33.4569");
        nuevo.setUbicacionLongitud("-70.6483");
        nuevo.setDescripcion("Foco de incendio en sector norte");

        ResponseEntity<Reporte> created = restTemplate.postForEntity(baseUrl + "/api/reportes", nuevo, Reporte.class);
        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().getId());
        assertEquals("REPORTADO", created.getBody().getEstado());

        ResponseEntity<List> list = restTemplate.getForEntity(baseUrl + "/api/reportes", List.class);
        assertEquals(HttpStatus.OK, list.getStatusCode());
        assertNotNull(list.getBody());
        assertTrue(list.getBody().size() > 0);
    }

    @Test
    @DisplayName("GET /api/reportes/{id} → retorna 200 cuando existe")
    void testObtenerPorIdExistente() {
        Reporte nuevo = new Reporte();
        nuevo.setUbicacionLatitud("-33.4569");
        nuevo.setUbicacionLongitud("-70.6483");
        nuevo.setDescripcion("Incendio prueba");
        Reporte saved = reporteRepository.save(nuevo);

        ResponseEntity<Reporte> response = restTemplate.getForEntity(baseUrl + "/api/reportes/" + saved.getId(), Reporte.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Incendio prueba", response.getBody().getDescripcion());
    }

    @Test
    @DisplayName("GET /api/reportes/{id} → retorna 404 cuando no existe")
    void testObtenerPorIdNoExistente() {
        ResponseEntity<Reporte> response = restTemplate.getForEntity(baseUrl + "/api/reportes/9999", Reporte.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /api/reportes/{id} → eliminar reporte existente")
    void testEliminarReporte() {
        Reporte reporte = new Reporte();
        reporte.setUbicacionLatitud("-33.4569");
        reporte.setUbicacionLongitud("-70.6483");
        reporte.setDescripcion("Prueba eliminación");
        Reporte saved = reporteRepository.save(reporte);

        restTemplate.delete(baseUrl + "/api/reportes/" + saved.getId());

        assertFalse(reporteRepository.existsById(saved.getId()));
    }

    @Test
    @DisplayName("DELETE /api/reportes/{id} → retorna 404 cuando no existe")
    void testEliminarNoExistente() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/api/reportes/9999",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
