package me.matamor.hundirlaflota.juego.packets;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.util.Posicion;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;
import me.matamor.hundirlaflota.util.bytes.ByteBufferException;

public class GameAttackResponsePacket implements Packet {

    private Posicion posicion;
    private boolean self;
    private boolean tocado;
    private boolean hundido;

    public GameAttackResponsePacket() {

    }

    public GameAttackResponsePacket(Posicion posicion, boolean self, boolean tocado, boolean hundido) {
        this.posicion = posicion;
        this.self = self;
        this.tocado = tocado;
        this.hundido = hundido;
    }

    public Posicion getPosicion() {
        return this.posicion;
    }

    public boolean isSelf() {
        return this.self;
    }

    public boolean isTocado() {
        return this.tocado;
    }

    public boolean isHundido() {
        return this.hundido;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        try {
            this.posicion.write(byteBuff);
            byteBuff.writeBoolean(this.self);
            byteBuff.writeBoolean(this.tocado);
            byteBuff.writeBoolean(this.hundido);
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido escribir la posición!", e);
        }
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        try {
            this.posicion = new Posicion(byteBuff);
            this.self = byteBuff.readBoolean();
            this.tocado = byteBuff.readBoolean();
            this.hundido = byteBuff.readBoolean();
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido leer la posición!", e);
        }
    }
}
