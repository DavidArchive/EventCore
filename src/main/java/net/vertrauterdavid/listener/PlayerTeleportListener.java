package net.vertrauterdavid.listener;

import net.vertrauterdavid.EventCore;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        final Location to = event.getTo();
        final World world = to.getWorld();

        if (EventCore.getInstance().getConfig().getBoolean("Settings.WorldBorder.DisableEnderPeals")) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                if (!(world.getWorldBorder().isInside(to))) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
