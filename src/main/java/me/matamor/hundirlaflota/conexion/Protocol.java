package me.matamor.hundirlaflota.conexion;

import me.matamor.hundirlaflota.conexion.defaults.*;
import me.matamor.hundirlaflota.juego.packets.GameAttackPacket;
import me.matamor.hundirlaflota.juego.packets.*;

import java.util.HashMap;
import java.util.Map;

public enum Protocol {

    CONEXION {
        {
            //Todos los paquetes básicos de cualquier conexión
            registerPacket(Direction.TO_SERVER, 1, ConnectionPacket.class);
            registerPacket(Direction.TO_CLIENT, 2, ConnectionResponsePacket.class);
            registerPacket(Direction.BIDIRECTIONAL, 3, MessagePacket.class);
            registerPacket(Direction.BIDIRECTIONAL, 4, DisconnectPacket.class);
            registerPacket(Direction.TO_CLIENT, 5, AlivePacket.class);
        }
    },
    GAME {
        {
            //Todos los paquetes relacionados con la partida
            registerPacket(Direction.TO_CLIENT, 6, GameBeginPacket.class);
            registerPacket(Direction.TO_CLIENT, 7, GameCancelPaket.class);
            registerPacket(Direction.TO_CLIENT, 8, GameStartPacket.class);
            registerPacket(Direction.TO_SERVER, 9, PlayerTableroPacket.class);
            registerPacket(Direction.TO_CLIENT, 10, GameTurnPacket.class);
            registerPacket(Direction.TO_SERVER, 11, GameAttackPacket.class);
            registerPacket(Direction.TO_CLIENT, 12, GameAttackResponsePacket.class);
            registerPacket(Direction.TO_CLIENT, 13, GameEndPacket.class);
        }
    };

    public static final DirectionProtocol TO_SERVER;
    public static final DirectionProtocol TO_CLIENT;

    static {
        TO_SERVER = new DirectionProtocol(Direction.TO_SERVER);
        TO_CLIENT = new DirectionProtocol(Direction.TO_CLIENT);

        Protocol[] protocols = values();

        for (Protocol protocol : protocols) {
            protocol.getToServer().register(TO_SERVER);
            protocol.getToClient().register(TO_CLIENT);
        }

        TO_SERVER.close();
        TO_CLIENT.close();
    }

    private final DirectionProtocol toServer;
    private final DirectionProtocol toClient;

    Protocol() {
        toServer = new DirectionProtocol(Direction.TO_SERVER);
        toClient = new DirectionProtocol(Direction.TO_CLIENT);
    }

    void registerPacket(Direction direction, int id, Class<? extends Packet> clazz) {
        if (direction == Direction.TO_SERVER || direction == Direction.BIDIRECTIONAL) {
            this.toServer.registerPacket(id, clazz);
        }

        if (direction == Direction.TO_CLIENT || direction == Direction.BIDIRECTIONAL) {
            this.toClient.registerPacket(id, clazz);
        }
    }

    public DirectionProtocol getToClient() {
        return this.toClient;
    }

    public DirectionProtocol getToServer() {
        return this.toServer;
    }

    public enum Direction {

        TO_SERVER,
        TO_CLIENT,
        BIDIRECTIONAL

    }

    public static final class DirectionProtocol {

        private final Direction direction;

        private final Map<Integer, Class<? extends Packet>> byId;
        private final Map<Class<? extends Packet>, Integer> byClass;

        private boolean canRegister;

        public DirectionProtocol(Direction direction) {
            this.direction = direction;

            this.byId = new HashMap<>();
            this.byClass = new HashMap<>();

            this.canRegister = true;
        }

        public Direction getDirection() {
            return this.direction;
        }

        private void registerPacket(int id, Class<? extends Packet> clazz) {
            if (!canRegister) {
                throw new IllegalStateException("Can't register any packet!");
            }

            if (this.byId.containsKey(id) || this.byClass.containsKey(clazz)) {
                throw new IllegalArgumentException("Packet already registered!");
            }

            this.byId.put(id, clazz);
            this.byClass.put(clazz, id);
        }

        public Class<? extends Packet> getById(int id) {
            return this.byId.get(id);
        }

        public int getByClass(Class<? extends Packet> clazz) {
            return this.byClass.getOrDefault(clazz, -1);
        }

        private void close() {
            this.canRegister = false;
        }

        private void register(DirectionProtocol directionProtocol) {
            for (Map.Entry<Integer, Class<? extends Packet>> entry : this.byId.entrySet()) {
                directionProtocol.registerPacket(entry.getKey(), entry.getValue());
            }
        }
    }
}