package me.david.listener;

import me.david.EventCore;
import me.david.util.HostUtil;
import me.david.util.MessageUtil;
import me.david.util.PlayerUtil;
import me.david.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        HostUtil.giveHost(player);

        if (EventCore.getInstance().getConfig().getBoolean("Messages.PlayerJoin.Enabled")) {
            event.setJoinMessage(MessageUtil.getPrefix() + MessageUtil.get("Messages.PlayerJoin.Message").replaceAll("%player%", player.getName()));
        } else {
            event.setJoinMessage("");
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
    }

}
