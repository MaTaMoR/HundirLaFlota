package me.matamor.hundirlaflota.servidor.lobby.commands;

import me.matamor.hundirlaflota.commands.ClientCommand;
import me.matamor.hundirlaflota.commands.CommandException;
import me.matamor.hundirlaflota.commands.CommandData;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;
import me.matamor.hundirlaflota.servidor.lobby.sala.Sala;

public class StartClienteCommand extends ClientCommand {

    private final SubControllerLobby controlador;

    public StartClienteCommand(SubControllerLobby controlador) {
        super("start", "inicia un partida");

        this.controlador = controlador;
    }

    @Override
    public void onCommand(CommandData info) throws CommandException {
        Sala sala = this.controlador.getSalaManager().buscarSala(info.getSender());
        ifNull(sala, Message.NOT_IN_SALA);

        //Comprobamos que haya suficiente gente
        ifFalse(sala.getClientes().size() == 2, Message.SALA_NOT_READY);

        //Comprobamos si el usuario es el dueño de la sala
        ServerClient owner = sala.getClientes().get(0);
        ifFalse(owner == info.getSender(), Message.SALA_NOT_OWNER);

        //Cogemos al enemigo que lo usaremos para la partida
        ServerClient enemy = sala.getClientes().get(1);

        /*

            Borramos la sala
            Movemos los jugadores a un controlador de partida

         */

        System.out.println("eliminar sala");
        //Borramos la sala
        this.controlador.getSalaManager().eliminarSala(sala.getNombre());

        System.out.println("sacar clientes");
        //Sacamos ambos clientes del lobby
        this.controlador.sacarCliente(owner);
        this.controlador.sacarCliente(enemy);

        System.out.println("Crear juego");
        //Creamos la partida
        this.controlador.getConnectionHandler().getGameManager().crearJuego(owner, enemy);
    }
}
