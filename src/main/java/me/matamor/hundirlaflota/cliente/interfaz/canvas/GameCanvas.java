package me.matamor.hundirlaflota.cliente.interfaz.canvas;

import me.matamor.hundirlaflota.juego.tablero.Casilla;
import me.matamor.hundirlaflota.juego.tablero.Tablero;
import me.matamor.hundirlaflota.util.ColoredMessage;
import me.matamor.hundirlaflota.util.Constantes;

import javax.swing.*;
import java.awt.*;

public class GameCanvas extends JPanel {

    private static final double START_SPACE_WIDTH = 0.075;
    private static final double START_SPACE_HEIGHT = 0.1;

    private static final double END_SPACE_WIDTH = 0.05;
    private static final double END_SPACE_HEIGHT = 0.05;

    private ColoredMessage title;
    private Tablero tablero;

    public GameCanvas() {
        setMinimumSize(new Dimension(400, 400));
        setPreferredSize(getMinimumSize());
    }

    public ColoredMessage getTitle() {
        return this.title;
    }

    public boolean tieneTitle() {
        return this.title != null;
    }

    public void setTitle(ColoredMessage title) {
        this.title = title;
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    public boolean tieneTablero() {
        return this.tablero != null;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public void update() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D canvas = (Graphics2D) g;

        int maxWidth = getWidth();
        int maxHeight = getHeight();

        canvas.setColor(Color.WHITE);
        canvas.fillRect(0, 0, maxWidth, maxHeight);

        canvas.setColor(Color.GRAY);
        canvas.drawRect(0, 0, maxWidth - 1, maxHeight - 1);

        int startSpaceWidth = (int) (maxWidth * START_SPACE_WIDTH);
        int startSpaceHeight = (int) (maxHeight * START_SPACE_HEIGHT);

        int endSpaceWidth = (int) (maxWidth * END_SPACE_WIDTH);
        int endSpaceHeight = (int) (maxHeight * END_SPACE_HEIGHT);

        if (tieneTitle()) {
            //Guardamos la fuente anterior
            Font oldFold = canvas.getFont();

            //Cambiamos la fuente
            canvas.setColor(Color.BLUE);
            canvas.setFont(new Font(canvas.getFont().getName(), Font.BOLD, 12));

            //Tamaño del título
            int titleWidth = canvas.getFontMetrics().stringWidth(this.title.getText());

            //Calculamos la mitad de width para luego restarle la mitad del tamaño del título
            int middleWidth = maxWidth / 2;
            int titleStartWidth = middleWidth - (titleWidth / 2);

            //La altura del titulo
            int height = canvas.getFontMetrics().getHeight();

            //Dibujamos el titulo
            canvas.setColor(this.title.getColor());
            canvas.drawString(this.title.getText(), titleStartWidth, height);

            //Volvemos a la fuente anterior
            canvas.setFont(oldFold);

            //Cambiamos el color al anterior
            canvas.setColor(Color.BLACK);
        }

        int width = maxWidth - startSpaceWidth - endSpaceWidth;
        int height = maxHeight - startSpaceHeight - endSpaceHeight;

        int casillas = Constantes.LONGITUD;

        int widthCasilla = width / casillas;
        int heightCasilla = height / casillas;

        int heightMargin = (int) (height * 0.01);
        int widthMargin = (int) (width * 0.01);

        //Dibujamos las casillas
        for (int x = 0; casillas > x; x++) {
            int startX = (x * widthCasilla) + startSpaceWidth + widthMargin;
            int endX = startX + widthCasilla;

            int differenceX = endX - startX;
            int middleX = (differenceX / 2) + startX;

            int letraY = startSpaceHeight - (int) (maxHeight * 0.01);

            String letra = String.valueOf(x + 1);
            int letraSize = canvas.getFontMetrics().stringWidth(letra);

            //Dibujamos el número de la casilla
            canvas.setColor(Color.BLACK);
            canvas.drawString(String.valueOf(x + 1), middleX - (letraSize / 2), letraY);

            for (int y = 0; casillas > y; y++) {
                int startY = (y * heightCasilla) + startSpaceHeight;

                canvas.setColor(Color.BLACK);
                canvas.drawRect(startX, startY, widthCasilla, heightCasilla);

                //Comprobamos si tiene un tablero
                if (tieneTablero()) {
                    //Leemos la casilla
                    Casilla casilla = this.tablero.buscarCasilla(x, y);
                    Color color = null;

                    if (casilla.isTocado()) {
                        color = Color.RED;
                    } else if (casilla.isAtacado()) {
                        color = Color.GRAY;
                    } else if (casilla.tieneBarco()) {
                        color = Color.BLUE;
                    }

                    if (color != null) {
                        canvas.setColor(color);
                        canvas.fillRect(startX + widthMargin, startY + heightMargin, widthCasilla - (widthMargin * 2), heightCasilla - (heightMargin * 2));
                    }
                }
            }
        }

        String letras = Constantes.LETRAS;
        canvas.setColor(Color.BLACK);

        for (int y = 0; casillas > y; y++) {
            int startY = (y * heightCasilla) + startSpaceHeight;

            int middleY = startY + (heightCasilla / 2);
            int letraHeight = canvas.getFontMetrics().getHeight();

            String letra = String.valueOf(letras.charAt(y));
            int letraWidth = canvas.getFontMetrics().stringWidth(letra);

            int letraX = (startSpaceWidth / 2) - (letraWidth / 2);
            int letraY = middleY + (letraHeight / 2);

            canvas.drawString(String.valueOf(letras.charAt(y)), letraX, letraY);
        }
    }
}
