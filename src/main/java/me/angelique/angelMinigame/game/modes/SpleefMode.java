package me.angelique.angelMinigame.game.modes;

import me.angelique.angelMinigame.game.ArenaSnapshot;
import me.angelique.angelMinigame.game.GameMode;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SpleefMode extends GameMode {

    public SpleefMode() { super("SPLEEF"); }

    @Override
    public void onStart(GameSession session) {
        for (Player p : session.getOnlineAlivePlayers()) {
            p.setGameMode(org.bukkit.GameMode.ADVENTURE);
        }
    }

    @Override
    public boolean onBlockBreak(Player player, Block block, GameSession session) {
        if (!session.isAlive(player.getUniqueId())) return true;
        if (session.getArena().getBreakableBlocks().contains(block.getType())) {
            block.setType(Material.AIR, true);
        }
        return true;
    }

    @Override
    public void onPlayerMove(Player player, GameSession session) {
        if (!session.isAlive(player.getUniqueId())) return;
        int floor = session.getArena().getFloorLevel();
        if (player.getLocation().getBlockY() < floor) {
            session.getGameManager().onPlayerEliminated(player, session, "fell");
        }
    }

    @Override
    public void onEnd(GameSession session) {
        if (session.getArena().isRestoreOnEnd()) {
            ArenaSnapshot.restore(session, () -> {});
        }
    }
}
