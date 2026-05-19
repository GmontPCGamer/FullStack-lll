package com.valledelsol.reportes.repository;

import com.valledelsol.reportes.entities.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
}
