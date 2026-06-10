package me.angelique.angelMinigame.gui;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ArenaListGui implements Listener {

    private static final String TITLE = AngelMinigame.clr("&6Arena Selection");
    private final AngelMinigame plugin;

    public ArenaListGui(AngelMinigame plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        List<Arena> arenas = List.copyOf(plugin.getArenaManager().getArenas());
        int size = Math.max(9, ((arenas.size() / 9) + 1) * 9);
        if (size > 54) size = 54;
        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        for (int i = 0; i < arenas.size() && i < size; i++) {
            Arena arena = arenas.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(AngelMinigame.clr("&6" + arena.getName()));
            var session = plugin.getGameManager().getSessionByArena(arena.getName());
            String state = session != null ? session.getState().name() : "WAITING";
            meta.setLore(List.of(
                "",
                AngelMinigame.clr("&7Mode: &f" + arena.getMode()),
                AngelMinigame.clr("&7Status: &f" + state),
                AngelMinigame.clr("&7Players: &f" + (session != null ? session.getPlayers().size() : 0) + "/" + arena.getMaxPlayers()),
                "",
                AngelMinigame.clr("&eClick to join!")
            ));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(TITLE)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player p)) return;
        int slot = event.getRawSlot();
        List<Arena> arenas = List.copyOf(plugin.getArenaManager().getArenas());
        if (slot < 0 || slot >= arenas.size()) return;
        Arena arena = arenas.get(slot);
        p.closeInventory();
        p.performCommand("game join " + arena.getName());
    }
}
