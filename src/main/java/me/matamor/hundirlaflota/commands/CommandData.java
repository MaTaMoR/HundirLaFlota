package me.matamor.hundirlaflota.commands;

import me.matamor.hundirlaflota.conexion.PacketException;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.servidor.client.ServerClient;
import me.matamor.hundirlaflota.util.CastUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

public class CommandData {

    private final ServerClient sender;

    private final String[] arguments;
    public final int length;

    public CommandData(ServerClient sender, String[] arguments) {
        this.sender = sender;
        this.arguments = arguments;
        this.length = arguments.length;
    }

    public ServerClient getSender() {
        return this.sender;
    }

    public String[] getArgs() {
        return this.arguments;
    }

    public void sendMessage(Message message, Object... args) {
        sendMessage(message.getMessage(args));
    }

    public void sendMessage(String message, Object... args) {
        sendMessage(String.format(message, args));
    }

    public void sendMessage(String message) {
        if (!this.sender.isClosed()) {
            try {
                this.sender.sendPacket(new MessagePacket(message));
            } catch (PacketException e) {
                throw new CommandException("Error al enviar el mensaje!", e);
            }
        }
    }

    public int getInt(int position) {
        return CastUtils.asInt(getString(position));
    }

    public double getDouble(int position) {
        return CastUtils.asDouble(getString(position));
    }

    public boolean getBoolean(int position) {
        return CastUtils.asBoolean(getString(position));
    }

    public String getString(int position) {
        if (position > this.length) {
            return null;
        }

        return this.arguments[position];
    }

    public String join() {
        return join(" ", 0);
    }

    public String join(String split, int start) {
        StringBuilder stringBuilder = new StringBuilder();

        IntStream.range(start, this.length).forEach(i -> stringBuilder.append(this.arguments[i]).append(split));

        return stringBuilder.toString().trim();
    }

    public String[] args(int start) {
        return Arrays.copyOfRange(this.arguments, start, this.arguments.length);
    }
}