package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.World;
import java.util.Map;

public class SetWeatherAction implements Action {
    @Override public String getType() { return "set_weather"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        String weather = params.getOrDefault("weather", "CLEAR").toString().toUpperCase();
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        switch (weather) {
            case "CLEAR": world.setClearWeatherDuration(Integer.MAX_VALUE); world.setStorm(false); world.setThundering(false); break;
            case "RAIN": world.setStorm(true); world.setThundering(false); break;
            case "THUNDER": world.setStorm(true); world.setThundering(true); break;
        }
    }
}
