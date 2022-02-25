package me.matamor.hundirlaflota.cliente.handlers;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.juego.packets.GameAttackResponsePacket;
import me.matamor.hundirlaflota.juego.tablero.Casilla;
import me.matamor.hundirlaflota.juego.tablero.Tablero;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.ColoredMessage;
import me.matamor.hundirlaflota.util.Posicion;

public class GameAttackResponsePacketHandler implements PacketHandler<SocketHandler> {

    private final ClientHandler clientHandler;

    public GameAttackResponsePacketHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(SocketHandler socketHandler, Packet packet) {
        if (this.clientHandler.tieneGameClient()) {
            GameAttackResponsePacket responsePacket = (GameAttackResponsePacket) packet;

            Posicion posicion = responsePacket.getPosicion();

            Tablero tablero;

            //Comprobamos el quién ha sido atacado
            if (responsePacket.isSelf()) {
                tablero = this.clientHandler.getGameClient().getTableroEnemigo();
            } else {
                tablero = this.clientHandler.getGameClient().getTableroPersonal();
            }

            //Buscamos la casilla del ataque
            Casilla casilla = tablero.buscarCasilla(posicion.getX(), posicion.getY());
            casilla.setAtacado(true);

            //Comprobamos si el ataque ha tocado el barco
            if (responsePacket.isTocado()) {
                //Marcamos la casilla como tocada
                casilla.setTocado(true);

                if (responsePacket.isSelf()) {
                    //Mostramos por pantalla que hemos atacado
                    this.clientHandler.printMessage(Message.ATTACK_HIT_SELF);
                } else {
                    //Mostramos por pantalla que hemos sido atacados
                    this.clientHandler.printMessage(Message.ATTACK_HIT_ENEMY);
                }

                if (responsePacket.isHundido()) {
                    if (responsePacket.isSelf()) {
                        //Mostramos por pantalla que hemos atacado
                        this.clientHandler.printMessage(Message.BARCO_HUNDIDO_SELF);
                    } else {
                        //Mostramos por pantalla que hemos sido atacados
                        this.clientHandler.printMessage(Message.BARCO_HUNDIDO_ENEMY);
                    }
                }
            } else {
                if (responsePacket.isSelf()) {
                    //Mostramos por pantalla que hemos atacado
                    this.clientHandler.printMessage(Message.ATTACK_MISS_SELF);
                } else {
                    //Mostramos por pantalla que hemos sido atacados
                    this.clientHandler.printMessage(Message.ATTACK_HIT_ENEMY);
                }
            }

            this.clientHandler.updateInterface();
        }
    }
}
