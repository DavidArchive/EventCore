package me.david.command.impl;

import me.david.EventCore;
import me.david.command.BukkitCommand;
import me.david.util.BorderUtil;
import me.david.util.MessageUtil;
import me.david.util.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCommand extends BukkitCommand {

    private final EventCore plugin;

    public EventCommand(EventCore plugin) {
        super("event", "event.command", "e");
        this.plugin = plugin;
    }

    private String getSoftware() {
        return Scheduler.isFOLIA() ? "Folia" : "PaperMC";
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof final Player player)) return;

        if (args.length == 0) {
            player.sendMessage(" ");
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§7Running §aEventCore §7v" + plugin.getPluginMeta().getVersion() + " §7on §a" + getSoftware())));
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§7Download at §ahttps://github.com/VertrauterDavid")));
            player.sendMessage(" ");
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("start")) {
                plugin.getGameManager().start();
                return;
            }

            if (args[0].equalsIgnoreCase("stop")) {
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event stop <winner>")));
                return;
            }

            if (args[0].equalsIgnoreCase("drop")) {
                plugin.getMapManager().drop();
                return;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                plugin.getMapManager().reset();
                return;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                double currentMS = System.currentTimeMillis();
                plugin.reloadConfig();
                double reloadMS = System.currentTimeMillis() - currentMS;

                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§aYou successfully reloaded the config within %ms%ms!").replaceText(b -> b.matchLiteral("%ms%").replacement(Component.text(String.valueOf(reloadMS))))));
                return;
            }

            if (args[0].equalsIgnoreCase("setSpawn")) {
                plugin.getMapManager().saveSpawnLocation(player);
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§aSpawn location saved!")));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 5);
                player.closeInventory();
                return;
            }

            if (args[0].equalsIgnoreCase("kickspec")) {
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    if (!(target.hasPermission("event.spec")) && target.getGameMode() == GameMode.SPECTATOR) {
                        target.kick();
                    }
                }

                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("All spectators has been kicked!")));
                return;
            }

            if (args[0].equalsIgnoreCase("kickall")) {
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    if (!(target.hasPermission("event.spec"))) {
                        target.kick();
                    }
                }
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("All players has been kicked!")));
                return;
            }

            if (args[0].equalsIgnoreCase("clearall")) {
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    target.getInventory().setArmorContents(null);
                    target.getInventory().clear();
                }
                player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("All players has been cleared!")));
                return;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("autoBorder")) {
                if (args[1].equalsIgnoreCase("on")) {
                    BorderUtil.setAutoBorder(true);

                    player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("AutoBorder has been §aactivated!")));
                    return;
                }

                if (args[1].equalsIgnoreCase("off")) {
                    BorderUtil.setAutoBorder(false);
                    BorderUtil.lastOptimal = BorderUtil.borderDefault;

                    player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("AutoBorder has been §cdeactivated!")));
                    return;
                }
            }
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("stop")) {
                StringBuilder winner = null;
                for (int i = 1; i < args.length; i++) {
                    if (winner == null) {
                        winner = new StringBuilder(args[i]);
                    } else {
                        winner.append(" ").append(args[i]);
                    }
                }

                plugin.getGameManager().stop(winner.toString());
                return;
            }
        }

        player.sendMessage(" ");
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event start")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event stop <winner>")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event drop")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event reset")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event autoBorder <on / off>")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event setSpawn")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event reload")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event kickspec")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: §c/event kickall")));
        player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("Usage: &c/event clearall")));
        player.sendMessage(" ");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof final Player player)) return new ArrayList<>();
        if (!(player.hasPermission("event.command"))) return new ArrayList<>();

        List<String> list = new ArrayList<>();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("stop")) {
                list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            }

            if (args[0].equalsIgnoreCase("autoBorder")) {
                list.addAll(Arrays.asList("on", "off"));
            }
        }

        if (args.length == 1) {
            list.addAll(Arrays.asList("start", "stop", "drop", "reset", "autoBorder", "kickspec", "kickall", "clearall", "setSpawn", "reload"));
        }

        return list.stream().filter(content -> content.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).sorted().toList();
    }

}
