package com.valledelsol.alertas.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests - GlobalExceptionHandler (Alertas)")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleValidation() retorna 400 con detalles de errores de validación")
    void testHandleValidation() {
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "object");
        bindingResult.rejectValue("titulo", "NotBlank", "El título es obligatorio");
        bindingResult.rejectValue("mensaje", "NotBlank", "El mensaje es obligatorio");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Validation Error", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("details"));
        assertTrue(response.getBody().containsKey("timestamp"));

        Map<String, String> details = (Map<String, String>) response.getBody().get("details");
        assertEquals("El título es obligatorio", details.get("titulo"));
        assertEquals("El mensaje es obligatorio", details.get("mensaje"));
    }

    @Test
    @DisplayName("handleValidation() retorna 400 con un solo error de campo")
    void testHandleValidationSingleError() {
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "object");
        bindingResult.rejectValue("titulo", "NotBlank", "El título es obligatorio");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        Map<String, String> details = (Map<String, String>) response.getBody().get("details");
        assertEquals(1, details.size());
    }

    @Test
    @DisplayName("handleValidation() retorna 400 con binding result vacío")
    void testHandleValidationEmptyErrors() {
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "object");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        Map<String, String> details = (Map<String, String>) response.getBody().get("details");
        assertTrue(details.isEmpty());
    }

    @Test
    @DisplayName("handleGeneral() retorna 500 con mensaje de error")
    void testHandleGeneral() {
        Exception ex = new RuntimeException("Error interno del servidor");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Error interno del servidor", response.getBody().get("message"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }

    @Test
    @DisplayName("handleGeneral() retorna 500 sin mensaje nulo")
    void testHandleGeneralNullMessage() {
        Exception ex = new RuntimeException();

        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("message"));
    }
}
