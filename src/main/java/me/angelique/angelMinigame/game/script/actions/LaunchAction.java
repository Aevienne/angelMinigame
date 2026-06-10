package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.Map;

public class LaunchAction implements Action {
    @Override public String getType() { return "launch"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        double vy = params.containsKey("velocity") ? Double.parseDouble(params.get("velocity").toString()) : 1.5;
        for (Player p : session.getOnlineAlivePlayers()) p.setVelocity(new Vector(0, vy, 0));
    }
}
