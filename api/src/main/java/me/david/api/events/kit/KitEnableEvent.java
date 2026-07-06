package me.david.api.events.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class KitEnableEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final String kitName;
    private final String previousKit;

    /**
     * @param kitName The name of the kit being enabled
     * @param previousKit The name of the previously enabled kit
     */
    public KitEnableEvent(final String kitName, final String previousKit) {
        this.kitName = kitName;
        this.previousKit = previousKit;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
