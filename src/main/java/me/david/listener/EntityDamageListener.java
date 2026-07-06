package me.david.listener;

import me.david.EventCore;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (EventCore.getInstance().getConfig().getBoolean("Settings.DisableFallDamage", true)) {
            if (event.getCause() == EntityDamageByEntityEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
        }

        if (!(EventCore.getInstance().getGameManager().isRunning())) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getEntity() instanceof Player player)) return;

        if (EventCore.getInstance().getConfig().getBoolean("Settings.WorldBorder.Boost.Enabled")) {
            if (event.getCause() == EntityDamageEvent.DamageCause.WORLD_BORDER) {
                World world = player.getWorld();
                WorldBorder worldBorder = world.getWorldBorder();
                if (!(worldBorder.isInside(player.getLocation()))) {
                    double boostXZ = EventCore.getInstance().getConfig().getDouble("Settings.WorldBorder.Boost.StrengthXZ", 1.3);
                    double boostY = EventCore.getInstance().getConfig().getDouble("Settings.WorldBorder.Boost.StrengthY", 0.1);
                    double maxX = worldBorder.getCenter().getBlockX() + worldBorder.getSize() / 2;
                    double minX = worldBorder.getCenter().getBlockX() - worldBorder.getSize() / 2;
                    double maxZ = worldBorder.getCenter().getBlockZ() + worldBorder.getSize() / 2;
                    double minZ = worldBorder.getCenter().getBlockZ() - worldBorder.getSize() / 2;
                    if (player.getLocation().getBlockX() > maxX) {
                        player.setVelocity(player.getVelocity().add(new Vector(-boostXZ, boostY, 0)));
                    } else if (player.getLocation().getBlockX() < minX) {
                        player.setVelocity(player.getVelocity().add(new Vector(boostXZ, boostY, 0)));
                    }
                    if (player.getLocation().getBlockZ() > maxZ) {
                        player.setVelocity(player.getVelocity().add(new Vector(0, boostY, -boostXZ)));
                    } else if (player.getLocation().getBlockZ() < minZ) {
                        player.setVelocity(player.getVelocity().add(new Vector(0, boostY, boostXZ)));
                    }
                }
            }
        }
    }

}
