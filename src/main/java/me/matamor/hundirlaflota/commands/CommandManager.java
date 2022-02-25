package me.matamor.hundirlaflota.commands;

import me.matamor.hundirlaflota.servidor.client.ServerClient;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final Map<String, ClientCommand> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
    }

    public void registrar(ClientCommand command) {
        if (this.commands.containsKey(command.getName())) {
            throw new IllegalArgumentException("El comando ya está registrado!");
        }

        this.commands.put(command.getName(), command);
    }

    public boolean executeCommand(ServerClient cliente, String command, String[] arguments) {
        ClientCommand clientCommand = this.commands.get(command);
        if (clientCommand == null) {
            return false;
        }

        clientCommand.execute(new CommandData(cliente, arguments));
        return true;
    }
}
