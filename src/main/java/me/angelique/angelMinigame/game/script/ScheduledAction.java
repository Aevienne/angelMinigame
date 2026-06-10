package me.angelique.angelMinigame.game.script;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScheduledAction {
    public enum TriggerType {
        AT, EVERY, REPEAT, ON_START, ON_DEATH, ON_FIRST_DEATH, ON_COUNT, ON_WIN, ON_RESET
    }

    private final TriggerType triggerType;
    private final double atSeconds;
    private final double intervalSeconds;
    private final int repeatCount;
    private int executedCount;
    private int countThreshold;
    private double nextFireTime;
    private double startTimeOffset;
    private final Map<String, Object> raw;

    @SuppressWarnings("unchecked")
    public ScheduledAction(Map<String, Object> raw) {
        this.raw = raw;
        if (raw.containsKey("at")) {
            this.triggerType = TriggerType.AT;
            this.atSeconds = toDouble(raw.get("at"));
            this.intervalSeconds = 0;
            this.repeatCount = 0;
            this.countThreshold = 0;
            this.nextFireTime = this.atSeconds;
        } else if (raw.containsKey("every")) {
            this.triggerType = TriggerType.EVERY;
            this.intervalSeconds = toDouble(raw.get("every"));
            this.atSeconds = 0;
            this.repeatCount = 0;
            this.countThreshold = 0;
            this.nextFireTime = this.intervalSeconds;
        } else if (raw.containsKey("repeat")) {
            this.triggerType = TriggerType.REPEAT;
            Object repeat = raw.get("repeat");
            if (repeat instanceof Map) {
                Map<String, Object> r = (Map<String, Object>) repeat;
                this.repeatCount = toInt(r.get("count"));
                this.intervalSeconds = toDouble(r.get("every"));
            } else {
                this.repeatCount = toInt(raw.get("count"));
                this.intervalSeconds = toDouble(raw.get("every"));
            }
            this.atSeconds = 0;
            this.countThreshold = 0;
            this.nextFireTime = this.intervalSeconds;
        } else if (raw.containsKey("on")) {
            this.atSeconds = 0;
            this.intervalSeconds = 0;
            this.repeatCount = 0;
            this.countThreshold = 0;
            Object onVal = raw.get("on");
            String onStr = onVal.toString().toLowerCase();
            switch (onStr) {
                case "start": this.triggerType = TriggerType.ON_START; break;
                case "death": this.triggerType = TriggerType.ON_DEATH; break;
                case "first_death": this.triggerType = TriggerType.ON_FIRST_DEATH; break;
                case "win": this.triggerType = TriggerType.ON_WIN; break;
                case "reset": this.triggerType = TriggerType.ON_RESET; break;
                default:
                    if (onStr.startsWith("count")) {
                        this.triggerType = TriggerType.ON_COUNT;
                        this.countThreshold = Integer.parseInt(onStr.substring(5).replace("<", ""));
                    } else {
                        this.triggerType = TriggerType.ON_START;
                    }
                    break;
            }
        } else {
            this.triggerType = TriggerType.ON_START;
            this.atSeconds = 0;
            this.intervalSeconds = 0;
            this.repeatCount = 0;
            this.countThreshold = 0;
        }
        this.executedCount = 0;
        this.nextFireTime = 0;
        this.startTimeOffset = 0;
    }

    public void setStartTimeOffset(double offset) { this.startTimeOffset = offset; }

    public TriggerType getTriggerType() { return triggerType; }
    public boolean hasFired() { return executedCount > 0; }
    public int getExecutedCount() { return executedCount; }
    public Map<String, Object> getRaw() { return raw; }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getActions() {
        if (raw.containsKey("action")) {
            Map<String, Object> single = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : raw.entrySet()) {
                if (!e.getKey().equals("at") && !e.getKey().equals("every")
                    && !e.getKey().equals("repeat") && !e.getKey().equals("on")
                    && !e.getKey().equals("count") && !e.getKey().equals("actions")) {
                    if (e.getValue() instanceof Map) {
                        single.put(e.getKey(), e.getValue());
                    } else {
                        single.put(e.getKey(), e.getValue());
                    }
                }
            }
            if (!single.isEmpty()) return List.of(single);
        }
        Object actionsObj = raw.get("actions");
        if (actionsObj instanceof List) {
            return (List<Map<String, Object>>) (Object) ((List<?>) actionsObj).stream()
                .filter(o -> o instanceof Map).map(o -> (Map<String, Object>) o).toList();
        }
        return List.of();
    }

    public boolean shouldFire(double elapsedSeconds) {
        double adjustedTime = elapsedSeconds - startTimeOffset;
        switch (triggerType) {
            case AT:
                if (adjustedTime >= atSeconds && !hasFired()) { executedCount++; return true; }
                return false;
            case EVERY:
                if (adjustedTime >= nextFireTime) { executedCount++; nextFireTime += intervalSeconds; return true; }
                return false;
            case REPEAT:
                if (executedCount < repeatCount && adjustedTime >= nextFireTime) {
                    executedCount++; nextFireTime += intervalSeconds; return true;
                }
                return false;
            default:
                return false;
        }
    }

    public boolean shouldFireOnDeath(int aliveCount, boolean firstDeath) {
        if (triggerType == TriggerType.ON_DEATH && !hasFired()) return true;
        if (triggerType == TriggerType.ON_FIRST_DEATH && firstDeath && !hasFired()) { executedCount++; return true; }
        if (triggerType == TriggerType.ON_COUNT && aliveCount == countThreshold && !hasFired()) { executedCount++; return true; }
        return false;
    }

    public boolean shouldFireOnWin() { return triggerType == TriggerType.ON_WIN && !hasFired(); }
    public boolean shouldFireOnReset() { return triggerType == TriggerType.ON_RESET && !hasFired(); }
    public boolean shouldFireOnStart() { return triggerType == TriggerType.ON_START && !hasFired(); }

    private double toDouble(Object o) {
        if (o instanceof Number n) return n.doubleValue();
        if (o instanceof String s) {
            s = s.toLowerCase().replace("s", "").replace("t", "").replace(" ", "");
            try { return Double.parseDouble(s); } catch (NumberFormatException ignored) {}
        }
        return 0;
    }

    private int toInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        if (o instanceof String s) {
            s = s.replaceAll("[^0-9]", "");
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return 0;
    }
}
