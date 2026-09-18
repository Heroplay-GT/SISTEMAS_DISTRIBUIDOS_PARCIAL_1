package com.parcial.promedio.aplicacion.dto;

/** DTO que contiene el resultado del cálculo de promedio para la capa de aplicación. */
public record ResultadoPromedioDto(
        double promedio, String promedioFormateado, String condicion, String observacion) {
}
