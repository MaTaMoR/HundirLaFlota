package me.matamor.hundirlaflota.cliente.tasks;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.conexion.defaults.AlivePacket;

public class AlivePacketTask implements Runnable {

    private final ClientHandler clientHandler;

    public AlivePacketTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void run() {
        if (this.clientHandler.isActive()) {
            this.clientHandler.sendPacket(new AlivePacket());
        }
    }
}
