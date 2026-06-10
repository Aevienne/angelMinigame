package me.angelique.angelMinigame.game.modes;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.ArenaSnapshot;
import me.angelique.angelMinigame.game.GameMode;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TntRunMode extends GameMode {

    private final Map<UUID, Location> lastStepLocation = new HashMap<>();
    private final Set<Location> scheduledRemoval = new HashSet<>();

    public TntRunMode() { super("TNT_RUN"); }

    @Override
    public void onStart(GameSession session) {
        lastStepLocation.clear();
        scheduledRemoval.clear();
    }

    @Override
    public boolean onBlockBreak(Player player, Block block, GameSession session) {
        return true;
    }

    @Override
    public void onPlayerMove(Player player, GameSession session) {
        if (!session.isAlive(player.getUniqueId())) return;
        Location blockLoc = player.getLocation().getBlock().getLocation();
        UUID uuid = player.getUniqueId();
        Location last = lastStepLocation.get(uuid);

        if (last != null && last.equals(blockLoc)) return;

        Location currentBlock = blockLoc.clone();
        Block b = currentBlock.getBlock();
        Material type = b.getType();
        if (session.getArena().getBreakableBlocks().contains(type) && !scheduledRemoval.contains(currentBlock)) {
            scheduledRemoval.add(currentBlock);
            AngelMinigame plugin = AngelMinigame.getInstance();
            int delay = plugin.getConfig().getInt("tnt-run.disappear-ticks", 10);
            new BukkitRunnable() {
                @Override public void run() {
                    Location above = currentBlock.clone().add(0, -1, 0);
                    if (currentBlock.getBlock().getType() == type) {
                        currentBlock.getBlock().setType(Material.AIR, false);
                    }
                    scheduledRemoval.remove(currentBlock);
                }
            }.runTaskLater(plugin, delay);
        }

        lastStepLocation.put(uuid, currentBlock);

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
        lastStepLocation.clear();
        scheduledRemoval.clear();
    }
}
