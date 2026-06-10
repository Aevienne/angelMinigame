package me.angelique.angelMinigame.game.modes;

import me.angelique.angelMinigame.game.GameMode;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CustomGameMode extends GameMode {

    public CustomGameMode() { super("CUSTOM"); }

    @Override
    public void onStart(GameSession session) {}

    @Override
    public void onTick(GameSession session) {}

    @Override
    public boolean onBlockBreak(Player player, Block block, GameSession session) {
        if (!session.isAlive(player.getUniqueId())) return true;
        if (session.getArena().getBreakableBlocks().contains(block.getType())) {
            return false;
        }
        return !session.getArena().isAllowBlockBreak();
    }

    @Override
    public void onBlockPlace(Player player, Block block, GameSession session) {
        if (!session.isAlive(player.getUniqueId())) return;
    }

    @Override
    public void onPlayerDeath(Player player, GameSession session) {
        if (session.getArena().isDeathEliminates() && session.isAlive(player.getUniqueId())) {
            session.getGameManager().onPlayerEliminated(player, session, "death");
        }
    }

    @Override
    public Player checkWinCondition(GameSession session) {
        return null;
    }
}
