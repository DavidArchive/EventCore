package me.david.api.events.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SpawnLocationChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Player player;
    private Location newLocation;
    private final Location oldLocation;

    /**
     * @param player The player setting the spawn location
     * @param newLocation The new spawn location
     * @param oldLocation The old spawn location
     */
    public SpawnLocationChangeEvent(final Player player, final Location newLocation, final Location oldLocation) {
        this.player = player;
        this.newLocation = newLocation;
        this.oldLocation = oldLocation;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
