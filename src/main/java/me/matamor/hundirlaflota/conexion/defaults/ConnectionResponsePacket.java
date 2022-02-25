package me.matamor.hundirlaflota.conexion.defaults;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;

public class ConnectionResponsePacket implements Packet {

    private boolean successful;
    private String message;

    public ConnectionResponsePacket() {

    }

    public ConnectionResponsePacket(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        byteBuff.writeBoolean(this.successful);
        byteBuff.writeString(this.message);
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        this.successful = byteBuff.readBoolean();
        this.message = byteBuff.readString();
    }
}
