package me.matamor.hundirlaflota.servidor.game.handlers;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.defaults.DisconnectPacket;
import me.matamor.hundirlaflota.juego.packets.PlayerTableroPacket;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.game.ServerGame;

public class PlayerTableroPacketHandler implements PacketHandler<ServerClient> {

    private final ServerGame serverGame;

    public PlayerTableroPacketHandler(ServerGame serverGame) {
        this.serverGame = serverGame;
    }

    @Override
    public void handle(ServerClient socketHandler, Packet packet) {
        if (socketHandler.isServerPlayer()) {
            PlayerTableroPacket playerTableroPacket = (PlayerTableroPacket) packet;

            //Asignamos el tablero al jugador
            socketHandler.getServerPlayer().setTablero(playerTableroPacket.getTablero());

            if (this.serverGame.getCurrentPlayer() == null) {
                this.serverGame.setCurrentPlayer(socketHandler.getServerPlayer());
            }

            //Comprobamos si ambos jugadores han enviado su tablero
            if (this.serverGame.arePlayersReady()) {
                //Ambos jugadores han enviado su tablero, ahora empezamos la partida

                //Empezamos la partida
                this.serverGame.empezarJuego();
            }
        } else {
            this.serverGame.sendPacket(socketHandler, new DisconnectPacket("No estas en un partida, no puedes enviar este paquete!"));
            this.serverGame.sacarCliente(socketHandler);

            socketHandler.close();
        }
    }
}
