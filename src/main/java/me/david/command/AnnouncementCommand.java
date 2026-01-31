package me.david.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
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

    private void onCommand(CommandSender sender, CommandArguments args) {
        String message = Objects.requireNonNull(args.get("message")).toString();

        var replacements = Map.of(
                "%prefix%", MessageUtil.getPrefix(),
                "%message%", MessageUtil.translateColorCodes(message)
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MessageUtil.format("Messages.AnnoucementCommand.MessageFormat", replacements));

            if (plugin.getConfig().getBoolean("Messages.AnnoucementCommand.Title.Enabled")) {
                Component titleComponent = MessageUtil.format("Messages.AnnoucementCommand.Title.Title", replacements);
                Component subTitleComponent = MessageUtil.format("Messages.AnnoucementCommand.Title.SubTitle", replacements);

                Title title = Title.title(titleComponent, subTitleComponent);
                player.showTitle(title);
            }
        }
    }
}
