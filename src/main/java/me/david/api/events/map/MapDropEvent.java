package me.david.api.events.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MapDropEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Location spawnLocation;
    private final double borderSize;

    /**
     * @param spawnLocation The spawn location
     * @param borderSize The world border size
     */
    public MapDropEvent(final Location spawnLocation, final double borderSize) {
        this.spawnLocation = spawnLocation;
        this.borderSize = borderSize;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
