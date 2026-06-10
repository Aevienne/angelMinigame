package me.angelique.angelMinigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AngelPlayerEliminatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String arenaName;
    private final String mode;
    private final String playerName;
    private final String cause;

    public AngelPlayerEliminatedEvent(String arenaName, String mode, String playerName, String cause) {
        this.arenaName = arenaName;
        this.mode = mode;
        this.playerName = playerName;
        this.cause = cause;
    }

    public String getArenaName() { return arenaName; }
    public String getMode() { return mode; }
    public String getPlayerName() { return playerName; }
    public String getCause() { return cause; }

    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
