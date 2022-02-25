package me.matamor.hundirlaflota.conexion.defaults;

import me.matamor.hundirlaflota.conexion.Packet;
import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.ColoredMessage;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;
import me.matamor.hundirlaflota.util.bytes.ByteBufferException;

import java.awt.*;

public class MessagePacket implements Packet {

    private ColoredMessage message;

    public MessagePacket() {

    }

    public MessagePacket(String message) {
        this.message = new ColoredMessage(message);
    }

    public MessagePacket(String message, Color color) {
        this.message = new ColoredMessage(message, color);
    }

    public MessagePacket(Message message) {
        this.message = new ColoredMessage(message.getText(), message.getColor());
    }

    public MessagePacket(ColoredMessage message) {
        this.message = message;
    }

    public ColoredMessage getMessage() {
        return this.message;
    }

    @Override
    public void write(ByteBuff byteBuff) throws PacketException {
        try {
            this.message.write(byteBuff);
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido escribir el mensaje!", e);
        }
    }

    @Override
    public void read(ByteBuff byteBuff) throws PacketException {
        try {
            this.message = new ColoredMessage(byteBuff);
        } catch (ByteBufferException e) {
            throw new PacketException("No se ha podido leer el mensaje!", e);
        }
    }
}
