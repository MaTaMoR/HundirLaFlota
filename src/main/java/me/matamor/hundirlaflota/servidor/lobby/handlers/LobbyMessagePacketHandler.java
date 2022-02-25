package me.matamor.hundirlaflota.servidor.lobby.handlers;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketHandler;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;
import me.matamor.hundirlaflota.servidor.lobby.sala.Sala;

import java.util.Arrays;

public class LobbyMessagePacketHandler implements PacketHandler<ServerClient> {

    private final SubControllerLobby controlador;

    public LobbyMessagePacketHandler(SubControllerLobby controlador) {
        this.controlador = controlador;
    }

    private void handleCommand(ServerClient serverClient, String mensaje) {
        String[] args = mensaje.split(" ");
        String comando = args[0].replaceFirst("/", "");

        args = Arrays.copyOfRange(args, 1, args.length);

        if (!this.controlador.getCommandManager().executeCommand(serverClient, comando, args)) { //Si devuelve false era un comando invalido
            this.controlador.sendMessage(serverClient, "Comando desconocido!");
        }
    }

    @Override
    public void handle(ServerClient client, Packet packet) {
        MessagePacket messagePacket = (MessagePacket) packet;

        System.out.println("Got message: " + messagePacket.getMessage());

        String mensaje = messagePacket.getMessage().getText();
        if (mensaje.startsWith("/")) {
            this.handleCommand(client, mensaje);
        } else {
            //Buscamos la sala del cliente
            Sala sala = this.controlador.getSalaManager().buscarSala(client);

            if (sala == null) {
                for (ServerClient serverClient : this.controlador.getClientes()) {
                    if (this.controlador.getSalaManager().buscarSala(serverClient) == null) {
                        System.out.println("send message");
                        this.controlador.sendMessage(serverClient, Message.SPEAK, client.getUsername(), mensaje);
                    }
                }
            } else {
                sala.sendMessage(Message.SPEAK, client.getUsername(), mensaje);
            }
        }
    }
}
