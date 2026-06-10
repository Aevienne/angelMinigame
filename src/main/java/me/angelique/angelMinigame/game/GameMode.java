package me.angelique.angelMinigame.game;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class GameMode {

    private final String name;

    protected GameMode(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void onStart(GameSession session) {}
    public void onTick(GameSession session) {}
    public boolean onBlockBreak(Player player, Block block, GameSession session) { return false; }
    public void onBlockPlace(Player player, Block block, GameSession session) {}
    public void onPlayerMove(Player player, GameSession session) {}
    public void onPlayerDeath(Player player, GameSession session) {}
    public Player checkWinCondition(GameSession session) { return null; }
    public void onEnd(GameSession session) {}
    public void onReset(GameSession session) {}
}
