package me.matamor.hundirlaflota.conexion;

import me.matamor.hundirlaflota.util.Constantes;

import java.util.*;

public class PacketManager<T extends SocketHandler> {

    private final PacketEncoder packetEncoder = new PacketEncoder();
    private final PacketDecoder packetDecoder = new PacketDecoder();

    private final Map<Class<? extends Packet>, List<PacketHandler<T>>> handlers = new LinkedHashMap<>();

    public PacketEncoder getPacketEncoder() {
        return this.packetEncoder;
    }

    public PacketDecoder getPacketDecoder() {
        return this.packetDecoder;
    }

    public <E extends Packet> void registerHandler(Class<E> packetClass, PacketHandler<T> packetHandler) {
        List<PacketHandler<T>> handlers = this.handlers.computeIfAbsent(packetClass, k -> new ArrayList<>());

        if (!handlers.contains(packetHandler)) {
            handlers.add(packetHandler);
        }
    }

    public  boolean hasHandler(Class<? extends Packet> clazz) {
        return this.handlers.containsKey(clazz);
    }

    public boolean executeHandlers(T socketHandler, Packet packet) {
        if (Constantes.DEBUG) {
            System.out.println("Trying to execute handler for packet: " + packet.getClass());
        }

        List<PacketHandler<T>> handlers = this.handlers.get(packet.getClass());
        if (handlers == null) {
            return false;
        }

        Iterator<PacketHandler<T>> iterator = handlers.iterator();
        while (iterator.hasNext()) {
            try {
                iterator.next().handle(socketHandler, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public <E extends Packet> void unregisterHandler(Class<E> packetClass, PacketHandler<T> packetHandler) {
        List<PacketHandler<T>> handlers = this.handlers.get(packetClass);

        if (handlers != null) {
            handlers.remove(packetHandler);
        }
    }

    public List<PacketHandler<T>> getHandlers(Class<? extends Packet> packetClass) {
        return this.handlers.getOrDefault(packetClass, Collections.emptyList());
    }
}
