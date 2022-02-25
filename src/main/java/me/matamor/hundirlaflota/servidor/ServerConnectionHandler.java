package me.matamor.hundirlaflota.servidor;

import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.conexion.defaults.ConnectionPacket;
import me.matamor.hundirlaflota.conexion.defaults.ConnectionResponsePacket;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.controller.MasterController;
import me.matamor.hundirlaflota.servidor.game.ServerGameManager;
import me.matamor.hundirlaflota.servidor.lobby.SubControllerLobby;
import me.matamor.hundirlaflota.servidor.usuarios.UserRegistry;
import me.matamor.hundirlaflota.tasks.SocketPacketWaitTask;
import me.matamor.hundirlaflota.tasks.TaskExecutor;

import javax.net.ssl.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHandler extends Thread {

    private final ServerSocket serverSocket;

    private final MasterController masterController;
    private final TaskExecutor taskExecutor;
    private final SubControllerLobby controladorLobby;
    private final ServerGameManager serverGameManager;
    private final ServerInfo serverInfo;
    private final UserRegistry userRegistry;

    private int clientsId;

    public ServerConnectionHandler(ServerSocket serverSocket, ServerInfo serverInfo) {
        this.serverSocket = serverSocket;

        this.masterController = new MasterController();
        this.taskExecutor = new TaskExecutor();
        this.controladorLobby = new SubControllerLobby(this);
        this.serverGameManager = new ServerGameManager(this);
        this.serverInfo = serverInfo;
        this.userRegistry = new UserRegistry(serverInfo.getEncryptPassword());

        //Registramos 1 usuario
        this.userRegistry.registerUsuario("matamor", "1234");

        this.taskExecutor.runTask(this.masterController, 1, true);
        this.taskExecutor.start();

        this.clientsId = 0;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public MasterController getMasterController() {
        return this.masterController;
    }

    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    public SubControllerLobby getControladorLobby() {
        return this.controladorLobby;
    }

    public ServerGameManager getGameManager() {
        return this.serverGameManager;
    }

    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    public UserRegistry getUserRegistry() {
        return this.userRegistry;
    }

    @Override
    public void run() {
        while (!this.serverSocket.isClosed()) {
            try {
                Socket socket = this.serverSocket.accept();
                ServerClient serverClient = new ServerClient(this.clientsId++, socket);

                System.out.println("Conexión nueva!");

                //Esperamos la respuesta del servidor un máximo de 20 ticks
                Runnable testConnectionTask = new SocketPacketWaitTask(serverClient, 20 * 20, (packet, e) -> {
                    //Comprobamos si no hay ningún error y el packet no es null
                    if (e == null && packet != null) {
                        //Comprobamos si el packet que hemos recibido es el correcto
                        if (packet instanceof ConnectionPacket) {
                            ConnectionPacket conexionPacket = (ConnectionPacket) packet;
                            String username = conexionPacket.getUsername();

                            if (this.masterController.tieneCliente(username)) {
                                try {
                                    serverClient.sendPacket(new ConnectionResponsePacket(false, "Ya hay un usuario conectado con tu nombre!"));
                                } catch (PacketException ignored) {

                                }

                                serverClient.close();
                            } else {
                                if (this.userRegistry.validLogin(username, conexionPacket.getPassword())) {
                                    try {
                                        serverClient.sendPacket(new ConnectionResponsePacket(true, "Te has conectado correctamente!"));

                                        //Le asignamos el nombre al cliente
                                        serverClient.setUsername(username);

                                        //Registramos el cliente al registro
                                        this.masterController.unirCliente(serverClient);

                                        //Mover el cliente al lobby
                                        this.controladorLobby.unirCliente(serverClient);
                                    } catch (PacketException ex) {
                                        ex.printStackTrace();

                                        serverClient.close();
                                    }
                                } else {
                                    try {
                                        serverClient.sendPacket(new ConnectionResponsePacket(false, "Usuario o contraseña incorrecta!"));
                                    } catch (PacketException ignored) {

                                    }

                                    serverClient.close();
                                }
                            }
                        } else {
                            //Si el paquete no es el correcto desconectamos el cliente
                            serverClient.close();
                        }
                    } else if (e != null) {
                        //Si ha habido un error lo mostramos
                        e.printStackTrace();
                    } else {
                        System.out.println("close d");
                        //Si no ha llegado ninguna respuesta desconectamos el cliente
                        serverClient.close();
                    }
                });

                //Add new task??
                System.out.println("add new task");
                this.taskExecutor.runTask(testConnectionTask, 1, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
