package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class HungerAction implements Action {
    @Override public String getType() { return "hunger"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        int amount = params.containsKey("amount") ? Integer.parseInt(params.get("amount").toString()) : 20;
        for (Player p : session.getOnlineAlivePlayers()) p.setFoodLevel(Math.max(0, Math.min(amount, 20)));
    }
}
