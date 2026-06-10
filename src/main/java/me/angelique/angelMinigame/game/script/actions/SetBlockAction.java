package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Material;
import org.bukkit.World;
import java.util.Map;

public class SetBlockAction implements Action {
    @Override public String getType() { return "set_block"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) return;
        Material mat = Material.matchMaterial(params.getOrDefault("material", "STONE").toString());
        if (mat == null) return;
        int x = arena.getMinX() + Integer.parseInt(params.getOrDefault("x", "0").toString());
        int y = arena.getMinY() + Integer.parseInt(params.getOrDefault("y", "0").toString());
        int z = arena.getMinZ() + Integer.parseInt(params.getOrDefault("z", "0").toString());
        world.getBlockAt(x, y, z).setType(mat, false);
    }
}
