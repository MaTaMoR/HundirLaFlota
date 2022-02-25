package me.matamor.hundirlaflota.cliente;

import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.conexion.Protocol;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.conexion.defaults.ConnectionPacket;
import me.matamor.hundirlaflota.conexion.defaults.ConnectionResponsePacket;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.tasks.SocketPacketWaitTask;
import me.matamor.hundirlaflota.tasks.TaskExecutor;
import me.matamor.hundirlaflota.util.Callback;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

public class ClientConnection {

    private static final File CLIENT_KEY = new File("./seguridad/client/clientKey.jks");
    private static final File CLIENT_TRUST = new File("./seguridad/client/clientTrustedCerts.jks");

    private final ClientConnectionData data;
    private final TaskExecutor taskExecutor;

    private SocketHandler socketHandler;

    public ClientConnection(ClientConnectionData data) {
        this.data = data;
        this.taskExecutor = new TaskExecutor();
        this.taskExecutor.start();
    }

    public ClientConnectionData getData() {
        return this.data;
    }

    public SocketHandler getSocketHandler() {
        return this.socketHandler;
    }

    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    public void close() {
        this.taskExecutor.stopExecutor();

        if (this.socketHandler != null) {
            this.socketHandler.close();
            this.socketHandler = null;
        }
    }

    public void openConnection(Callback<ConnectionResponsePacket> callback) {
        try {
            System.out.println("Abriendo conexión...");
            Socket socket = new Socket(this.data.getAddress(), this.data.getPort());
            System.out.println("Conexión abierta.");
            this.socketHandler = new SocketHandler(Protocol.TO_CLIENT, Protocol.TO_SERVER, socket);
            System.out.println("Enviando packet de login!");
            this.socketHandler.sendPacket(new ConnectionPacket(this.data.getUsername(), this.data.getPassword()));
            System.out.println("Despues del envio");

            this.taskExecutor.runTask(new SocketPacketWaitTask(this.socketHandler, 20 * 30, (packet, e) -> {
                if (packet != null && e == null) {
                    if (packet instanceof ConnectionResponsePacket) {
                        callback.callback((ConnectionResponsePacket) packet, null);
                    } else {
                        callback.callback(null, null);
                    }
                } else {
                    callback.callback(null, e);
                }
            }), 1, true);
        } catch (IOException | PacketException e) {
            callback.callback(null, e);
            e.printStackTrace();
        }
    }
}