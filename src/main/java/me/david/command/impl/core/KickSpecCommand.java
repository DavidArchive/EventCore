package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickSpecCommand {
    private final EventCore plugin;

    public KickSpecCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("kickspec")
                .withPermission("eventcore.command.event.kickspec")
                .executes(this::onCommand);
    }

    private void onCommand(CommandSender sender, CommandArguments args) {
        int amount = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.hasPermission("event.spectator") && target.getGameMode() == GameMode.SPECTATOR) {
                target.kick(Component.empty());
                amount++;
            }
        }

        int result = amount;
        sender.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("You successfully kicked %amount% players!").replaceText(b -> b.matchLiteral("%amount%").replacement(Component.text(String.valueOf(result))))));
    }
}