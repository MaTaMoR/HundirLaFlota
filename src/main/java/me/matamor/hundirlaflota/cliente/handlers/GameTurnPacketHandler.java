package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.cliente.game.ClientGameState;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.juego.packets.GameTurnPacket;
import me.matamor.hundirlaflota.messages.Message;

public class GameTurnPacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public GameTurnPacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        if (this.clientHandler.tieneGameClient()) {
            GameTurnPacket gameTurnPacket = (GameTurnPacket) packet;

            if (gameTurnPacket.isTurno()) {
                //Cambiamos el estado a atacando
                this.clientHandler.getGameClient().setGameState(ClientGameState.ATACANDO);

                //Mostramos por pantalla que es nuestro turno
                this.clientHandler.printMessage(Message.GAME_TURN_SELF);
            } else {
                //Mostramos por pantalla que es turno del enemigo
                this.clientHandler.printMessage(Message.GAME_TURN_ENEMY);
            }

            this.clientHandler.updateInterface();
        }
    }
}
