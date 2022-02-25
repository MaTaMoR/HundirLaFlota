package me.matamor.hundirlaflota.juego.packets;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.util.Posicion;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;
import me.matamor.hundirlaflota.util.bytes.ByteBufferException;

public class GameAttackPacket implements Packet {

    private Posicion posicion;

    public GameAttackPacket() {

    }

    public GameAttackPacket(Posicion posicion) {
        this.posicion = posicion;
    }

    public Posicion getPosicion() {
        return this.posicion;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        try {
            this.posicion.write(byteBuff);
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido escribir la información de la posición!", e);
        }
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        try {
            this.posicion = new Posicion(byteBuff);
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido leer la información de la posición!", e);
        }
    }
}
