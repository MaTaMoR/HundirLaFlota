package me.matamor.hundirlaflota.servidor.lobby.commands;

import me.matamor.hundirlaflota.commands.ClientCommand;
import me.matamor.hundirlaflota.commands.CommandException;
import me.matamor.hundirlaflota.commands.CommandData;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;
import me.matamor.hundirlaflota.servidor.lobby.sala.Sala;

public class LeaveClientCommand extends ClientCommand {

    private final SubControllerLobby controlador;

    public LeaveClientCommand(SubControllerLobby controlador) {
        super("leave", "te permite salir de una sala");

        this.controlador = controlador;
    }

    @Override
    public void onCommand(CommandData info) throws CommandException {
        Sala sala = this.controlador.getSalaManager().buscarSala(info.getSender());
        ifNull(sala, Message.NOT_IN_SALA);

        sala.quitarCliente(info.getSender());
        info.sendMessage(Message.LEFT_SALA);

        if (sala.getClientes().isEmpty()) {
            this.controlador.getSalaManager().eliminarSala(sala.getNombre());
        } else {
            sala.sendMessage(Message.JOIN_SALA, info.getSender().getUsername());
        }
    }
}
