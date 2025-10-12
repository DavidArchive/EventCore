package me.david.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AnnouncementCommand {

    private final EventCore plugin;

    public AnnouncementCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("announcement")
                .withAliases("announce")
                .withPermission("event.command.announcement")
                .withArguments(new GreedyStringArgument("message"))
                .executes(this::onCommand);
    }

    @SuppressWarnings("deprecation")
    private void onCommand(CommandSender sender, CommandArguments args) {
        String message = Objects.requireNonNull(args.get("message")).toString();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MessageUtil.get("Messages.AnnoucementCommand.MessageFormat")
                    .replace("%prefix%", MessageUtil.getPrefix())
                    .replace("%message%", message));

            if (plugin.getConfig().getBoolean("Messages.AnnoucementCommand.Title.Enabled")) {
                player.sendTitle(
                        MessageUtil.get("Messages.AnnoucementCommand.Title.Title")
                                .replace("%prefix%", MessageUtil.getPrefix())
                                .replace("%message%", message),
                        MessageUtil.get("Messages.AnnoucementCommand.Title.SubTitle")
                                .replace("%prefix%", MessageUtil.getPrefix())
                                .replace("%message%", message)
                );
            }
        }
    }
}
