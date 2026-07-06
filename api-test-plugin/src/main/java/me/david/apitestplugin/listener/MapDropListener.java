package me.david.apitestplugin.listener;

import me.david.api.events.map.MapDropEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class MapDropListener implements Listener {

    @EventHandler
    public void onMapDrop(final MapDropEvent event) {
        Logger.getAnonymousLogger().info("Map drop at " + event.getSpawnLocation() + " with border size " + event.getBorderSize());
    }
}
