package me.matamor.hundirlaflota.servidor.game;

public enum ServerGameState {

    NOT_STARTED,
    WAITING,
    IN_GAME,
    FINISHED,
    CANCELLED;

    public boolean is(ServerGameState... values) {
        for (ServerGameState serverGameState : values) {
            if (this == serverGameState) {
                return true;
            }
        }

        return false;
    }
}
