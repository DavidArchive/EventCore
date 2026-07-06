package me.david.listener;

import me.david.EventCore;
import me.david.util.HostUtil;
import me.david.util.MessageUtil;
import me.david.util.folia.FoliaScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        HostUtil.removeHost(player);

        List<String> quitCommands = EventCore.getInstance().getConfig().getStringList("Settings.PlayerQuit.Commands");
        for (String command : quitCommands) {
            final String finalCommand = command.replace("%player%", player.getName()).substring(1);
            FoliaScheduler.getGlobalRegionScheduler().execute(EventCore.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
        }

        if (EventCore.getInstance().getConfig().getBoolean("Messages.PlayerQuit.Enabled")) {
            Component message = MessageUtil.format("Messages.PlayerQuit.Message", Map.of("%player%", Component.text(player.getName())));
            event.quitMessage(message);
        } else {
            event.quitMessage(Component.empty());
        }
    }

}
