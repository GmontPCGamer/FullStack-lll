package com.valledelsol.reportes.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ubicacionLatitud;
    private String ubicacionLongitud;
    private String descripcion;
    private String estado; // e.g., REPORTADO, EN_REVISION, CONTROLADO
    private LocalDateTime fechaReporte;

    @PrePersist
    protected void onCreate() {
        this.fechaReporte = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "REPORTADO";
        }
    }
}
