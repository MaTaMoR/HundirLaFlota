package me.matamor.hundirlaflota.cliente;

import me.matamor.hundirlaflota.cliente.handlers.*;
import me.matamor.hundirlaflota.cliente.game.ClientGame;
import me.matamor.hundirlaflota.cliente.interfaz.GameInterface;
import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.conexion.PacketManager;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.conexion.defaults.DisconnectPacket;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.juego.packets.*;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.tasks.TaskExecutor;
import me.matamor.hundirlaflota.util.ColoredMessage;
import me.matamor.hundirlaflota.util.Constantes;

public class ClientHandler {

    private final GameInterface gameInterface;
    private final SocketHandler socketHandler;
    private final TaskExecutor taskExecutor;

    private final PacketManager<SocketHandler> packetHandler;

    private ClientGame clientGame;

    public ClientHandler(GameInterface gameInterface, SocketHandler socketHandler, TaskExecutor taskExecutor) {
        this.gameInterface = gameInterface;
        this.socketHandler = socketHandler;
        this.taskExecutor = taskExecutor;

        this.packetHandler = new PacketManager<>();

        //Registramos la task que estará leyendo del servidor
        this.taskExecutor.runTask(new ClientServerPacketReader(this), 1, true);

        //Registramos el packet handler que ejecutara los handlers para los packets
        this.packetHandler.registerHandler(MessagePacket.class, new MessagePacketHandler(this));
        this.packetHandler.registerHandler(DisconnectPacket.class, new DisconnectPacketHandler(this));
        this.packetHandler.registerHandler(GameBeginPacket.class, new GameBeginPacketHandler(this));
        this.packetHandler.registerHandler(GameStartPacket.class, new GameStartPacketHandler(this));
        this.packetHandler.registerHandler(GameTurnPacket.class, new GameTurnPacketHandler(this));
        this.packetHandler.registerHandler(GameAttackResponsePacket.class, new GameAttackResponsePacketHandler(this));
        this.packetHandler.registerHandler(GameEndPacket.class, new GameEndPacketHandler(this));
        this.packetHandler.registerHandler(GameCancelPaket.class, new GameCancelPacketHandler(this));

        this.clientGame = null;
    }

    public SocketHandler getSocketHandler() {
        return this.socketHandler;
    }

    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    public PacketManager<SocketHandler> getPacketHandler() {
        return this.packetHandler;
    }

    public void setGameClient(ClientGame clientGame) {
        this.clientGame = clientGame;

        //Actualizamos los tableros
        this.gameInterface.getGameCanvasPersonal().setTablero((tieneGameClient() ? clientGame.getTableroPersonal() : null));
        this.gameInterface.getGameCanvasEnemigo().setTablero((tieneGameClient() ? clientGame.getTableroEnemigo() : null));

        //Actualizamos los títulos
        this.gameInterface.getGameCanvasPersonal().setTitle((tieneGameClient() ? Message.TABLERO_PERSONAL.toMessage() : null));
        this.gameInterface.getGameCanvasEnemigo().setTitle((tieneGameClient() ? Message.TABLERO_ENEMIGO.toMessage() : null));

        //Actualizamos la interfaz
        this.gameInterface.update();
    }

    public boolean tieneGameClient() {
        return this.clientGame != null;
    }

    public ClientGame getGameClient() {
        return this.clientGame;
    }

    public void printMessage(String message) {
        this.gameInterface.getGameChat().printMessage(message);
    }

    public void printMessage(Message message, Object... args) {
        this.gameInterface.getGameChat().printMessage(message, args);
    }

    public void printMessage(ColoredMessage message) {
        this.gameInterface.getGameChat().printMessage(message);
    }

    public void updateInterface() {
        this.gameInterface.getGameCanvasPersonal().update();
        this.gameInterface.getGameCanvasEnemigo().update();
    }

    public void handleInput(String message) {
        if (Constantes.DEBUG) {
            System.out.println("Handle input: " + message);
        }

        if (this.clientGame == null) {
            sendPacket(new MessagePacket(message));
        } else {
            this.clientGame.handleInput(message);
        }
    }

    public void sendPacket(Packet packet) {
        if (!this.socketHandler.isClosed()) {
            try {
                this.socketHandler.sendPacket(packet);
            } catch (PacketException e) {
                //Si no podemos enviar el paquete cerramos la conexión
                stop();

                //Mostramos el error que ha ocurrido
                e.printStackTrace();
            }
        }
    }

    public boolean isActive() {
        return !this.socketHandler.isClosed() && this.taskExecutor.isActive();
    }

    public void stop() {
        if (!this.socketHandler.isClosed()) {
            this.socketHandler.close();
        }

        if (this.taskExecutor.isActive()) {
            this.taskExecutor.stopExecutor();
        }
    }
}
