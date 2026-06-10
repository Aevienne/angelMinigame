package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import java.util.Map;

public class ActionBarAction implements Action {
    @Override public String getType() { return "actionbar"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String text = AngelMinigame.clr(params.getOrDefault("text", "").toString());
        for (Player p : session.getOnlineAlivePlayers()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(text));
        }
    }
}
