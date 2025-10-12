package me.david.manager;

import lombok.Getter;
import me.david.EventCore;
import me.david.api.events.map.MapDropEvent;
import me.david.api.events.map.MapResetEvent;
import me.david.api.events.map.SpawnLocationChangeEvent;
import me.david.util.LocationUtil;
import me.david.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class MapManager {

    private Location spawnLocation;

    public MapManager() {
        Scheduler.wait(() -> spawnLocation = LocationUtil.fromString(EventCore.getInstance().getConfig().getString("Settings.SpawnLocation", "world/0/200/0")), 2);
    }

    public void saveSpawnLocation(@NotNull final Player player) {
        Location oldLocation = spawnLocation;
        Location newLocation = player.getLocation();

        SpawnLocationChangeEvent spawnLocationChangeEvent = new SpawnLocationChangeEvent(player, newLocation, oldLocation);
        Bukkit.getPluginManager().callEvent(spawnLocationChangeEvent);

        if (spawnLocationChangeEvent.isCancelled()) {
            return;
        }

        String location = LocationUtil.toString(spawnLocationChangeEvent.getNewLocation());
        spawnLocation = spawnLocationChangeEvent.getNewLocation();

        EventCore.getInstance().getConfig().set("Settings.SpawnLocation", location);
        EventCore.getInstance().saveConfig();
    }

    public void drop() {
        long borderExtra = EventCore.getInstance().getConfig().getLong("Settings.Drop.BorderExtra", 3);
        double borderSize = spawnLocation.getWorld().getWorldBorder().getSize();

        MapDropEvent mapDropEvent = new MapDropEvent(spawnLocation, borderSize);
        Bukkit.getPluginManager().callEvent(mapDropEvent);

        if (mapDropEvent.isCancelled()) {
            return;
        }

        Location edgeMin = spawnLocation.clone().subtract(borderSize / 2D + borderExtra, 0, borderSize / 2D + borderExtra);
        Location edgeMax = spawnLocation.clone().add(borderSize / 2D + borderExtra, 0, borderSize / 2D + borderExtra);

        Scheduler.dispatchCommand(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world " + spawnLocation.getWorld().getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + edgeMin.getBlockX() + ",-63," + edgeMin.getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + edgeMax.getBlockX() + ",350," + edgeMax.getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/set 0");
            EventCore.getInstance().getConfig().getStringList("Settings.Drop.CustomCommands").forEach(command ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(1)));
        });
    }

    public void reset() {
        MapResetEvent mapResetEvent = new MapResetEvent();
        Bukkit.getPluginManager().callEvent(mapResetEvent);

        if (mapResetEvent.isCancelled()) {
            return;
        }

        Scheduler.dispatchCommand(() -> EventCore.getInstance().getConfig().getStringList("Settings.MapReset.Commands").forEach(command ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(1))));
    }

}
