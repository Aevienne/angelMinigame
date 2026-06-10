package me.angelique.angelMinigame.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

public class Arena {

    private String name;
    private String mode;
    private String worldName;
    private Location pos1;
    private Location pos2;
    private Location lobbySpawn;
    private Location spectatorSpawn;
    private List<Location> playerSpawns = new ArrayList<>();
    private int minPlayers = 2;
    private int maxPlayers = 16;
    private int countdown = 10;
    private Set<Material> breakableBlocks = new HashSet<>();
    private int floorLevel = 0;
    private boolean restoreOnEnd = true;
    private boolean lobbyInMainWorld = false;
    private double currencyReward = 0;
    private List<String> rewardCommands = new ArrayList<>();
    private List<String> allowedCommands = new ArrayList<>();
    private boolean allowPvp = false;
    private boolean allowBlockBreak = false;
    private boolean allowBlockPlace = false;
    private boolean deathEliminates = true;
    private boolean fallVoid = true;
    private int maxTimeSeconds = 0;
    private List<Map<String, Object>> script = new ArrayList<>();

    public Arena(String name, String mode) {
        this.name = name;
        this.mode = mode;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getWorldName() { return worldName; }
    public void setWorldName(String worldName) { this.worldName = worldName; }

    public World getWorld() { return worldName != null ? Bukkit.getWorld(worldName) : null; }

    public Location getPos1() { return pos1; }
    public void setPos1(Location pos1) { this.pos1 = pos1; }

    public Location getPos2() { return pos2; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }

    public boolean hasRegion() {
        return pos1 != null && pos2 != null;
    }

    public int getMinX() { return Math.min(pos1.getBlockX(), pos2.getBlockX()); }
    public int getMinY() { return Math.min(pos1.getBlockY(), pos2.getBlockY()); }
    public int getMinZ() { return Math.min(pos1.getBlockZ(), pos2.getBlockZ()); }
    public int getMaxX() { return Math.max(pos1.getBlockX(), pos2.getBlockX()); }
    public int getMaxY() { return Math.max(pos1.getBlockY(), pos2.getBlockY()); }
    public int getMaxZ() { return Math.max(pos1.getBlockZ(), pos2.getBlockZ()); }

    public boolean isInRegion(Location loc) {
        if (!hasRegion() || !loc.getWorld().getName().equals(worldName)) return false;
        return loc.getBlockX() >= getMinX() && loc.getBlockX() <= getMaxX()
            && loc.getBlockY() >= getMinY() && loc.getBlockY() <= getMaxY()
            && loc.getBlockZ() >= getMinZ() && loc.getBlockZ() <= getMaxZ();
    }

    public Location getLobbySpawn() { return lobbySpawn; }
    public void setLobbySpawn(Location lobbySpawn) { this.lobbySpawn = lobbySpawn; }

    public Location getSpectatorSpawn() { return spectatorSpawn; }
    public void setSpectatorSpawn(Location spectatorSpawn) { this.spectatorSpawn = spectatorSpawn; }

    public List<Location> getPlayerSpawns() { return playerSpawns; }
    public void setPlayerSpawns(List<Location> playerSpawns) { this.playerSpawns = playerSpawns; }
    public void addPlayerSpawn(Location loc) { this.playerSpawns.add(loc); }
    public void clearPlayerSpawns() { this.playerSpawns.clear(); }

    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getCountdown() { return countdown; }
    public void setCountdown(int countdown) { this.countdown = countdown; }

    public Set<Material> getBreakableBlocks() { return breakableBlocks; }
    public void setBreakableBlocks(Set<Material> breakableBlocks) { this.breakableBlocks = breakableBlocks; }

    public int getFloorLevel() { return floorLevel; }
    public void setFloorLevel(int floorLevel) { this.floorLevel = floorLevel; }

    public boolean isRestoreOnEnd() { return restoreOnEnd; }
    public void setRestoreOnEnd(boolean restoreOnEnd) { this.restoreOnEnd = restoreOnEnd; }

    public boolean isLobbyInMainWorld() { return lobbyInMainWorld; }
    public void setLobbyInMainWorld(boolean lobbyInMainWorld) { this.lobbyInMainWorld = lobbyInMainWorld; }

    public double getCurrencyReward() { return currencyReward; }
    public void setCurrencyReward(double currencyReward) { this.currencyReward = currencyReward; }

    public List<String> getRewardCommands() { return rewardCommands; }
    public void setRewardCommands(List<String> rewardCommands) { this.rewardCommands = rewardCommands; }

    public List<String> getAllowedCommands() { return allowedCommands; }
    public void setAllowedCommands(List<String> allowedCommands) { this.allowedCommands = allowedCommands; }

    public boolean isAllowPvp() { return allowPvp; }
    public void setAllowPvp(boolean allowPvp) { this.allowPvp = allowPvp; }

    public boolean isAllowBlockBreak() { return allowBlockBreak; }
    public void setAllowBlockBreak(boolean allowBlockBreak) { this.allowBlockBreak = allowBlockBreak; }

    public boolean isAllowBlockPlace() { return allowBlockPlace; }
    public void setAllowBlockPlace(boolean allowBlockPlace) { this.allowBlockPlace = allowBlockPlace; }

    public boolean isDeathEliminates() { return deathEliminates; }
    public void setDeathEliminates(boolean deathEliminates) { this.deathEliminates = deathEliminates; }

    public boolean isFallVoid() { return fallVoid; }
    public void setFallVoid(boolean fallVoid) { this.fallVoid = fallVoid; }

    public int getMaxTimeSeconds() { return maxTimeSeconds; }
    public void setMaxTimeSeconds(int maxTimeSeconds) { this.maxTimeSeconds = maxTimeSeconds; }

    public List<Map<String, Object>> getScript() { return script; }
    public void setScript(List<Map<String, Object>> script) { this.script = script; }
}
