package me.david.command.impl;

import me.david.EventCore;
import me.david.command.BukkitCommand;
import me.david.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends BukkitCommand {

    private final EventCore plugin;

    public SpawnCommand(EventCore plugin) {
        super("spawn", "event.command.spawn");
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof final Player player)) return;

        if (plugin.getMapManager().getSpawnLocation() == null) {
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§cThere isn't a spawn location yet. Set one using the command /event setspawn")));
            return;
        }

        if (plugin.getGameManager().isRunning() && !player.hasPermission("event.bypass")) {
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§cYou cannot teleport to the spawn while the event is running")));
            return;
        }

        player.teleportAsync(plugin.getMapManager().getSpawnLocation());
    }
}
