package me.david.apitestplugin.listener;

import me.david.api.events.kit.KitGiveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class KitGiveListener implements Listener {

    @EventHandler
    public void onKitGive(final KitGiveEvent event) {
        Logger.getAnonymousLogger().info("Giving kit " + event.getKitName() + " to " + event.getPlayer().getName());
    }
}
