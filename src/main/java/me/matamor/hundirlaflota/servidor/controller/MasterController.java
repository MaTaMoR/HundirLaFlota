package me.matamor.hundirlaflota.servidor.controller;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.conexion.PacketManager;
import me.matamor.hundirlaflota.conexion.defaults.AlivePacket;
import me.matamor.hundirlaflota.conexion.defaults.DisconnectPacket;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.servidor.handlers.DisconnectPacketHandler;
import me.matamor.hundirlaflota.util.Constantes;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MasterController implements Controller, Runnable {

    private final PacketManager<ServerClient> packetManager;

    private final Map<String, ServerClient> clientes;
    private final List<ServerClient> toAdd;
    private final List<ServerClient> toRemove;

    public MasterController() {
        this.packetManager = new PacketManager<>();

        this.clientes = new HashMap<>();
        this.toAdd = new ArrayList<>();
        this.toRemove = new ArrayList<>();

        this.packetManager.registerHandler(DisconnectPacket.class, new DisconnectPacketHandler());
    }

    @Override
    public PacketManager<ServerClient> getPacketManager() {
        return this.packetManager;
    }

    @Override
    public synchronized void unirCliente(ServerClient serverClient) {
        if (serverClient.isClosed()) {
            throw new IllegalArgumentException("No se puede añadir un cliente desconectado!");
        }

        //Comprobamos que el cliente no este en este server
        if (!this.clientes.containsKey(serverClient.getUsername()) && !this.toAdd.contains(serverClient)) {
            this.toAdd.add(serverClient);
        }
    }

    @Override
    public Collection<ServerClient> getClientes() {
        return this.clientes.values();
    }

    @Override
    public synchronized void sacarCliente(ServerClient serverClient) {
        if (this.clientes.containsKey(serverClient.getUsername()) && !this.toRemove.contains(serverClient)) {
            this.toRemove.add(serverClient);
        }
    }

    @Override
    public void sacarClientes() {
        this.clientes.values().forEach(this::sacarCliente);
    }

    @Override
    public boolean tieneCliente(String username) {
        return this.clientes.containsKey(username);
    }

    @Override
    public boolean executeHandlers(ServerClient serverClient, Packet packet) {
        if (!this.packetManager.hasHandler(packet.getClass()) && serverClient.tieneControlador()) {
            return serverClient.getControlador().executeHandlers(serverClient, packet);
        } else {
            return this.packetManager.executeHandlers(serverClient, packet);
        }
    }

    @Override
    public void onExit(ServerClient serverClient) {
        if (serverClient.tieneControlador()) {
            serverClient.getControlador().sacarCliente(serverClient);
        }
    }

    private void closeClient(ServerClient serverClient, Iterator<ServerClient> iterator, Message message) {
        if (message != null) {
            //Enviamos un paquete de desconexión, aunque si el socket está cerrado no funcionara
            try {
                serverClient.sendPacket(new DisconnectPacket(message.getText()));
            } catch (PacketException ignored) {

            }
        }

        //Cerramos al conexión y lo quitamos de la cola
        serverClient.close();
        iterator.remove();

        //Ejecutamos la salida del controlador
        onExit(serverClient);
    }


    @Override
    public void run() {
        synchronized (this.clientes) {
            //Añadimos todos los clientes que están a la espera
            synchronized (this.toAdd) {
                for (ServerClient serverClient : this.toAdd) {
                    if (!serverClient.isClosed()) {
                        this.clientes.put(serverClient.getUsername(), serverClient);

                        onJoin(serverClient);
                    }
                }

                this.toAdd.clear();
            }

            //Borramos todos los clientes que están a la espera
            synchronized (this.toRemove) {
                for (ServerClient serverClient : this.toRemove) {
                    if (this.clientes.remove(serverClient.getUsername()) != null) {
                        onExit(serverClient);
                    }
                }

                this.toRemove.clear();
            }

            Iterator<ServerClient> iterator = this.clientes.values().iterator();

            //Interactuamos con todos los clientes uno por uno
            while (iterator.hasNext()) {
                ServerClient serverClient = iterator.next();

                //Comprobamos si el jugador ha superado el máximo de ticks sin enviar el packet alive
                if (serverClient.getLastAliveTicks() == 0) {
                    try {
                        serverClient.sendPacket(new AlivePacket());
                    } catch (PacketException e) {
                        closeClient(serverClient, iterator, Message.ALIVE_KICK);
                        continue;
                    }

                    serverClient.setLastAliveTicks(Constantes.MAX_TICKS_LAST_ALIVE);
                } else {
                    //Aumentamos los ticks sin recibir el packet alive
                    serverClient.setLastAliveTicks(serverClient.getLastAliveTicks() - 1);
                }

                //Comprobamos si la conexión está cerrada
                if (serverClient.isClosed()) {
                    //Si la conexión está cerrada quitamos el cliente
                    closeClient(serverClient, iterator, null);
                } else {
                    try {
                        //Intentamos leer un packet del cliente
                        Packet packet = serverClient.readPacket();

                        //Comprobamos si el packet no es null
                        if (packet != null) {
                            //Si el packet no es null ejecutamos los handlers
                            if (!executeHandlers(serverClient, packet)) {
                                //El packet enviado no tiene ningún handler, ejecutamos el onInvalidPacket y que él decida
                                if (onInvalidPacket(serverClient, packet)) {
                                    closeClient(serverClient, iterator, Message.NO_HANDLER_PACKET);
                                }
                            }
                        }
                    } catch (PacketException e) {
                        //Dejamos que onError decida que hacer
                        if (onError(serverClient, e)) {
                            closeClient(serverClient, iterator, Message.ON_PACKET_ERROR);
                        }
                    }
                }
            }
        }
    }

    public void sendPacket(ServerClient serverClient, Packet packet) {
        try {
            serverClient.sendPacket(packet);
        } catch (PacketException e) {
            if (onError(serverClient, e)) {
                //No se ha podido enviar el paquete
                //Sacamos el cliente del controlador
                sacarCliente(serverClient);
            }
        }
    }

    public void broadcastPacket(Packet packet) {
        for (ServerClient cliente : getClientes()) {
            sendPacket(cliente, packet);
        }
    }

    public void sendMessage(ServerClient serverClient, String message, Color color) {
        sendPacket(serverClient, new MessagePacket(message, color));
    }

    public void sendMessage(ServerClient serverClient, String message) {
        sendMessage(serverClient, message, null);
    }

    public void sendMessage(ServerClient serverClient, Message message) {
        sendMessage(serverClient, message.getText(), message.getColor());
    }

    public void sendMessage(ServerClient serverClient, Message message, Object... args) {
        sendMessage(serverClient, message.getMessage(args), message.getColor());
    }

    public void broadcastMessage(String message) {
        for (ServerClient cliente : getClientes()) {
            sendMessage(cliente, message, null);
        }
    }

    public void broadcastMessage(Message message, Object... args) {
        broadcastMessage(message.getMessage(args));
    }
}
