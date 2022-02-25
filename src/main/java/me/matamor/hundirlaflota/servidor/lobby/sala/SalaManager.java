package me.matamor.hundirlaflota.servidor.lobby.sala;

import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;

import java.util.HashMap;
import java.util.Map;

public class SalaManager {

    private final SubControllerLobby controlador;
    private final Map<String, Sala> salas;

    public SalaManager(SubControllerLobby controlador) {
        this.controlador = controlador;
        this.salas = new HashMap<>();
    }

    public Sala crearSala(String nombre) {
        return this.salas.computeIfAbsent(nombre, k -> new Sala(this.controlador, nombre));
    }

    public Sala buscarSala(String nombre) {
        return this.salas.get(nombre);
    }

    public Sala buscarSala(ServerClient cliente) {
        return this.salas.values().stream().filter(e -> e.getClientes().contains(cliente)).findFirst().orElse(null);
    }

    public boolean existeSala(String nombre) {
        return this.salas.containsKey(nombre);
    }

    public Sala eliminarSala(String nombre) {
        return this.salas.remove(nombre);
    }
}
