package me.matamor.hundirlaflota.servidor.handlers;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.servidor.client.ServerClient;

public class DisconnectPacketHandler implements PacketHandler<ServerClient> {

    @Override
    public void handle(ServerClient socketHandler, Packet packet) {
        socketHandler.close();
    }
}
