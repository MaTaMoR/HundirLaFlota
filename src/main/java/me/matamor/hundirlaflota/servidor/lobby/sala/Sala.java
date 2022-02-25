package me.matamor.hundirlaflota.servidor.lobby.sala;

import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;

import java.util.ArrayList;
import java.util.List;

public class Sala {

    private final SubControllerLobby controlador;

    private final String nombre;
    private final List<ServerClient> clientes;

    public Sala(SubControllerLobby controlador, String nombre) {
        this.controlador = controlador;

        this.nombre = nombre;
        this.clientes = new ArrayList<>();
    }

    public SubControllerLobby getControlador() {
        return this.controlador;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void nuevoCliente(ServerClient serverClient) {
        this.clientes.add(serverClient);
    }

    public boolean tieneCliente(ServerClient serverClient) {
        return this.clientes.contains(serverClient);
    }

    public void quitarCliente(ServerClient serverClient) {
        this.clientes.remove(serverClient);
    }

    public List<ServerClient> getClientes() {
        return this.clientes;
    }

    public void sendMessage(String message) {
        for (ServerClient cliente : this.clientes) {
            this.controlador.sendMessage(cliente, message);
        }
    }

    public void sendMessage(Message message, Object... args) {
        sendMessage(message.getMessage(args));
    }
}
