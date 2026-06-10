package me.angelique.angelMinigame.game;

import me.angelique.angelMinigame.arena.Arena;
import me.angelique.angelMinigame.game.script.GameScript;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GameSession {

    private final Arena arena;
    private GameState state = GameState.WAITING;
    private final GameMode gameMode;
    private GameScript script;
    private GameManager gameManager;
    private final List<UUID> players = new ArrayList<>();
    private final Set<UUID> alivePlayers = new LinkedHashSet<>();
    private final Map<UUID, PlayerState> playerStates = new HashMap<>();
    private final Set<UUID> spectators = new HashSet<>();
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Map<UUID, Location> cagedPlayers = new HashMap<>();
    private final Map<UUID, Set<Material>> glowingPlayers = new HashMap<>();
    private UUID winner;
    private long gameStartTime;
    private int countdownRemaining;
    private int taskId = -1;
    private int countdownTaskId = -1;
    private int elapsedTicks;
    private boolean firstDeathFired;

    public GameSession(Arena arena, GameMode gameMode) {
        this.arena = arena;
        this.gameMode = gameMode;
    }

    public GameManager getGameManager() { return gameManager; }
    public void setGameManager(GameManager gm) { this.gameManager = gm; }

    public Arena getArena() { return arena; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }

    public GameMode getGameMode() { return gameMode; }

    public GameScript getScript() { return script; }
    public void setScript(GameScript script) { this.script = script; }

    public List<UUID> getPlayers() { return players; }
    public Set<UUID> getAlivePlayers() { return alivePlayers; }

    public List<Player> getOnlinePlayers() {
        return players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Player> getOnlineAlivePlayers() {
        return alivePlayers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void addPlayer(UUID uuid) {
        players.add(uuid);
        alivePlayers.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
        alivePlayers.remove(uuid);
        spectators.remove(uuid);
        frozenPlayers.remove(uuid);
        cagedPlayers.remove(uuid);
        glowingPlayers.remove(uuid);
    }

    public void eliminate(UUID uuid) {
        alivePlayers.remove(uuid);
    }

    public boolean isAlive(UUID uuid) { return alivePlayers.contains(uuid); }
    public boolean isPlayer(UUID uuid) { return players.contains(uuid); }

    public UUID getWinner() { return winner; }
    public void setWinner(UUID winner) { this.winner = winner; }

    public long getGameStartTime() { return gameStartTime; }
    public void setGameStartTime(long gameStartTime) { this.gameStartTime = gameStartTime; }

    public long getElapsedSeconds() {
        if (gameStartTime == 0) return 0;
        return (System.currentTimeMillis() - gameStartTime) / 1000;
    }

    public int getElapsedTicks() { return elapsedTicks; }
    public void setElapsedTicks(int elapsedTicks) { this.elapsedTicks = elapsedTicks; }

    public int getCountdownRemaining() { return countdownRemaining; }
    public void setCountdownRemaining(int countdownRemaining) { this.countdownRemaining = countdownRemaining; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getCountdownTaskId() { return countdownTaskId; }
    public void setCountdownTaskId(int countdownTaskId) { this.countdownTaskId = countdownTaskId; }

    public Map<UUID, PlayerState> getPlayerStates() { return playerStates; }
    public PlayerState getPlayerState(UUID uuid) { return playerStates.get(uuid); }
    public void putPlayerState(UUID uuid, PlayerState state) { playerStates.put(uuid, state); }
    public void removePlayerState(UUID uuid) { playerStates.remove(uuid); }

    public Set<UUID> getSpectators() { return spectators; }
    public boolean isSpectator(UUID uuid) { return spectators.contains(uuid); }
    public void addSpectator(UUID uuid) { spectators.add(uuid); }
    public void removeSpectator(UUID uuid) { spectators.remove(uuid); }

    public Set<UUID> getFrozenPlayers() { return frozenPlayers; }
    public boolean isFrozen(UUID uuid) { return frozenPlayers.contains(uuid); }

    public Map<UUID, Location> getCagedPlayers() { return cagedPlayers; }
    public boolean isCaged(UUID uuid) { return cagedPlayers.containsKey(uuid); }

    public boolean isFirstDeathFired() { return firstDeathFired; }
    public void setFirstDeathFired(boolean firstDeathFired) { this.firstDeathFired = firstDeathFired; }

    public void broadcast(String message) {
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(message);
        }
    }

    public void broadcastAction(String message) {
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(message);
        }
    }
}
