package me.angelique.angelMinigame.game.script;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {
    private final Map<String, Action> actions = new HashMap<>();

    public void register(String name, Action action) {
        actions.put(name.toLowerCase(), action);
    }

    public Action get(String name) {
        return actions.get(name.toLowerCase());
    }

    public boolean has(String name) {
        return actions.containsKey(name.toLowerCase());
    }
}
