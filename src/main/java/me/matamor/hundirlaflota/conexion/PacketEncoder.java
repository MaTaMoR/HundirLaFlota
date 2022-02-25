package me.matamor.hundirlaflota.conexion;

import me.matamor.hundirlaflota.util.bytes.ByteBuff;

public class PacketEncoder {

    public byte[] encode(Protocol.DirectionProtocol directionProtocol, Packet packet) throws PacketException {
        int id = directionProtocol.getByClass(packet.getClass());
        if (id == -1) {
            throw new PacketException("El packet no está registrado en el protocolo!");
        }

        ByteBuff byteBuff = new ByteBuff();
        byteBuff.writeInt(id);

        //Escribimos la información del packet
        packet.write(byteBuff);

        return byteBuff.toArray();
    }
}
