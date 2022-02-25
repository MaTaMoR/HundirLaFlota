package me.matamor.hundirlaflota.util;

import me.matamor.hundirlaflota.color.Color;
import me.matamor.hundirlaflota.util.bytes.BitSize;

public class Constantes {

    // Activa el debug del sistema
    public static final boolean DEBUG = false;

    // Ticks máximos del task executor
    public static final int MAX_TICKS = 20;

    public static final int MAX_TICKS_LAST_ALIVE = MAX_TICKS * 5;

    // Tamaño máximo de un paquete
    public static final int MAX_PACKET_SIZE = (int) BitSize.KILO_BYTE.toBytes(1);

    // Abecedario usado para el tablero
    public static final String LETRAS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";

    // La longitud del tablero
    public static final int LONGITUD = 10;

    // Caracter usado para representar que una casilla ha sido atacada
    public static final char CARACTER_CASILLA_ATACADA = 'O';

    // Caracter usado para representar que un barco ha sido tocado
    public static final char CARACTER_BARCO_TOCADO = 'X';

    // Color usado para representar que una casilla ha sido atacada
    public static final Color COLOR_CASILLA_ATACADA = Color.NEGRO;

    // Color usado para representar que un barco ha sido tocado
    public static final Color COLOR_BARCO_TOCADO = Color.ROJO;

    public static final char CARACTER_MAR = '*';

    public static final Color COLOR_MAR = Color.AZUL;

    // Colores validos para un barco
    public static final Color[] COLORES_BARCO = new Color[] { Color.NEGRO, Color.VERDE, Color.AMARILLO, Color.AZUL, Color.MAGENTA, Color.CYAN, Color.GRIS };

    // Caracteres validos para un barco
    public static final Character[] CARACTERES_BARCO = new Character[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'Y', 'Z' };

    public static final int ESPACIO_HORIZONTAL_CASILLA = 3;

    public static final int LONGITUD_HORIZONTAL_CASILLA = 3;

    public static final char BARRA_HORIZONTAL = '▔';

    public static final char BARRA_VERTICAL = '|';

    public static final int ESPACIO_VERTICAL_CASILLA = 1;

    public static final int LONGITUD_VERTICAL_CASILLA = 1;

    /* GAME SETTINGS */
    public static final int GAME_START_WAIT = MAX_TICKS * 60 * 10;
    public static final int GAME_TURN_WAIT = MAX_TICKS * 60 * 5;

    public static final int BARCOS = 1;

    public static final int BARCOS_MAX_LONGITUD = 5;


}
