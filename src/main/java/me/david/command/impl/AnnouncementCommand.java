package me.david.command.impl;

import me.david.EventCore;
import me.david.command.BukkitCommand;
import me.david.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnnouncementCommand extends BukkitCommand {

    private final EventCore plugin;

    public AnnouncementCommand(EventCore plugin) {
        super("annoucement", "event.command.annoucement", "announce");
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender.hasPermission("event.command"))) return;

        if (args.length >= 1) {
            String message = String.join(" ", args);
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

        sender.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: Â§c/annoucement <message>")));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        return new ArrayList<>();
    }
}
