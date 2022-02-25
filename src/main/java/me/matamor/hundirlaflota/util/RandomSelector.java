package me.matamor.hundirlaflota.util;

import me.matamor.hundirlaflota.color.Color;

public class RandomSelector<T> {

    private final T[] valores;
    private int posicionInicio;

    public RandomSelector(T[] valores) {
        this.valores = valores;
        this.posicionInicio = 0;
    }

    public RandomSelector(T[] valores, int posicionInicio) {
        this.valores = valores;
        this.posicionInicio = posicionInicio;
    }

    public synchronized int longitud() {
        return this.valores.length;
    }

    public synchronized int getPosicionInicio() {
        return this.posicionInicio;
    }

    public synchronized void reset() {
        this.posicionInicio = 0;
    }

    public synchronized T random() {
        //Estoy significa que no quedan más valores
        if (this.posicionInicio == this.valores.length) {
            this.posicionInicio = 0;
        }

        //Cogemos una posicion aleatoria de las restantes
        int randomPos = Randomizer.randomInt(this.posicionInicio, this.valores.length - 1);

        //Cogemos los valores
        T randomValue = this.valores[randomPos];
        T inicioValue = this.valores[this.posicionInicio];

        //Cambiamos de posición los valores
        this.valores[randomPos] = inicioValue;
        this.valores[this.posicionInicio] = randomValue;

        //Aumetamos en 1 la posicion de inicio
        this.posicionInicio = this.posicionInicio + 1;

        //Devolvemos el valor aleatorio
        return randomValue;
    }

    public static void main(String[] args) {
        RandomSelector<Color> colores = new RandomSelector<>(Color.values());

        while (colores.getPosicionInicio() < colores.longitud()) {
            System.out.println(colores.random());
        }
    }
}