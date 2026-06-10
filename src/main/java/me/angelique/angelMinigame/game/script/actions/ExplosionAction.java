package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.Map;

public class ExplosionAction implements Action {
    @Override public String getType() { return "explosion"; }
    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        float power = params.containsKey("power") ? Float.parseFloat(params.get("power").toString()) : 4f;
        boolean breakBlocks = !params.getOrDefault("break_blocks", "true").toString().equals("false");
        boolean setFire = params.getOrDefault("fire", "false").toString().equals("true");
        double cx = arena.getMinX() + (arena.getMaxX() - arena.getMinX()) / 2.0;
        double cy = arena.getMinY() + (arena.getMaxY() - arena.getMinY()) / 2.0;
        double cz = arena.getMinZ() + (arena.getMaxZ() - arena.getMinZ()) / 2.0;
        if (params.containsKey("x")) cx = arena.getMinX() + Double.parseDouble(params.get("x").toString());
        if (params.containsKey("y")) cy = arena.getMinY() + Double.parseDouble(params.get("y").toString());
        if (params.containsKey("z")) cz = arena.getMinZ() + Double.parseDouble(params.get("z").toString());
        world.createExplosion(new Location(world, cx, cy, cz), power, setFire, breakBlocks);
    }
}
