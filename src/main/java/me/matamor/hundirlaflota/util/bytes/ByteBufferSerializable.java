package me.matamor.hundirlaflota.util.bytes;

public abstract class ByteBufferSerializable {

    public abstract void write(ByteBuff byteBuff) throws ByteBufferException;

    public ByteBufferSerializable() {

    }

    public ByteBufferSerializable(ByteBuff byteBuff) throws ByteBufferException {

    }

    public void ifTrue(boolean value, String message, Object... args) throws ByteBufferException {
        if (value) {
            throw new ByteBufferException(String.format(message, args));
        }
    }
}
