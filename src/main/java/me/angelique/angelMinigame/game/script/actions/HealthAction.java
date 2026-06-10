package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class HealthAction implements Action {
    @Override public String getType() { return "health"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        double amount = params.containsKey("amount") ? Double.parseDouble(params.get("amount").toString()) : 20;
        for (Player p : session.getOnlineAlivePlayers()) {
            p.setHealth(Math.min(Math.max(amount, 0.5), 20));
        }
    }
}
