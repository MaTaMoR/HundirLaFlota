package me.matamor.hundirlaflota.juego.tablero;

import me.matamor.hundirlaflota.juego.barcos.Barco;
import me.matamor.hundirlaflota.util.Posicion;

public class Casilla {

    private final Tablero tablero;
    private final Posicion posicion;

    private Barco barco;

    private boolean atacado;
    private boolean tocado;

    public Casilla(Tablero tablero, Posicion posicion) {
        this.tablero = tablero;
        this.posicion = posicion;

        this.barco = null;

        this.atacado = false;
        this.tocado = false;
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    public Posicion getPosicion() {
        return this.posicion;
    }

    public void setBarco(Barco barco) {
        this.barco = barco;
    }

    public boolean tieneBarco() {
        return this.barco != null;
    }

    public Barco getBarco() {
        return this.barco;
    }

    public boolean isAtacado() {
        return this.atacado;
    }

    public void setAtacado(boolean atacado) {
        this.atacado = atacado;

        if (this.atacado && tieneBarco()) {
            this.tocado = true;
        }
    }

    public boolean isTocado() {
        return this.tocado;
    }

    public void setTocado(boolean tocado) {
        this.tocado = tocado;
    }
}
