package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class DamageAction implements Action {
    @Override public String getType() { return "damage"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        double amount = params.containsKey("amount") ? Double.parseDouble(params.get("amount").toString()) : 1;
        for (Player p : session.getOnlineAlivePlayers()) p.damage(amount);
    }
}
