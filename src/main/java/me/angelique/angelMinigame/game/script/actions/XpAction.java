package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class XpAction implements Action {
    @Override public String getType() { return "xp"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        int amount = params.containsKey("amount") ? Integer.parseInt(params.get("amount").toString()) : 1;
        for (Player p : session.getOnlineAlivePlayers()) p.giveExp(amount);
    }
}
