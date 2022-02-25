package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.conexion.defaults.DisconnectPacket;

public class DisconnectPacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public DisconnectPacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        DisconnectPacket disconnectPacket = (DisconnectPacket) packet;

        this.clientHandler.printMessage(disconnectPacket.getMessage());
        this.clientHandler.stop();
    }
}
