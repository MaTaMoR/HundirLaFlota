package me.matamor.hundirlaflota.juego.packets;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;

public class GameTurnPacket implements Packet {

    private boolean turno;

    public GameTurnPacket() {

    }

    public GameTurnPacket(boolean turno) {
        this.turno = turno;
    }

    public boolean isTurno() {
        return this.turno;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        byteBuff.writeBoolean(this.turno);
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        this.turno = byteBuff.readBoolean();
    }
}
