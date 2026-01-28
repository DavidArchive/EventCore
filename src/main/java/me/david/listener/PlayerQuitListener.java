package me.david.listener;

import me.david.EventCore;
import me.david.util.HostUtil;
import me.david.util.MessageUtil;
import me.david.util.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Objects;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        HostUtil.removeHost(player);

        if (EventCore.getInstance().getConfig().getBoolean("Messages.PlayerQuit.Enabled")) {
            Component message = MessageUtil.format("Messages.PlayerQuit.Message", Map.of("%player%", Component.text(player.getName())));
            event.quitMessage(message);
        } else {
            event.quitMessage(Component.empty());
        }
    }

}
