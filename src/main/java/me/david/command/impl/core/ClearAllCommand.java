package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import me.david.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearAllCommand {
    private final EventCore plugin;

    public ClearAllCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("clearall")
                .withPermission("eventcore.command.event.clearall")
                .executes(this::onCommand);
    }

    private void onCommand(CommandSender sender, CommandArguments args) {
        int amount = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            PlayerUtil.cleanPlayer(target);
            amount++;
        }

        sender.sendMessage(MessageUtil.getPrefix() + "You successfully cleared %amount% players!".replaceAll("%amount%", String.valueOf(amount)));
    }
}