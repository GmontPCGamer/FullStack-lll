package com.valledelsol.alertas.controller;

import com.valledelsol.alertas.entities.Alerta;
import com.valledelsol.alertas.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    @Autowired
    private AlertaRepository alertaRepository;

    @GetMapping
    public List<Alerta> listar() {
        return alertaRepository.findAll();
    }

    @PostMapping
    public Alerta crear(@RequestBody Alerta alerta) {
        return alertaRepository.save(alerta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerta> obtenerPorId(@PathVariable Long id) {
        return alertaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
