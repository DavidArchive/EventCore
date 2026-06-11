package me.david.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import me.david.EventCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

@Getter
@SuppressWarnings("unused")
public class Scheduler {
    public static void runSync(@NotNull Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().execute(EventCore.getInstance(), runnable);
    }

    @CanIgnoreReturnValue
    public static @NotNull ScheduledTask runLaterSync(@NotNull Runnable runnable, long delay) {
        final long safeDelay = Math.max(delay, 1);
        return Bukkit.getGlobalRegionScheduler().runDelayed(EventCore.getInstance(), task -> {
            runnable.run();
        }, safeDelay);
    }

    @CanIgnoreReturnValue
    public static @NotNull ScheduledTask repeated(@NotNull Runnable runnable, long delay, long period) {
        final long safeDelay = Math.max(delay, 1);
        final long safePeriod = Math.max(period, 1);

        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(EventCore.getInstance(), task -> {
            runnable.run();
        }, safeDelay, safePeriod);
    }

    @CanIgnoreReturnValue
    public static @NotNull ScheduledTask repeatedAsync(@NotNull Runnable runnable, long delay, long period) {
        final long safeDelay = Math.max(delay, 1);
        final long safePeriod = Math.max(period, 1);

        return Bukkit.getAsyncScheduler().runAtFixedRate(EventCore.getInstance(), task -> {
            runnable.run();
        }, safeDelay * 50, safePeriod * 50, TimeUnit.MILLISECONDS);
    }

    public static void cancelTask(@Nullable ScheduledTask task) {
        if (task == null) return;

        task.cancel();
    }

    public static void dispatchCommand(@NotNull Runnable commandRunnable) {
        Bukkit.getGlobalRegionScheduler().execute(EventCore.getInstance(), commandRunnable);
    }
}
