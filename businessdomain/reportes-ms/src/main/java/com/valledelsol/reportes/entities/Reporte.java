package com.valledelsol.reportes.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La latitud es obligatoria")
    private String ubicacionLatitud;

    @NotBlank(message = "La longitud es obligatoria")
    private String ubicacionLongitud;

    @NotBlank(message = "La descripción es obligatoria")
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
