package me.david.api;

import lombok.Getter;
import me.david.EventCore;
import me.david.manager.GameManager;
import me.david.manager.KitManager;
import me.david.manager.MapManager;
import org.jetbrains.annotations.NotNull;

@Getter
public final class EventCoreAPI {

    private static EventCoreAPI instance;

    private final @NotNull EventCore plugin;
    private final @NotNull GameManager gameManager;
    private final @NotNull KitManager kitManager;
    private final @NotNull MapManager mapManager;

    private EventCoreAPI(@NotNull EventCore plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.kitManager = plugin.getKitManager();
        this.mapManager = plugin.getMapManager();
    }

    public static void initialize(@NotNull EventCore plugin) {
        if (instance != null) {
            throw new IllegalStateException("EventCoreAPI is already initialized!");
        }
        instance = new EventCoreAPI(plugin);
    }

    public static @NotNull EventCoreAPI get() {
        if (instance == null) {
            throw new IllegalStateException("EventCoreAPI is not initialized yet!");
        }
        return instance;
    }
}
