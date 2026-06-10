package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;

public class LightningAction implements Action {
    @Override public String getType() { return "lightning"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        String target = params.getOrDefault("target", "random").toString();
        if ("all".equals(target)) {
            for (Player p : session.getOnlineAlivePlayers()) world.strikeLightningEffect(p.getLocation());
        } else if ("random".equals(target)) {
            var alive = new java.util.ArrayList<>(session.getAlivePlayers());
            if (!alive.isEmpty()) {
                UUID randUuid = alive.get(new java.util.Random().nextInt(alive.size()));
                Player p = Bukkit.getPlayer(randUuid);
                if (p != null) world.strikeLightningEffect(p.getLocation());
            }
        } else {
            Player p = Bukkit.getPlayer(target);
            if (p != null) world.strikeLightningEffect(p.getLocation());
        }
    }
}
