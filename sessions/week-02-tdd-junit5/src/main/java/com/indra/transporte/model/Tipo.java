package com.indra.transporte.model;

import java.util.Arrays;

import com.indra.transporte.exception.UnsupportedTypeException;

/**
 * Catalogo cerrado de tipos compartido por Bus y Ruta.
 */
public enum Tipo {
    ELECTRIC,
    DIESEL,
    GAS,
    GENERAL;

    public static Tipo from(String valor) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.name().equalsIgnoreCase(valor))
                .findFirst()
                .orElseThrow(() -> new UnsupportedTypeException("Tipo desconocido: " + valor));
    }
}
