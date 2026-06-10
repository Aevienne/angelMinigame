package me.angelique.angelMinigame.game.script.actions;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import me.angelique.angelMinigame.game.script.Action;
import org.bukkit.Bukkit;
import java.util.List;
import java.util.Map;

public class CommandAction implements Action {
    @Override public String getType() { return "command"; }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(GameSession session, Map<String, Object> params) {
        AngelMinigame plugin = AngelMinigame.getInstance();
        boolean enabled = plugin.getConfig().getBoolean("allowed-commands.enabled", false);
        if (!enabled) {
            plugin.getLogger().warning("Command action blocked (allowed-commands.enabled = false)");
            return;
        }
        List<String> prefixes = plugin.getConfig().getStringList("allowed-commands.prefixes");

        String cmd;
        if (params.containsKey("command")) {
            cmd = params.get("command").toString();
        } else if (params.containsKey("cmd")) {
            cmd = params.get("cmd").toString();
        } else {
            return;
        }

        String cmdName = cmd.split(" ")[0].toLowerCase();
        boolean allowed = false;
        for (String prefix : prefixes) {
            if (cmdName.equals(prefix.toLowerCase()) || cmdName.startsWith(prefix.toLowerCase())) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            plugin.getLogger().warning("Command blocked (not in allowlist): " + cmd);
            return;
        }

        if (params.containsKey("run") && params.get("run").toString().contains("each")) {
            for (var uuid : session.getAlivePlayers()) {
                var p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
                }
            }
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
}
