package me.matamor.hundirlaflota.conexion.defaults;

import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.ColoredMessage;

import java.awt.*;

public class DisconnectPacket extends MessagePacket {

    public DisconnectPacket() {

    }

    public DisconnectPacket(String message) {
        super(message);
    }

    public DisconnectPacket(String message, Color color) {
        super(message, color);
    }

    public DisconnectPacket(Message message) {
        super(message);
    }

    public DisconnectPacket(ColoredMessage message) {
        super(message);
    }
}
