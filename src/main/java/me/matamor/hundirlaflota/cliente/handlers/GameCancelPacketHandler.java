package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.juego.packets.GameCancelPaket;

public class GameCancelPacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public GameCancelPacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        GameCancelPaket gameCancelPaket = (GameCancelPaket) packet;

        if (this.clientHandler.tieneGameClient()) {
            this.clientHandler.setGameClient(null);

            this.clientHandler.updateInterface();

            this.clientHandler.printMessage(gameCancelPaket.getMessage());
        }
    }
}
