package me.angelique.angelMinigame.commands;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class GameCommand implements CommandExecutor, TabCompleter {

    private final AngelMinigame plugin;

    public GameCommand(AngelMinigame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "join": return handleJoin(sender, args);
            case "leave": return handleLeave(sender);
            case "start": return handleStart(sender, args);
            case "stop": return handleStop(sender, args);
            case "list": return handleList(sender);
            default:
                sender.sendMessage(AngelMinigame.clr("&cUnknown subcommand. /game for help."));
                return true;
        }
    }

    private boolean handleJoin(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        if (!p.hasPermission("angelminigame.player")) {
            p.sendMessage(AngelMinigame.clr("&cNo permission."));
            return true;
        }
        if (args.length < 2) {
            p.sendMessage(AngelMinigame.clr("&cUsage: /game join <arena>"));
            return true;
        }
        if (plugin.getGameManager().getSessionByPlayer(p.getUniqueId()) != null) {
            p.sendMessage(AngelMinigame.clr("&cYou are already in a game. Use /game leave first."));
            return true;
        }
        if (plugin.getGameManager().joinArena(p, args[1])) {
            p.sendMessage(AngelMinigame.clr("&aJoined &6" + args[1] + "&a."));
        } else {
            p.sendMessage(AngelMinigame.clr("&cCould not join. Arena not found, full, or already running."));
        }
        return true;
    }

    private boolean handleLeave(CommandSender sender) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        GameSession session = plugin.getGameManager().getSessionByPlayer(p.getUniqueId());
        if (session == null) {
            p.sendMessage(AngelMinigame.clr("&cYou are not in a game."));
            return true;
        }
        plugin.getGameManager().leaveArena(p);
        p.sendMessage(AngelMinigame.clr("&aLeft the game."));
        return true;
    }

    private boolean handleStart(CommandSender sender, String[] args) {
        if (!sender.hasPermission("angelminigame.admin")) {
            sender.sendMessage(AngelMinigame.clr("&cNo permission."));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(AngelMinigame.clr("&cUsage: /game start <arena>"));
            return true;
        }
        if (plugin.getGameManager().forceStart(args[1])) {
            sender.sendMessage(AngelMinigame.clr("&aGame force-started for &6" + args[1] + "&a."));
        } else {
            sender.sendMessage(AngelMinigame.clr("&cCould not start. Arena not in waiting state or insufficient players."));
        }
        return true;
    }

    private boolean handleStop(CommandSender sender, String[] args) {
        if (!sender.hasPermission("angelminigame.admin")) {
            sender.sendMessage(AngelMinigame.clr("&cNo permission."));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(AngelMinigame.clr("&cUsage: /game stop <arena>"));
            return true;
        }
        if (plugin.getGameManager().forceStop(args[1])) {
            sender.sendMessage(AngelMinigame.clr("&aGame force-stopped for &6" + args[1] + "&a."));
        } else {
            sender.sendMessage(AngelMinigame.clr("&cNo active game for that arena."));
        }
        return true;
    }

    private boolean handleList(CommandSender sender) {
        Collection<GameSession> sessions = plugin.getGameManager().getActiveSessions();
        if (sessions.isEmpty()) {
            sender.sendMessage(AngelMinigame.clr("&7No active games."));
            return true;
        }
        sender.sendMessage(AngelMinigame.clr("&6=== Active Games ==="));
        for (GameSession s : sessions) {
            sender.sendMessage(AngelMinigame.clr("  &f" + s.getArena().getName() + " &8[" + s.getState() + "] &f"
                + s.getAlivePlayers().size() + "&7/&f" + s.getPlayers().size() + " players"));
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(AngelMinigame.clr("&6=== AngelMinigame Player Commands ==="));
        sender.sendMessage(AngelMinigame.clr("&e/game join <arena> &7- Join a game"));
        sender.sendMessage(AngelMinigame.clr("&e/game leave &7- Leave current game"));
        sender.sendMessage(AngelMinigame.clr("&e/game list &7- List active games"));
        if (sender.hasPermission("angelminigame.admin")) {
            sender.sendMessage(AngelMinigame.clr("&e/game start <arena> &7- Force-start"));
            sender.sendMessage(AngelMinigame.clr("&e/game stop <arena> &7- Force-stop"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> cmds = new ArrayList<>(List.of("join", "leave", "list"));
            if (sender.hasPermission("angelminigame.admin")) { cmds.add("start"); cmds.add("stop"); }
            return cmds;
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop"))) {
            return plugin.getArenaManager().getArenaNames();
        }
        return List.of();
    }
}
