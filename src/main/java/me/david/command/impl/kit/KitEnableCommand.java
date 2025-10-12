package me.david.command.impl.kit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class KitEnableCommand {

    private final EventCore plugin;

    public KitEnableCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("enable")
                .withPermission("event.command.kit.enable")
                .withArguments(new StringArgument("kit")
                        .replaceSuggestions(ArgumentSuggestions.strings(info ->
                                plugin.getKitManager().getKits().keySet().toArray(String[]::new))))
                .executes(this::onCommand);
    }


    private void onCommand(CommandSender sender, CommandArguments args) {
        final String kit = ((String) Objects.requireNonNull(args.get("kit"))).toLowerCase();

        if (!plugin.getKitManager().getKits().containsKey(kit)) {
            sender.sendMessage(MessageUtil.getPrefix() + "§cThis kit does not exist!");
            return;
        }

        plugin.getKitManager().enable(kit);
        sender.sendMessage(MessageUtil.getPrefix() + "§a" + kit + " §7has been enabled!");
    }
}
