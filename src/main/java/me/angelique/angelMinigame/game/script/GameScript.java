package me.angelique.angelMinigame.game.script;

import me.angelique.angelMinigame.AngelMinigame;
import me.angelique.angelMinigame.game.GameSession;

import java.util.*;

public class GameScript {

    private final GameSession session;
    private final ActionRegistry registry;
    private final List<ScheduledAction> scheduledActions = new ArrayList<>();

    public GameScript(GameSession session, List<Map<String, Object>> scriptEntries, ActionRegistry registry) {
        this.session = session;
        this.registry = registry;
        for (Map<String, Object> entry : scriptEntries) {
            scheduledActions.add(new ScheduledAction(entry));
        }
    }

    public void onStart() {
        for (ScheduledAction sa : scheduledActions) {
            if (sa.shouldFireOnStart()) { fireActions(sa); }
        }
    }

    public void onTick() {
        double elapsed = session.getElapsedSeconds();
        for (ScheduledAction sa : scheduledActions) {
            if (sa.shouldFire(elapsed)) { fireActions(sa); }
        }
    }

    public void onEnd() {
        for (ScheduledAction sa : scheduledActions) {
            if (sa.shouldFireOnWin()) { fireActions(sa); }
        }
    }

    public void onReset() {
        for (ScheduledAction sa : scheduledActions) {
            if (sa.shouldFireOnReset()) { fireActions(sa); }
        }
    }

    public void onElimination(UUID eliminatedUuid) {
        boolean firstDeath = !session.isFirstDeathFired();
        if (firstDeath) session.setFirstDeathFired(true);
        int alive = session.getAlivePlayers().size();
        for (ScheduledAction sa : scheduledActions) {
            if (sa.shouldFireOnDeath(alive, firstDeath)) { fireActions(sa); }
        }
    }

    @SuppressWarnings("unchecked")
    private void fireActions(ScheduledAction sa) {
        List<Map<String, Object>> actionDefs = sa.getActions();
        if (actionDefs.isEmpty()) {
            actionDefs = extractFromTopLevel(sa.getRaw());
        }
        if (actionDefs.isEmpty()) return;

        for (Map<String, Object> actionDef : actionDefs) {
            Map<String, Object> resolved = VariableResolver.resolveMap(actionDef, session, sa.getExecutedCount());
            for (Map.Entry<String, Object> entry : resolved.entrySet()) {
                String actionName = entry.getKey();
                Action action = registry.get(actionName);
                if (action == null) continue;
                Map<String, Object> params = new LinkedHashMap<>();
                if (entry.getValue() instanceof Map) {
                    params = (Map<String, Object>) entry.getValue();
                }
                params = VariableResolver.resolveMap(params, session, sa.getExecutedCount());
                action.execute(session, params);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractFromTopLevel(Map<String, Object> raw) {
        Map<String, Object> single = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : raw.entrySet()) {
            String k = e.getKey();
            if (!k.equals("at") && !k.equals("every") && !k.equals("repeat") && !k.equals("on")
                && !k.equals("count") && !k.equals("actions") && !k.equals("action")) {
                single.put(k, e.getValue());
            }
        }
        return single.isEmpty() ? List.of() : List.of(single);
    }
}
