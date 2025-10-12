package me.david.command.impl.kit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import org.bukkit.command.CommandSender;

public class KitDeleteCommand {

    private final EventCore plugin;

    public KitDeleteCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("delete")
                .withPermission("event.command.kit.delete")
                .withArguments(new StringArgument("kit")
                        .replaceSuggestions(ArgumentSuggestions.strings(info ->
                                plugin.getKitManager().getKits().keySet().toArray(String[]::new))))
                .executes(this::onCommand);
    }

    private void onCommand(CommandSender sender, CommandArguments args) {
        String kit = ((String) args.get("kit")).toLowerCase();

        if (!plugin.getKitManager().getKits().containsKey(kit)) {
            sender.sendMessage(MessageUtil.getPrefix() + "§cThis kit does not exist!");
            return;
        }

        if (plugin.getKitManager().getEnabledKit().equalsIgnoreCase(kit)) {
            sender.sendMessage(MessageUtil.getPrefix() + "§cYou can't delete the enabled kit!");
            return;
        }

        plugin.getKitManager().delete(kit);
        sender.sendMessage(MessageUtil.getPrefix() + "§a" + kit + " §7has been deleted!");
    }
}
