package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.cliente.game.ClientGame;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.juego.barcos.OrientacionBarco;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.Constantes;

import java.util.Arrays;

public class GameBeginPacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public GameBeginPacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        if (this.clientHandler.tieneGameClient()) {
            this.clientHandler.printMessage("ERROR! Recibido paquete de partida pero ya se está en una partida!!!!");
        } else {
            ClientGame clientGame = new ClientGame(this.clientHandler);
            this.clientHandler.setGameClient(clientGame);

            this.clientHandler.updateInterface();

            //Mostramos por consola que debe poner los barcos
            this.clientHandler.printMessage(Message.GAME_BEGIN, Constantes.BARCOS);

            //Mostramos los barcos que se pueden crear
            for (int i = Constantes.BARCOS_MAX_LONGITUD; i > 0; i--) {
                this.clientHandler.printMessage(Message.BARCO_LENGTH_INFO, i);
            }

            //Mostramos el formato para poner el barco
            this.clientHandler.printMessage(Message.BARCO_PLACE_INFO, Arrays.toString(OrientacionBarco.values()));
        }
    }
}
