package me.matamor.hundirlaflota.juego.packets;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;

public class GameEndPacket implements Packet {

    private boolean winner;

    public GameEndPacket() {

    }

    public GameEndPacket(boolean winner) {
        this.winner = winner;
    }

    public boolean isWinner() {
        return this.winner;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        byteBuff.writeBoolean(this.winner);
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        this.winner = byteBuff.readBoolean();
    }
}
