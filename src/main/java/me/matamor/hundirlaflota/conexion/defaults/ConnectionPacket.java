package me.matamor.hundirlaflota.conexion.defaults;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;

public class ConnectionPacket implements Packet {

    private String username;
    private String password;

    public ConnectionPacket() {

    }

    public ConnectionPacket(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        byteBuff.writeString(this.username);
        byteBuff.writeString(this.password);
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        this.username = byteBuff.readString();
        this.password = byteBuff.readString();
    }
}
