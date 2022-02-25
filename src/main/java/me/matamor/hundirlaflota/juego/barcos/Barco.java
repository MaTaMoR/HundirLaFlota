package me.matamor.hundirlaflota.juego.barcos;

import me.matamor.hundirlaflota.juego.tablero.Casilla;
import me.matamor.hundirlaflota.juego.tablero.Tablero;
import me.matamor.hundirlaflota.util.Posicion;
import me.matamor.hundirlaflota.color.Color;

import java.util.List;

public class Barco {

    private final Tablero tablero;

    private final int id;
    private final Posicion posicion;
    private final int longitud;
    private final OrientacionBarco orientacionBarco;
    private final Color color;
    private final char caracter;
    private final List<Casilla> casillas;

    public Barco(Tablero tablero, int id, Posicion posicion, int longitud, OrientacionBarco orientacionBarco, Color color, Character caracter, List<Casilla> casillas) {
        this.tablero = tablero;

        this.id = id;
        this.posicion = posicion;
        this.color = color;
        this.caracter = caracter;
        this.longitud = longitud;
        this.orientacionBarco = orientacionBarco;
        this.casillas = casillas;
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    public int getId() {
        return this.id;
    }

    public Posicion getPosicion() {
        return this.posicion;
    }

    public int getX() {
        return this.posicion.getX();
    }

    public int getY() {
        return this.posicion.getY();
    }

    public int getLongitud() {
        return this.longitud;
    }

    public OrientacionBarco getOrientacionBarco() {
        return this.orientacionBarco;
    }

    public Color getColor() {
        return this.color;
    }

    public char getCaracter() {
        return this.caracter;
    }

    public List<Casilla> getCasillas() {
        return this.casillas;
    }

    public boolean isAlive() {
        return this.casillas.stream().anyMatch(e -> !e.isTocado());
    }
}
