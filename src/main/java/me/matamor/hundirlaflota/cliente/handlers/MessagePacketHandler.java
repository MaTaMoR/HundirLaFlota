package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;

public class MessagePacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public MessagePacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        MessagePacket messagePacket = (MessagePacket) packet;

        this.clientHandler.printMessage(messagePacket.getMessage());
    }
}
