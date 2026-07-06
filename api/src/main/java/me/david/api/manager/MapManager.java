package me.david.api.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface MapManager {

    Location getSpawnLocation();

    void saveSpawnLocation(@NotNull Player player);

    void drop();

    void reset();
}
