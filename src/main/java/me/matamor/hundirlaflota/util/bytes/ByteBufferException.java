package me.matamor.hundirlaflota.util.bytes;

public class ByteBufferException extends Exception {

    public ByteBufferException(String message) {
        super(message);
    }

    public ByteBufferException(String message, Exception e) {
        super(message, e);
    }
}
