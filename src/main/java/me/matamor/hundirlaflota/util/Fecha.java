package me.matamor.hundirlaflota.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Fecha {

    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    private final int dia;
    private final int mes;
    private final int anyo;

    private Date date;

    private Fecha(int dia, int mes, int anyo) {
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
    }

    /**
     * @return el día de la Fecha
     */

    public int getDia() {
        return this.dia;
    }

    /**
     * @return el mes de la Fecha
     */

    public int getMes() {
        return this.mes;
    }

    /**
     * @return el año de la Fecha
     */

    public int getAnyo() {
        return this.anyo;
    }

    /**
     * Comprueba si la fecha definida es menor que esta fecha
     * @param fecha la fecha a comprobar
     * @return true si la fecha definida es menor
     */

    public boolean esMayor(Fecha fecha) {
        if (this.anyo > fecha.anyo) {
            return true;
        }

        if (fecha.anyo == this.anyo) {
            if (this.mes > fecha.mes) {
                return true;
            }

            if (fecha.mes == this.mes) {
                return this.dia > fecha.dia;
            }
        }

        return false;
    }

    /**
     * Comprueba si la fecha es mayor o igual a esta
     * @param fecha la fecha a comprobar
     * @return true si la fecha es mayor o igual a esta
     */

    public boolean esMayorIgual(Fecha fecha) {
        return esMayor(fecha) || equals(fecha);
    }

    /**
     * Comprueba si la fecha definida es mayor que esta fecha
     * @param fecha la fecha a comprobar
     * @return true si la fecha definida es mayor
     */

    public boolean esMenor(Fecha fecha) {
        if (this.anyo < fecha.anyo) {
            return true;
        }

        if (fecha.anyo == this.anyo) {
            if (this.mes < fecha.mes) {
                return true;
            }

            if (fecha.mes == this.mes) {
                return this.dia < fecha.dia;
            }
        }

        return false;
    }

    /**
     * Comprueba si una fecha es menor o igual a esta
     * @param fecha la fecha a comprobar
     * @return true si la fecha es menor o igual a esta
     */

    public boolean esMenorIgual(Fecha fecha) {
        return esMenor(fecha) || equals(fecha);
    }

    /**
     * Convierte la Fecha a Date
     * @return la Date
     * @throws FechaException si no se puede convertir la Fecha a Date
     */

    public Date toDate() throws FechaException {
        if (this.date == null) {
            try {
                this.date = formatoFecha.parse(toString());
            } catch (ParseException e) {
                throw new FechaException("No se ha podido convertir la Fecha a Date!", e);
            }
        }

        return this.date;
    }

    @Override
    public String toString() {
        return this.dia + "/" + this.mes + "/" + this.anyo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj.getClass() != getClass()) {
            return false;
        }

        Fecha fecha = (Fecha) obj;
        return this.dia == fecha.dia && this.mes == fecha.mes && this.anyo == fecha.anyo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dia, this.mes, this.anyo);
    }

    public static class FechaException extends RuntimeException {

        public FechaException(String message) {
            super(message);
        }

        public FechaException(String message, Exception exception) {
            super(message, exception);
        }
    }

    public static final int DAY_MIN = 1;
    public static final int DAY_MAX = 31;

    public static final int MONTH_MIN = 1;
    public static final int MONTH_MAX = 12;

    public static final int YEAR_MIN = 1;
    public static final int YEAR_MAX = 3000;

    /**
     * Valida el parametro y crea una Fecha
     * @param rawDate la fecha sin valida
     * @return una Fecha valida
     * @throws FechaException si la Fecha no es valida
     */

    public static Fecha parse(String rawDate) throws FechaException {
        String[] partes = rawDate.split("/");

        if (partes.length != 3) {
            throw new FechaException("Formato de fecha invalido! Formato valido: dia/mes/año");
        } else {
            int[] parsed = new int[partes.length];

            for (int i = 0; parsed.length > i; i++) {
                String parte = partes[i];

                try {
                    parsed[i] = Integer.parseInt(parte);
                } catch (NumberFormatException e) {
                    throw new FechaException("El valor '" + parte + "' no es número valido!");
                }
            }

            return parse(parsed[0], parsed[1], parsed[2]);
        }
    }

    /**
     * Valida los parametros y crea una Fecha
     * @param dia el día de la fecha
     * @param mes el mes de la fecha
     * @param anyo el año de la fecha
     * @return la Fecha ya creada y comprobada
     * @throws FechaException si los parametros no son validos
     */

    public static Fecha parse(int dia, int mes, int anyo) throws FechaException {
        if (dia < DAY_MIN || dia > DAY_MAX) {
            throw new FechaException("El día está fuera del rango valido!");
        }

        if (mes < MONTH_MIN || mes > MONTH_MAX) {
            throw new FechaException("El mes está fuera del rango valido!");
        }

        if (anyo < YEAR_MIN || anyo > YEAR_MAX) {
            throw new FechaException("El año está fuera del rango valido!");
        }

        int diasDelMes = diasDelMes(mes, anyo);
        if (dia > diasDelMes) {
            throw new IllegalArgumentException("El día está fuera del rango del mes valido!");
        }

        return new Fecha(dia, mes, anyo);
    }

    /**
     * Comprueba si un año es bisiesto
     * @param anyo el año a comprobar
     * @return true si el año es bisiesto
     */

    public static boolean esBisiesto(int anyo) {
        if (anyo % 4 == 0) {
            if (anyo % 100 == 0) {
                return anyo % 400 == 0;
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve la cantidad de días del mes, depende del año ya que puede variar si este es bisiesto
     * @param mes el mes a comprobar
     * @param year el año del mes a comprobar
     * @return la cantidad de días del mes según el año
     * @throws FechaException si el mes está fuera del rango valido
     */

    public static int diasDelMes(int mes, int year) throws FechaException {
        switch (mes) {
            case 1: //Enero
                return 31;
            case 2: //Febrero
                return (esBisiesto(year) ? 29 : 28);
            case 3: //Marzo
                return 31;
            case 4: //Abril
                return 30;
            case 5: //Mayo
                return 31;
            case 6: //Junio
                return 30;
            case 7: //Julio
                return 31;
            case 8: //Agosto
                return 31;
            case 9: //Septiembre
                return 30;
            case 10: //Octubre
                return 31;
            case 11: //Noviembre
                return 30;
            case 12: //Diciembre
                return 31;
            default:
                throw new FechaException("El mes es menor que 1 o mayor que 12!");
        }
    }

    /**
     * Devuelve la fecha de hoy
     * @return la fecha de hoy
     */

    public static Fecha hoy() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        //Los meses en calendar va de 0 a 11, así que debemos sumar 1 para convertilo a nuestro sistema
        return new Fecha(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    public static void main(String[] args) {
        System.out.println(hoy());
    }
}
