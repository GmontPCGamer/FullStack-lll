package com.valledelsol.alertas.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Data
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensaje;
    private String nivelGravedad; // ALTO, MEDIO, BAJO
    private LocalDateTime fechaEmision;

    @PrePersist
    protected void onCreate() {
        this.fechaEmision = LocalDateTime.now();
    }
}
