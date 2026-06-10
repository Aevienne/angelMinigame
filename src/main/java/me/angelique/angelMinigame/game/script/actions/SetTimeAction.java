package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.World;
import java.util.Map;

public class SetTimeAction implements Action {
    @Override public String getType() { return "set_time"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        int time = params.containsKey("time") ? Integer.parseInt(params.get("time").toString()) : 0;
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world != null) world.setTime(time);
    }
}
