package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Map;

public class TeleportAction implements Action {
    @Override public String getType() { return "teleport"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        String dest = params.getOrDefault("to", "spawn").toString();
        Arena arena = session.getArena();
        for (Player p : session.getOnlineAlivePlayers()) {
            Location loc = null;
            switch (dest) {
                case "lobby": loc = arena.getLobbySpawn(); break;
                case "spectator": loc = arena.getSpectatorSpawn(); break;
                case "spawn":
                    var spawns = arena.getPlayerSpawns();
                    if (!spawns.isEmpty()) loc = spawns.get(0);
                    break;
                default:
                    if (params.containsKey("x")) {
                        double x = arena.getMinX() + Double.parseDouble(params.get("x").toString());
                        double y = arena.getMinY() + Double.parseDouble(params.getOrDefault("y", "0").toString());
                        double z = arena.getMinZ() + Double.parseDouble(params.getOrDefault("z", "0").toString());
                        loc = new Location(arena.getWorld(), x, y, z);
                    }
            }
            if (loc != null) p.teleport(loc);
        }
    }
}
