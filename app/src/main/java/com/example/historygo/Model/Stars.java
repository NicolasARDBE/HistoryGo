package com.example.historygo.Model;

public enum Stars {
    UNO(1, "Pobre"),
    DOS(2, "Regular"),
    TRES(3, "Buena"),
    CUATRO(4, "Muy buena"),
    CINCO(5, "Excelente");

    private final int valor;
    private final String calificacion;

    Stars(int valor, String calificacion) {
        this.valor = valor;
        this.calificacion = calificacion;
    }

    public int getValor() {
        return valor;
    }

    public String getCalificacion() {
        return calificacion;
    }

    @Override
    public String toString() {
        return valor + " - " + calificacion;
    }
}