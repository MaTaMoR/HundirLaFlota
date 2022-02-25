package me.matamor.hundirlaflota.servidor.lobby.commands;

import me.matamor.hundirlaflota.commands.ClientCommand;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;

public class SalaClientCommand extends ClientCommand {

    public SalaClientCommand(SubControllerLobby controlador) {
        super("sala", "comandos relacionados a las salas");

        addChildren(new JoinClientCommand(controlador), new LeaveClientCommand(controlador), new StartClienteCommand(controlador));
    }
}
