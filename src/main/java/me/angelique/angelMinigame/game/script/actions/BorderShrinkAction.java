package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import java.util.Map;

public class BorderShrinkAction implements Action {
    @Override public String getType() { return "border_shrink"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        WorldBorder border = world.getWorldBorder();
        double size = params.containsKey("size") ? Double.parseDouble(params.get("size").toString()) : 50;
        long timeSeconds = params.containsKey("time") ? Long.parseLong(params.get("time").toString()) : 10;
        border.setSize(size, timeSeconds);
    }
}
