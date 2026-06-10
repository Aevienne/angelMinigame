package me.angelique.angelMinigame.game.modes;

import me.angelique.angelMinigame.game.GameMode;
import me.angelique.angelMinigame.game.GameSession;
import org.bukkit.entity.Player;

public class LastManStandingMode extends GameMode {

    public LastManStandingMode() { super("LAST_MAN_STANDING"); }

    @Override
    public void onStart(GameSession session) {}

    @Override
    public void onPlayerDeath(Player player, GameSession session) {
        if (session.isAlive(player.getUniqueId())) {
            session.getGameManager().onPlayerEliminated(player, session, "death");
        }
    }
}
