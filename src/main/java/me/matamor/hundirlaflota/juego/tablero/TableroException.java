package me.matamor.hundirlaflota.juego.tablero;

public class TableroException extends RuntimeException {

    public TableroException(String message) {
        super(message);
    }

    public TableroException(String message, Exception e) {
        super(message, e);
    }
}
