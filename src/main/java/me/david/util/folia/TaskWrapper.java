package me.david.util.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

// Credits @ PacketEvents

public class TaskWrapper {

    private BukkitTask bukkitTask;
    private ScheduledTask scheduledTask;

    public TaskWrapper(@NotNull BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public TaskWrapper(@NotNull ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public Plugin getOwner() {
        return bukkitTask != null ? bukkitTask.getOwner() : scheduledTask.getOwningPlugin();
    }

    public boolean isCancelled() {
        return bukkitTask != null ? bukkitTask.isCancelled() : scheduledTask.isCancelled();
    }

    public void cancel() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
        } else {
            scheduledTask.cancel();
        }
    }
}