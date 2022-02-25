package me.matamor.hundirlaflota.util;

import java.util.Random;

public class Randomizer {

    private static final Random random = new Random();

    private static final String CHARACTERS = "qwertyuiopasdfghjklzxcvbnm";
    private static final String NUMBERS = "1234567789";

    private static final String CHARACTERS_NUMBERS = CHARACTERS + NUMBERS;

    private static final int STRING_MIN = 3;
    private static final int STRING_MAX = 10;

    public static int randomInt() {
        return random.nextInt();
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static int[] randomIntArray(int cantidad, int min, int max) {
        int[] array = new int[cantidad];

        for (int i = 0; array.length > i; i++) {
            array[i] = randomInt(min, max);
        }

        return array;
    }

    public static double randomDouble() {
        return random.nextDouble();
    }

    public static double randomDouble(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    public static double[] randomDoubleArray(int cantidad, double min, double max) {
        double[] array = new double[cantidad];

        for (int i = 0; array.length > i; i++) {
            array[i] = randomDouble(min, max);
        }

        return array;
    }

    public static String randomString() {
        return randomString(STRING_MIN, STRING_MAX);
    }

    public static String randomString(int min, int max) {
        int longitud = randomInt(min, max);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; longitud > i; i++) {
            stringBuilder.append(
                    CHARACTERS_NUMBERS.charAt(
                            randomInt(0, CHARACTERS_NUMBERS.length() - 1)));
        }

        return stringBuilder.toString();
    }

    public static String[] randomStringArray(int cantidad) {
        return randomStringArray(cantidad, STRING_MIN, STRING_MAX);
    }

    public static String[] randomStringArray(int cantidad, int min, int max) {
        String[] array = new String[cantidad];

        for (int i = 0; cantidad > i; i++) {
            array[i] = randomString(min, max);
        }

        return array;
    }


}
