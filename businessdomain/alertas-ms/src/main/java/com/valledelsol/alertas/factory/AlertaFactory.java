package com.valledelsol.alertas.factory;

import com.valledelsol.alertas.entities.Alerta;

/**
 * Factory Method Pattern
 * Define la interfaz para crear objetos de tipo Alerta según su nivel de gravedad.
 * Permite instanciar alertas preconfiguradas sin que el cliente conozca la lógica de creación.
 */
public abstract class AlertaFactory {

    /**
     * Factory Method: subclases concretas implementan cómo crear la alerta.
     */
    public abstract Alerta crearAlerta(String titulo, String mensaje);

    /**
     * Template method que usa el factory method.
     */
    public Alerta fabricar(String titulo, String mensaje) {
        Alerta alerta = crearAlerta(titulo, mensaje);
        return alerta;
    }

    // ── Fábricas concretas ──────────────────────────────────────────────────

    public static AlertaFactory deNivelAlto() {
        return new AlertaFactory() {
            @Override
            public Alerta crearAlerta(String titulo, String mensaje) {
                Alerta a = new Alerta();
                a.setTitulo(titulo);
                a.setMensaje(mensaje);
                a.setNivelGravedad("ALTO");
                return a;
            }
        };
    }

    public static AlertaFactory deNivelMedio() {
        return new AlertaFactory() {
            @Override
            public Alerta crearAlerta(String titulo, String mensaje) {
                Alerta a = new Alerta();
                a.setTitulo(titulo);
                a.setMensaje(mensaje);
                a.setNivelGravedad("MEDIO");
                return a;
            }
        };
    }

    public static AlertaFactory deNivelBajo() {
        return new AlertaFactory() {
            @Override
            public Alerta crearAlerta(String titulo, String mensaje) {
                Alerta a = new Alerta();
                a.setTitulo(titulo);
                a.setMensaje(mensaje);
                a.setNivelGravedad("BAJO");
                return a;
            }
        };
    }
}
