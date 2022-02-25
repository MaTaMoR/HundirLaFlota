package me.matamor.hundirlaflota.util;

import me.matamor.hundirlaflota.util.bytes.ByteBuff;
import me.matamor.hundirlaflota.util.bytes.ByteBufferException;
import me.matamor.hundirlaflota.util.bytes.ByteBufferSerializable;

import java.awt.*;

public class ColoredMessage extends ByteBufferSerializable {

    private final String message;
    private Color color;

    public ColoredMessage(String message) {
        this(message, null);
    }

    public ColoredMessage(String message, Color color) {
        this.message = message;
        this.color = color;
    }

    public ColoredMessage(ByteBuff byteBuff) throws ByteBufferException {
        this.message = byteBuff.readString();

        if (byteBuff.readBoolean()) {
            int red = byteBuff.readInt();
            int green = byteBuff.readInt();
            int blue = byteBuff.readInt();

            if ((red < 0 || red > 255) || (green < 0 || green > 255) || (blue < 0 || blue > 255)) {
                throw new ByteBufferException("Invalid color!");
            }

            this.color = new Color(red, green, blue);
        }
    }

    public String getText() {
        return this.message;
    }

    public boolean tieneColor() {
        return this.color != null;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public void write(ByteBuff byteBuff) throws ByteBufferException {
        byteBuff.writeString(this.message);
        byteBuff.writeBoolean(tieneColor());

        if (tieneColor()) {
            byteBuff.writeInt(this.color.getRed());
            byteBuff.writeInt(this.color.getGreen());
            byteBuff.writeInt(this.color.getBlue());
        }
    }
}
