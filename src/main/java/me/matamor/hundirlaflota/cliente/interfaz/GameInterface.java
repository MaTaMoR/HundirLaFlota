package me.matamor.hundirlaflota.cliente.interfaz;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.cliente.interfaz.canvas.GameCanvas;
import me.matamor.hundirlaflota.cliente.interfaz.chat.GameChat;
import me.matamor.hundirlaflota.cliente.interfaz.connect.GameConnection;
import me.matamor.hundirlaflota.conexion.defaults.DisconnectPacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameInterface extends JFrame {

    private final JPanel contenedor;

    private final GameConnection gameConnection;
    private final GameCanvas gameCanvasPersonal;
    private final GameCanvas gameCanvasEnemigo;
    private final GameChat gameChat;

    private ClientHandler clientHandler;

    public GameInterface() {
        super("Hundir la flota");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (clientHandler != null && clientHandler.isActive()) {
                    //Comprobamos de que el cliente realmente quiera desconectarse
                    if (JOptionPane.showConfirmDialog(GameInterface.this,
                            "¿ Estas seguro de salir ?", "Salir",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                        clientHandler.sendPacket(new DisconnectPacket("Desconexion"));

                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });

        this.contenedor = new JPanel(new GridBagLayout());
        setContentPane(this.contenedor);

        this.gameConnection = new GameConnection((cliente, e) -> {
            this.clientHandler = new ClientHandler(this, cliente.getSocketHandler(), cliente.getTaskExecutor());
            update();
        });

        this.gameCanvasPersonal = new GameCanvas();
        this.gameCanvasEnemigo = new GameCanvas();
        this.gameChat = new GameChat(this);

        update();
    }

    public boolean tieneServerHandler() {
        return this.clientHandler != null;
    }

    public ClientHandler getServerHandler() {
        return this.clientHandler;
    }

    public GameCanvas getGameCanvasPersonal() {
        return this.gameCanvasPersonal;
    }

    public GameCanvas getGameCanvasEnemigo() {
        return this.gameCanvasEnemigo;
    }

    public GameChat getGameChat() {
        return this.gameChat;
    }

    public void update() {
        this.contenedor.removeAll();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridheight = 1;
        constraints.gridwidth = 1;

        if (tieneServerHandler()) {
            if (this.clientHandler.tieneGameClient()) {
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.weightx = 1;
                constraints.weighty = 1;

                this.contenedor.add(this.gameCanvasEnemigo, constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.weightx = 1;
                constraints.weighty = 1;

                this.contenedor.add(this.gameCanvasPersonal, constraints);

                constraints.gridx = 2;
                constraints.gridy = 1;
                constraints.gridheight = 2;
                constraints.weightx = 1;
                constraints.weighty = 1;

                this.contenedor.add(this.gameChat, constraints);
            } else {
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.weightx = 1;
                constraints.weighty = 1;

                this.contenedor.add(this.gameChat, constraints);
            }
        } else {
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.weightx = 1;
            constraints.weighty = 1;

            this.contenedor.add(this.gameConnection, constraints);
        }

        this.contenedor.revalidate();
        this.contenedor.repaint();

        pack();
        setLocationRelativeTo(null);
    }
}
