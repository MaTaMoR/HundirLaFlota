package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.cliente.game.ClientGameState;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.messages.Message;

public class GameStartPacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public GameStartPacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        if (this.clientHandler.tieneGameClient()) {
            //Cambiamos el estado a esperando
            this.clientHandler.getGameClient().setGameState(ClientGameState.ESPERANDO);

            //Mostramos un mensaje de que la partida ha empezado
            this.clientHandler.printMessage(Message.GAME_START);
        }
    }
}
