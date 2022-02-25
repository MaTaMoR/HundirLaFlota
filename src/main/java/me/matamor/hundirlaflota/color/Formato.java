package me.matamor.hundirlaflota.color;

public enum  Formato {

    NORMAL(0),
    BOLD(1);

    private final int codigo;

    Formato(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return this.codigo;
    }
}
