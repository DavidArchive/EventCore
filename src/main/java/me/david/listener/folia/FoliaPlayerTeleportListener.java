package me.david.listener.folia;

import me.david.EventCore;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class FoliaPlayerTeleportListener implements Listener {
    // this implementation theoretically works on paper, folia and canvas, but the respawn implementation doesnt
    // but incase of future compatibility of respawn event with folia, keep this non-specific to canvas.
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        final Location to = pearl.getLocation();
        final World world = to.getWorld();

        if (EventCore.getInstance().getConfig().getBoolean("Settings.WorldBorder.DisableEnderPeals")) {
            if (!(world.getWorldBorder().isInside(to))) {
                pearl.setShooter(null);
            }
        }
    }
}
