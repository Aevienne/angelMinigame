package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class ClearEffectsAction implements Action {
    @Override public String getType() { return "clear_effects"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        for (Player p : session.getOnlineAlivePlayers())
            for (var pe : p.getActivePotionEffects()) p.removePotionEffect(pe.getType());
    }
}
