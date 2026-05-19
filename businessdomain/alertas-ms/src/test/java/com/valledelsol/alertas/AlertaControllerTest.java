package com.valledelsol.alertas;

import com.valledelsol.alertas.controller.AlertaController;
import com.valledelsol.alertas.entities.Alerta;
import com.valledelsol.alertas.factory.AlertaFactory;
import com.valledelsol.alertas.repository.AlertaRepository;
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
 * Unit Tests para AlertaController.
 * Patrones aplicados:
 * - Repository Pattern (mockeo con Mockito)
 * - Factory Method Pattern (AlertaFactory)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Microservicio Alertas")
class AlertaControllerTest {

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private AlertaController alertaController;

    private Alerta alertaMock;

    @BeforeEach
    void setUp() {
        alertaMock = new Alerta();
        alertaMock.setId(1L);
        alertaMock.setTitulo("Riesgo de Incendio en Sector Norte");
        alertaMock.setMensaje("Evacúe de inmediato el sector indicado.");
        alertaMock.setNivelGravedad("ALTO");
        alertaMock.setFechaEmision(LocalDateTime.now());
    }

    @Test
    @DisplayName("listar() debería retornar todas las alertas")
    void testListar_RetornaListaDeAlertas() {
        // Given
        when(alertaRepository.findAll()).thenReturn(Arrays.asList(alertaMock));

        // When
        List<Alerta> resultado = alertaController.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("ALTO", resultado.get(0).getNivelGravedad());
        verify(alertaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("crear() debería asignar nivel MEDIO por defecto si no se especifica")
    void testCrear_NivelPorDefecto() {
        // Given
        Alerta sinNivel = new Alerta();
        sinNivel.setTitulo("Humo detectado");
        sinNivel.setMensaje("Precaución en el sector.");
        sinNivel.setNivelGravedad(null); // sin nivel

        Alerta alertaGuardada = new Alerta();
        alertaGuardada.setId(2L);
        alertaGuardada.setTitulo("Humo detectado");
        alertaGuardada.setNivelGravedad("MEDIO");
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaGuardada);

        // When
        Alerta resultado = alertaController.crear(sinNivel);

        // Then
        assertNotNull(resultado);
        assertEquals("MEDIO", resultado.getNivelGravedad());
    }

    @Test
    @DisplayName("crear() debería respetar el nivel ALTO indicado")
    void testCrear_NivelAlto() {
        // Given
        Alerta alertaAlta = new Alerta();
        alertaAlta.setTitulo("Incendio masivo");
        alertaAlta.setMensaje("Zona de alto riesgo.");
        alertaAlta.setNivelGravedad("ALTO");
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaMock);

        // When
        Alerta resultado = alertaController.crear(alertaAlta);

        // Then
        assertEquals("ALTO", resultado.getNivelGravedad());
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    @DisplayName("obtenerPorId() debería retornar 200 cuando la alerta existe")
    void testObtenerPorId_Encontrado() {
        // Given
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaMock));

        // When
        ResponseEntity<Alerta> response = alertaController.obtenerPorId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ALTO", response.getBody().getNivelGravedad());
    }

    @Test
    @DisplayName("obtenerPorId() debería retornar 404 cuando la alerta no existe")
    void testObtenerPorId_NoEncontrado() {
        // Given
        when(alertaRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Alerta> response = alertaController.obtenerPorId(999L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ── Tests para el Factory Method Pattern ─────────────────────────────────

    @Test
    @DisplayName("Factory Method: AlertaFactory.deNivelAlto() crea alerta con nivel ALTO")
    void testFactory_NivelAlto() {
        AlertaFactory factory = AlertaFactory.deNivelAlto();
        Alerta alerta = factory.fabricar("Incendio", "Descripción de prueba");

        assertEquals("ALTO", alerta.getNivelGravedad());
        assertEquals("Incendio", alerta.getTitulo());
    }

    @Test
    @DisplayName("Factory Method: AlertaFactory.deNivelMedio() crea alerta con nivel MEDIO")
    void testFactory_NivelMedio() {
        AlertaFactory factory = AlertaFactory.deNivelMedio();
        Alerta alerta = factory.fabricar("Humo", "Precaución");

        assertEquals("MEDIO", alerta.getNivelGravedad());
    }

    @Test
    @DisplayName("Factory Method: AlertaFactory.deNivelBajo() crea alerta con nivel BAJO")
    void testFactory_NivelBajo() {
        AlertaFactory factory = AlertaFactory.deNivelBajo();
        Alerta alerta = factory.fabricar("Aviso preventivo", "Vigilancia activa");

        assertEquals("BAJO", alerta.getNivelGravedad());
    }
}
