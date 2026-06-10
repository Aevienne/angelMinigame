package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.Map;

public class SoundAction implements Action {
    @Override public String getType() { return "sound"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String name = params.getOrDefault("sound", "BLOCK_NOTE_BLOCK_PLING").toString();
        float volume = params.containsKey("volume") ? Float.parseFloat(params.get("volume").toString()) : 1f;
        float pitch = params.containsKey("pitch") ? Float.parseFloat(params.get("pitch").toString()) : 1f;
        try {
            Sound sound = Sound.valueOf(name.toUpperCase());
            for (Player p : session.getOnlineAlivePlayers()) p.playSound(p.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {}
    }
}
