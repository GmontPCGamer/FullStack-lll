package com.valledelsol.alertas.repository;

import com.valledelsol.alertas.entities.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
