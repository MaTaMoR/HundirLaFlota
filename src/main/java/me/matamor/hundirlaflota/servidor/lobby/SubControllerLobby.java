package me.matamor.hundirlaflota.servidor.lobby;

import me.matamor.hundirlaflota.commands.CommandManager;
import me.matamor.hundirlaflota.servidor.controller.SubController;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.ServerConnectionHandler;
import me.matamor.hundirlaflota.servidor.lobby.commands.InfoCommand;
import me.matamor.hundirlaflota.servidor.lobby.commands.SalaClientCommand;
import me.matamor.hundirlaflota.servidor.lobby.handlers.LobbyMessagePacketHandler;
import me.matamor.hundirlaflota.servidor.lobby.sala.Sala;
import me.matamor.hundirlaflota.servidor.lobby.sala.SalaManager;

import java.awt.*;

public class SubControllerLobby extends SubController {

    private final ServerConnectionHandler connectionHandler;

    private final SalaManager salaManager;
    private final CommandManager commandManager;

    public SubControllerLobby(ServerConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;

        this.salaManager = new SalaManager(this);

        this.commandManager = new CommandManager();
        this.commandManager.registrar(new SalaClientCommand(this));
        this.commandManager.registrar(new InfoCommand(this));

        getPacketManager().registerHandler(MessagePacket.class, new LobbyMessagePacketHandler(this));
    }

    public ServerConnectionHandler getConnectionHandler() {
        return this.connectionHandler;
    }

    public SalaManager getSalaManager() {
        return this.salaManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public void onJoin(ServerClient serverClient) {
        //Notificamos al servidor de las salas
        sendMessage(serverClient, "Bienvenido al servidor de hundir la flota!", Color.BLUE);
        sendMessage(serverClient, "Puedes unirte a una sala con el comando: /sala", Color.BLUE);
    }

    @Override
    public void onExit(ServerClient serverClient) {
        Sala sala = this.salaManager.buscarSala(serverClient);
        if (sala != null) {
            sala.quitarCliente(serverClient);

            if (sala.getClientes().isEmpty()) {
                this.salaManager.eliminarSala(sala.getNombre());
            } else {
                sala.sendMessage(serverClient.getUsername() + " ha salido de la sala!");
            }
        }
    }
}
