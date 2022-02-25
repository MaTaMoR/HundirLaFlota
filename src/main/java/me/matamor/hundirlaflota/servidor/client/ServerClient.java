package me.matamor.hundirlaflota.servidor.client;

import me.matamor.hundirlaflota.conexion.Protocol;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.servidor.controller.Controller;
import me.matamor.hundirlaflota.servidor.game.player.ServerGamePlayer;

import java.io.IOException;
import java.net.Socket;

public class ServerClient extends SocketHandler {

    private final int clientId;

    private String username;
    private int lastAliveTicks;

    private Controller controller;
    private ServerGamePlayer serverGamePlayer;

    public ServerClient(int clientId, Socket socket) throws IOException {
        super(Protocol.TO_SERVER, Protocol.TO_CLIENT, socket);

        this.clientId = clientId;

        this.username = null;
        this.lastAliveTicks = 0;

        this.controller = null;
        this.serverGamePlayer = null;
    }

    public int getClientId() {
        return this.clientId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLastAliveTicks() {
        return this.lastAliveTicks;
    }

    public void setLastAliveTicks(int lastAliveTicks) {
        this.lastAliveTicks = lastAliveTicks;
    }

    public boolean tieneControlador() {
        return this.controller != null;
    }

    public Controller getControlador() {
        return this.controller;
    }

    public void setControlador(Controller controller) {
        this.controller = controller;
    }

    public void setServerPlayer(ServerGamePlayer serverGamePlayer) {
        this.serverGamePlayer = serverGamePlayer;
    }

    public boolean isServerPlayer() {
        return this.serverGamePlayer != null;
    }

    public ServerGamePlayer getServerPlayer() {
        return this.serverGamePlayer;
    }
}
