package me.matamor.hundirlaflota.commands;

public class CommandException extends RuntimeException {

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Exception e) {
        super(message, e);
    }
}
