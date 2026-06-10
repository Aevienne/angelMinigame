package me.angelique.angelMinigame.game;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaSnapshot {

    private static AngelMinigame plugin;
    private static final ConcurrentHashMap<String, Map<Location, BlockData>> snapshots = new ConcurrentHashMap<>();

    public static void init(AngelMinigame p) { plugin = p; }

    public static void capture(GameSession session, Runnable onComplete) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        if (world == null) { if (onComplete != null) onComplete.run(); return; }

        Set<Material> targetBlocks = arena.getBreakableBlocks();
        if (targetBlocks.isEmpty()) { if (onComplete != null) onComplete.run(); return; }

        String key = arena.getName();
        Map<Location, BlockData> snapshot = new ConcurrentHashMap<>();
        snapshots.put(key, snapshot);

        int minX = arena.getMinX(), maxX = arena.getMaxX();
        int minY = arena.getMinY(), maxY = arena.getMaxY();
        int minZ = arena.getMinZ(), maxZ = arena.getMaxZ();

        List<Location> locs = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);
                    Material type = world.getBlockAt(loc).getType();
                    if (targetBlocks.contains(type)) locs.add(loc);
                }
            }
        }

        if (locs.isEmpty()) { if (onComplete != null) onComplete.run(); return; }

        int batchSize = plugin.getConfig().getInt("snapshot.batch-size", 200);
        new BukkitRunnable() {
            int index = 0;
            @Override public void run() {
                int end = Math.min(index + batchSize, locs.size());
                for (int i = index; i < end; i++) {
                    Location loc = locs.get(i);
                    Block b = world.getBlockAt(loc);
                    snapshot.put(loc.clone(), new BlockData(b.getType(), b.getState()));
                }
                index = end;
                if (index >= locs.size()) {
                    snapshots.put(key, snapshot);
                    if (onComplete != null) onComplete.run();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static void restore(GameSession session, Runnable onComplete) {
        Arena arena = session.getArena();
        World world = arena.getWorld();
        String key = arena.getName();
        Map<Location, BlockData> snapshot = snapshots.remove(key);
        if (world == null || snapshot == null || snapshot.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        List<Map.Entry<Location, BlockData>> entries = new ArrayList<>(snapshot.entrySet());
        int batchSize = plugin.getConfig().getInt("snapshot.batch-size", 200);

        new BukkitRunnable() {
            int index = 0;
            @Override public void run() {
                int end = Math.min(index + batchSize, entries.size());
                for (int i = index; i < end; i++) {
                    Map.Entry<Location, BlockData> entry = entries.get(i);
                    Location loc = entry.getKey();
                    BlockData data = entry.getValue();
                    Block b = world.getBlockAt(loc);
                    b.setType(data.type, false);
                    if (data.state != null) data.state.update(true, false);
                }
                index = end;
                if (index >= entries.size()) {
                    if (onComplete != null) onComplete.run();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static void remove(String arenaName) {
        snapshots.remove(arenaName);
    }

    public static class BlockData {
        public final Material type;
        public final BlockState state;
        public BlockData(Material type, BlockState state) {
            this.type = type;
            this.state = state;
        }
    }
}
