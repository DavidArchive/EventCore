package me.david.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import me.david.EventCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

@Getter
@SuppressWarnings("unused")
public class Scheduler {

    @Getter
    private static final boolean FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        FOLIA = folia;
    }

    public interface TaskWrapper {
        void cancel();
        boolean isCancelled();
    }

    private static class BukkitTaskWrapper implements TaskWrapper {
        private final @NotNull BukkitTask task;

        public BukkitTaskWrapper(@NotNull BukkitTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }
    }

    private static class FoliaTaskWrapper implements TaskWrapper {
        private @Nullable Object task;

        public FoliaTaskWrapper(@NotNull Object task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            if (task != null) {
                try {
                    Class<?> iface = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
                    iface.getMethod("cancel").invoke(task);
                } catch (Exception ignored) {
                } finally {
                    task = null;
                }
            }
        }

        @Override
        public boolean isCancelled() {
            if (task == null) return true;
            try {
                Class<?> iface = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
                return (Boolean) iface.getMethod("isCancelled").invoke(task);
            } catch (Exception ignored) {
                return true;
            }
        }
    }

    public static void runSync(@NotNull Runnable runnable) {
        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().execute(EventCore.getInstance(), runnable);
        } else {
            Bukkit.getScheduler().runTask(EventCore.getInstance(), runnable);
        }
    }

    @CanIgnoreReturnValue
    public static @NotNull TaskWrapper wait(@NotNull Runnable runnable, long delay) {
        final long safeDelay = Math.max(delay, 1);

        if (FOLIA) {
            Object foliaTask = Bukkit.getGlobalRegionScheduler().runDelayed(EventCore.getInstance(),
                    (task) -> runnable.run(), safeDelay);
            return new FoliaTaskWrapper(foliaTask);
        } else {
            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(EventCore.getInstance(), runnable, safeDelay);
            return new BukkitTaskWrapper(bukkitTask);
        }
    }

    @CanIgnoreReturnValue
    public static @NotNull TaskWrapper timer(@NotNull Runnable runnable, long delay, long period) {
        final long safeDelay = Math.max(delay, 1);
        final long safePeriod = Math.max(period, 1);

        if (FOLIA) {
            Object foliaTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(EventCore.getInstance(),
                    (task) -> runnable.run(), safeDelay, safePeriod);
            return new FoliaTaskWrapper(foliaTask);
        } else {
            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(EventCore.getInstance(), runnable, safeDelay, safePeriod);
            return new BukkitTaskWrapper(bukkitTask);
        }
    }

    @CanIgnoreReturnValue
    public static @NotNull TaskWrapper timerAsync(@NotNull Runnable runnable, long delay, long period) {
        final long safeDelay = Math.max(delay, 1);
        final long safePeriod = Math.max(period, 1);

        if (FOLIA) {
            Object foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(EventCore.getInstance(),
                    (task) -> runnable.run(), safeDelay * 50, safePeriod * 50, TimeUnit.MILLISECONDS);
            return new FoliaTaskWrapper(foliaTask);
        } else {
            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(EventCore.getInstance(),
                    runnable, safeDelay, safePeriod);
            return new BukkitTaskWrapper(bukkitTask);
        }
    }

    public static void cancelTask(@Nullable Object task) {
        if (task == null) return;

        if (task instanceof TaskWrapper wrapper) {
            wrapper.cancel();
        } else if (task instanceof BukkitTask bukkitTask) {
            bukkitTask.cancel();
        } else if (FOLIA) {
            try {
                task.getClass().getMethod("cancel").invoke(task);
            } catch (Exception ignored) {
            }
        }
    }

    public static void dispatchCommand(@NotNull Runnable commandRunnable) {
        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().execute(Homes.getInstance(), commandRunnable);
        } else {
            commandRunnable.run();
        }
    }
}
