package me.david.util.folia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

// Credits @ PacketEvents

public class RegionScheduler {

    private BukkitScheduler bukkitScheduler;
    private io.papermc.paper.threadedregions.scheduler.RegionScheduler regionScheduler;

    protected RegionScheduler() {
        if (FoliaScheduler.isFolia) {
            regionScheduler = Bukkit.getRegionScheduler();
        } else {
            bukkitScheduler = Bukkit.getScheduler();
        }
    }

    public void execute(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Runnable run) {
        if (!FoliaScheduler.isFolia) {
            bukkitScheduler.runTask(plugin, run);
            return;
        }

        regionScheduler.execute(plugin, world, chunkX, chunkZ, run);
    }

    public void execute(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable run) {
        if (!FoliaScheduler.isFolia) {
            Bukkit.getScheduler().runTask(plugin, run);
            return;
        }

        regionScheduler.execute(plugin, location, run);
    }

    public TaskWrapper run(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Consumer<Object> task) {
        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, () -> task.accept(null)));
        }

        return new TaskWrapper(regionScheduler.run(plugin, world, chunkX, chunkZ, (o) -> task.accept(null)));
    }

    public TaskWrapper run(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task) {
        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, () -> task.accept(null)));
        }

        return new TaskWrapper(regionScheduler.run(plugin, location, (o) -> task.accept(null)));
    }

    public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Consumer<Object> task, long delayTicks) {
        if (delayTicks < 1) delayTicks = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), delayTicks));
        }

        return new TaskWrapper(regionScheduler.runDelayed(plugin, world, chunkX, chunkZ, (o) -> task.accept(null), delayTicks));
    }

    public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task, long delayTicks) {
        if (delayTicks < 1) delayTicks = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), delayTicks));
        }

        return new TaskWrapper(regionScheduler.runDelayed(plugin, location, (o) -> task.accept(null), delayTicks));
    }

    public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull World world, int chunkX, int chunkZ, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
        if (initialDelayTicks < 1) initialDelayTicks = 1;
        if (periodTicks < 1) periodTicks = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
        }

        return new TaskWrapper(regionScheduler.runAtFixedRate(plugin, world, chunkX, chunkZ, (o) -> task.accept(null), initialDelayTicks, periodTicks));
    }

    public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Location location, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
        if (initialDelayTicks < 1) initialDelayTicks = 1;
        if (periodTicks < 1) periodTicks = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
        }

        return new TaskWrapper(regionScheduler.runAtFixedRate(plugin, location, (o) -> task.accept(null), initialDelayTicks, periodTicks));
    }
}