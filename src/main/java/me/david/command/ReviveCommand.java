package me.david.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import me.david.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ReviveCommand {

    private final EventCore plugin;

    public ReviveCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("revive")
                .withPermission("event.command.revive")
                .withAliases("respawn")
                .withOptionalArguments(
                        new StringArgument("target")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> {
                                    List<String> names = new ArrayList<>();
                                    Bukkit.getOnlinePlayers().forEach(p -> names.add(p.getName()));
                                    names.add("*");
                                    names.sort(Comparator.naturalOrder());
                                    return names.toArray(new String[0]);
                                }))
                )
                .executesPlayer(this::onCommand);
    }

    private void onCommand(Player player, CommandArguments args) {
        final String targetName = Objects.requireNonNull(args.getOptional("target").orElse(null)).toString();

        if (targetName == null) {
            player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/revive <player>");
            return;
        }

        if (targetName.equalsIgnoreCase("*")) {
            Bukkit.getOnlinePlayers().forEach(PlayerUtil::cleanPlayer);
            player.sendMessage(MessageUtil.getPrefix() + "§aEveryone §7has been revived!");
            return;
        }

        final Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage(MessageUtil.getPrefix() + "§cThis player is not online!");
            return;
        }

        PlayerUtil.cleanPlayer(target);
        player.sendMessage(MessageUtil.getPrefix() + "§a" + target.getName() + " §7has been revived!");
    }
}
