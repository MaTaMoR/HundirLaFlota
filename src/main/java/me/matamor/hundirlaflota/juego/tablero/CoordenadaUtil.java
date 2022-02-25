package me.matamor.hundirlaflota.juego.tablero;

import me.matamor.hundirlaflota.util.ConsolaDual;
import me.matamor.hundirlaflota.util.Constantes;
import me.matamor.hundirlaflota.util.Posicion;

public class CoordenadaUtil {

    private CoordenadaUtil() {

    }

    public static Posicion parseCoordenada(String coordenada) throws CoordenadaException {
        if (coordenada.length() < 2) {
            throw new CoordenadaException("Una coordenada debe tener al menos 2 caracteres!");
        }

        char letra = coordenada.charAt(0);

        int y = Constantes.LETRAS.indexOf(letra);
        if (y == -1) {
            throw new CoordenadaException("La coordenada '" + letra + "' no es valida!");
        }

        if (y >= Constantes.LONGITUD) {
            throw new CoordenadaException("La coordenada '" + letra + "' está fuera del rango!");
        }

        String numero = coordenada.substring(1);

        try {
            int x = Integer.parseInt(numero);
            if (x < 1 || y > Constantes.LONGITUD) {
                throw new CoordenadaException("La coordenada '" + y + "' está fuera del rango!");
            }

            return new Posicion(x - 1, y);
        } catch (NumberFormatException e) {
            throw new CoordenadaException("La coordenada '" + numero + "' no es valida!");
        }
    }

    public static class CoordenadaException extends RuntimeException {

        public CoordenadaException(String message) {
            super(message);
        }

        public CoordenadaException(String message, Exception e) {
            super(message, e);
        }
    }
}
