package me.matamor.hundirlaflota.conexion;

public class PacketException extends Exception {

    public PacketException() {

    }

    public PacketException(String message) {
        super(message);
    }

    public PacketException(String message, Exception e) {
        super(message, e);
    }
}
