package me.matamor.hundirlaflota.servidor.lobby.commands;

import me.matamor.hundirlaflota.commands.ClientCommand;
import me.matamor.hundirlaflota.commands.CommandException;
import me.matamor.hundirlaflota.commands.CommandData;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;
import me.matamor.hundirlaflota.servidor.lobby.sala.Sala;

public class JoinClientCommand extends ClientCommand {

    private final SubControllerLobby controlador;

    public JoinClientCommand(SubControllerLobby controlador) {
        super("join", "te permite unirte a una sala");

        this.controlador = controlador;
    }

    @Override
    public void onCommand(CommandData info) throws CommandException {
        if (info.length != 1) {
            info.sendMessage("Uso incorrecto del comando: /sala join <nombre>");
        } else {
            String nombre = info.getString(0);

            Sala salaActual = this.controlador.getSalaManager().buscarSala(info.getSender());
            ifNotNull(salaActual, Message.ALREADY_IN_SALA);

            Sala sala = this.controlador.getSalaManager().crearSala(nombre);
            ifTrue(sala.getClientes().size() == 2, Message.FULL_SALA);

            //Enviamos un mensaje a la sala que el jugador se ha unido
            sala.sendMessage(Message.JOIN_SALA, info.getSender().getUsername());

            //Añadimos el usuario a la sala
            sala.nuevoCliente(info.getSender());

            //Enviamos al jugar un mensaje de que se ha unido a la sala
            info.sendMessage(Message.JOIN_SALA_SELF, sala.getNombre());

            //Si la sala tiene suficientes jugadores para empezar enviamos un mensaje
            if (sala.getClientes().size() == 2) {
                sala.sendMessage(Message.SALA_READY);
            }
        }
    }
}
