package me.angelique.angelMinigame.game;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.arena.Arena;
import me.angelique.angelMinigame.arena.ArenaManager;
import me.angelique.angelMinigame.game.modes.*;
import me.angelique.angelMinigame.game.script.ActionRegistry;
import me.angelique.angelMinigame.game.script.GameScript;
import me.angelique.angelMinigame.integration.AngelCoreBridge;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    private final AngelMinigame plugin;
    private final ArenaManager arenaManager;
    private final ActionRegistry actionRegistry;
    private final Map<String, GameSession> sessions = new HashMap<>();

    public GameManager(AngelMinigame plugin, ArenaManager arenaManager, ActionRegistry actionRegistry) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.actionRegistry = actionRegistry;
    }

    public GameSession getSessionByPlayer(UUID uuid) {
        for (GameSession s : sessions.values()) {
            if (s.isPlayer(uuid) || s.isSpectator(uuid)) return s;
        }
        return null;
    }

    public GameSession getSessionByArena(String arenaName) {
        return sessions.get(arenaName.toLowerCase());
    }

    public boolean joinArena(Player player, String arenaName) {
        Arena arena = arenaManager.get(arenaName);
        if (arena == null) return false;
        if (getSessionByPlayer(player.getUniqueId()) != null) return false;

        GameSession session = sessions.get(arena.getName().toLowerCase());
        if (session == null) {
            GameMode mode = createGameMode(arena.getMode());
            if (mode == null) return false;
            session = new GameSession(arena, mode);
            session.setGameManager(this);
            if (arena.getMode().equalsIgnoreCase("CUSTOM") && !arena.getScript().isEmpty()) {
                session.setScript(new GameScript(session, arena.getScript(), actionRegistry));
            }
            sessions.put(arena.getName().toLowerCase(), session);
        }

        if (session.getState() != GameState.WAITING) return false;
        if (session.getPlayers().size() >= arena.getMaxPlayers()) return false;
        if (session.getAlivePlayers().contains(player.getUniqueId())) return false;

        session.addPlayer(player.getUniqueId());
        session.putPlayerState(player.getUniqueId(), capturePlayerState(player));

        Location lobby = arena.getLobbySpawn();
        if (lobby != null) player.teleport(lobby);

        player.setGameMode(org.bukkit.GameMode.ADVENTURE);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5f);

        for (PotionEffect pe : player.getActivePotionEffects()) player.removePotionEffect(pe.getType());

        player.sendMessage(AngelMinigame.clr("&aYou joined &6" + arena.getName() + " &a(" + session.getPlayers().size() + "/" + arena.getMaxPlayers() + ")"));

        if (session.getPlayers().size() >= arena.getMinPlayers()) {
            startCountdown(session);
        }

        return true;
    }

    public boolean leaveArena(Player player) {
        GameSession session = getSessionByPlayer(player.getUniqueId());
        if (session == null) return false;
        removePlayerFromGame(player, session, false);
        return true;
    }

    public boolean forceStart(String arenaName) {
        GameSession session = sessions.get(arenaName.toLowerCase());
        if (session == null || session.getState() != GameState.WAITING) return false;
        if (session.getPlayers().size() < 2) return false;
        if (session.getCountdownTaskId() != -1) Bukkit.getScheduler().cancelTask(session.getCountdownTaskId());
        startGame(session);
        return true;
    }

    public boolean forceStop(String arenaName) {
        GameSession session = sessions.get(arenaName.toLowerCase());
        if (session == null) return false;
        endGame(session, null);
        return true;
    }

    public void shutdown() {
        for (GameSession session : new ArrayList<>(sessions.values())) {
            endGame(session, null);
        }
    }

    private void startCountdown(GameSession session) {
        session.setState(GameState.STARTING);
        session.setCountdownRemaining(session.getArena().getCountdown());

        session.broadcast(AngelMinigame.clr("&eGame starting in &6" + session.getCountdownRemaining() + " &eseconds..."));
        for (UUID uuid : session.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
        }

        session.setCountdownTaskId(new BukkitRunnable() {
            @Override public void run() {
                session.setCountdownRemaining(session.getCountdownRemaining() - 1);
                if (session.getCountdownRemaining() <= 0) {
                    startGame(session);
                    cancel();
                    return;
                }
                if (session.getCountdownRemaining() <= 5) {
                    session.broadcast(AngelMinigame.clr("&e" + session.getCountdownRemaining() + " &e..."));
                    for (UUID uuid : session.getPlayers()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L).getTaskId());
    }

    private void startGame(GameSession session) {
        session.setState(GameState.RUNNING);
        session.setGameStartTime(System.currentTimeMillis());
        int spawnIndex = 0;
        List<Location> spawns = session.getArena().getPlayerSpawns();

        for (UUID uuid : new ArrayList<>(session.getAlivePlayers())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) {
                session.removePlayer(uuid);
                continue;
            }
            if (!spawns.isEmpty()) {
                p.teleport(spawns.get(spawnIndex % spawns.size()));
                spawnIndex++;
            }
            p.setGameMode(org.bukkit.GameMode.ADVENTURE);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(5f);
            p.getInventory().clear();
        }

        if (session.getArena().isRestoreOnEnd()) {
            session.broadcast(AngelMinigame.clr("&ePreparing arena..."));
            ArenaSnapshot.capture(session, () -> {});
        }

        session.getGameMode().onStart(session);

        if (session.getScript() != null) {
            session.getScript().onStart();
        }

        session.broadcast(AngelMinigame.clr("&6Go!"));
        for (UUID uuid : session.getAlivePlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.5f);
                p.sendTitle(AngelMinigame.clr("&6GO!"), "", 0, 20, 10);
            }
        }

        AngelCoreBridge.publishGameStart(session);

        session.setTaskId(new BukkitRunnable() {
            @Override public void run() {
                session.setElapsedTicks(session.getElapsedTicks() + 1);

                if (session.getArena().getMaxTimeSeconds() > 0) {
                    if (session.getElapsedSeconds() >= session.getArena().getMaxTimeSeconds()) {
                        endGame(session, null);
                        cancel();
                        return;
                    }
                }

                session.getGameMode().onTick(session);

                if (session.getScript() != null) {
                    session.getScript().onTick();
                }

                Player winner = session.getGameMode().checkWinCondition(session);
                if (winner != null) {
                    endGame(session, winner.getUniqueId());
                    cancel();
                    return;
                }

                if (session.getAlivePlayers().size() <= 1) {
                    UUID winnerUuid = session.getAlivePlayers().isEmpty() ? null : session.getAlivePlayers().iterator().next();
                    endGame(session, winnerUuid);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L).getTaskId());
    }

    public void endGame(GameSession session, UUID winnerUuid) {
        if (session.getState() == GameState.ENDING || session.getState() == GameState.RESETTING) return;
        session.setState(GameState.ENDING);

        if (session.getTaskId() != -1) Bukkit.getScheduler().cancelTask(session.getTaskId());
        if (session.getCountdownTaskId() != -1) Bukkit.getScheduler().cancelTask(session.getCountdownTaskId());

        session.setWinner(winnerUuid);
        session.getGameMode().onEnd(session);

        if (session.getScript() != null) {
            session.getScript().onEnd();
        }

        String winnerName = "Nobody";
        Player winnerPlayer = null;
        if (winnerUuid != null) {
            winnerPlayer = Bukkit.getPlayer(winnerUuid);
            if (winnerPlayer != null) winnerName = winnerPlayer.getName();
        }

        session.broadcast(AngelMinigame.clr("&6" + winnerName + " &ewon &6" + session.getArena().getName() + "&e!"));

        AngelCoreBridge.publishGameEnd(session);

        if (winnerUuid != null && session.getArena().getCurrencyReward() > 0) {
            plugin.getCoreBridge().grantReward(Bukkit.getOfflinePlayer(winnerUuid), session.getArena().getCurrencyReward());
        }

        if (winnerUuid != null) {
            for (String cmd : session.getArena().getRewardCommands()) {
                String resolved = cmd.replace("%winner%", winnerName).replace("%player%", winnerName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved);
            }
        }

        celebrateWin(session, winnerPlayer, winnerName);
    }

    private void celebrateWin(GameSession session, Player winnerPlayer, String winnerName) {
        Location center;
        if (winnerPlayer != null) {
            center = winnerPlayer.getLocation().clone();
        } else if (session.getArena().getLobbySpawn() != null) {
            center = session.getArena().getLobbySpawn().clone();
        } else {
            center = session.getArena().getWorld().getSpawnLocation().clone();
        }

        for (UUID uuid : session.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendTitle(
                    AngelMinigame.clr("&6&l" + winnerName),
                    AngelMinigame.clr("&eWinner!"),
                    5, 60, 10
                );
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            }
        }

        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;
            final int celebrationTicks = 100;

            @Override public void run() {
                if (session.getState() != GameState.ENDING) { cancel(); return; }
                if (ticks >= celebrationTicks) {
                    startReset(session);
                    cancel();
                    return;
                }
                if (ticks % 20 == 0 && winnerPlayer != null) {
                    Location loc = winnerPlayer.getLocation().clone().add(0, 2, 0);
                    Firework fw = winnerPlayer.getWorld().spawn(loc, Firework.class);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.RED, Color.ORANGE, Color.GREEN, Color.AQUA)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .withFlicker().build());
                    meta.setPower(1);
                    fw.setFireworkMeta(meta);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private void startReset(GameSession session) {
        session.setState(GameState.RESETTING);
        if (session.getArena().isRestoreOnEnd()) {
            ArenaSnapshot.restore(session, () -> finishReset(session));
        } else {
            finishReset(session);
        }
    }

    private void finishReset(GameSession session) {
        session.getGameMode().onReset(session);
        if (session.getScript() != null) session.getScript().onReset();

        for (UUID uuid : new ArrayList<>(session.getPlayers())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) restorePlayerState(p, session.getPlayerState(uuid));
        }

        for (UUID uuid : new ArrayList<>(session.getSpectators())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                PlayerState ps = session.getPlayerState(uuid);
                if (ps != null) {
                    restorePlayerState(p, ps);
                } else {
                    Location lobby = session.getArena().getLobbySpawn();
                    if (lobby != null) p.teleport(lobby);
                }
            }
        }

        sessions.remove(session.getArena().getName().toLowerCase());
        ArenaSnapshot.remove(session.getArena().getName());
    }

    public void onPlayerEliminated(Player player, GameSession session, String cause) {
        session.eliminate(player.getUniqueId());
        session.broadcast(AngelMinigame.clr("&c" + player.getName() + " &7was eliminated &8(" + cause + ")"));
        player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        Location spec = session.getArena().getSpectatorSpawn();
        if (spec != null) player.teleport(spec);
        session.addSpectator(player.getUniqueId());

        AngelCoreBridge.publishPlayerEliminated(session, player.getUniqueId(), cause);

        if (session.getScript() != null) {
            session.getScript().onElimination(player.getUniqueId());
        }

        if (session.getAlivePlayers().size() <= 1) {
            UUID winnerUuid = session.getAlivePlayers().isEmpty() ? null : session.getAlivePlayers().iterator().next();
            endGame(session, winnerUuid);
        }
    }

    private void removePlayerFromGame(Player player, GameSession session, boolean disconnected) {
        session.getGameMode().onPlayerDeath(player, session);

        if (session.getAlivePlayers().contains(player.getUniqueId())) {
            session.eliminate(player.getUniqueId());
            if (session.getState() == GameState.RUNNING) {
                AngelCoreBridge.publishPlayerEliminated(session, player.getUniqueId(), "left");
            }
        }

        restorePlayerState(player, session.getPlayerState(player.getUniqueId()));
        session.removePlayer(player.getUniqueId());

        if (session.getState() == GameState.RUNNING || session.getState() == GameState.STARTING) {
            if (session.getAlivePlayers().size() <= 1) {
                UUID winnerUuid = session.getAlivePlayers().isEmpty() ? null : session.getAlivePlayers().iterator().next();
                endGame(session, winnerUuid);
                return;
            }
        }

        if (session.getPlayers().isEmpty() && session.getState() != GameState.RESETTING) {
            if (session.getTaskId() != -1) Bukkit.getScheduler().cancelTask(session.getTaskId());
            if (session.getCountdownTaskId() != -1) Bukkit.getScheduler().cancelTask(session.getCountdownTaskId());
            sessions.remove(session.getArena().getName().toLowerCase());
            ArenaSnapshot.remove(session.getArena().getName());
        }
    }

    private PlayerState capturePlayerState(Player player) {
        return new PlayerState(
            player.getUniqueId(),
            player.getLocation().clone(),
            player.getInventory().getContents().clone(),
            player.getInventory().getArmorContents().clone(),
            player.getHealth(),
            player.getFoodLevel(),
            player.getSaturation(),
            new ArrayList<>(player.getActivePotionEffects()),
            player.getGameMode(),
            player.getLevel(),
            player.getExp()
        );
    }

    private void restorePlayerState(Player player, PlayerState state) {
        if (state == null) return;
        player.teleport(state.getOriginalLocation());
        player.getInventory().setContents(state.getSavedInventory());
        player.getInventory().setArmorContents(state.getSavedArmor());
        player.setHealth(Math.min(state.getSavedHealth(), 20));
        player.setFoodLevel(state.getSavedFood());
        player.setSaturation(state.getSavedSaturation());
        for (PotionEffect pe : player.getActivePotionEffects()) player.removePotionEffect(pe.getType());
        player.addPotionEffects(state.getSavedEffects());
        player.setGameMode(state.getSavedGameMode());
        player.setLevel(state.getSavedXpLevel());
        player.setExp(state.getSavedXpProgress());
    }

    private GameMode createGameMode(String mode) {
        return switch (mode.toUpperCase()) {
            case "SPLEEF" -> new SpleefMode();
            case "TNT_RUN" -> new TntRunMode();
            case "PARKOUR" -> new ParkourMode();
            case "LMS", "LAST_MAN_STANDING" -> new LastManStandingMode();
            case "CUSTOM" -> new CustomGameMode();
            default -> null;
        };
    }

    public Collection<GameSession> getActiveSessions() {
        return sessions.values();
    }
}
