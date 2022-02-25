package me.matamor.hundirlaflota.cliente.game;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.juego.packets.GameAttackPacket;
import me.matamor.hundirlaflota.juego.packets.PlayerTableroPacket;
import me.matamor.hundirlaflota.juego.tablero.CoordenadaUtil;
import me.matamor.hundirlaflota.juego.tablero.Tablero;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.Constantes;
import me.matamor.hundirlaflota.util.Posicion;

public class ClientGame {

    private final ClientHandler clientHandler;

    private final Tablero tableroPersonal;
    private final Tablero tableroEnemigo;

    private final BarcoPlacer barcoPlacer;

    private ClientGameState gameState;

    public ClientGame(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;

        this.tableroPersonal = new Tablero();
        this.tableroEnemigo = new Tablero();

        this.barcoPlacer = new BarcoPlacer(clientHandler);

        this.gameState = ClientGameState.PONIENDO_BARCOS;
    }

    public ClientHandler getServerHandler() {
        return this.clientHandler;
    }

    public Tablero getTableroPersonal() {
        return this.tableroPersonal;
    }

    public Tablero getTableroEnemigo() {
        return this.tableroEnemigo;
    }

    public boolean isPlayerReady() {
        return this.tableroPersonal.getBarcos().size() == Constantes.BARCOS;
    }

    public ClientGameState getGameState() {
        return this.gameState;
    }

    public void setGameState(ClientGameState gameState) {
        this.gameState = gameState;
    }

    public void handleInput(String message) {
        //Comprobamos el estado del juego
        if (this.gameState.is(ClientGameState.PONIENDO_BARCOS)) {
            //Si la partida está empezando comprobamos si todavía no ha puesto todos los barcos
            this.barcoPlacer.handleInput(message);
        } else if (this.gameState.is(ClientGameState.ATACANDO)) {
            try {
                //Leemos la coordenada
                Posicion posicion = CoordenadaUtil.parseCoordenada(message);

                if (this.tableroEnemigo.buscarCasilla(posicion.getX(), posicion.getY()).isAtacado()) {
                    this.clientHandler.printMessage(Message.ALREADY_ATTACKED);
                } else {
                    //Enviamos el packet del ataque
                    this.clientHandler.sendPacket(new GameAttackPacket(posicion));

                    //Volvemos al estado de espera
                    setGameState(ClientGameState.ESPERANDO);

                    //Mostramos mensaje de ataque correcto
                    this.clientHandler.printMessage(Message.ATTACK_SENT);
                }
            } catch (CoordenadaUtil.CoordenadaException e) {
                this.clientHandler.printMessage(e.getMessage());
            }
        } else {
            System.out.println("send packet");
            this.clientHandler.sendPacket(new MessagePacket(message));
        }
    }

    public void enviarTablero() {
        if (this.gameState.is(ClientGameState.PONIENDO_BARCOS)) {
            //Cambiamos el estado del juego
            setGameState(ClientGameState.ESPERANDO_INICIO);

            //Enviamos al servidor nuestro tablero
            this.clientHandler.sendPacket(new PlayerTableroPacket(this.tableroPersonal));

            //Mostramos mensaje de tablero listo
            this.clientHandler.printMessage(Message.TABLERO_READY);
        }
    }
}
