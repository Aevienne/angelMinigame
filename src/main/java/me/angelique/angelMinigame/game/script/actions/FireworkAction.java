package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.Map;

public class FireworkAction implements Action {
    @Override public String getType() { return "firework"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        double cx = arena.getMinX() + (arena.getMaxX() - arena.getMinX()) / 2.0;
        double cy = arena.getMinY() + (arena.getMaxY() - arena.getMinY()) / 2.0;
        double cz = arena.getMinZ() + (arena.getMaxZ() - arena.getMinZ()) / 2.0;
        Location loc = new Location(world, cx, cy + 5, cz);
        Firework fw = world.spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(Color.RED, Color.ORANGE, Color.GREEN)
            .with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }
}
