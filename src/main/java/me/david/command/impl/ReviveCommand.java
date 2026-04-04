package me.david.command.impl;

import me.david.command.BukkitCommand;
import me.david.util.MessageUtil;
import me.david.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviveCommand extends BukkitCommand {

    public ReviveCommand() {
        super("revive", "event.command.revive", "respawn");
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof final Player player)) return;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("*")) {
                Bukkit.getOnlinePlayers().forEach(PlayerUtil::cleanPlayer);
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§aEveryone §7has been revived!")));
                return;
            }

            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§cThis player is not online!")));
                return;
            }

            PlayerUtil.cleanPlayer(target);
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§a" + target.getName() + " §7has been revived!")));
            return;
        }

        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/revive <player>")));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        if (!(sender instanceof final Player player)) return new ArrayList<>();
        if (!(player.hasPermission("event.command.revive"))) return new ArrayList<>();

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            list.add("*");
        }

        try {
            Collections.sort(list);
        } catch (Exception ignored) { }
        return list.stream().filter(content -> content.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).sorted().toList();
    }

}
