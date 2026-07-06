package me.david.api.events.game;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class GameTimerTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final int remainingSeconds;

    /**
     * @param remainingSeconds The remaining seconds in the countdown
     */
    public GameTimerTickEvent(final int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
