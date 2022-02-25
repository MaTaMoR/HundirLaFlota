package me.matamor.hundirlaflota.util.bytes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteBuff {

    private byte[] content;
    private int currentPosition;

    public ByteBuff() {
        this(0);
    }

    public ByteBuff(int bytes) {
        this.content = new byte[bytes];
    }

    public ByteBuff(byte[] content) {
        this.content = content;
        this.currentPosition = content.length;
    }

    public ByteBuff writeInt(int value) {
        return writeBytes(ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
    }

    public ByteBuff writeLong(long value) {
        return writeBytes(ByteBuffer.allocate(Long.BYTES).putLong(value).array());
    }

    public ByteBuff writeBoolean(boolean value) {
        return writeBytes(new byte[] { (value ? (byte) 1 : (byte) 0) });
    }

    public ByteBuff writeString(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        return writeInt(bytes.length).writeBytes(bytes);
    }

    public int length() {
        return this.currentPosition;
    }

    public int readInt() {
        if (this.content.length < Integer.BYTES) {
            throw new IllegalStateException("Not enough bytes to read an Int!");
        }

        byte[] content = Arrays.copyOfRange(this.content, 0, Integer.BYTES);

        removeBytes(Integer.BYTES);

        return ByteBuffer.wrap(content).getInt();
    }

    public long readLong() {
        if (this.content.length < Long.BYTES) {
            throw new IllegalStateException("Not enough bytes to read an Int!");
        }

        byte[] content = Arrays.copyOfRange(this.content, 0, Long.BYTES);

        removeBytes(Long.BYTES);

        return ByteBuffer.wrap(content).getLong();
    }

    public boolean readBoolean() {
        if (this.content.length < 1) {
            throw new IllegalStateException("Not enough bytes to read an Boolean!");
        }

        byte value = this.content[0];

        removeBytes(1);

        return (value == 1);
    }

    public String readString() {
        int size = readInt();

        if (this.content.length < size) {
            throw new IllegalStateException("Not enough bytes to read a String!");
        }

        byte[] content = Arrays.copyOfRange(this.content, 0, size);

        removeBytes(size);

        return new String(content, StandardCharsets.UTF_8);
    }

    public ByteBuff writeBytes(byte[] bytes) {
        //First we need to know how many more bytes are left free in our array
        int remainingBytes = this.content.length - this.currentPosition;
        //Now we get how many extra bytes we need
        int neededBytes = bytes.length - remainingBytes;

        //If we need any more bytes we have to expand our array
        if (neededBytes > 0) {
            byte[] content = new byte[this.content.length + neededBytes];
            System.arraycopy(this.content, 0, content, 0, this.content.length);

            this.content = content;
        }

        //Now we can finally add the extra bytes
        System.arraycopy(bytes, 0, this.content, this.currentPosition, bytes.length);

        this.currentPosition += bytes.length;

        return this;
    }

    private ByteBuff removeBytes(int bytes) {
        if (bytes > this.content.length) {
            throw new IllegalStateException("Can't remove more bytes than the actual content!");
        }

        this.content = Arrays.copyOfRange(this.content, bytes, this.content.length);
        this.currentPosition -= bytes;

        return this;
    }

    public byte[] toArray() {
        return Arrays.copyOfRange(this.content, 0, this.currentPosition);
    }
}
