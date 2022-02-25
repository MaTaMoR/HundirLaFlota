package me.matamor.hundirlaflota.conexion;

import me.matamor.hundirlaflota.util.Constantes;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class PacketDecoder {

    public Packet decode(Protocol.DirectionProtocol directionProtocol, byte[] bytes) throws PacketException {
        ByteBuff byteBuffer = new ByteBuff(bytes);
        int packetId = byteBuffer.readInt();

        Class<? extends Packet> packetClass = directionProtocol.getById(packetId);
        if (packetClass == null) {
            if (Constantes.DEBUG) {
                System.out.println("Error al leer bytes: " + Arrays.toString(bytes));
            }

            throw new PacketException("Invalid packet id: " + packetId);
        }

        try {
            Packet packet = packetClass.getConstructor().newInstance();
            packet.read(byteBuffer);

            return packet;
        } catch (InstantiationException e) {
            throw new PacketException("La clase del packet es abstracta!", e);
        } catch (IllegalAccessException e) {
            throw new PacketException("Acceso ilegal al constructor!", e);
        } catch (InvocationTargetException e) {
            throw new PacketException("Error al ejecutar el constructor del paquete!", e);
        } catch (NoSuchMethodException e) {
            throw new PacketException("El packet no tiene un constructor predeterminado!", e);
        }
    }
}
