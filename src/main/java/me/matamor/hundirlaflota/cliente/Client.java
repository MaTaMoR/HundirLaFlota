package me.matamor.hundirlaflota.cliente;

import me.matamor.hundirlaflota.cliente.interfaz.GameInterface;

import java.awt.*;

public class Client {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GameInterface gameInterface = new GameInterface();
            gameInterface.setVisible(true);
        });
    }
}
