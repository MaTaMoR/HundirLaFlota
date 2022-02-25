package me.matamor.hundirlaflota.servidor.game.handlers;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.defaults.DisconnectPacket;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.game.ServerGame;

public class MessagePacketHandler implements PacketHandler<ServerClient> {

    private final ServerGame serverGame;

    public MessagePacketHandler(ServerGame ServerGame) {
        this.serverGame = ServerGame;
    }

    @Override
    public void handle(ServerClient socketHandler, Packet packet) {
        if (socketHandler.isServerPlayer()) {

        }else {
            this.serverGame.sendPacket(socketHandler, new DisconnectPacket("No estas en un partida, no puedes enviar este paquete!"));
            this.serverGame.sacarCliente(socketHandler);

            socketHandler.close();
        }
    }
}
