package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Map;

public class EffectAction implements Action {
    @Override public String getType() { return "effect"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String name = params.getOrDefault("effect", "SPEED").toString();
        int duration = params.containsKey("duration") ? Integer.parseInt(params.get("duration").toString()) : 200;
        int amp = params.containsKey("amplifier") ? Integer.parseInt(params.get("amplifier").toString()) : 0;
        PotionEffectType type = PotionEffectType.getByName(name.toUpperCase());
        if (type == null) return;
        for (Player p : session.getOnlineAlivePlayers())
            p.addPotionEffect(new PotionEffect(type, duration, amp, false, false));
    }
}
