package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Map;

public class MessageAction implements Action {
    @Override public String getType() { return "message"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String text = AngelMinigame.clr(params.getOrDefault("text", "").toString());
        String target = params.getOrDefault("target", "all").toString();
        if ("all".equals(target)) {
            session.broadcast(text);
        } else {
            Player p = Bukkit.getPlayer(target);
            if (p != null) p.sendMessage(text);
        }
    }
}
