package me.matamor.hundirlaflota.servidor.game.player.stats;

import java.util.HashMap;
import java.util.Map;

public class GameStats {

    public enum GameStat {

        DISPAROS,
        ACIERTOS

    }

    private final Map<GameStat, Integer> stats;

    public GameStats() {
        this.stats = new HashMap<>();

        for (GameStat stat : GameStat.values()) {
            this.stats.put(stat, 0);
        }
    }

    public void setStat(GameStat stat, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value can't be negative!");
        }

        this.stats.put(stat, value);
    }

    public void increaseStat(GameStat stat) {
        setStat(stat, getStat(stat) + 1);
    }

    public int getStat(GameStat stat) {
        return this.stats.getOrDefault(stat, 0);
    }

    public Map<GameStat, Integer> getStats() {
        return this.stats;
    }
}
