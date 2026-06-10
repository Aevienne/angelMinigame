package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import java.util.Map;

public class EntityClearAction implements Action {
    @Override public String getType() { return "entity_clear"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        String typeName = params.getOrDefault("type", "ALL").toString().toUpperCase();
        for (Entity entity : world.getEntities()) {
            if (!arena.isInRegion(entity.getLocation())) continue;
            if (entity instanceof org.bukkit.entity.Player) continue;
            if (typeName.equals("ALL") || entity.getType().name().equalsIgnoreCase(typeName)) {
                entity.remove();
            }
        }
    }
}
