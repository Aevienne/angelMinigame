package me.angelique.angelMinigame.game.script;

import me.angelique.angelMinigame.arena.Arena;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class VariableResolver {

    private static final Random RANDOM = new Random();

    public static String resolve(String input, GameSession session, int iteration) {
        if (input == null) return null;
        Arena arena = session.getArena();
        String result = input
            .replace("{elapsed}", String.valueOf(session.getElapsedSeconds()))
            .replace("{remaining}", String.valueOf(session.getAlivePlayers().size()))
            .replace("{iteration}", String.valueOf(iteration))
            .replace("{arena_min_x}", String.valueOf(arena.getMinX()))
            .replace("{arena_min_y}", String.valueOf(arena.getMinY()))
            .replace("{arena_min_z}", String.valueOf(arena.getMinZ()))
            .replace("{arena_max_x}", String.valueOf(arena.getMaxX()))
            .replace("{arena_max_y}", String.valueOf(arena.getMaxY()))
            .replace("{arena_max_z}", String.valueOf(arena.getMaxZ()))
            .replace("{arena_name}", arena.getName());

        if (result.contains("{rand_player}")) {
            var alive = new java.util.ArrayList<>(session.getAlivePlayers());
            if (!alive.isEmpty()) {
                UUID randUuid = alive.get(RANDOM.nextInt(alive.size()));
                Player rp = Bukkit.getPlayer(randUuid);
                result = result.replace("{rand_player}", rp != null ? rp.getName() : "?");
            }
        }

        if (result.contains("{winner}")) {
            UUID winnerUuid = session.getWinner();
            Player w = winnerUuid != null ? Bukkit.getPlayer(winnerUuid) : null;
            result = result.replace("{winner}", w != null ? w.getName() : "Nobody");
        }

        return result;
    }

    public static double resolveDouble(String input, GameSession session, int iteration) {
        String resolved = resolve(input, session, iteration);
        try { return Double.parseDouble(resolved); } catch (NumberFormatException e) { return 0; }
    }

    public static int resolveInt(String input, GameSession session, int iteration) {
        String resolved = resolve(input, session, iteration);
        try { return Integer.parseInt(resolved); } catch (NumberFormatException e) { return 0; }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> resolveMap(Map<String, Object> params, GameSession session, int iteration) {
        Map<String, Object> resolved = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof String s) {
                resolved.put(entry.getKey(), resolve(s, session, iteration));
            } else if (entry.getValue() instanceof Map) {
                resolved.put(entry.getKey(), resolveMap((Map<String, Object>) entry.getValue(), session, iteration));
            } else {
                resolved.put(entry.getKey(), entry.getValue());
            }
        }
        return resolved;
    }
}
