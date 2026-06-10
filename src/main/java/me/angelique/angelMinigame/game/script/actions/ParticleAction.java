package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.Map;

public class ParticleAction implements Action {
    @Override public String getType() { return "particle"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        String name = params.getOrDefault("particle", "FLAME").toString().toUpperCase();
        int count = params.containsKey("count") ? Integer.parseInt(params.get("count").toString()) : 20;
        Particle particle;
        try { particle = Particle.valueOf(name); } catch (IllegalArgumentException e) { return; }
        double cx = arena.getMinX() + (arena.getMaxX() - arena.getMinX()) / 2.0;
        double cy = arena.getMinY() + (arena.getMaxY() - arena.getMinY()) / 2.0;
        double cz = arena.getMinZ() + (arena.getMaxZ() - arena.getMinZ()) / 2.0;
        for (Player p : session.getOnlineAlivePlayers())
            p.spawnParticle(particle, new Location(world, cx, cy, cz), count, 2, 2, 2, 0);
    }
}
