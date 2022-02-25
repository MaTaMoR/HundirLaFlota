package me.matamor.hundirlaflota.util;

import me.matamor.hundirlaflota.util.bytes.ByteBuff;
import me.matamor.hundirlaflota.util.bytes.ByteBufferException;
import me.matamor.hundirlaflota.util.bytes.ByteBufferSerializable;

import java.util.Objects;

public class Posicion extends ByteBufferSerializable {

    private final int x;
    private final int y;

    public Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Posicion(ByteBuff byteBuff) throws ByteBufferException {
        this.x = byteBuff.readInt();
        this.y = byteBuff.readInt();
    }

    @Override
    public void write(ByteBuff byteBuff) throws ByteBufferException {
        byteBuff.writeInt(this.x);
        byteBuff.writeInt(this.y);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Posicion relativeTo(int x, int y) {
        return new Posicion(this.x + x, this.y + y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posicion posicion = (Posicion) o;
        return x == posicion.x && y == posicion.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
