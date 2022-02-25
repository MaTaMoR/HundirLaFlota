package me.matamor.hundirlaflota.servidor.game.player;

import me.matamor.hundirlaflota.juego.tablero.Tablero;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.game.player.stats.GameStats;

public class ServerGamePlayer {

    private final ServerClient serverClient;
    private final GameStats stats;

    private Tablero tablero;

    public ServerGamePlayer(ServerClient serverClient) {
        this.serverClient = serverClient;
        this.stats = new GameStats();

        this.tablero = null;

        serverClient.setServerPlayer(this);
    }

    public ServerClient getClientServer() {
        return this.serverClient;
    }

    public GameStats getStats() {
        return this.stats;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public boolean tieneTablero() {
        return this.tablero != null;
    }

    public Tablero getTablero() {
        return this.tablero;
    }
}
