package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import java.util.Map;

public class FillAction implements Action {
    @Override public String getType() { return "fill"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        Material mat = Material.matchMaterial(params.getOrDefault("material", "AIR").toString());
        if (mat == null) return;
        String region = params.getOrDefault("region", "whole").toString();

        int minX, maxX, minZ, maxZ, minY, maxY;
        if ("whole".equals(region)) {
            minX = arena.getMinX(); maxX = arena.getMaxX();
            minZ = arena.getMinZ(); maxZ = arena.getMaxZ();
            minY = arena.getMinY(); maxY = arena.getMaxY();
        } else if ("layer".equals(region)) {
            minX = arena.getMinX(); maxX = arena.getMaxX();
            minZ = arena.getMinZ(); maxZ = arena.getMaxZ();
            minY = maxY = getInt(params, "y", arena.getMinY());
            if (params.containsKey("offset")) {
                int off = getInt(params, "offset", 0);
                minY += off; maxY += off;
            }
        } else {
            return;
        }

        for (int x = minX; x <= maxX; x++)
            for (int z = minZ; z <= maxZ; z++)
                for (int y = minY; y <= maxY; y++)
                    world.getBlockAt(x, y, z).setType(mat, false);
    }

    private int getInt(Map<String, Object> params, String key, int def) {
        Object o = params.getOrDefault(key, def);
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); } catch (NumberFormatException e) { return def; }
    }
}
