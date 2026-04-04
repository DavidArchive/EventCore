package me.david.command.impl;

import me.david.EventCore;
import me.david.command.BukkitCommand;
import me.david.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KitCommand extends BukkitCommand {

    private final EventCore plugin;

    public KitCommand(EventCore plugin) {
        super("kit", "event.command.kit", "kits", "playerkit");
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof final Player player)) return;
        if (!(player.hasPermission("event.command"))) return;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("*")) {
                Bukkit.getOnlinePlayers().forEach(plugin.getKitManager()::give);
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Everyone §7has been equipped!")));
                return;
            }

            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("This player is not online!")));
                return;
            }

            plugin.getKitManager().give(target);
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§a" + target.getName() + " §7has been equipped!")));
            return;
        }

        if (args.length == 2) {
            String kit = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("enable")) {
                if (!plugin.getKitManager().getKits().containsKey(kit)) {
                    player.sendMessage(MessageUtil.getPrefix() + "§cThis kit does not exist!");
                    return;
                }

                plugin.getKitManager().enable(kit);
                player.sendMessage(MessageUtil.getPrefix() + "§a" + kit + " §7has been enabled!");
                return;
            }

            if (args[0].equalsIgnoreCase("save")) {
                plugin.getKitManager().save(kit, player);
                player.sendMessage(MessageUtil.getPrefix() + "§a" + kit + " §7has been saved!");
                return;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (!plugin.getKitManager().getKits().containsKey(kit)) {
                    player.sendMessage(MessageUtil.getPrefix() + "§cThis kit does not exist!");
                    return;
                }

                if (plugin.getKitManager().getEnabledKit().equalsIgnoreCase(kit)) {
                    player.sendMessage(MessageUtil.getPrefix() + "§cYou can't delete the enabled kit!");
                    return;
                }

                plugin.getKitManager().delete(kit);
                player.sendMessage(MessageUtil.getPrefix() + "§a" + kit + " §7has been deleted!");
                return;
            }
        }

        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/kit <player>")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/kit enable <kit>")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/kit save <kit>")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/kit delete <kit>")));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        if (!(sender instanceof final Player player)) return new ArrayList<>();
        if (!(player.hasPermission("event.command.kit"))) return new ArrayList<>();
        final List<String> list = new ArrayList<>();

        if (args.length == 2) {
            list.addAll(plugin.getKitManager().getKits().keySet());
        }

        if (args.length == 1) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            list.add("*");
            list.add("enable");
            list.add("save");
            list.add("delete");
        }

        return list.stream().filter(content -> content.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).sorted().toList();
    }

}
