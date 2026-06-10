package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import java.util.Map;

public class SpawnEntityAction implements Action {
    @Override public String getType() { return "spawn_entity"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        String typeName = params.getOrDefault("type", "ZOMBIE").toString().toUpperCase();
        EntityType type;
        try { type = EntityType.valueOf(typeName); } catch (IllegalArgumentException e) { return; }
        int amount = params.containsKey("amount") ? Integer.parseInt(params.get("amount").toString()) : 1;
        double cx = arena.getMinX() + (arena.getMaxX() - arena.getMinX()) / 2.0;
        double cz = arena.getMinZ() + (arena.getMaxZ() - arena.getMinZ()) / 2.0;
        double cy = arena.getMinY() + 2;
        for (int i = 0; i < amount; i++) {
            double ox = (Math.random() - 0.5) * (arena.getMaxX() - arena.getMinX()) * 0.8;
            double oz = (Math.random() - 0.5) * (arena.getMaxZ() - arena.getMinZ()) * 0.8;
            world.spawnEntity(new Location(world, cx + ox, cy, cz + oz), type);
        }
    }
}
