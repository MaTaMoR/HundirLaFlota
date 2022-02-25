package me.matamor.hundirlaflota.juego.barcos;

public enum OrientacionBarco {

    VERTICAL("V"),
    HORIZONTAL("H");

    private final String key;

    OrientacionBarco(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public static OrientacionBarco buscarOrientacion(int posicion) {
        for (OrientacionBarco orientacionBarco : values()) {
            if (orientacionBarco.ordinal() == posicion) {
                return orientacionBarco;
            }
        }

        return null;
    }

    public static OrientacionBarco buscarOrientacion(String value) {
        for (OrientacionBarco orientacionBarco : values()) {
            if (orientacionBarco.name().equalsIgnoreCase(value) || orientacionBarco.getKey().equalsIgnoreCase(value)) {
                return orientacionBarco;
            }
        }

        return null;
    }
}
