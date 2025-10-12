package me.david.command.impl.kit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KitGiveCommand {

    private final EventCore plugin;

    public KitGiveCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("give")
                .withPermission("event.command.kit.give")
                .withArguments(new StringArgument("target")
                        .replaceSuggestions(ArgumentSuggestions.strings(info -> {
                            List<String> names = new ArrayList<>();
                            Bukkit.getOnlinePlayers().forEach(p -> names.add(p.getName()));
                            names.add("*");
                            names.sort(Comparator.naturalOrder());
                            return names.toArray(new String[0]);
                        })))
                .executes(this::onCommand);
    }

    @SuppressWarnings("ConstantConditions")
    private void onCommand(CommandSender sender, CommandArguments args) {
        final String targetName = (String) args.get("target");

        if (targetName.equalsIgnoreCase("*")) {
            int amount = 0;

            for (Player target : Bukkit.getOnlinePlayers()) {
                plugin.getKitManager().give(target);
                amount++;
            }

            sender.sendMessage(MessageUtil.getPrefix() + "§aSuccessfully gave %amount% players the kit!".replaceAll("%amount%", String.valueOf(amount)));
            return;
        }

        final Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(MessageUtil.getPrefix() + "§cThis player is not online!");
            return;
        }

        plugin.getKitManager().give(target);
        sender.sendMessage(MessageUtil.getPrefix() + "§a" + target.getName() + " §7has been equipped!");
    }
}
