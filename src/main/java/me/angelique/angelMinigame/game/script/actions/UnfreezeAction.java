package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class UnfreezeAction implements Action {
    @Override public String getType() { return "unfreeze"; }
    @Override public void execute(GameSession session, Map<String, Object> params) {
        for (Player p : session.getOnlineAlivePlayers()) session.getFrozenPlayers().remove(p.getUniqueId());
    }
}
