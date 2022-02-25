package me.matamor.hundirlaflota.conexion;

import me.matamor.hundirlaflota.util.Constantes;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SocketHandler {

    private static final PacketEncoder PACKET_ENCODER = new PacketEncoder();
    private static final PacketDecoder PACKET_DECODER = new PacketDecoder();

    //El tamaño mínimo del packet es el '0' inicial, el tamaño de los bytes como segundo int y por último el ID del packet
    private static final int PACKET_MIN_SIZE = Integer.BYTES * 3;
    private static final int PACKET_INT = 0;

    private final Protocol.DirectionProtocol receiveProtocol;
    private final Protocol.DirectionProtocol sendProtocol;

    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    public SocketHandler(Protocol.DirectionProtocol receiveProtocol, Protocol.DirectionProtocol sendProtocol, Socket socket) throws IOException {
        this.socket = socket;

        this.receiveProtocol = receiveProtocol;
        this.sendProtocol = sendProtocol;

        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isClosed() {
        return this.socket == null || this.socket.isClosed();
    }

    public void close() {
        if (this.socket != null) {
            this.inputStream = null;
            this.outputStream = null;

            try {
                this.socket.close();
            } catch (IOException ignored) {

            }

            this.socket = null;
        }
    }

    public void sendPacket(Packet packet) throws PacketException {
        if (!isClosed()) {
            //Creamos el byte buff del packet
            ByteBuff byteBuff = new ByteBuff();
            byteBuff.writeInt(PACKET_INT); //Escribimos el 1 que significa que viene un packet

            //Escribimos la información del packet
            byte[] bytes = PACKET_ENCODER.encode(this.sendProtocol, packet);

            byteBuff.writeInt(bytes.length); //Escribimos el tamaño del packet
            byteBuff.writeBytes(bytes); //Escribimos los bytes

            try {
                //Escribimos toda la información
                this.outputStream.write(byteBuff.toArray());

                //Enviamos la información
                this.outputStream.flush();

                if (Constantes.DEBUG) {
                    System.out.printf("Sending packet: packet=%s, bytes=%s\n", packet.getClass().getSimpleName(), Arrays.toString(bytes));
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new PacketException("Couldn't send packet!", e);
            }
        }
    }

    private int readInt(InputStream inputStream) throws IOException, PacketException{
        byte[] bytes = new byte[Integer.BYTES];
        if (inputStream.read(bytes) != Integer.BYTES) {
            throw new PacketException("No se han podido leer todos los bytes del int!");
        }

        return ByteBuffer.wrap(bytes).getInt();
    }

    public Packet readPacket() throws PacketException {
        if (!isClosed()) {
            try {
                //Comprobamos si la cantidad de bytes disponibles es la mínima
                if (this.inputStream.available() >= PACKET_MIN_SIZE) {
                    int packetInt = readInt(this.inputStream);
                    //Comprobamos si el primer int es 0, esto significa que es un packet
                    if (packetInt == PACKET_INT) {
                        //Leemos el tamaño del packet
                        int packetSize = readInt(this.inputStream);

                        //Comprobamos que la cantidad de bytes no supere el límite
                        if (packetSize > Constantes.MAX_PACKET_SIZE) {
                            throw new PacketException("Illegal packet size: " + packetSize);
                        }

                        //Create the array to handle all the incoming bytes
                        byte[] packetBytes = new byte[packetSize];

                        //Now we start reading the packet
                        long startTime = System.currentTimeMillis();

                        //Total read bytes
                        int totalReadBytes = 0;

                        //Leemos los bytes durante 50 milliseconds como máximo
                        while (!isClosed() && totalReadBytes != packetSize && (System.currentTimeMillis() - startTime < 50)) {
                            totalReadBytes += this.inputStream.read(packetBytes);
                        }

                        //Comprobamos si hemos lido todos los bytes
                        if (totalReadBytes != packetSize) {
                            throw new PacketException("No se ha podido leer todos los bytes! " + totalReadBytes + " / " + packetSize);
                        } else {
                            //Decodificamos el packet
                            Packet packet = PACKET_DECODER.decode(this.receiveProtocol, packetBytes);

                            if (Constantes.DEBUG) {
                                System.out.println("Received packet: " + packet.getClass().getSimpleName() + " | From: " + this.socket.getInetAddress().getHostAddress());
                            }

                            return packet;
                        }
                    }
                }
            } catch (IOException e) {
                throw new PacketException("Couldn't read packet!", e);
            }
        } else {
            throw new PacketException("Connection is closed!");
        }

        return null;
    }
}