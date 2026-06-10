package me.angelique.angelMinigame.arena;

import me.angelique.angelMinigame.AngelMinigame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager {

    private final AngelMinigame plugin;
    private final Map<String, Arena> arenas = new LinkedHashMap<>();
    private final File arenasFile;

    public ArenaManager(AngelMinigame plugin) {
        this.plugin = plugin;
        this.arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
    }

    public void load() {
        if (!arenasFile.exists()) {
            plugin.saveResource("config/arenas.yml", false);
            if (!arenasFile.exists()) {
                try { arenasFile.createNewFile(); } catch (IOException e) {
                    plugin.getLogger().warning("Failed to create arenas.yml: " + e.getMessage());
                }
            }
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(arenasFile);
        ConfigurationSection sec = cfg.getConfigurationSection("arenas");
        if (sec == null) return;

        for (String name : sec.getKeys(false)) {
            ConfigurationSection a = sec.getConfigurationSection(name);
            if (a == null) continue;
            String mode = a.getString("mode", "CUSTOM");
            Arena arena = new Arena(name, mode);
            arena.setWorldName(a.getString("world"));
            arena.setMinPlayers(a.getInt("min-players", 2));
            arena.setMaxPlayers(a.getInt("max-players", 16));
            arena.setCountdown(a.getInt("countdown", 10));
            arena.setFloorLevel(a.getInt("floor-level", 0));
            arena.setRestoreOnEnd(a.getBoolean("restore-arena", true));
            arena.setLobbyInMainWorld(a.getBoolean("lobby-in-main-world", false));
            arena.setCurrencyReward(a.getDouble("reward.currency", 0));
            arena.getRewardCommands().addAll(a.getStringList("reward.commands"));
            arena.getAllowedCommands().addAll(a.getStringList("allowed-commands"));

            String bbs = a.getString("breakable-blocks", "");
            if (!bbs.isEmpty()) {
                try {
                    arena.getBreakableBlocks().addAll(
                        Arrays.stream(bbs.split(",")).map(String::trim).map(Material::valueOf).collect(Collectors.toSet())
                    );
                } catch (IllegalArgumentException ignored) {}
            }

            ConfigurationSection rules = a.getConfigurationSection("rules");
            if (rules != null) {
                arena.setAllowPvp(rules.getBoolean("pvp", false));
                arena.setAllowBlockBreak(rules.getBoolean("block-break", false) || rules.getString("block-break", "").equalsIgnoreCase("ALL"));
                arena.setAllowBlockPlace(rules.getBoolean("block-place", false) || rules.getString("block-place", "").equalsIgnoreCase("ALL"));
                arena.setDeathEliminates(rules.getBoolean("death-eliminates", true));
                arena.setFallVoid(rules.getBoolean("fall-void", true));
                arena.setMaxTimeSeconds(rules.getInt("max-time", 0));
            }

            arena.setPos1(loadLocation(a.getString("pos1"), arena));
            arena.setPos2(loadLocation(a.getString("pos2"), arena));
            arena.setLobbySpawn(loadLocation(a.getString("lobby"), arena));
            arena.setSpectatorSpawn(loadLocation(a.getString("spectator"), arena));

            List<String> spawns = a.getStringList("player-spawns");
            for (String s : spawns) {
                Location loc = loadLocation(s, arena);
                if (loc != null) arena.addPlayerSpawn(loc);
            }

            List<Map<?, ?>> scriptRaw = a.getMapList("script");
            for (Map<?, ?> entry : scriptRaw) {
                Map<String, Object> converted = new LinkedHashMap<>();
                for (Map.Entry<?, ?> e : entry.entrySet()) {
                    converted.put(e.getKey().toString(), e.getValue());
                }
                arena.getScript().add(converted);
            }

            arenas.put(name.toLowerCase(), arena);
        }
        plugin.getLogger().info("Loaded " + arenas.size() + " arenas.");
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Arena arena : arenas.values()) {
            String path = "arenas." + arena.getName();
            cfg.set(path + ".mode", arena.getMode());
            cfg.set(path + ".world", arena.getWorldName());
            cfg.set(path + ".min-players", arena.getMinPlayers());
            cfg.set(path + ".max-players", arena.getMaxPlayers());
            cfg.set(path + ".countdown", arena.getCountdown());
            cfg.set(path + ".floor-level", arena.getFloorLevel());
            cfg.set(path + ".restore-arena", arena.isRestoreOnEnd());
            cfg.set(path + ".lobby-in-main-world", arena.isLobbyInMainWorld());
            cfg.set(path + ".reward.currency", arena.getCurrencyReward());
            cfg.set(path + ".reward.commands", arena.getRewardCommands());
            cfg.set(path + ".allowed-commands", arena.getAllowedCommands());
            cfg.set(path + ".breakable-blocks", arena.getBreakableBlocks().stream().map(Enum::name).collect(Collectors.joining(",")));
            cfg.set(path + ".player-spawns", arena.getPlayerSpawns().stream().map(l -> serializeLocation(l, arena)).collect(Collectors.toList()));
            cfg.set(path + ".pos1", serializeLocation(arena.getPos1(), arena));
            cfg.set(path + ".pos2", serializeLocation(arena.getPos2(), arena));
            cfg.set(path + ".lobby", serializeLocation(arena.getLobbySpawn(), arena));
            cfg.set(path + ".spectator", serializeLocation(arena.getSpectatorSpawn(), arena));
            cfg.set(path + ".rules.pvp", arena.isAllowPvp());
            cfg.set(path + ".rules.block-break", arena.isAllowBlockBreak() ? "ALL" : false);
            cfg.set(path + ".rules.block-place", arena.isAllowBlockPlace() ? "ALL" : false);
            cfg.set(path + ".rules.death-eliminates", arena.isDeathEliminates());
            cfg.set(path + ".rules.fall-void", arena.isFallVoid());
            cfg.set(path + ".rules.max-time", arena.getMaxTimeSeconds());
            cfg.set(path + ".script", arena.getScript());
        }
        try { cfg.save(arenasFile); } catch (IOException e) {
            plugin.getLogger().warning("Failed to save arenas.yml: " + e.getMessage());
        }
    }

    public void reload() {
        arenas.clear();
        load();
    }

    public Arena create(String name, String mode) {
        Arena arena = new Arena(name, mode.toUpperCase());
        arenas.put(name.toLowerCase(), arena);
        save();
        return arena;
    }

    public void delete(String name) {
        arenas.remove(name.toLowerCase());
        save();
    }

    public Arena get(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public List<String> getArenaNames() {
        return new ArrayList<>(arenas.keySet());
    }

    private Location loadLocation(String str, Arena arena) {
        if (str == null || str.isEmpty()) return null;
        World w = arena.getWorld();
        if (w == null) return null;
        String clean = str.replace("&comma;", ",").replace("&colon;", ":");
        int idx1 = clean.indexOf(',');
        int idx2 = clean.indexOf(',', idx1 + 1);
        if (idx1 == -1 || idx2 == -1) return null;
        try {
            double x = Double.parseDouble(clean.substring(0, idx1).trim());
            double y = Double.parseDouble(clean.substring(idx1 + 1, idx2).trim());
            double z = Double.parseDouble(clean.substring(idx2 + 1).trim());
            return new Location(w, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String serializeLocation(Location loc, Arena arena) {
        if (loc == null) return "";
        return loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }
}
