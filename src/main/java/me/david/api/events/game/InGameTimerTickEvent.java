package me.david.api.events.game;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class InGameTimerTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final long elapsedSeconds;

    /**
     * @param elapsedSeconds The elapsed seconds since game start
     */
    public InGameTimerTickEvent(final long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
