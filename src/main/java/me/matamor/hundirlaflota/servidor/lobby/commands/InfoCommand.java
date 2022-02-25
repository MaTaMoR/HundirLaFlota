package me.matamor.hundirlaflota.servidor.lobby.commands;

import me.matamor.hundirlaflota.commands.ClientCommand;
import me.matamor.hundirlaflota.commands.CommandData;
import me.matamor.hundirlaflota.commands.CommandException;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;

import java.util.Collection;

public class InfoCommand extends ClientCommand {

    private final SubControllerLobby controlador;

    public InfoCommand(SubControllerLobby controlador) {
        super("info", "comando de admin");

        this.controlador = controlador;
    }

    @Override
    public void onCommand(CommandData data) throws CommandException {
        Collection<ServerClient> clients = this.controlador.getConnectionHandler().getMasterController().getClientes();

        if (clients.isEmpty()) {
            data.sendMessage("No hay ningún usuario conectado!");
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (ServerClient serverClient : clients) {
                stringBuilder.append(serverClient.getUsername()).append(" ");
            }

            data.sendMessage("Usuarios: " + stringBuilder);
        }
    }
}
