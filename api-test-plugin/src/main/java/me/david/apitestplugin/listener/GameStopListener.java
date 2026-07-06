package me.david.apitestplugin.listener;

import me.david.api.events.game.GameStopEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class GameStopListener implements Listener {

    @EventHandler
    public void onGameStop(final GameStopEvent event) {
        String winner = event.getWinner() == null ? "none" : event.getWinner();
        Logger.getAnonymousLogger().info("Game stopped. Winner: " + winner);
    }
}
