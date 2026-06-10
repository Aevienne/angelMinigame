package me.angelique.angelMinigame.game.modes;

import me.angelique.angelMinigame.game.GameMode;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player;

import java.util.*;

public class ParkourMode extends GameMode {

    private final Map<UUID, Integer> checkpointIndex = new HashMap<>();
    private Location finishLocation;
    private final Set<UUID> finished = new HashSet<>();

    public ParkourMode() { super("PARKOUR"); }

    @Override
    public void onStart(GameSession session) {
        checkpointIndex.clear();
        finished.clear();
        for (UUID uuid : session.getAlivePlayers()) {
            checkpointIndex.put(uuid, -1);
        }

        Location specSpawn = session.getArena().getSpectatorSpawn();
        if (specSpawn != null) {
            finishLocation = specSpawn.clone();
        }
    }

    @Override
    public void onPlayerMove(Player player, GameSession session) {
        if (!session.isAlive(player.getUniqueId())) return;
        if (finished.contains(player.getUniqueId())) return;

        Block standingOn = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (standingOn.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || standingOn.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            UUID uuid = player.getUniqueId();
            int current = checkpointIndex.getOrDefault(uuid, -1);
            Location blockLoc = standingOn.getLocation();
            List<Location> spawns = session.getArena().getPlayerSpawns();
            int idx = -1;
            for (int i = 0; i < spawns.size(); i++) {
                if (spawns.get(i).getBlockX() == blockLoc.getBlockX()
                    && spawns.get(i).getBlockY() == blockLoc.getBlockY()
                    && spawns.get(i).getBlockZ() == blockLoc.getBlockZ()) {
                    idx = i;
                    break;
                }
            }
            if (idx > current) {
                checkpointIndex.put(uuid, idx);
                player.sendMessage("§aCheckpoint " + (idx + 1) + " reached!");
            }
        }

        if (finishLocation != null && player.getLocation().distanceSquared(finishLocation) < 4) {
            finished.add(player.getUniqueId());
        }

        if (session.getArena().isFallVoid() && player.getLocation().getBlockY() < session.getArena().getFloorLevel()) {
            session.getGameManager().onPlayerEliminated(player, session, "fell");
        }
    }

    @Override
    public Player checkWinCondition(GameSession session) {
        if (!finished.isEmpty()) {
            UUID winnerUuid = finished.iterator().next();
            return Bukkit.getPlayer(winnerUuid);
        }
        return null;
    }
}
