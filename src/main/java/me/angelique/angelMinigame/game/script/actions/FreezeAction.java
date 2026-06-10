package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class FreezeAction implements Action {
    @Override public String getType() { return "freeze"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        for (Player p : session.getOnlineAlivePlayers()) session.getFrozenPlayers().add(p.getUniqueId());
    }
}
