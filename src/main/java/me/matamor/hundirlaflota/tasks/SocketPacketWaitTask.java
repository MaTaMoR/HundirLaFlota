package me.matamor.hundirlaflota.tasks;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.conexion.SocketHandler;
import me.matamor.hundirlaflota.util.Callback;

import java.io.IOException;

public class SocketPacketWaitTask extends CancellableRunnable {

    private final SocketHandler socketHandler;
    private final int maxTicks;
    private final Callback<Packet> callback;

    private int ticks;

    public SocketPacketWaitTask(SocketHandler socketHandler, int maxTicks, Callback<Packet> callback) {
        this.socketHandler = socketHandler;
        this.maxTicks = maxTicks;
        this.callback = callback;

        this.ticks = 0;
    }

    @Override
    public void run() {
        //Incrementamos los ticks que han pasado
        this.ticks = this.ticks + 1;

        try {
            Packet packet = this.socketHandler.readPacket();
            if (packet != null) {
                //Si ha llegado el packet cancelamos el runnable
                cancel();

                //Ejecutamos el callback con el resultado
                this.callback.callback(packet, null);
            }

            //Comprobamos si hemos llegado al máximo de ticks
            if (this.ticks == this.maxTicks) {
                //Hemos llegado al máximo de ticks así que cancelamos el runnable
                cancel();

                //Ejecutamos el callback sin ningún resultado
                this.callback.callback(null, null);
            }
        } catch (PacketException e) {
            //Si ocurre un error cancelamos el runnable
            cancel();

            //Ejecutamos el callback para que se sepa que ha habido un error
            this.callback.callback(null, e);
        }
    }
}
