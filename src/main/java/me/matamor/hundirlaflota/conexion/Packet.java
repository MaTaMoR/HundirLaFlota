package me.matamor.hundirlaflota.conexion;

import me.matamor.hundirlaflota.util.bytes.ByteBuff;

public interface Packet {

    void write(ByteBuff byteBuff) throws PacketException;

    void read(ByteBuff byteBuff) throws PacketException;

    default void ifTrue(boolean value, String message, Object... args) throws PacketException {
        if (value) {
            throw new PacketException(String.format(message, args));
        }
    }
}
