package me.matamor.hundirlaflota.juego.packets;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.juego.tablero.Tablero;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;
import me.matamor.hundirlaflota.util.bytes.ByteBufferException;

public class PlayerTableroPacket implements Packet {

    private Tablero tablero;

    public PlayerTableroPacket() {

    }

    public PlayerTableroPacket(Tablero tablero) {
        this.tablero = tablero;
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        try {
            this.tablero.write(byteBuff);
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido escribir la información del tablero!", e);
        }
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        try {
            this.tablero = new Tablero(byteBuff);
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido leer la información del tablero!", e);
        }
    }
}
