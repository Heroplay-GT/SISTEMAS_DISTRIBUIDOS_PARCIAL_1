package com.parcial.promedio.aplicacion.dto;

/** Comando DTO para solicitar el cálculo del promedio con valores numéricos primitivos. */
public record CalcularPromedioCommand(double nota1, double nota2, double nota3) {
}
