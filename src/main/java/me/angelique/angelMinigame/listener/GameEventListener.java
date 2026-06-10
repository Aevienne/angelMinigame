package me.angelique.angelMinigame.listener;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.GameState;
import me.angelique.angelMinigame.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GameEventListener implements Listener {

    private final AngelMinigame plugin;

    public GameEventListener(AngelMinigame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session != null) {
            plugin.getGameManager().leaveArena(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session == null || session.getState() != GameState.RUNNING) return;
        if (!session.isAlive(p.getUniqueId())) return;

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        event.setKeepLevel(true);

        session.getGameMode().onPlayerDeath(p, session);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session == null) return;
        if (session.getState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }
        if (!session.getArena().isAllowPvp() && event instanceof EntityDamageByEntityEvent dmg
            && dmg.getDamager() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session == null) return;

        if (session.isFrozen(p.getUniqueId()) && session.getState() == GameState.RUNNING) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setCancelled(true);
            }
            return;
        }

        if (session.getState() == GameState.RUNNING && session.isAlive(p.getUniqueId())) {
            if (!session.getArena().isInRegion(event.getTo())) {
                session.getGameManager().onPlayerEliminated(p, session, "left arena");
                return;
            }
            session.getGameMode().onPlayerMove(p, session);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session == null) return;
        if (p.hasPermission("angelminigame.admin")) return;

        String cmd = event.getMessage().split(" ")[0].toLowerCase();
        if (cmd.equals("/game") || cmd.equals("/minigame")) return;

        java.util.List<String> allowed = plugin.getConfig().getStringList("game-commands.allowed");
        for (String ac : allowed) {
            if (cmd.equals(ac.toLowerCase())) return;
        }

        for (String ac : session.getArena().getAllowedCommands()) {
            if (cmd.equals(ac.toLowerCase())) return;
        }

        event.setCancelled(true);
        p.sendMessage(AngelMinigame.clr("&cCannot use commands during a minigame. Use /game leave to exit."));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session != null && event.getCurrentItem() != null) {
            String dn = event.getCurrentItem().getItemMeta().getDisplayName();
            if (dn != null && dn.contains("Arena Wand")) event.setCancelled(true);
        }
    }
}
