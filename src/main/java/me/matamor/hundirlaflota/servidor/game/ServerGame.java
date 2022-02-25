package me.matamor.hundirlaflota.servidor.game;

import me.matamor.hundirlaflota.juego.packets.*;
import me.matamor.hundirlaflota.juego.tablero.Casilla;
import me.matamor.hundirlaflota.servidor.controller.SubController;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.game.handlers.GameAttackPacketHandler;
import me.matamor.hundirlaflota.servidor.game.handlers.PlayerTableroPacketHandler;
import me.matamor.hundirlaflota.servidor.game.player.ServerGamePlayer;
import me.matamor.hundirlaflota.servidor.game.player.stats.GameStats;
import me.matamor.hundirlaflota.tasks.CountdownTask;
import me.matamor.hundirlaflota.tasks.TaskExecutor;
import me.matamor.hundirlaflota.util.Constantes;
import me.matamor.hundirlaflota.util.Posicion;
import me.matamor.hundirlaflota.util.Randomizer;

import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ServerGame extends SubController {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    private final ServerGameManager serverGameManager;

    private final int id;
    private final ServerGamePlayer firstPlayer;
    private final ServerGamePlayer secondPlayer;

    private ServerGameState gameState;
    private TaskExecutor.TaskHolder currentTask;

    private long gameStart;
    private long gameEnd;

    private ServerGamePlayer currentPlayer;

    public ServerGame(ServerGameManager serverGameManager, int id, ServerClient primerJugador, ServerClient segundoJugador) {
        this.serverGameManager = serverGameManager;

        this.id = id;
        this.firstPlayer = new ServerGamePlayer(primerJugador);
        this.secondPlayer = new ServerGamePlayer(segundoJugador);

        this.gameState = ServerGameState.NOT_STARTED;
        this.currentTask = null;
        this.gameStart = 0;

        //Registramos el handler para el tablero
        getPacketManager().registerHandler(PlayerTableroPacket.class, new PlayerTableroPacketHandler(this));
        getPacketManager().registerHandler(GameAttackPacket.class, new GameAttackPacketHandler(this));
    }

    public int getId() {
        return this.id;
    }

    public ServerGamePlayer getFirstPlayer() {
        return this.firstPlayer;
    }

    public ServerGamePlayer getSecondPlayer() {
        return this.secondPlayer;
    }

    private ServerGamePlayer randomPlayer() {
        return (Randomizer.randomInt(0, 1) == 0 ? this.firstPlayer : this.secondPlayer);
    }

    private ServerGamePlayer jugadorOpuesto() {
        if (this.currentPlayer == null) {
            return null;
        }

        return (this.currentPlayer == this.firstPlayer ? this.secondPlayer : this.firstPlayer);
    }

    public boolean tieneCurrentPlayer() {
        return this.currentPlayer != null;
    }

    public ServerGamePlayer getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(ServerGamePlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean arePlayersReady() {
        return this.firstPlayer.tieneTablero() && this.secondPlayer.tieneTablero();
    }

    public ServerGameState getGameState() {
        return this.gameState;
    }

    public void setGameState(ServerGameState gameState) {
        this.gameState = gameState;
    }

    public void cancelCurrentTask() {
        if (this.currentTask != null) {
            this.currentTask.cancel();
            this.currentTask = null;
        }
    }

    private void runCurrentTask(Runnable runnable, int ticks, boolean repeatable) {
        cancelCurrentTask();

        this.currentTask = this.serverGameManager.getTaskExecutor().runTask(runnable, ticks, repeatable);
    }

    public void iniciarJuego() {
        if (getGameState().is(ServerGameState.NOT_STARTED)) {
            //Cambiamos el estado a esperando
            setGameState(ServerGameState.WAITING);

            //Game start
            this.gameStart = System.currentTimeMillis();

            //Enviamos el paquete de que el juego se ha iniciado
            broadcastPacket(new GameBeginPacket());

            //Creamos una cuenta atras, si llega a cero uno o los dos jugadores no han enviado su tablero, cancelamos la partida
            runCurrentTask(new CountdownTask(Constantes.GAME_START_WAIT, () -> {
                broadcastMessage(Message.NO_TABLERO_PLAYERS);
                cancelGame();
            }), 1, true);
        }
    }

    public void empezarJuego() {
        if (getGameState().is(ServerGameState.WAITING) && arePlayersReady()) {
            //Cambiamos el estado a en juego
            setGameState(ServerGameState.IN_GAME);

            //Cancelamos la tarea actual
            cancelCurrentTask();

            //Partida empezada
            broadcastPacket(new GameStartPacket());

            //Empezamos el primer turno
            siguienteTurnoDelayed();
        }
    }

    public void ataqueJugador(ServerGamePlayer gamePlayer, Posicion posicion) {
        if (getGameState().is(ServerGameState.IN_GAME)) {
            //Cancelamos la tarea actual
            cancelCurrentTask();

            ServerGamePlayer enemy = jugadorOpuesto();
            Casilla casilla = enemy.getTablero().buscarCasilla(posicion.getX(), posicion.getY());

            //Comprobamos que la casilla no haya sido atacada ya, no debería ser posible a no ser que se modifique el cliente
            if (!casilla.isAtacado()) {
                //Marcamos la casilla como atacada
                casilla.setAtacado(true);

                boolean tocado = casilla.isTocado();
                boolean hundido = casilla.tieneBarco() && !casilla.getBarco().isAlive();

                //Aumentamos el stat de SHOTS
                gamePlayer.getStats().increaseStat(GameStats.GameStat.DISPAROS);

                if (tocado) {
                    //Aumentamos el stat de HITS
                    gamePlayer.getStats().increaseStat(GameStats.GameStat.ACIERTOS);
                }

                //Enviamos la respuesta del ataque al atacante
                sendPacket(gamePlayer.getClientServer(), new GameAttackResponsePacket(posicion, true, tocado, hundido));

                //Enviamos la respuesta del ataque al enemigo
                sendPacket(enemy.getClientServer(), new GameAttackResponsePacket(posicion, false, tocado, hundido));

                //Comprobamos si el enemigo sigue teniendo algún barco
                if (enemy.getTablero().isAlive()) {
                    siguienteTurnoDelayed();
                } else {
                    //Enviamos el paquete de que el jugador ha ganado
                    sendPacket(gamePlayer.getClientServer(), new GameEndPacket(true));

                    //Enviamos el paquete que el jugador ha perdido
                    sendPacket(enemy.getClientServer(), new GameEndPacket(false));

                    //Terminamos la partida
                    finishGame();
                }
            }
        }
    }

    private void siguienteTurnoDelayed() {
        this.serverGameManager.getTaskExecutor().runTask(this::siguienteTurno, Constantes.MAX_TICKS * 3);
    }

    private void siguienteTurno() {
        if (getGameState().is(ServerGameState.IN_GAME) && tieneCurrentPlayer()) {
            //Ahora es el turno del rival

            //Si el jugador no tiene ningún tiro significa que es el primer turno
            if (this.currentPlayer.getStats().getStat(GameStats.GameStat.DISPAROS) > 0) {
                this.currentPlayer = jugadorOpuesto();
            }

            //Enviamos el packet al jugador diciendo que ha sido elegido
            sendPacket(this.currentPlayer.getClientServer(), new GameTurnPacket(true));

            //Le decimos al otro jugador que no es su turno
            sendPacket(jugadorOpuesto().getClientServer(), new GameTurnPacket(false));

            //Creamos una cuenta atras, si llega a cero significa que el jugador atacante no ha hecho su movimiento
            runCurrentTask(new CountdownTask(Constantes.GAME_TURN_WAIT, () -> {
                broadcastMessage(Message.NO_MOVE_PLAYER);
                cancelGame();
            }), 1, true);
        }
    }

    public void cancelGame() {
        if (getGameState().is(ServerGameState.WAITING, ServerGameState.IN_GAME)) {
            //Cancelamos la tarea actual
            cancelCurrentTask();

            //Cambiamos el estado a cancelado
            setGameState(ServerGameState.CANCELLED);

            //Enviamos un mensaje de que la partida ha sido cancelada
            broadcastPacket(new GameCancelPaket(Message.PARTIDA_CANCELADA));

            //Enviamos los jugadores al lobby
            kickPlayers();

            //Borramos la partida
            unregisterGame();
        }
    }

    private void finishGame() {
        if (getGameState().is(ServerGameState.IN_GAME)) {
            //Cambiamos el estado de la partida
            setGameState(ServerGameState.FINISHED);

            //Cancelamos la task actual
            cancelCurrentTask();

            //Guardamos el fin del juego
            this.gameEnd = System.currentTimeMillis();

            //Guardamos las estadísticas de la partida en FTP
            this.serverGameManager.getFtpGameManager().saveToFTP(createStats());

            //Enviamos los jugadores al lobby
            kickPlayers();

            //Borramos la partida
            unregisterGame();
        }
    }

    private void unregisterGame() {
        this.serverGameManager.cancelarPartida(this.id);
    }

    private void kickPlayers() {
        //Enviamos todos los jugadores que están online al lobby
        sacarClientes();
    }

    public Properties createStats() {
        if (!getGameState().is(ServerGameState.FINISHED)) {
            throw new IllegalStateException("Solo se pueden crear las estadísticas de una partida finalizada!");
        }

        Properties properties = new Properties();

        //Escribimos la fecha de inicio
        properties.setProperty("FechaInicio", DATE_FORMAT.format(this.gameStart));

        //Escribimos la fecha de fin
        properties.setProperty("FechaFin", DATE_FORMAT.format(this.gameEnd));

        //Escribimos la duración de la partida en segundos
        properties.setProperty("Duracion", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(this.gameEnd - this.gameStart)));

        //Escribimos los nombres de los 2 jugadores
        properties.setProperty("Jugador1", this.firstPlayer.getClientServer().getUsername());
        properties.setProperty("Jugador2", this.secondPlayer.getClientServer().getUsername());

        //Escribimos los disparos de los 2 jugadores
        properties.setProperty("DisparosJugador1", String.valueOf(this.firstPlayer.getStats().getStat(GameStats.GameStat.DISPAROS)));
        properties.setProperty("DisparosJugador2", String.valueOf(this.secondPlayer.getStats().getStat(GameStats.GameStat.DISPAROS)));

        //Escribimos los aciertos de los 2 jugadores
        properties.setProperty("AciertosJugador1", String.valueOf(this.firstPlayer.getStats().getStat(GameStats.GameStat.ACIERTOS)));
        properties.setProperty("AciertosJugador2", String.valueOf(this.secondPlayer.getStats().getStat(GameStats.GameStat.ACIERTOS)));

        return properties;
    }

    @Override
    public void onExit(ServerClient serverClient) {
        if (getGameState().is(ServerGameState.NOT_STARTED, ServerGameState.WAITING, ServerGameState.IN_GAME) && serverClient.isServerPlayer()) {
            serverClient.setServerPlayer(null);

            //stop game
            broadcastMessage(Message.LEFT_PARTIDA, serverClient.getUsername());

            //Si alguien se sale cancelamos la partida
            cancelGame();
        }

        //Comprobamos si el cliente sigue conectado
        if (!serverClient.isClosed()) {
            //El cliente está conectado, lo enviamos al lobby
            this.serverGameManager.getConnectionHandler().getControladorLobby().unirCliente(serverClient);
        }
    }
}
