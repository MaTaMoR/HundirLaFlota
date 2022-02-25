package me.matamor.hundirlaflota.commands;

import me.matamor.hundirlaflota.messages.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ClientCommand {

    private final String name;
    private final String descripcion;

    private ClientCommand parent;
    private ClientCommand helpCommand;

    private final Map<String, ClientCommand> children;

    public ClientCommand(String name, String descripcion) {
        this.name = name;
        this.descripcion = descripcion;

        this.parent = null;
        this.children = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public String getLabel() {
        if (this.parent == null) {
            return "/" + this.name;
        } else {
            return getParent().getLabel() + " " + getName();
        }
    }

    public ClientCommand getParent() {
        return this.parent;
    }

    public void setParent(ClientCommand parent) {
        if (this.parent != null) {
            this.parent.children.remove(this.name);
        }

        this.parent = parent;

        if (this.parent != null) {
            this.parent.children.put(this.name, this);
        }
    }

    public ClientCommand getChild(String childName) {
        return this.children.get(childName);
    }

    public void addChildren(ClientCommand... children) {
        for (ClientCommand child : children) {
            child.setParent(this);
        }
    }

    public void removeChildren(ClientCommand... children) {
        for (ClientCommand child : children) {
            child.setParent(null);
        }
    }

    public Collection<ClientCommand> getChildren() {
        return this.children.values();
    }

    public ClientCommand getHelpCommand() {
        return this.helpCommand;
    }

    public void setHelpCommand(ClientCommand helpCommand) {
        this.helpCommand = helpCommand;
    }

    protected void ifTrue(boolean value, Message message, Object... objects) throws CommandException {
        if (value) {
            throw new CommandException(message.getMessage(objects));
        }
    }

    protected void ifFalse(boolean value, Message message, Object... objects) throws CommandException {
        if (!value) {
            throw new CommandException(message.getMessage(objects));
        }
    }

    protected void ifNull(Object object, Message message, Object... objects) throws CommandException {
        if (object == null) {
            throw new CommandException(message.getMessage(objects));
        }
    }

    protected void ifNotNull(Object object, Message message, Object... objects) throws CommandException {
        if (object != null) {
            throw new CommandException(message.getMessage(objects));
        }
    }

    protected void ifTrue(boolean value, String message, Object... objects) throws CommandException {
        if (value) {
            throw new CommandException(String.format(message, objects));
        }
    }

    protected void ifFalse(boolean value, String message, Object... objects) throws CommandException {
        if (!value) {
            throw new CommandException(String.format(message, objects));
        }
    }

    protected void ifNull(Object object, String message, Object... objects) throws CommandException {
        if (object == null) {
            throw new CommandException(String.format(message, objects));
        }
    }

    protected void ifNotNull(Object object, String message, Object... objects) throws CommandException {
        if (object != null) {
            throw new CommandException(String.format(message, objects));
        }
    }

    protected void throwMessage(String message, Object... objects) throws CommandException {
        throw new CommandException(String.format(message, objects));
    }

    public void execute(CommandData args) throws CommandException {
        if (args.length == 0 || this.children.isEmpty()) {
            try {
                if (this.helpCommand == null) {
                    onCommand(args);
                } else {
                    this.helpCommand.onCommand(args);
                }
            } catch (CommandException e) {
                args.sendMessage("Error: " + e.getMessage());
            }
        } else {
            String childName = args.getString(0);
            ClientCommand child = getChild(childName);

            if (child == null) {
                args.sendMessage("Ese sub-comando no existe!");
            } else {
                child.execute(new CommandData(args.getSender(), args.args(1)));
            }
        }
    }

    public void onCommand(CommandData info) throws CommandException {
        Collection<ClientCommand> children = getChildren();

        info.sendMessage("------------------ [" + getName() + "] ------------------");

        if (children.isEmpty()) {
            info.sendMessage("Este comando está vacio!");
        } else {
            children.forEach(c -> info.sendMessage("%s - %s", c.getLabel(), c.getDescripcion()));
        }
    }
}
