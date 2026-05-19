package com.valledelsol.alertas.controller;

import com.valledelsol.alertas.entities.Alerta;
import com.valledelsol.alertas.factory.AlertaFactory;
import com.valledelsol.alertas.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Alertas microservice.
 *
 * Patrones aplicados:
 * - Repository Pattern: acceso a datos via AlertaRepository (Spring Data JPA)
 * - Factory Method Pattern: uso de AlertaFactory para crear alertas según nivel de gravedad
 */
@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    @Autowired
    private AlertaRepository alertaRepository;

    @GetMapping
    public List<Alerta> listar() {
        return alertaRepository.findAll();
    }

    /**
     * Crea una alerta. Utiliza el Factory Method si no se especifica nivelGravedad,
     * para garantizar valores correctos por defecto.
     */
    @PostMapping
    public Alerta crear(@RequestBody Alerta alerta) {
        if (alerta.getNivelGravedad() == null || alerta.getNivelGravedad().isBlank()) {
            // Factory Method Pattern: seleccionamos la fábrica según el nivel
            AlertaFactory factory = AlertaFactory.deNivelMedio();
            alerta = factory.fabricar(alerta.getTitulo(), alerta.getMensaje());
        } else {
            // Factory Method Pattern con nivel explícito
            AlertaFactory factory = switch (alerta.getNivelGravedad().toUpperCase()) {
                case "ALTO"  -> AlertaFactory.deNivelAlto();
                case "BAJO"  -> AlertaFactory.deNivelBajo();
                default      -> AlertaFactory.deNivelMedio();
            };
            alerta = factory.fabricar(alerta.getTitulo(), alerta.getMensaje());
        }
        return alertaRepository.save(alerta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerta> obtenerPorId(@PathVariable Long id) {
        return alertaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!alertaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        alertaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
