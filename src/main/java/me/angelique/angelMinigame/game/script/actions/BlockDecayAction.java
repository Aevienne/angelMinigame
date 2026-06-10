package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import me.angelique.angelMinigame.AngelMinigame;
import java.util.Map;

public class BlockDecayAction implements Action {
    @Override public String getType() { return "block_decay"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        Material mat = Material.matchMaterial(params.getOrDefault("material", "AIR").toString());
        if (mat == null) return;
        int delay = params.containsKey("ticks") ? Integer.parseInt(params.get("ticks").toString()) : 40;

        new BukkitRunnable() {
            @Override public void run() {
                for (int x = arena.getMinX(); x <= arena.getMaxX(); x++)
                    for (int z = arena.getMinZ(); z <= arena.getMaxZ(); z++)
                        for (int y = arena.getMinY(); y <= arena.getMaxY(); y++)
                            if (world.getBlockAt(x, y, z).getType() == mat)
                                world.getBlockAt(x, y, z).setType(Material.AIR, false);
            }
        }.runTaskLater(AngelMinigame.getInstance(), delay);
    }
}
