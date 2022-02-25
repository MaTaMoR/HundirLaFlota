package me.matamor.hundirlaflota.juego.packets;

import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.ColoredMessage;

import java.awt.*;

public class GameCancelPaket extends MessagePacket {

    public GameCancelPaket() {

    }

    public GameCancelPaket(String message) {
        super(message);
    }

    public GameCancelPaket(String message, Color color) {
        super(message, color);
    }

    public GameCancelPaket(Message message) {
        super(message);
    }

    public GameCancelPaket(ColoredMessage message) {
        super(message);
    }
}