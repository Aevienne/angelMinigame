package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.Map;

public class CageAction implements Action {
    @Override public String getType() { return "cage"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Material mat = Material.matchMaterial(params.getOrDefault("material", "BARRIER").toString());
        if (mat == null) mat = Material.BARRIER;
        for (Player p : session.getOnlineAlivePlayers()) {
            Location loc = p.getLocation().clone();
            session.getCagedPlayers().put(p.getUniqueId(), loc.clone());
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++)
                    for (int dy = 0; dy <= 2; dy++)
                        loc.getWorld().getBlockAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz).setType(mat, false);
        }
    }
}
