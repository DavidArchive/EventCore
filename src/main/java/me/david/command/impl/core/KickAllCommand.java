package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickAllCommand {
    private final EventCore plugin;

    public KickAllCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("kickall")
                .withPermission("eventcore.command.event.kickall")
                .executes(this::onCommand);
    }

    private void onCommand(CommandSender sender, CommandArguments args) {
        int amount = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.hasPermission("event.spectator")) {
                target.kick(Component.empty());
                amount++;
            }
        }

        sender.sendMessage(MessageUtil.getPrefix() + "You successfully kicked %amount% players!".replaceAll("%amount%", String.valueOf(amount)));
    }
}