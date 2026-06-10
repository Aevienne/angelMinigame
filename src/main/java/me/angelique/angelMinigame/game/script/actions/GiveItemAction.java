package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.Map;

public class GiveItemAction implements Action {
    @Override public String getType() { return "give_item"; }

    @Override
    public void execute(GameSession session, Map<String, Object> params) {
        Material mat = Material.matchMaterial(params.getOrDefault("material", "STONE").toString());
        if (mat == null) return;
        int amount = params.containsKey("amount") ? Integer.parseInt(params.get("amount").toString()) : 1;
        ItemStack item = new ItemStack(mat, amount);
        if (params.containsKey("name")) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(AngelMinigame.clr(params.get("name").toString()));
            if (params.containsKey("lore")) {
                String loreStr = params.get("lore").toString();
                meta.setLore(Arrays.stream(loreStr.split("\\|")).map(AngelMinigame::clr).toList());
            }
            item.setItemMeta(meta);
        }
        for (Player p : session.getOnlineAlivePlayers()) p.getInventory().addItem(item);
    }
}
