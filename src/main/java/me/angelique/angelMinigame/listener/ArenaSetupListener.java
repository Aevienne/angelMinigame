package me.angelique.angelMinigame.listener;

import me.angelique.angelMinigame.AngelMinigame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArenaSetupListener implements Listener {

    private final AngelMinigame plugin;

    public ArenaSetupListener(AngelMinigame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("angelminigame.admin")) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) return;
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName == null || !displayName.contains("Arena Wand")) return;

        event.setCancelled(true);
        Block block = event.getClickedBlock();
        Location loc = block != null ? block.getLocation() : p.getLocation();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            plugin.getConfig().set("selection." + p.getUniqueId() + ".pos1", loc);
            plugin.saveConfig();
            p.sendMessage(AngelMinigame.clr("&aPos1 set to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            plugin.getConfig().set("selection." + p.getUniqueId() + ".pos2", loc);
            plugin.saveConfig();
            p.sendMessage(AngelMinigame.clr("&aPos2 set to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
        }
    }
}
