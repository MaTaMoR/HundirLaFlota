package me.matamor.hundirlaflota.cliente.game;

public enum ClientGameState {

    PONIENDO_BARCOS,
    ESPERANDO_INICIO,
    ESPERANDO,
    ATACANDO;

    public boolean is(ClientGameState... clientGameStates) {
        for (ClientGameState clientGameState : clientGameStates) {
            if (this == clientGameState) {
                return true;
            }
        }

        return false;
    }
}
