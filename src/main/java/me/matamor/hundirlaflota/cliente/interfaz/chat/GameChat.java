package me.matamor.hundirlaflota.cliente.interfaz.chat;

import me.matamor.hundirlaflota.cliente.interfaz.GameInterface;
import me.matamor.hundirlaflota.conexion.defaults.MessagePacket;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.ColoredMessage;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class GameChat extends JPanel {

    private final GameInterface gameInterface;

    private final JTextPane output;
    private final JTextField input;
    private final JButton send;

    public GameChat(GameInterface gameInterface) {
        super(new GridBagLayout());

        setMinimumSize(new Dimension(400, 400));
        setPreferredSize(getMinimumSize());

        this.gameInterface = gameInterface;

        this.output = new JTextPane(new DefaultStyledDocument());
        this.output.setEditable(false);
        this.output.setMargin(new Insets(10, 10, 10, 10));

        //Hacemos que el texto siempre haga scroll all the way down
        DefaultCaret caret = (DefaultCaret) this.output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.input = new JTextField();
        this.input.setMargin(new Insets(10, 10, 10, 10));

        this.send = new JButton("Enviar");

        JScrollPane scrollPane = new JScrollPane(this.output, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 99;

        add(scrollPane, constraints);

        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 9;
        constraints.weighty = 1;

        add(this.input, constraints);

        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;

        add(this.send, constraints);

        Runnable send = () -> {
            this.input.setEditable(false);
            this.send.setEnabled(false);

            String texto = this.input.getText();
            if (texto != null && !texto.isEmpty()) {
                this.gameInterface.getServerHandler().handleInput(texto);
            }

            this.input.setText("");

            this.input.setEditable(true);
            this.send.setEnabled(true);
        };

        this.input.addActionListener(e -> send.run());
        this.send.addActionListener(e -> send.run());
    }

    public void printMessage(Message message, Object... args) {
        printMessage(message.getMessage(args), message.getColor());
    }

    public void printMessage(ColoredMessage message) {
        printMessage(message.getText(), message.getColor());
    }

    public void printMessage(String message) {
        printMessage(message, Color.BLACK);
    }

    public void printMessage(String message, Color color) {
        if (color == null) {
            color = Color.BLACK;
        }

        System.out.println(message);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        int len = this.output.getDocument().getLength(); // same value as
        // getText().length();
        try {
            this.output.getDocument().insertString(len, message + "\n", attributeSet);
        } catch (BadLocationException ignored) {

        }
    }
}
