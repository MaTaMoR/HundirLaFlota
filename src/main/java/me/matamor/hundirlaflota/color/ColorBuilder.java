package me.matamor.hundirlaflota.color;

public class ColorBuilder {

    public static final String ABRIR_COLOR = "\u001B[";
    public static final String CERRAR_COLOR = "\u001B[0m";

    private Formato formato;
    private Color color;
    private Color fondo;

    public ColorBuilder() {
        this.formato = Formato.NORMAL;
        this.color = Color.NEGRO;
        this.fondo = null;
    }

    public ColorBuilder formato(Formato formato) {
        if (formato != null) {
            this.formato = formato;
        }

        return this;
    }

    public Formato getFormato() {
        return this.formato;
    }

    public ColorBuilder color(Color color) {
        if (color != null) {
            this.color = color;
        }

        return this;
    }

    public Color getColor() {
        return this.color;
    }

    public ColorBuilder fondo(Color fondo) {
        if (fondo != null) {
            this.fondo = fondo;
        }

        return this;
    }

    public Color getFondo() {
        return this.fondo;
    }

    public String build(Object input) {
        return ABRIR_COLOR + toString() + input + CERRAR_COLOR;
    }

    @Override
    public String toString() {
        // return this.formato.getCodigo() + ";" + "3" + this.color.getCodigo() + (this.fondo == null ? "m" : ";4" + this.fondo.getCodigo() + "m");

        StringBuilder stringBuilder = new StringBuilder();
        //Añadimos el formato "<formato>;"
        stringBuilder.append(this.formato.getCodigo()).append(";");

        //Añadimos el color "<formato>;3<color>"
        stringBuilder.append('3').append(this.color.getCodigo());

        //Añadimos el color de fondo si existe
        if (this.fondo == null) { //<formato>;3<color>m
            stringBuilder.append('m');
        } else { //<formato>;3<color>;4<fondo>m
            stringBuilder.append(";").append('4').append(this.fondo.getCodigo()).append('m');
        }

        return stringBuilder.toString();
    }

    public static ColorBuilder builder() {
        return new ColorBuilder();
    }

    public static ColorBuilder builder(Formato formato) {
        return new ColorBuilder().formato(formato);
    }

    public static ColorBuilder builder(Color color) {
        return new ColorBuilder().color(color);
    }

    public static ColorBuilder builder(Formato formato, Color color) {
        return new ColorBuilder().formato(formato).color(color);
    }

    public static ColorBuilder builder(Formato formato, Color color, Color fondo) {
        return new ColorBuilder().formato(formato).color(color).fondo(fondo);
    }
}
