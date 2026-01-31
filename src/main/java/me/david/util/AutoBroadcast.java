package me.david.util;

import me.david.EventCore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class AutoBroadcast implements Runnable {

    private final List<String> messages;
    private int index = 0;

    public AutoBroadcast() {
        messages = EventCore.getInstance().getConfig().getStringList("AutoBroadcast.Messages");
    }

    @Override
    public void run() {
        if (!(EventCore.getInstance().getConfig().getBoolean("AutoBroadcast.Enabled")) || messages.isEmpty()) {
            return;
        }

        if (index >= messages.size()) {
            index = 0;
        }

        String message = messages.get(index);
        if (EventCore.getInstance().getConfig().getBoolean("AutoBroadcast.UseBroadcastCommand")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), EventCore.getInstance().getConfig().getString("AutoBroadcast.BroadcastCommand", "").replaceAll("%message%", message));
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(Component.empty());
                player.sendMessage(Component.empty());
                player.sendMessage(MessageUtil.translateColorCodes(message));
                player.sendMessage(Component.empty());
                player.sendMessage(Component.empty());
            }
        }

        index++;
    }

}
