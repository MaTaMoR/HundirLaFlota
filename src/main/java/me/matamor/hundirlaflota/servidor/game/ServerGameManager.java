package me.matamor.hundirlaflota.servidor.game;

import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.ServerConnectionHandler;
import me.matamor.hundirlaflota.servidor.ftp.FTPGameManager;
import me.matamor.hundirlaflota.servidor.ftp.FTPTask;
import me.matamor.hundirlaflota.tasks.TaskExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServerGameManager {

    private final ServerConnectionHandler connectionHandler;
    private final FTPGameManager ftpGameManager;

    private final Map<Integer, ServerGame> games;
    private int idCount;

    public ServerGameManager(ServerConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.ftpGameManager = new FTPGameManager(connectionHandler);

        this.games = new HashMap<>();
        this.idCount = 0;
    }

    public ServerConnectionHandler getConnectionHandler() {
        return this.connectionHandler;
    }

    public FTPGameManager getFtpGameManager() {
        return this.ftpGameManager;
    }

    public TaskExecutor getTaskExecutor() {
        return this.connectionHandler.getTaskExecutor();
    }

    public Collection<ServerGame> getGames() {
        return this.games.values();
    }

    public ServerGame buscarGame(int id) {
        return this.games.get(id);
    }

    public synchronized void cancelarPartida(int id) {
        this.games.remove(id);
    }

    public synchronized ServerGame crearJuego(ServerClient primerJugador, ServerClient segundoJugador) {
        int id = this.idCount++;

        //Creamos el juego y lo añadimos al registro
        ServerGame serverGame = new ServerGame(this, id, primerJugador, segundoJugador);
        serverGame.unirCliente(primerJugador);
        serverGame.unirCliente(segundoJugador);

        //Registramos el juego
        this.games.put(id, serverGame);

        //Iniciamos el juego tras un pequeño delay
        getTaskExecutor().runTask(serverGame::iniciarJuego, 5);

        return serverGame;
    }
}
