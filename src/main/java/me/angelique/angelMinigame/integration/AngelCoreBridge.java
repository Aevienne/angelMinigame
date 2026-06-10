package me.angelique.angelMinigame.integration;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.events.AngelGameEndEvent;
import me.angelique.angelMinigame.events.AngelGameStartEvent;
import me.angelique.angelMinigame.events.AngelPlayerEliminatedEvent;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.stream.Collectors;

public class AngelCoreBridge {

    private final boolean corePresent;

    public AngelCoreBridge(AngelMinigame plugin) {
        Plugin core = Bukkit.getPluginManager().getPlugin("AngelNCore");
        this.corePresent = core != null && core.isEnabled();
        if (corePresent) {
            plugin.getLogger().info("AngelNCore detected — event integration active.");
        } else {
            plugin.getLogger().info("AngelNCore not found — running standalone.");
        }
    }

    public boolean isCorePresent() {
        return corePresent;
    }

    public void grantReward(OfflinePlayer player, double amount) {
        if (!corePresent || amount <= 0) return;
        try {
            Object corePlugin = Bukkit.getPluginManager().getPlugin("AngelNCore");
            if (corePlugin == null) return;
            Object manager = corePlugin.getClass().getMethod("getEconomyManager").invoke(corePlugin);
            if (manager != null) {
                manager.getClass().getMethod("deposit", UUID.class, double.class).invoke(manager, player.getUniqueId(), amount);
                if (player.isOnline()) {
                    player.getPlayer().sendMessage(AngelMinigame.clr("&6+" + amount + " &eearned from the game!"));
                }
            }
        } catch (Exception e) {
            AngelMinigame.getInstance().getLogger().warning("Failed to grant reward: " + e.getMessage());
        }
    }

    public static void publishGameStart(GameSession session) {
        AngelGameStartEvent event = new AngelGameStartEvent(
            session.getArena().getName(),
            session.getArena().getMode(),
            session.getPlayers().stream().map(Bukkit::getOfflinePlayer).map(OfflinePlayer::getName).collect(Collectors.toList())
        );
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void publishGameEnd(GameSession session) {
        String winnerName = "Nobody";
        if (session.getWinner() != null) {
            var wp = Bukkit.getOfflinePlayer(session.getWinner());
            if (wp != null) winnerName = wp.getName();
        }
        AngelGameEndEvent event = new AngelGameEndEvent(
            session.getArena().getName(),
            session.getArena().getMode(),
            winnerName,
            session.getElapsedSeconds()
        );
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void publishPlayerEliminated(GameSession session, UUID playerUuid, String cause) {
        String name = "?";
        var op = Bukkit.getOfflinePlayer(playerUuid);
        if (op != null) name = op.getName();
        AngelPlayerEliminatedEvent event = new AngelPlayerEliminatedEvent(
            session.getArena().getName(),
            session.getArena().getMode(),
            name,
            cause
        );
        Bukkit.getPluginManager().callEvent(event);
    }
}
