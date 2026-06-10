package me.angelique.angelMinigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class AngelGameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String arenaName;
    private final String mode;
    private final List<String> players;

    public AngelGameStartEvent(String arenaName, String mode, List<String> players) {
        this.arenaName = arenaName;
        this.mode = mode;
        this.players = players;
    }

    public String getArenaName() { return arenaName; }
    public String getMode() { return mode; }
    public List<String> getPlayers() { return players; }

    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
