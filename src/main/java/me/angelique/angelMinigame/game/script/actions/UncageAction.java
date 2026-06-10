package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.Map;
import java.util.UUID;

public class UncageAction implements Action {
    @Override public String getType() { return "uncage"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        for (Map.Entry<UUID, Location> entry : new java.util.LinkedHashMap<>(session.getCagedPlayers()).entrySet()) {
            Location loc = entry.getValue();
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++)
                    for (int dy = 0; dy <= 2; dy++)
                        loc.getWorld().getBlockAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz).setType(Material.AIR, false);
            session.getCagedPlayers().remove(entry.getKey());
        }
    }
}
