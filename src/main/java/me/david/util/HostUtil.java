package me.david.util;

import lombok.experimental.UtilityClass;
import me.david.EventCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

@UtilityClass
public class HostUtil {

    public LinkedList<@NotNull Player> host = new LinkedList<>();

    public boolean isHost(final @NotNull Player player) {
        return host.contains(player);
    }

    public void giveHost(final @NotNull Player player) {
        if (EventCore.getInstance().getConfig().getBoolean("Settings.HostRank.Enabled") && host.isEmpty()) {
            if (player.hasPermission(Objects.requireNonNull(EventCore.getInstance().getConfig().getString("Settings.HostRank.Permission"),"event.host"))) {
                String command = Objects.requireNonNull(EventCore.getInstance().getConfig().getString("Settings.HostRank.JoinCommand").replaceAll("%player%", player.getName()), "event.host");
                Scheduler.dispatchCommand(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }
            host.add(player);
        }
    }

    public void removeHost(final @NotNull Player player) {
        if (EventCore.getInstance().getConfig().getBoolean("Settings.HostRank.Enabled")) {
            if (player.hasPermission(Objects.requireNonNull(EventCore.getInstance().getConfig().getString("Settings.HostRank.Permission"),"event.host"))) {
                Scheduler.dispatchCommand(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Objects.requireNonNull(EventCore.getInstance().getConfig().getString("Settings.HostRank.QuitCommand").replaceAll("%player%", player.getName()), "event.host")));
            }
            host.remove(player);
        }
    }
}


