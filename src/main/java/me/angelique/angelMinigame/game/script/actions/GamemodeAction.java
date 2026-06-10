package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import java.util.Map;

public class GamemodeAction implements Action {
    @Override public String getType() { return "gamemode"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String mode = params.getOrDefault("mode", "ADVENTURE").toString().toUpperCase();
        try {
            GameMode gm = GameMode.valueOf(mode);
            for (Player p : session.getOnlineAlivePlayers()) p.setGameMode(gm);
        } catch (IllegalArgumentException ignored) {}
    }
}
