package me.angelique.angelMinigame.game.script;

import me.angelique.angelMinigame.game.GameSession;
import java.util.Map;

public interface Action {
    String getType();
    void execute(GameSession session, Map<String, Object> params);
}
