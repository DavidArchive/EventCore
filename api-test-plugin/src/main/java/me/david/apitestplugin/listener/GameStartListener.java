package me.david.apitestplugin.listener;

import lombok.RequiredArgsConstructor;
import me.david.api.EventCoreAPI;
import me.david.api.events.game.GameStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class GameStartListener implements Listener {

    private final EventCoreAPI api;

    @EventHandler
    public void onGameStart(final GameStartEvent event) {
        Logger.getAnonymousLogger().info("Game start countdown: " + event.getStartTimer() + ", timer running: " + api.getGameManager().isTimerRunning());
    }
}
