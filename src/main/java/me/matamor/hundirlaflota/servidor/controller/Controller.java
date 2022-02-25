package me.matamor.hundirlaflota.servidor.controller;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketManager;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.util.Constantes;

import java.util.Collection;

public interface Controller {

    /**
     * @return el packet manager del controlador
     */

    PacketManager<ServerClient> getPacketManager();

    /**
     * Añade un cliente servidor al controlador
     * @param serverClient el cliente a añadir
     */

    void unirCliente(ServerClient serverClient);

    /**
     * Se ejecuta cuando un cliente se conecta al controlador
     * @param serverClient el cliente que se ha conectado
     */

    default void onJoin(ServerClient serverClient) {

    }

    /**
     * @return los clientes registrados en el controlador
     */

    Collection<ServerClient> getClientes();

    /**
     * Elimina un cliente del controlador
     * @param serverClient el cliente a eliminar
     */

    void sacarCliente(ServerClient serverClient);

    void sacarClientes();

    boolean tieneCliente(String username);

    /*
        Ejecutamos los handlers del packet
     */

    boolean executeHandlers(ServerClient serverClient, Packet packet);


    /**
     * Se ejecuta cuando un cliente sale del controlador
     * @param serverClient el cliente que ha salido
     */

    default void onExit(ServerClient serverClient) {

    }

    /**
     * Se ejecuta cuando da un error al interactuar con un cliente
     * si el metodo devuelve true el usuario será quitado del controlador
     * al quitar el usuario del controlador también se llama al metodo Controlador#onQuit()
     * @param serverClient el cliente
     * @return true si se quiere quitar el cliente
     */

    default boolean onError(ServerClient serverClient, Exception e) {
        //En caso de haber un error al leer el packet mostramos el error
        e.printStackTrace();
        return true;
    }

    /**
     * Se ejecuta cada vez que el controlador hace un tick
     */

    default void onTick() {

    }

    /**
     * Se ejecuta cuando se recibe un paquete para el cual no hay ningún listener registrado
     * @param serverClient el cliente que ha enviado el paquete
     * @param packet el paquete que no tiene listener
     */

    default boolean onInvalidPacket(ServerClient serverClient, Packet packet) {
        if (Constantes.DEBUG) {
            System.out.println("Paquete recibido sin handler: " + packet.getClass().getSimpleName());
        }

        return false;
    }
}
