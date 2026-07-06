package me.david.util.folia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

// Credits @ PacketEvents

public class GlobalRegionScheduler {

    private BukkitScheduler bukkitScheduler;
    private io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler globalRegionScheduler;

    protected GlobalRegionScheduler() {
        if (FoliaScheduler.isFolia) {
            globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
        } else {
            bukkitScheduler = Bukkit.getScheduler();
        }
    }

    public void execute(@NotNull Plugin plugin, @NotNull Runnable run) {
        if (!FoliaScheduler.isFolia) {
            bukkitScheduler.runTask(plugin, run);
            return;
        }

        globalRegionScheduler.execute(plugin, run);
    }

    public TaskWrapper run(@NotNull Plugin plugin, @NotNull Consumer<Object> task) {
        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(bukkitScheduler.runTask(plugin, () -> task.accept(null)));
        }

        return new TaskWrapper(globalRegionScheduler.run(plugin, (o) -> task.accept(null)));
    }

    public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long delay) {
        if (delay < 1) delay = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(bukkitScheduler.runTaskLater(plugin, () -> task.accept(null), delay));
        }

        return new TaskWrapper(globalRegionScheduler.runDelayed(plugin, (o) -> task.accept(null), delay));
    }

    public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
        if (initialDelayTicks < 1) initialDelayTicks = 1;
        if (periodTicks < 1) periodTicks = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(bukkitScheduler.runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
        }

        return new TaskWrapper(globalRegionScheduler.runAtFixedRate(plugin, (o) -> task.accept(null), initialDelayTicks, periodTicks));
    }

    public void cancel(@NotNull Plugin plugin) {
        if (!FoliaScheduler.isFolia) {
            Bukkit.getScheduler().cancelTasks(plugin);
            return;
        }

        globalRegionScheduler.cancelTasks(plugin);
    }
}