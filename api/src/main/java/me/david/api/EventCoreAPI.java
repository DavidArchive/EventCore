package me.david.api;

import lombok.Getter;
import me.david.api.manager.GameManager;
import me.david.api.manager.KitManager;
import me.david.api.manager.MapManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Getter
public final class EventCoreAPI {

    private static EventCoreAPI instance;

    private final @NotNull Plugin plugin;
    private final @NotNull GameManager gameManager;
    private final @NotNull KitManager kitManager;
    private final @NotNull MapManager mapManager;

    private EventCoreAPI(
            @NotNull Plugin plugin,
            @NotNull GameManager gameManager,
            @NotNull KitManager kitManager,
            @NotNull MapManager mapManager
    ) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.kitManager = kitManager;
        this.mapManager = mapManager;
    }

    public static void initialize(
            @NotNull Plugin plugin,
            @NotNull GameManager gameManager,
            @NotNull KitManager kitManager,
            @NotNull MapManager mapManager
    ) {
        if (instance != null) {
            throw new IllegalStateException("EventCoreAPI is already initialized!");
        }
        instance = new EventCoreAPI(plugin, gameManager, kitManager, mapManager);
    }

    public static void shutdown() {
        instance = null;
    }

    public static @NotNull EventCoreAPI get() {
        if (instance == null) {
            throw new IllegalStateException("EventCoreAPI is not initialized yet!");
        }
        return instance;
    }
}
