package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.juego.packets.GameEndPacket;
import me.matamor.hundirlaflota.messages.Message;

public class GameEndPacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public GameEndPacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        if (this.clientHandler.tieneGameClient()) {
            GameEndPacket gameEndPacket = (GameEndPacket) packet;

            if (gameEndPacket.isWinner()) {
                this.clientHandler.printMessage(Message.WIN);
            } else {
                this.clientHandler.printMessage(Message.LOSE);
            }

            this.clientHandler.setGameClient(null);
            this.clientHandler.updateInterface();
        }
    }
}
