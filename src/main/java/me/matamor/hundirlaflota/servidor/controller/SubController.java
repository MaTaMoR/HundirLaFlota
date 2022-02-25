package me.matamor.hundirlaflota.servidor.controller;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.conexion.PacketManager;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.client.ServerClient;

import java.awt.*;
import java.util.*;

public class SubController implements Controller {

    private final PacketManager<ServerClient> packetManager;

    private final Map<String, ServerClient> clientes;

    public SubController() {
        this.packetManager = new PacketManager<>();

        this.clientes = new HashMap<>();
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

        if (!this.clientes.containsKey(serverClient.getUsername())) {
            this.clientes.put(serverClient.getUsername(), serverClient);

            serverClient.setControlador(this);

            onJoin(serverClient);
        }
    }

    @Override
    public Collection<ServerClient> getClientes() {
        return this.clientes.values();
    }

    @Override
    public synchronized void sacarCliente(ServerClient serverClient) {
        if (this.clientes.remove(serverClient.getUsername()) != null) {
            serverClient.setControlador(null);

            onExit(serverClient);
        }
    }

    @Override
    public void sacarClientes() {
        this.clientes.values().forEach(c -> {
            c.setControlador(null);
            onExit(c);
        });

        this.clientes.clear();
    }

    @Override
    public synchronized boolean tieneCliente(String username) {
        return this.clientes.containsKey(username);
    }

    @Override
    public boolean executeHandlers(ServerClient serverClient, Packet packet) {
        return serverClient.getControlador() == this && this.packetManager.executeHandlers(serverClient, packet);
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

    public void broadcastPacket(Packet packet) {
        for (ServerClient cliente : getClientes()) {
            sendPacket(cliente, packet);
        }
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
