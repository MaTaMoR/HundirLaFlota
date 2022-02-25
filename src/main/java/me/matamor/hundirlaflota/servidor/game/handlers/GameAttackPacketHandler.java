package me.matamor.hundirlaflota.servidor.game.handlers;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.defaults.DisconnectPacket;
import me.matamor.hundirlaflota.juego.packets.GameAttackPacket;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.game.ServerGame;
import me.matamor.hundirlaflota.util.Constantes;
import me.matamor.hundirlaflota.util.Posicion;

public class GameAttackPacketHandler implements PacketHandler<ServerClient> {

    private final ServerGame serverGame;

    public GameAttackPacketHandler(ServerGame serverGame) {
        this.serverGame = serverGame;
    }

    @Override
    public void handle(ServerClient socketHandler, Packet packet) {
        if (socketHandler.isServerPlayer()) {
            GameAttackPacket gameAttackPacket = (GameAttackPacket) packet;

            if (this.serverGame.getCurrentPlayer() == socketHandler.getServerPlayer()) {
                Posicion posicion = gameAttackPacket.getPosicion();

                //Comprobamos si el ataque está fuera del rango
                if ((posicion.getX() < 0 || posicion.getX() >= Constantes.LONGITUD) || (posicion.getY() < 0 || posicion.getY() >= Constantes.LONGITUD)) {
                    this.serverGame.sendMessage(socketHandler, "No has trampas!! Tú ataque está fuera del rango valido....");
                    this.serverGame.sacarCliente(socketHandler);

                    socketHandler.close();
                } else {
                    //Ejecutamos el ataque
                    this.serverGame.ataqueJugador(socketHandler.getServerPlayer(), posicion);
                }
            } else {
                this.serverGame.sendMessage(socketHandler, "No has trampas!! No es tu turno para atacar listillo....");
                this.serverGame.sacarCliente(socketHandler);

                socketHandler.close();
            }
        } else {
            this.serverGame.sendPacket(socketHandler, new DisconnectPacket("No estas en un partida, no puedes enviar este paquete!"));
            this.serverGame.sacarCliente(socketHandler);

            socketHandler.close();
        }
    }
}
