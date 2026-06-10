package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import java.util.Map;

public class BorderSetAction implements Action {
    @Override public String getType() { return "border_set"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        WorldBorder border = world.getWorldBorder();
        double size = params.containsKey("size") ? Double.parseDouble(params.get("size").toString()) : 100;
        double cx = arena.getMinX() + (arena.getMaxX() - arena.getMinX()) / 2.0;
        double cz = arena.getMinZ() + (arena.getMaxZ() - arena.getMinZ()) / 2.0;
        border.setCenter(cx, cz);
        border.setSize(size);
    }
}
