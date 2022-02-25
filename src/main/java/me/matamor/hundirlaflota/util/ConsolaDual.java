package me.matamor.hundirlaflota.util;

import me.matamor.hundirlaflota.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class ConsolaDual {

    private static ConsolaDual consola;

    public static ConsolaDual consola() {
        if (consola == null) {
            consola = new ConsolaDual();
        }

        return consola;
    }

    public static void limpiarizquierda() {
        consola().limpiarConsolaIzquierda();
    }

    public static void limpiarDerecha() {
        consola().limpiarConsolaDerecha();
    }

    public static void printIzquierda(Message message, Object... args) {
        printIzquierda(message.getMessage(args));
    }

    public static void printIzquierda(String message) {
        consola().printConsolaIzquierda(message);
    }

    public static void printderecha(Message message, Object... args) {
        printderecha(message.getMessage(args));
    }

    public static void printderecha(String message) {
        consola().printConsolaDerecha(message);
    }

    private static final int MAX_LINES = 100;

    private static final int LONGITUD_IZQUIERDA = 65;
    private static final int LONGITUD_DERECHA = 200;

    private static final int LONGITUD_BARRA = 125;

    private final List<String> izquierda;
    private final List<String> derecha;

    private ConsolaDual() {
        this.izquierda = new ArrayList<>();
        this.derecha = new ArrayList<>();
    }

    private String stripColors(String message) {
        return message.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private void printConsolas() {
        for (int i = 0; MAX_LINES > i; i++) {
            System.out.println("\n");
        }

        int maxSize = Math.max(this.izquierda.size(), this.derecha.size());

        for (int i = (maxSize - 1); i >= 0; i--) {
            String izquierda = (this.izquierda.size() > i ? this.izquierda.get(i) : "");
            String derecha = (this.derecha.size() > i ? this.derecha.get(i) : "");

            String sinColorIzquierda = stripColors(izquierda);
            if (sinColorIzquierda.length() < LONGITUD_IZQUIERDA) {
                izquierda = izquierda + " ".repeat(LONGITUD_IZQUIERDA - sinColorIzquierda.length());
            }

            String output = izquierda + "  |  " + derecha;
            System.out.println(output);
        }

        System.out.println("-".repeat(LONGITUD_BARRA));
        System.out.println();
    }

    private void printConsola(String message, List<String> consola, int length) {
        String[] lineas = message.split("\n");

        for (String linea : lineas) {
            String sinColor = stripColors(linea);

            if (sinColor.length() > length) {
                linea = linea.substring(0, length);
            } else if (sinColor.length() < length) {
                linea = linea + " ".repeat(length - sinColor.length());
            }

            consola.add(0, linea);

            if (consola.size() > MAX_LINES) {
                consola.remove(consola.size() - 1);
            }
        }

        printConsolas();
    }

    public void limpiarConsolaIzquierda() {
        this.izquierda.clear();
    }

    public void limpiarConsolaDerecha() {
        this.izquierda.clear();
    }

    public void printConsolaIzquierda(String message) {
        printConsola(message, this.izquierda, LONGITUD_IZQUIERDA);
    }

    public void printConsolaDerecha(String message) {
        printConsola(message, this.derecha, LONGITUD_DERECHA);
    }
}
