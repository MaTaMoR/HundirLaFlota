package me.matamor.hundirlaflota.color;

public enum Color {

    NEGRO(0),
    ROJO(1),
    VERDE(2),
    AMARILLO(3),
    AZUL(4),
    MAGENTA(5),
    CYAN(6),
    GRIS(7);

    private static final Color[] VALUES = values();
    public static final int MAX_CODE = VALUES.length - 1;

    private final int codigo;

    Color(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return this.codigo;
    }

    public static Color getByCode(int code) {
        return (code < 0 || code > MAX_CODE ? null : VALUES[code]);
    }
}
