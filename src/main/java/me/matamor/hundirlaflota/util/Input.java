package me.matamor.hundirlaflota.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Input {

    public static boolean DEBUG = false;

    private static final Scanner scanner = new Scanner(System.in);

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat fullDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    public static String cancel = "cancel";

    public static void setCancel(String cancel) {
        if (cancel != null && !cancel.isEmpty()) {
            Input.cancel = cancel;
        }
    }

    public static String leerLinea() throws CancelException {
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase(cancel)) {
            throw new CancelException();
        }

        if (DEBUG) {
            System.out.println("Input: " + input);
        }

        return input;
    }

    public static String leerLinea(int min, int max) throws CancelException {
        do {
            String linea = leerLinea();
            int longitud = linea.length();

            if (min != -1 && longitud < min) {
                System.out.printf("El texto no puede medir menos de %d caracteres!\n", min);
            } else if (max != -1 && longitud > max) {
                System.out.printf("El texto no puede medir más de %d caracteres!\n", max);
            } else {
                return linea;
            }
        } while (true);
    }

    public static int leerInt() throws CancelException {
        do {
            String input = leerLinea();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("El input '" + input + "' no es un numero valido!");
            }
        } while (true);
    }

    public static int leerInt(int min, int max) throws CancelException {
        do {
            int numero = leerInt();
            
            if (min != -1 && numero < min) {
                System.out.printf("El numero no puede ser menor que %d!\n", min);
            } else if (max != -1 && numero > max) {
                System.out.printf("El numero no puede ser mayor que %d!\n", max);
            } else {
                return numero;
            }
        } while (true);
    }

    public static double leerDouble() throws CancelException {
        do {
            String input = leerLinea();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("El input '" + input + "' no es un numero valido!");
            }
        } while (true);
    }

    public static double leerDouble(double min, double max) throws CancelException {
        do {
            double numero = leerDouble();

            if (min != -1 && numero < min) {
                System.out.printf("El numero no puede ser menor que %.2f!\n", min);
            } else if (max != -1 && numero > max) {
                System.out.printf("El numero no puede ser mayor que %.2f!\n", max);
            } else {
                return numero;
            }
        } while (true);
    }

    public static boolean leerBoolean() throws CancelException {
        do {
            String input = leerLinea();

            if (Boolean.TRUE.toString().equalsIgnoreCase(input)) {
                return true;
            } else if (Boolean.FALSE.toString().equalsIgnoreCase(input)) {
                return false;
            } else {
                System.out.println("El valor '" + input + "' no es una boolean valida (true/false)!");
            }
        } while (true);
    }

    public static boolean leerPregunta() throws CancelException {
        do {
            String input = leerLinea();

            if ("si".equalsIgnoreCase(input)) {
                return true;
            } else if ("no".equalsIgnoreCase(input)) {
                return false;
            } else {
                System.out.println("El valor '" + input + "' no es una respuesta valida (si/no)!");
            }
        } while (true);
    }

    public static Fecha leerFecha() throws CancelException {
        Fecha fecha = null;

        do {
            try {
                fecha = Fecha.parse(leerLinea());
            } catch (Fecha.FechaException e) {
                System.out.println("La datos introducidos no son validos: " + e.getMessage());
            }
        } while (fecha == null);

        return fecha;
    }

    public static Fecha leerFechaSeparado() throws CancelException {
        Fecha fecha = null;

        do {
            try {
                int dia = leerInt(Fecha.DAY_MIN, Fecha.DAY_MAX);
                int mes = leerInt(Fecha.MONTH_MIN, Fecha.MONTH_MAX);
                int anyo = leerInt(Fecha.YEAR_MIN, Fecha.YEAR_MAX);

                fecha = Fecha.parse(dia, mes, anyo);
            } catch (Fecha.FechaException e) {
                System.out.println("La datos introducidos no son validos: " + e.getMessage());
            }
        } while (fecha == null);

        return fecha;
    }

    public static Date leerDate() throws CancelException {
        return leerDate(dateFormat);
    }

    public static Date leerDateHour() throws CancelException {
        return leerDate(hourFormat);
    }

    public static Date leerFullDate() throws CancelException {
        return leerDate(fullDate);
    }

    public static Date leerDate(SimpleDateFormat dateFormat) throws CancelException {
        do {
            String input = leerLinea();

            try {
                return dateFormat.parse(input);
            } catch (ParseException e) {
                System.out.printf("El valor '%s' no es una fecha valida! Formato: %s\n", input, dateFormat.toPattern());
                e.printStackTrace();
            }
        } while (true);
    }

    public static void esperarEnter() throws CancelException {
        System.out.println("\nPulsa ENTER para continuar!");
        scanner.nextLine();
    }

    public static class CancelException extends RuntimeException {

    }
}
