package me.angelique.angelMinigame.commands;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.arena.Arena;
import me.angelique.angelMinigame.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaCommand implements CommandExecutor, TabCompleter {

    private final AngelMinigame plugin;

    public ArenaCommand(AngelMinigame plugin) {
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
            case "create": return handleCreate(sender, args);
            case "delete": return handleDelete(sender, args);
            case "info": return handleInfo(sender, args);
            case "list": return handleList(sender);
            case "reload": return handleReload(sender);
            case "wand": return handleWand(sender);
            case "setregion": return handleSetRegion(sender, args);
            case "setlobby": return handleSetLocation(sender, args, "lobby");
            case "setspawn":
            case "addspawn": return handleAddSpawn(sender, args);
            case "clearspawns": return handleClearSpawns(sender, args);
            case "setspectator": return handleSetLocation(sender, args, "spectator");
            case "minmax": return handleMinMax(sender, args);
            case "countdown": return handleCountdown(sender, args);
            case "floor": return handleFloor(sender, args);
            case "reward": return handleReward(sender, args);
            case "blocks": return handleBlocks(sender, args);
            case "rules": return handleRules(sender, args);
            case "allowcmd": return handleAllowCmd(sender, args);
            case "script": return handleScript(sender, args);
            default:
                sender.sendMessage(AngelMinigame.clr("&cUnknown subcommand. Use /arena for help."));
                return true;
        }
    }

    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 3) {
            sender.sendMessage(AngelMinigame.clr("&cUsage: /arena create <name> <mode>"));
            sender.sendMessage(AngelMinigame.clr("&7Modes: SPLEEF, TNT_RUN, PARKOUR, LMS, CUSTOM"));
            return true;
        }
        String name = args[1];
        String mode = args[2].toUpperCase();
        ArenaManager mgr = plugin.getArenaManager();
        if (mgr.get(name) != null) {
            sender.sendMessage(AngelMinigame.clr("&cArena '" + name + "' already exists."));
            return true;
        }
        Set<String> valid = Set.of("SPLEEF", "TNT_RUN", "PARKOUR", "LMS", "CUSTOM");
        if (!valid.contains(mode)) {
            sender.sendMessage(AngelMinigame.clr("&cInvalid mode. Use: SPLEEF, TNT_RUN, PARKOUR, LMS, CUSTOM"));
            return true;
        }
        Arena arena = mgr.create(name, mode);
        if (sender instanceof Player p) arena.setWorldName(p.getWorld().getName());
        sender.sendMessage(AngelMinigame.clr("&aArena '&6" + name + "&a' created with mode &6" + mode + "&a."));
        return true;
    }

    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 2) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena delete <name>")); return true; }
        ArenaManager mgr = plugin.getArenaManager();
        Arena arena = mgr.get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        mgr.delete(args[1]);
        sender.sendMessage(AngelMinigame.clr("&aArena '&6" + args[1] + "&a' deleted."));
        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena info <name>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        sender.sendMessage(AngelMinigame.clr("&6=== " + arena.getName() + " ==="));
        sender.sendMessage(AngelMinigame.clr("&eMode: &f" + arena.getMode()));
        sender.sendMessage(AngelMinigame.clr("&eWorld: &f" + arena.getWorldName()));
        sender.sendMessage(AngelMinigame.clr("&eRegion: &f" + (arena.hasRegion() ? "set" : "&cnot set")));
        sender.sendMessage(AngelMinigame.clr("&eLobby: &f" + (arena.getLobbySpawn() != null ? "set" : "&cnot set")));
        sender.sendMessage(AngelMinigame.clr("&ePlayer Spawns: &f" + arena.getPlayerSpawns().size()));
        sender.sendMessage(AngelMinigame.clr("&ePlayers: &f" + arena.getMinPlayers() + "-" + arena.getMaxPlayers()));
        sender.sendMessage(AngelMinigame.clr("&eCountdown: &f" + arena.getCountdown() + "s"));
        sender.sendMessage(AngelMinigame.clr("&eBreakable blocks: &f" + (arena.getBreakableBlocks().isEmpty() ? "none" : arena.getBreakableBlocks().toString())));
        sender.sendMessage(AngelMinigame.clr("&eCurrency reward: &f" + arena.getCurrencyReward()));
        return true;
    }

    private boolean handleList(CommandSender sender) {
        Collection<Arena> arenas = plugin.getArenaManager().getArenas();
        if (arenas.isEmpty()) { sender.sendMessage(AngelMinigame.clr("&7No arenas defined.")); return true; }
        sender.sendMessage(AngelMinigame.clr("&6=== Arenas ==="));
        for (Arena a : arenas) {
            String status = plugin.getGameManager().getSessionByArena(a.getName()) != null ? "&aRunning" : "&7Idle";
            sender.sendMessage(AngelMinigame.clr("  " + status + " &f" + a.getName() + " &8(" + a.getMode() + ")"));
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        plugin.getArenaManager().reload();
        sender.sendMessage(AngelMinigame.clr("&aArenas reloaded."));
        return true;
    }

    private boolean handleWand(CommandSender sender) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        String wandName = plugin.getConfig().getString("wand-item", "STICK");
        Material wandMat = Material.matchMaterial(wandName);
        if (wandMat == null) wandMat = Material.STICK;
        org.bukkit.inventory.ItemStack wand = new org.bukkit.inventory.ItemStack(wandMat);
        var meta = wand.getItemMeta();
        meta.setDisplayName(AngelMinigame.clr("&6&lArena Wand"));
        meta.setLore(List.of("", AngelMinigame.clr("&7Left-click: set pos1"), AngelMinigame.clr("&7Right-click: set pos2")));
        wand.setItemMeta(meta);
        p.getInventory().addItem(wand);
        p.sendMessage(AngelMinigame.clr("&aArena selection wand given. Left-click=pos1, Right-click=pos2."));
        return true;
    }

    private boolean handleSetRegion(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        if (args.length < 2) { p.sendMessage(AngelMinigame.clr("&cUsage: /arena setregion <name>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { p.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        Location pos1 = plugin.getConfig().getLocation("selection." + p.getUniqueId() + ".pos1");
        Location pos2 = plugin.getConfig().getLocation("selection." + p.getUniqueId() + ".pos2");
        if (pos1 == null || pos2 == null) { p.sendMessage(AngelMinigame.clr("&cMake a selection first with /arena wand.")); return true; }
        if (!pos1.getWorld().equals(pos2.getWorld())) { p.sendMessage(AngelMinigame.clr("&cPositions must be in the same world.")); return true; }
        arena.setWorldName(pos1.getWorld().getName());
        arena.setPos1(pos1);
        arena.setPos2(pos2);
        plugin.getArenaManager().save();
        p.sendMessage(AngelMinigame.clr("&aRegion set for &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleSetLocation(CommandSender sender, String[] args, String type) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        if (args.length < 2) { p.sendMessage(AngelMinigame.clr("&cUsage: /arena set" + type + " <name>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { p.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        Location loc = p.getLocation();
        if (type.equals("lobby")) { arena.setLobbySpawn(loc); arena.setWorldName(loc.getWorld().getName()); }
        else if (type.equals("spectator")) arena.setSpectatorSpawn(loc);
        plugin.getArenaManager().save();
        p.sendMessage(AngelMinigame.clr("&a" + type.substring(0, 1).toUpperCase() + type.substring(1) + " set for &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleAddSpawn(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        if (args.length < 2) { p.sendMessage(AngelMinigame.clr("&cUsage: /arena addspawn <name>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { p.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        arena.addPlayerSpawn(p.getLocation());
        plugin.getArenaManager().save();
        p.sendMessage(AngelMinigame.clr("&aPlayer spawn #" + arena.getPlayerSpawns().size() + " added to &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleClearSpawns(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 2) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena clearspawns <name>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        arena.clearPlayerSpawns();
        plugin.getArenaManager().save();
        sender.sendMessage(AngelMinigame.clr("&aPlayer spawns cleared for &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleMinMax(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 4) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena minmax <name> <min> <max>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        try {
            int min = Integer.parseInt(args[2]), max = Integer.parseInt(args[3]);
            arena.setMinPlayers(min); arena.setMaxPlayers(max);
            plugin.getArenaManager().save();
            sender.sendMessage(AngelMinigame.clr("&aPlayers set to " + min + "-" + max + " for &6" + arena.getName() + "&a."));
        } catch (NumberFormatException e) { sender.sendMessage(AngelMinigame.clr("&cInvalid numbers.")); }
        return true;
    }

    private boolean handleCountdown(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 3) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena countdown <name> <seconds>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        try { arena.setCountdown(Integer.parseInt(args[2])); plugin.getArenaManager().save(); } catch (NumberFormatException e) { sender.sendMessage(AngelMinigame.clr("&cInvalid number.")); return true; }
        sender.sendMessage(AngelMinigame.clr("&aCountdown set to &6" + args[2] + "s &afor &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleFloor(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 3) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena floor <name> <y>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        try { arena.setFloorLevel(Integer.parseInt(args[2])); plugin.getArenaManager().save(); } catch (NumberFormatException e) { sender.sendMessage(AngelMinigame.clr("&cInvalid number.")); return true; }
        sender.sendMessage(AngelMinigame.clr("&aFloor level set to Y=" + args[2] + " for &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleReward(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 3) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena reward <name> <amount>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        try { arena.setCurrencyReward(Double.parseDouble(args[2])); plugin.getArenaManager().save(); } catch (NumberFormatException e) { sender.sendMessage(AngelMinigame.clr("&cInvalid number.")); return true; }
        sender.sendMessage(AngelMinigame.clr("&aCurrency reward set to &6" + args[2] + " &afor &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleBlocks(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 4) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena blocks <add|remove|list> <name> [material]")); return true; }
        Arena arena = plugin.getArenaManager().get(args[2]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        String action = args[1].toLowerCase();
        if (action.equals("list")) {
            sender.sendMessage(AngelMinigame.clr("&eBreakable blocks for &6" + arena.getName() + "&e: "
                + (arena.getBreakableBlocks().isEmpty() ? "&7none" : arena.getBreakableBlocks().toString())));
            return true;
        }
        if (args.length < 4) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena blocks <add|remove> <name> <material>")); return true; }
        Material mat = Material.matchMaterial(args[3].toUpperCase());
        if (mat == null) { sender.sendMessage(AngelMinigame.clr("&cUnknown material: " + args[3])); return true; }
        if (action.equals("add")) { arena.getBreakableBlocks().add(mat); }
        else { arena.getBreakableBlocks().remove(mat); }
        plugin.getArenaManager().save();
        sender.sendMessage(AngelMinigame.clr("&aBlocks updated for &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleRules(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 4) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena rules <name> <key> <value>")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        String key = args[2].toLowerCase();
        String val = args[3];
        switch (key) {
            case "pvp" -> arena.setAllowPvp(parseBool(val));
            case "block-break" -> arena.setAllowBlockBreak(parseBool(val));
            case "block-place" -> arena.setAllowBlockPlace(parseBool(val));
            case "death-eliminates" -> arena.setDeathEliminates(parseBool(val));
            case "fall-void" -> arena.setFallVoid(parseBool(val));
            case "max-time" -> { try { arena.setMaxTimeSeconds(Integer.parseInt(val)); } catch (NumberFormatException e) { sender.sendMessage(AngelMinigame.clr("&cInvalid number.")); return true; } }
            case "restore-arena" -> arena.setRestoreOnEnd(parseBool(val));
            default -> { sender.sendMessage(AngelMinigame.clr("&cUnknown rule: " + key)); return true; }
        }
        plugin.getArenaManager().save();
        sender.sendMessage(AngelMinigame.clr("&aRule &6" + key + "&a set to &6" + val + "&a for &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleAllowCmd(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        if (args.length < 3) { sender.sendMessage(AngelMinigame.clr("&cUsage: /arena allowcmd <name> <add|remove|list> [command]")); return true; }
        Arena arena = plugin.getArenaManager().get(args[1]);
        if (arena == null) { sender.sendMessage(AngelMinigame.clr("&cArena not found.")); return true; }
        String action = args[2].toLowerCase();
        if (action.equals("list")) {
            sender.sendMessage(AngelMinigame.clr("&eAllowed commands for &6" + arena.getName() + "&e: "
                + (arena.getAllowedCommands().isEmpty() ? "&7none" : arena.getAllowedCommands().toString())));
            return true;
        }
        if (args.length < 4) { sender.sendMessage(AngelMinigame.clr("&cSpecify a command.")); return true; }
        String cmd = args[3].toLowerCase();
        if (action.equals("add")) { arena.getAllowedCommands().add(cmd); }
        else { arena.getAllowedCommands().remove(cmd); }
        plugin.getArenaManager().save();
        sender.sendMessage(AngelMinigame.clr("&aAllowed commands updated for &6" + arena.getName() + "&a."));
        return true;
    }

    private boolean handleScript(CommandSender sender, String[] args) {
        if (!hasPerm(sender, "angelminigame.admin")) return true;
        sender.sendMessage(AngelMinigame.clr("&7Script editing is done through arenas.yml directly. Use /arena reload to apply changes."));
        return true;
    }

    private boolean parseBool(String val) {
        return val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes");
    }

    private boolean hasPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(AngelMinigame.clr("&cNo permission."));
            return false;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(AngelMinigame.clr("&6=== AngelMinigame Arena Commands ==="));
        sender.sendMessage(AngelMinigame.clr("&e/arena create <name> <mode> &7- Create an arena"));
        sender.sendMessage(AngelMinigame.clr("&e/arena delete <name> &7- Delete an arena"));
        sender.sendMessage(AngelMinigame.clr("&e/arena info <name> &7- Show arena info"));
        sender.sendMessage(AngelMinigame.clr("&e/arena list &7- List all arenas"));
        sender.sendMessage(AngelMinigame.clr("&e/arena reload &7- Reload arenas.yml"));
        sender.sendMessage(AngelMinigame.clr("&e/arena wand &7- Get selection wand"));
        sender.sendMessage(AngelMinigame.clr("&e/arena setregion <name> &7- Set region from selection"));
        sender.sendMessage(AngelMinigame.clr("&e/arena setlobby <name> &7- Set lobby spawn"));
        sender.sendMessage(AngelMinigame.clr("&e/arena setspawn <name> &7- Add player spawn"));
        sender.sendMessage(AngelMinigame.clr("&e/arena setspectator <name> &7- Set spectator spawn"));
        sender.sendMessage(AngelMinigame.clr("&e/arena minmax <name> <min> <max> &7- Player limits"));
        sender.sendMessage(AngelMinigame.clr("&e/arena countdown <name> <s> &7- Countdown duration"));
        sender.sendMessage(AngelMinigame.clr("&e/arena floor <name> <y> &7- Floor/void level"));
        sender.sendMessage(AngelMinigame.clr("&e/arena blocks <add|remove|list> <name> [mat] &7- Breakable blocks"));
        sender.sendMessage(AngelMinigame.clr("&e/arena rules <name> <key> <value> &7- Set game rules"));
        sender.sendMessage(AngelMinigame.clr("&e/arena allowcmd <name> <add|remove> <cmd> &7- Arena allowed commands"));
        sender.sendMessage(AngelMinigame.clr("&e/arena reward <name> <amount> &7- Currency reward"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("create", "delete", "info", "list", "reload", "wand", "setregion", "setlobby", "setspawn", "addspawn", "clearspawns", "setspectator", "minmax", "countdown", "floor", "reward", "blocks", "rules", "allowcmd", "script");
        if (args.length == 2) return plugin.getArenaManager().getArenaNames();
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("blocks")) return List.of("add", "remove", "list");
            if (sub.equals("allowcmd")) return List.of("add", "remove", "list");
            if (sub.equals("rules")) return List.of("pvp", "block-break", "block-place", "death-eliminates", "fall-void", "max-time", "restore-arena");
            if (sub.equals("create")) return List.of("SPLEEF", "TNT_RUN", "PARKOUR", "LMS", "CUSTOM");
        }
        return List.of();
    }
}
