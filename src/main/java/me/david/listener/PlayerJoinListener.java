package me.david.listener;

import me.david.EventCore;
import me.david.util.*;
import me.david.util.folia.FoliaScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        HostUtil.giveHost(player);

        List<String> joinCommands = EventCore.getInstance().getConfig().getStringList("Settings.PlayerJoin.Commands");
        for (String command : joinCommands) {
            final String finalCommand = command.replace("%player%", player.getName()).substring(1);
            FoliaScheduler.getGlobalRegionScheduler().execute(EventCore.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
        }

        if (EventCore.getInstance().getConfig().getBoolean("Messages.PlayerJoin.Enabled")) {
            Component message = MessageUtil.getPrefix().append(MessageUtil.format("Messages.PlayerJoin.Message", Map.of("%player%", Component.text(player.getName()))));
            event.joinMessage(message);
        } else {
            event.joinMessage(Component.empty());
        }

        PlayerUtil.cleanPlayer(player);
        if (EventCore.getInstance().getGameManager().isRunning()) {
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
        }
        FoliaScheduler.getEntityScheduler().runDelayed(player, EventCore.getInstance(), o -> {
            player.teleportAsync(EventCore.getInstance().getMapManager().getSpawnLocation());
            if (EventCore.getInstance().getGameManager().isRunning()) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }, null, 2);

        if (player.hasPermission("event.notify") && EventCore.getInstance().getConfig().getBoolean("Settings.Updates.NotifyOnJoin")) {
            UpdateChecker updateChecker = new UpdateChecker(EventCore.getInstance(), "DavidArchive", "EventCore");
            updateChecker.check();

            FoliaScheduler.getEntityScheduler().runDelayed(player, EventCore.getInstance(), o -> {
                if (updateChecker.isHasUpdate()) {
                    player.sendMessage(Component.empty());
                    player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("You're running an outdated version of EventCore. Please update to the latest version:")));
                    player.sendMessage(updateChecker.getUpdateComponent());
                    player.sendMessage(Component.empty());
                }
            }, null, 20L);
        }
    }

}
