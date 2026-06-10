package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.Bukkit;
import java.util.Map;

public class BroadcastAction implements Action {
    @Override public String getType() { return "broadcast"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String text = AngelMinigame.clr(params.getOrDefault("text", "").toString());
        Bukkit.broadcastMessage(text);
    }
}
