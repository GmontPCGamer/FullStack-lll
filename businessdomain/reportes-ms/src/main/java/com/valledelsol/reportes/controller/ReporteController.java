package com.valledelsol.reportes.controller;

import com.valledelsol.reportes.entities.Reporte;
import com.valledelsol.reportes.repository.ReporteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteRepository reporteRepository;

    @GetMapping
    public List<Reporte> listar() {
        return reporteRepository.findAll();
    }

    @PostMapping
    public Reporte crear(@Valid @RequestBody Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerPorId(@PathVariable Long id) {
        return reporteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!reporteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reporteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
