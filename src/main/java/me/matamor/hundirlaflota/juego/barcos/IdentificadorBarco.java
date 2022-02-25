package me.matamor.hundirlaflota.juego.barcos;

import me.matamor.hundirlaflota.color.Color;
import me.matamor.hundirlaflota.color.ColorBuilder;

public class IdentificadorBarco {

    public static int contadorIds = 0;

    private final int id;

    private final Color color;
    private final char caracter;

    public final String identificador;

    public IdentificadorBarco(Color color, char caracter) {
        this(contadorIds++, color, caracter);
    }

    public IdentificadorBarco(int id, Color color, char caracter) {
        this.id = id;

        this.color = color;
        this.caracter = caracter;

        this.identificador = ColorBuilder.builder(color).build(caracter);
    }

    public int getId() {
        return this.id;
    }

    public Color getColor() {
        return this.color;
    }

    public char getCaracter() {
        return this.caracter;
    }

    public String getKey() {
        return this.identificador;
    }

    @Override
    public String toString() {
        return this.identificador;
    }
}
