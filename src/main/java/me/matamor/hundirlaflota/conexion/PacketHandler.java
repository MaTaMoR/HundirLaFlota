package me.matamor.hundirlaflota.conexion;

public interface PacketHandler<T extends SocketHandler> {

    void handle(T socketHandler, Packet packet);

}
