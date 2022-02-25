package me.matamor.hundirlaflota.cliente;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;

public class ClientServerPacketReader implements Runnable {

    private final ClientHandler clientHandler;

    public ClientServerPacketReader(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void run() {
        try {
            Packet packet = this.clientHandler.getSocketHandler().readPacket();
            if (packet != null) {
                this.clientHandler.getPacketHandler().executeHandlers(this.clientHandler.getSocketHandler(), packet);
            }
        } catch (PacketException e) {
            e.printStackTrace();
        }
    }
}
