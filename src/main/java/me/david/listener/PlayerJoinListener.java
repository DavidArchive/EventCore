package me.david.listener;

import me.david.EventCore;
import me.david.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        HostUtil.giveHost(player);

        if (EventCore.getInstance().getConfig().getBoolean("Messages.PlayerJoin.Enabled")) {
            Component message = MessageUtil.getPrefix().append(MessageUtil.format("Messages.PlayerJoin.Message", Map.of("%player%", Component.text(player.getName()))));
            event.joinMessage(message);
        } else {
            event.joinMessage(Component.empty());
        }

        player.teleportAsync(EventCore.getInstance().getMapManager().getSpawnLocation());
        PlayerUtil.cleanPlayer(player);
        if (EventCore.getInstance().getGameManager().isRunning()) {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
        }
        Scheduler.wait(() -> {
            player.teleportAsync(EventCore.getInstance().getMapManager().getSpawnLocation());
            if (EventCore.getInstance().getGameManager().isRunning()) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }, 2);

        if (player.hasPermission("event.notify") && EventCore.getInstance().getConfig().getBoolean("Settings.Updates.NotifyOnJoin")) {
            UpdateChecker updateChecker = new UpdateChecker(EventCore.getInstance(), "DavidArchive", "EventCore");
            updateChecker.check();

            Bukkit.getScheduler().runTaskLater(EventCore.getInstance(), () -> {
                if (updateChecker.isHasUpdate()) {
                    player.sendMessage(Component.empty());
                    player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("You're running an outdated version of EventCore. Please update to the latest version:")));
                    player.sendMessage(updateChecker.getUpdateComponent());
                    player.sendMessage(Component.empty());
                }
            }, 20L);
        }
    }

}
