package me.matamor.hundirlaflota.juego.tablero;

import me.matamor.hundirlaflota.color.Color;
import me.matamor.hundirlaflota.util.*;
import me.matamor.hundirlaflota.juego.barcos.Barco;
import me.matamor.hundirlaflota.juego.barcos.OrientacionBarco;
import me.matamor.hundirlaflota.color.ColorBuilder;
import me.matamor.hundirlaflota.util.bytes.ByteBuff;
import me.matamor.hundirlaflota.util.bytes.ByteBufferException;
import me.matamor.hundirlaflota.util.bytes.ByteBufferSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tablero extends ByteBufferSerializable {

    private final Casilla[][] tablero;
    private int idCount;

    private final RandomSelector<Color> colorSector;
    private final RandomSelector<Character> caracterSelector;

    private final List<Barco> barcos;

    public Tablero() {
        this.tablero = new Casilla[Constantes.LONGITUD][Constantes.LONGITUD];
        this.idCount = 0;

        this.colorSector = new RandomSelector<>(Constantes.COLORES_BARCO);
        this.caracterSelector = new RandomSelector<>(Constantes.CARACTERES_BARCO);

        this.barcos = new ArrayList<>();

        //Rellenamos el tablero con los puntos
        for (int x = 0; this.tablero.length > x; x++) {
            Casilla[] column = this.tablero[x];

            for (int y = 0; column.length > y; y++) {
                column[y] = new Casilla(this, new Posicion(x, y));
            }
        }
    }

    public Tablero(ByteBuff byteBuff) throws ByteBufferException {
        //Leemos, verificamos y asignamos longitud
        int longitudTablero = byteBuff.readInt();
        ifTrue(longitudTablero != Constantes.LONGITUD, "longitudTablero invalida!");
        this.tablero = new Casilla[longitudTablero][longitudTablero];

        //Leemos y verificamos iCount
        int idCount = byteBuff.readInt();
        System.out.println(idCount);
        ifTrue(idCount < 0 || idCount > Constantes.BARCOS, "idCount invalido!");
        this.idCount = idCount;

        //Leemos y verificamos colorPosition
        int colorPosition = byteBuff.readInt();
        ifTrue(colorPosition < 0 || colorPosition >= Constantes.COLORES_BARCO.length, "colorPosition invalido!");
        this.colorSector = new RandomSelector<>(Constantes.COLORES_BARCO, colorPosition);

        //Leemos y verificamos caracterPosition
        int caracterPosition = byteBuff.readInt();
        ifTrue(caracterPosition < 0 || caracterPosition >= Constantes.CARACTERES_BARCO.length, "caracterPosition invalido!");
        this.caracterSelector = new RandomSelector<>(Constantes.CARACTERES_BARCO, caracterPosition);

        int barcos = byteBuff.readInt();
        ifTrue(barcos < 0 || barcos > Constantes.BARCOS, "Barcos invalido!");
        this.barcos = new ArrayList<>(barcos);

        //Creamos las casillas antes de leer los barcos
        for (int x = 0; this.tablero.length > x; x++) {
            Casilla[] column = this.tablero[x];

            for (int y = 0; column.length > y; y++) {
                column[y] = new Casilla(this, new Posicion(x, y));
            }
        }

        //Leemos los barcos
        for (int i = 0; barcos > i; i++) {
            //Leemos la id del barco
            int id = byteBuff.readInt();
            ifTrue(id < 0 || id >= idCount, "id invalida!");

            //Leemos la x del barco
            int x = byteBuff.readInt();
            ifTrue(x < 0 || x >= Constantes.LONGITUD, "x invalido!");

            //Leemos la y del barco
            int y = byteBuff.readInt();
            ifTrue(y < 0 || y >= Constantes.LONGITUD, "y invalido!");

            //Leemos la longitud del barco
            int longitudBarco = byteBuff.readInt();
            ifTrue(longitudBarco < 0 || longitudBarco > Constantes.BARCOS_MAX_LONGITUD, "longitudBarco invalida!");
            ifTrue(tieneBarco(longitudBarco), "longitudBarco en uso!");

            //Leemos la orientación
            int codigoOrientacion = byteBuff.readInt();
            ifTrue(codigoOrientacion < 0 || codigoOrientacion > 2, "codigoOrientacion invalido!");
            OrientacionBarco orientacionBarco = OrientacionBarco.buscarOrientacion(codigoOrientacion);
            ifTrue(orientacionBarco == null, "orientacionBarco invalida!");

            //Leemos el codigo del color
            int codigoColor = byteBuff.readInt();
            ifTrue(codigoColor < 0 || codigoColor > Color.MAX_CODE,"codigoColor invalido!");
            Color color = Color.getByCode(codigoColor);

            //Leemos el caracter
            int caracterValue = byteBuff    .readInt();
            ifTrue(caracterValue < 0 || caracterValue > Character.MAX_VALUE, "caracterValue invalido!");
            char caracter = (char) caracterValue;
            ifTrue(Arrays.binarySearch(Constantes.CARACTERES_BARCO, caracter) == -1, "caracter invalid!");

            try {
                crearBarco(i, x, y, longitudBarco, orientacionBarco, color, caracter);
            } catch (TableroException e) {
                throw new ByteBufferException("Colisión de barco!");
            }
        }
    }

    @Override
    public void write(ByteBuff byteBuff) throws ByteBufferException {
        //Escribimos la longitud
        byteBuff.writeInt(this.tablero.length);

        //Escribimos el idCount
        byteBuff.writeInt(this.idCount);

        //Escribimos el colorPosition
        byteBuff.writeInt(this.colorSector.getPosicionInicio());

        //Escribimos el caracterPosition
        byteBuff.writeInt(this.caracterSelector.getPosicionInicio());

        //Escribimos la cantidad de barcos
        byteBuff.writeInt(this.barcos.size());

        for (Barco barco : this.barcos) {
            //Escribimos la id
            byteBuff.writeInt(barco.getId());

            //Escribimos la x
            byteBuff.writeInt(barco.getX());

            //Escribimos la y
            byteBuff.writeInt(barco.getY());

            //Escribimos el longitudBarco
            byteBuff.writeInt(barco.getLongitud());

            //Escribimos el codigoOrientacion
            byteBuff.writeInt(barco.getOrientacionBarco().ordinal());

            //Escribimos el codigoColor
            byteBuff.writeInt(barco.getColor().getCodigo());

            //Escribimos el caracterValue
            byteBuff.writeInt(barco.getCaracter());
        }
    }

    /**
     * Comprueba si la posición está dentro del rango válido del tablero
     * @param posicion la posición a comprobar
     * @return true si está en una posición válida
     */

    public boolean posicionValida(Posicion posicion) {
        return posicionValida(posicion.getX(), posicion.getY());
    }

    /**
     * Comprueba si la posición está dentro del rango válido del tablero
     * @param x,y la posición a comprobar
     * @return true si está en una posición válida
     */

    public boolean posicionValida(int x, int y) {
        return (x >= 0 && Constantes.LONGITUD > x) && (y >= 0 && Constantes.LONGITUD > y);
    }

    /**
     * Comprueba que la posición sea valida, en caso de no serlo tira una excepción
     * @param posicion la posición a comprobar
     */

    private void validarPosicion(Posicion posicion) {
        validarPosicion(posicion.getX(), posicion.getY());
    }

    /**
     * Comprueba que las coordenadas sean validas, en caso de no serlo tira una excepción
     * @param x la coordenada x
     * @param y la coordenada y
     * @throws IllegalArgumentException si las coordenadas no son validas
     */

    private void validarPosicion(int x, int y) throws IllegalArgumentException {
        if (!posicionValida(x, y)) {
            throw new IllegalArgumentException("La posición está fuera del rango valido del tablero!");
        }
    }

    /**
     * Busca la casilla en las coordenadas especificadas, pero invierte la 'x' y la 'y'
     * @param x la coordenada x
     * @param y la coordenada y
     */

    private Casilla casilla(int x, int y) {
        return this.tablero[x][y];
    }

    /**
     * Busca la casilla en las coordenadas especificadas
     * @param x la coordenada x
     * @param y la coordenada y
     */

    public Casilla buscarCasilla(int x, int y) {
        validarPosicion(x, y);

        return casilla(x, y);
    }

    /**
     * Busca el barco en la posición especificada
     * @param posicion la posición a buscar
     * @return el barco si existe uno, en caso de no haber barco devuelve null
     */

    public Barco buscarBarco(Posicion posicion)  {
        return buscarBarco(posicion.getX(), posicion.getY());
    }

    /**
     * Busca el barco en las coordenadas especificadas
     * @param x la coordenada x
     * @param y la coordenada y
     * @return  el barco si existe uno, en caso de no haber barco devuelve null
     */

    public Barco buscarBarco(int x, int y) {
        Casilla casilla = buscarCasilla(x, y);
        return (casilla.tieneBarco() ? casilla.getBarco() : null);
    }

    /**
     * Comprueba si hay un barco en las coordenadas especificadas
     * @param x la coordenada x
     * @param y la coordenada y
     * @return true si hay un barco
     */

    public boolean tieneBarco(int x, int y) {
        Casilla casilla = buscarCasilla(x, y);
        return casilla.tieneBarco();
    }

    /**
     * Comprueba si hay un barco en el tablero con la longitud especificada
     * @param longitud la longitud
     * @return true si hay un barco
     */

    public boolean tieneBarco(int longitud) {
        return this.barcos.stream().anyMatch(e -> e.getLongitud() == longitud);
    }

    /**
     * Devuelve todos los barcos registrados en el tablero
     * @return los barcos
     */

    public List<Barco> getBarcos() {
        return this.barcos;
    }

    /**
     * Crea un barco en la posición especificada
     * @param posicion la posición
     * @param longitud la longitud del barco
     * @param orientacionBarco la orientación del barco
     * @return el barco creado
     * @throws TableroException si ya hay un barco en esta posición o la posición es inválida
     */

    public Barco crearBarco(Posicion posicion, int longitud, OrientacionBarco orientacionBarco) throws TableroException {
        return crearBarco(posicion.getX(), posicion.getY(), longitud, orientacionBarco);
    }

    /**
     * Crea un barco en las coordenadas especificadas
     * @param x la coordenada x
     * @param y la coordenada y
     * @param longitud la longitud del barco
     * @param orientacionBarco la orientación del barco
     * @return el barco creado
     * @throws TableroException si ya hay un barco en esta posición o la posición es inválida
     */

    public Barco crearBarco(int x, int y, int longitud, OrientacionBarco orientacionBarco) throws TableroException {
        return crearBarco(this.idCount++, x, y, longitud, orientacionBarco, this.colorSector.random(), this.caracterSelector.random());
    }

    /**
     * Crea un barco en las coordenadas especificadas
     * @param id del barco
     * @param x la coordenada x
     * @param y la coordenada y
     * @param longitud la longitud del barco
     * @param orientacionBarco la orientación del barco
     * @return el barco creado
     * @throws TableroException si ya hay un barco en esta posición o la posición es inválida
     */

    private Barco crearBarco(int id, int x, int y, int longitud, OrientacionBarco orientacionBarco, Color color, Character caracter) throws TableroException {
        if (longitud < 1) {
            throw new TableroException("La longitud no puede ser inferior a 1!");
        }

        for (int i = 0; longitud > i; i++) {
            //Comprobamos que todas las partes del barco tienen una posición válida
            if ((orientacionBarco == OrientacionBarco.HORIZONTAL ? !posicionValida(x + i, y) : !posicionValida(x, y + i))) {
                throw new TableroException("Una o varias partes del barco tienen un posición invalida!");
            }

            //Comprobamos que ninguna parte del barco choque con otro barco
            if ((orientacionBarco == OrientacionBarco.HORIZONTAL ? tieneBarco(x + i, y) : tieneBarco(x, y + i))) {
                throw new TableroException("Una o varias partes del barco chocan con otro barco!");
            }
        }

        Posicion posicion = new Posicion(x, y);
        List<Casilla> casillas = new ArrayList<>();

        for (int i = 0; longitud > i; i++) {
            //Buscamos las casillas que ocupara el barco
            casillas.add((orientacionBarco == OrientacionBarco.HORIZONTAL ? casilla(x + i, y) : casilla(x, y + i)));
        }

        //Creamos el barco con toda la información
        Barco barco = new Barco(this, id, posicion, longitud, orientacionBarco, color, caracter, casillas);
        for (Casilla casilla : casillas) {
            casilla.setBarco(barco);
        }

        //Añadimos el barco al registro de los barcos
        this.barcos.add(barco);

        return barco;
    }

    private void append(StringBuilder tablero, char caracter, int cantidad) {
        tablero.append(String.valueOf(caracter).repeat(cantidad));
    }

    public boolean isAlive() {
        return this.barcos.stream().anyMatch(Barco::isAlive);
    }

    @Override
    public String toString() {
        StringBuilder tablero = new StringBuilder();

        //El 2 representa la suma de la letra y la barra separadora
        int espacioInicialCabecera = Constantes.ESPACIO_HORIZONTAL_CASILLA + 2;

        //Añadimos los espacios iniciales
        append(tablero, ' ', espacioInicialCabecera);

        //Ahora añadiremos todos los números de la cabecera
        for (int i = 0; Constantes.LONGITUD > i; i++) {
            //Añadimos el espacio inicial
            append(tablero, ' ', Constantes.ESPACIO_HORIZONTAL_CASILLA);

            //Si el número es mayor que el tamaño de la casilla hay que cortar el número
            String numero = String.valueOf(i + 1);
            if (numero.length() > Constantes.LONGITUD_HORIZONTAL_CASILLA) {
                numero = numero.substring(0, Constantes.LONGITUD_HORIZONTAL_CASILLA);
            }

            //Añadimos los espacios iniciales de la casilla
            int espacioInicialCasilla = (Constantes.LONGITUD_HORIZONTAL_CASILLA - numero.length()) / 2;
            if (espacioInicialCasilla > 0) {
                append(tablero, ' ', espacioInicialCasilla);
            }

            //Añadimos el número
            tablero.append(numero);

            int espacioRestanteCasilla = Constantes.LONGITUD_HORIZONTAL_CASILLA - (espacioInicialCasilla + numero.length());
            if (espacioRestanteCasilla > 0) {
                append(tablero, ' ', espacioRestanteCasilla);
            }
        }

        //Saltamos de línea
        tablero.append("\n");

        //Añadimos los espacios iniciales
        append(tablero, ' ', espacioInicialCabecera);

        //Añadimos los barras inferiores
        for (int i = 1; Constantes.LONGITUD >= i; i++) {
            append(tablero, ' ', Constantes.ESPACIO_HORIZONTAL_CASILLA);
            append(tablero, Constantes.BARRA_HORIZONTAL, Constantes.LONGITUD_HORIZONTAL_CASILLA);
        }

        //Saltamos de línea
        tablero.append("\n");

        //Ahora añadiremos todas las líneas
        for (int i = 0; Constantes.LONGITUD > i; i++) {
            //Si el número es mayor que el tamaño de la casilla hay que cortar el número
            Character letra = Constantes.LETRAS.charAt(i);

            //Añadimos los espacios iniciales de la casilla, el 1 representa el tamaño de la letra
            int espacioInicialVerticalCasilla = (Constantes.LONGITUD_VERTICAL_CASILLA - 1) / 2;
            if (espacioInicialVerticalCasilla > 0) {
                append(tablero, '\n', espacioInicialVerticalCasilla);
            }

            //Añadimos el número
            tablero.append(letra);

            //Añadimos los espacios antes de la barra
            append(tablero, ' ', Constantes.ESPACIO_HORIZONTAL_CASILLA);

            //Añadimos la barra vertical
            tablero.append(Constantes.BARRA_VERTICAL);

            //Añadimos todos los barcos de manera horizontal
            for (int x = 0; Constantes.LONGITUD > x; x++) {
                //Añadimos los espacios entre cada casilla
                append(tablero, ' ', Constantes.ESPACIO_HORIZONTAL_CASILLA);

                //Añadimos los espacios iniciales de la casilla, el 1 representa el tamaño de la letra
                int espacioInicialHorizontalCasilla = (Constantes.LONGITUD_HORIZONTAL_CASILLA - 1) / 2;
                if (espacioInicialHorizontalCasilla > 0) {
                    append(tablero, ' ', espacioInicialHorizontalCasilla);
                }

                //Buscamos la casilla en las coordenadas, 'i' representa la coordenada 'y'
                Casilla casilla = casilla(x, i);

                char caracter;
                Color color;

                //Si la casilla tiene un barco significa que es el tablero personal
                if (casilla.tieneBarco()) {
                    //Usamos el caracter propio del barco
                    caracter = casilla.getBarco().getCaracter();

                    //Comprobamos si el barco está tocado
                    if (casilla.isTocado()) {
                        //Si el barco está tocado cambiamos el color
                        color = Constantes.COLOR_BARCO_TOCADO;
                    } else {
                        color = casilla.getBarco().getColor();
                    }
                } else {
                    if (casilla.isTocado()) { //Comprobamos si la casilla ha sido tocada
                        caracter = Constantes.CARACTER_BARCO_TOCADO;
                        color = Constantes.COLOR_BARCO_TOCADO;
                    } else if (casilla.isAtacado()) {//Comprobamos si la casilla ha sido atacada
                        //Si la casilla ha sido atacado usamos un caracter y color diferente
                        caracter = Constantes.CARACTER_CASILLA_ATACADA;
                        color = Constantes.COLOR_CASILLA_ATACADA;
                    } else {
                        //Si no hay nada mostramos el mar
                        caracter = Constantes.CARACTER_MAR;
                        color = Constantes.COLOR_MAR;
                    }
                }

                //Añadimos el caracter y el color
                tablero.append(ColorBuilder.builder(color).build(caracter));

                int espacioRestanteHorizontalCasilla = Constantes.LONGITUD_HORIZONTAL_CASILLA - (espacioInicialHorizontalCasilla + 1);
                if (espacioRestanteHorizontalCasilla > 0) {
                    append(tablero, ' ', espacioRestanteHorizontalCasilla);
                }
            }

            int espacioRestanteVerticalCasilla = Constantes.LONGITUD_VERTICAL_CASILLA - (espacioInicialVerticalCasilla + 1);
            if (espacioRestanteVerticalCasilla > 0) {
                append(tablero, '\n', espacioRestanteVerticalCasilla);
            }

            //Saltamos de línea
            tablero.append("\n");

            //Añadimos el espacio vertical
            append(tablero, '\n', Constantes.ESPACIO_VERTICAL_CASILLA);
        }

        return tablero.toString();
    }

    public static void main(String[] args) {
        Tablero tablero = new Tablero();
        ConsolaDual consolaDual = ConsolaDual.consola();

        consolaDual.printConsolaIzquierda(tablero.toString());

        String input;

        do {
            consolaDual.printConsolaDerecha("Introduce coordenada:");

            input = Input.leerLinea();

            if (!input.equals("cancel")) {
                try {
                    Posicion coordenada = CoordenadaUtil.parseCoordenada(input);

                    consolaDual.printConsolaDerecha("Introduce la longitud:");
                    int longitud = Input.leerInt(0, Constantes.LONGITUD - 1);

                    try {
                        consolaDual.printConsolaDerecha("Introduce orientacion: " + Arrays.toString(OrientacionBarco.values()));

                        OrientacionBarco orientacionBarco = OrientacionBarco.buscarOrientacion(Input.leerLinea());

                        try {
                            tablero.crearBarco(coordenada, longitud, orientacionBarco);

                            consolaDual.limpiarConsolaIzquierda();
                            consolaDual.printConsolaIzquierda(tablero.toString());
                        } catch (TableroException e) {
                            consolaDual.printConsolaDerecha("Error al crear el barco: " + e.getMessage());
                        }
                    } catch (IllegalArgumentException e) {
                        consolaDual.printConsolaDerecha("Orientación invalida!");
                    }
                } catch (CoordenadaUtil.CoordenadaException e) {
                    consolaDual.printConsolaDerecha("Coordenada invalida: " + e.getMessage());
                }
            }
        } while (!input.equals("cancel"));
    }
}
