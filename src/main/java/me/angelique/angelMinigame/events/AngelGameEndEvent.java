package me.angelique.angelMinigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AngelGameEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String arenaName;
    private final String mode;
    private final String winner;
    private final long durationSeconds;

    public AngelGameEndEvent(String arenaName, String mode, String winner, long durationSeconds) {
        this.arenaName = arenaName;
        this.mode = mode;
        this.winner = winner;
        this.durationSeconds = durationSeconds;
    }

    public String getArenaName() { return arenaName; }
    public String getMode() { return mode; }
    public String getWinner() { return winner; }
    public long getDurationSeconds() { return durationSeconds; }

    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
