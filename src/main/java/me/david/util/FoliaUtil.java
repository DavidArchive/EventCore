package me.david.util;

import lombok.Getter;
import me.david.EventCore;
import me.david.listener.PlayerRespawnListener;
import me.david.listener.PlayerTeleportListener;
import me.david.listener.folia.CanvasPlayerRespawnListener;
import me.david.listener.folia.FoliaPlayerTeleportListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

public class FoliaUtil {
    @Getter
    private static final boolean FOLIA;
    @Getter
    private static final boolean CANVAS;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        FOLIA = folia;

        boolean canvas;
        try {
            Class.forName("io.canvasmc.canvas.event.PlayerPostRespawnAsyncEvent");
            canvas = true;
        } catch (ClassNotFoundException e) {
            canvas = false;
        }
        CANVAS = canvas;
    }

    public static void scheduleToOrRun(Entity entity, Runnable runnable) {
        if (Bukkit.isOwnedByCurrentRegion(entity)) {
            runnable.run();
            return;
        }

        entity.getScheduler().run(
                EventCore.getInstance(),
                task -> runnable.run(),
                null
        );
    }

    public static Listener getPlatformTeleportListener() {
        if (isCANVAS()) return new FoliaPlayerTeleportListener();
        return new PlayerTeleportListener();
    }

    public static Listener getPlatformRespawnListener() {
        if (isCANVAS()) return new CanvasPlayerRespawnListener();
        return new PlayerRespawnListener();
    }
}
