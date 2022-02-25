package me.matamor.hundirlaflota.cliente.interfaz.connect;

import me.matamor.hundirlaflota.cliente.ClientConnectionData;
import me.matamor.hundirlaflota.cliente.ClientConnection;
import me.matamor.hundirlaflota.util.Callback;
import me.matamor.hundirlaflota.util.Constantes;

import javax.swing.*;
import java.awt.*;

public class GameConnection extends JPanel {

    private final JLabel direccion_label;
    private final JTextField direccion;
    private final JLabel puerto_label;
    private final JTextField puerto;
    private final JLabel usuario_label;
    private final JTextField usuario;
    private final JLabel password_label;
    private final JTextField password;

    private final JButton conectar;

    public GameConnection(Callback<ClientConnection> callback) {
        super(new GridBagLayout());

        setPreferredSize(new Dimension(250, 300));
        setMinimumSize(getPreferredSize());

        this.direccion_label = new JLabel("Dirección", SwingConstants.CENTER);
        this.direccion = new JTextField("localhost");
        this.direccion.setHorizontalAlignment(JTextField.CENTER);

        this.puerto_label = new JLabel("Puerto", SwingConstants.CENTER);
        this.puerto = new JTextField("503", SwingConstants.CENTER);
        this.puerto.setHorizontalAlignment(JTextField.CENTER);

        this.usuario_label = new JLabel("Usuario", SwingConstants.CENTER);
        this.usuario = new JTextField(SwingConstants.CENTER);
        this.usuario.setHorizontalAlignment(JTextField.CENTER);

        this.password_label = new JLabel("Password", SwingConstants.CENTER);
        this.password = new JTextField(SwingConstants.CENTER);
        this.password.setHorizontalAlignment(JTextField.CENTER);

        this.conectar = new JButton("Conectar");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(3, 3, 3, 3);

        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 1;

        constraints.gridy = 1;
        add(this.direccion_label, constraints);
        constraints.gridy = 2;
        add(this.direccion, constraints);

        constraints.gridy = 3;
        add(this.puerto_label, constraints);
        constraints.gridy = 4;
        add(this.puerto, constraints);

        constraints.gridy = 5;
        add(this.usuario_label, constraints);
        constraints.gridy = 6;
        add(this.usuario, constraints);

        constraints.gridy = 7;
        add(this.password_label, constraints);
        constraints.gridy = 8;
        add(this.password, constraints);

        constraints.gridy = 9;
        add(this.conectar, constraints);

        this.conectar.addActionListener(a -> {
            //Desactivamos el botón para evitar intentar conectarse varias veces
            toggle(false);

            String direccion = this.direccion.getText();
            if (direccion.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes escribir la dirección del servidor!");
                toggle(true);
                return;
            }

            String rawPuerto = this.puerto.getText();
            if (rawPuerto.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes el puerto del servidor!");
                toggle(true);
                return;
            }

            int puerto;
            try {
                puerto = Integer.parseInt(rawPuerto);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El puerto introducido no es un número valido!");
                toggle(true);
                return;
            }

            String username = this.usuario.getText();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes escribir tu nombre de usuario!");
                toggle(true);
                return;
            }

            String password = this.password.getText();
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes escribir tu contraseña de usuario!");
                toggle(true);
                return;
            }

            ClientConnection clientConnection = new ClientConnection(new ClientConnectionData(direccion, puerto, username, password));
            clientConnection.openConnection((value, e) -> {
                if (value != null && e == null) {
                    JOptionPane.showMessageDialog(null, value.getMessage());

                    if (value.isSuccessful()) {
                        //Creamos el server handler
                        callback.callback(clientConnection, null);
                    } else {
                        clientConnection.close();
                        toggle(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No se ha podido abrir la conexión. Revisa los datos del servidor!");
                    toggle(true);
                }

                if (e != null && Constantes.DEBUG) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void toggle(boolean value) {
        this.direccion.setEnabled(value);
        this.puerto.setEnabled(value);
        this.usuario.setEnabled(value);
        this.conectar.setEnabled(value);
    }

    public static void main(String[] args) {
        GameConnection gameInterface = new GameConnection(new Callback<ClientConnection>() {
            @Override
            public void callback(ClientConnection value, Exception e) {

            }
        });

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(gameInterface);
        frame.pack();
        frame.setVisible(true);
    }
}
