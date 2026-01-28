package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    private final EventCore plugin;

    public ReloadCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("reload")
                .withPermission("eventcore.command.event.reload")
                .executes(this::onCommand);
    }

    private void onCommand(CommandSender sender, CommandArguments args) {
        final double currentMS = System.currentTimeMillis();
        EventCore.getInstance().reloadConfig();
        final double reloadMS = System.currentTimeMillis() - currentMS;

        sender.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§aYou successfully reloaded the config within %ms%ms!").replaceText(b -> b.matchLiteral("%ms%").replacement(Component.text(String.valueOf(reloadMS))))));
    }
}