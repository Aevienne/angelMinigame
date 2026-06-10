package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import java.util.Map;

public class TitleAction implements Action {
    @Override public String getType() { return "title"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String title = AngelMinigame.clr(params.getOrDefault("title", "").toString());
        String subtitle = AngelMinigame.clr(params.getOrDefault("subtitle", "").toString());
        int fadeIn = params.containsKey("fade_in") ? Integer.parseInt(params.get("fade_in").toString()) : 10;
        int stay = params.containsKey("stay") ? Integer.parseInt(params.get("stay").toString()) : 40;
        int fadeOut = params.containsKey("fade_out") ? Integer.parseInt(params.get("fade_out").toString()) : 10;
        for (Player p : session.getOnlineAlivePlayers()) {
            p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
}
