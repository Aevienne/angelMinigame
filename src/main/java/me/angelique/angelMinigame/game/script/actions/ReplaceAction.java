package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Material;
import org.bukkit.World;
import java.util.Map;

public class ReplaceAction implements Action {
    @Override public String getType() { return "replace"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        Material from = Material.matchMaterial(params.getOrDefault("from", "AIR").toString());
        Material to = Material.matchMaterial(params.getOrDefault("to", "AIR").toString());
        if (from == null || to == null) return;

        for (int x = arena.getMinX(); x <= arena.getMaxX(); x++)
            for (int z = arena.getMinZ(); z <= arena.getMaxZ(); z++)
                for (int y = arena.getMinY(); y <= arena.getMaxY(); y++)
                    if (world.getBlockAt(x, y, z).getType() == from)
                        world.getBlockAt(x, y, z).setType(to, false);
    }
}
