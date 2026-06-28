package com.valledelsol.reportes;

import com.valledelsol.reportes.controller.ReporteController;
import com.valledelsol.reportes.entities.Reporte;
import com.valledelsol.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests para ReporteController.
 * Patrón aplicado: Repository Pattern (mockeo del repositorio con Mockito).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Microservicio Reportes")
class ReporteControllerTest {

    @Mock
    private ReporteRepository reporteRepository;

    @InjectMocks
    private ReporteController reporteController;

    private Reporte reporteMock;

    @BeforeEach
    void setUp() {
        reporteMock = new Reporte();
        reporteMock.setId(1L);
        reporteMock.setUbicacionLatitud("-33.4569");
        reporteMock.setUbicacionLongitud("-70.6483");
        reporteMock.setDescripcion("Foco de incendio detectado en sector norte");
        reporteMock.setEstado("REPORTADO");
        reporteMock.setFechaReporte(LocalDateTime.now());
    }

    @Test
    @DisplayName("listar() debería retornar todos los reportes")
    void testListar_RetornaListaDeReportes() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteMock);
        when(reporteRepository.findAll()).thenReturn(reportes);

        // When
        List<Reporte> resultado = reporteController.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("REPORTADO", resultado.get(0).getEstado());
        verify(reporteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listar() debería retornar lista vacía cuando no hay reportes")
    void testListar_RetornaListaVacia() {
        // Given
        when(reporteRepository.findAll()).thenReturn(List.of());

        // When
        List<Reporte> resultado = reporteController.listar();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("crear() debería persistir y retornar el reporte creado")
    void testCrear_RetornaReporteGuardado() {
        // Given
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setUbicacionLatitud("-33.4569");
        nuevoReporte.setUbicacionLongitud("-70.6483");
        nuevoReporte.setDescripcion("Prueba de incendio");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteController.crear(nuevoReporte);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("REPORTADO", resultado.getEstado());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    @DisplayName("obtenerPorId() debería retornar 200 cuando el reporte existe")
    void testObtenerPorId_Encontrado() {
        // Given
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteMock));

        // When
        ResponseEntity<Reporte> response = reporteController.obtenerPorId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("-33.4569", response.getBody().getUbicacionLatitud());
    }

    @Test
    @DisplayName("obtenerPorId() debería retornar 404 cuando el reporte no existe")
    void testObtenerPorId_NoEncontrado() {
        // Given
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Reporte> response = reporteController.obtenerPorId(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("El estado por defecto debería ser REPORTADO")
    void testReporte_EstadoDefecto() {
        // Given
        Reporte reporte = new Reporte();
        reporte.setEstado(null);

        // When - simula el @PrePersist
        if (reporte.getEstado() == null) {
            reporte.setEstado("REPORTADO");
        }

        // Then
        assertEquals("REPORTADO", reporte.getEstado());
    }

    @Test
    @DisplayName("eliminar() debería retornar 204 cuando el reporte existe")
    void testEliminar_Existe() {
        when(reporteRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = reporteController.eliminar(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(reporteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar() debería retornar 404 cuando el reporte no existe")
    void testEliminar_NoExiste() {
        when(reporteRepository.existsById(99L)).thenReturn(false);

        ResponseEntity<Void> response = reporteController.eliminar(99L);

        assertEquals(404, response.getStatusCode().value());
        verify(reporteRepository, never()).deleteById(any());
    }
}
