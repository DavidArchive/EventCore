package me.david.util.folia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

// Credits @ PacketEvents

public class AsyncScheduler {

    private BukkitScheduler bukkitScheduler;
    private io.papermc.paper.threadedregions.scheduler.AsyncScheduler asyncScheduler;

    protected AsyncScheduler() {
        if (FoliaScheduler.isFolia) {
            asyncScheduler = Bukkit.getAsyncScheduler();
        } else {
            bukkitScheduler = Bukkit.getScheduler();
        }
    }

    public TaskWrapper runNow(@NotNull Plugin plugin, @NotNull Consumer<Object> task) {
        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(bukkitScheduler.runTaskAsynchronously(plugin, () -> task.accept(null)));
        }

        return new TaskWrapper(asyncScheduler.runNow(plugin, (o) -> task.accept(null)));
    }

    public TaskWrapper runDelayed(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(bukkitScheduler.runTaskLaterAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit)));
        }

        return new TaskWrapper(asyncScheduler.runDelayed(plugin, (o) -> task.accept(null), delay, timeUnit));
    }

    public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        if (period < 1) period = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(bukkitScheduler.runTaskTimerAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit), convertTimeToTicks(period, timeUnit)));
        }

        return new TaskWrapper(asyncScheduler.runAtFixedRate(plugin, (o) -> task.accept(null), delay, period, timeUnit));
    }

    public TaskWrapper runAtFixedRate(@NotNull Plugin plugin, @NotNull Consumer<Object> task, long initialDelayTicks, long periodTicks) {
        if (periodTicks < 1) periodTicks = 1;

        if (!FoliaScheduler.isFolia) {
            return new TaskWrapper(bukkitScheduler.runTaskTimerAsynchronously(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
        }

        return new TaskWrapper(asyncScheduler.runAtFixedRate(plugin, (o) -> task.accept(null), initialDelayTicks * 50, periodTicks * 50, TimeUnit.MILLISECONDS));
    }

    public void cancel(@NotNull Plugin plugin) {
        if (!FoliaScheduler.isFolia) {
            bukkitScheduler.cancelTasks(plugin);
            return;
        }

        asyncScheduler.cancelTasks(plugin);
    }

    private long convertTimeToTicks(long time, TimeUnit timeUnit) {
        return timeUnit.toMillis(time) / 50;
    }
}