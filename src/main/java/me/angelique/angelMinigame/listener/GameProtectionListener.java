package me.angelique.angelMinigame.listener;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.GameState;
import me.angelique.angelMinigame.game.GameManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class GameProtectionListener implements Listener {

    private final AngelMinigame plugin;

    public GameProtectionListener(AngelMinigame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        GameManager gm = plugin.getGameManager();
        GameSession session = gm.getSessionByPlayer(p.getUniqueId());
        if (session == null) return;
        if (session.getState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        if (!session.getArena().isInRegion(block.getLocation())) {
            event.setCancelled(true);
            return;
        }
        boolean allowed = session.getGameMode().onBlockBreak(p, block, session);
        if (!allowed) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session == null) return;
        if (session.getState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        if (!session.getArena().isInRegion(block.getLocation())) {
            event.setCancelled(true);
            return;
        }
        session.getGameMode().onBlockPlace(p, block, session);
        if (!session.getArena().isAllowBlockPlace()) event.setCancelled(true);
    }
}
