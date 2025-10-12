package me.david.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import org.bukkit.entity.Player;

public class SpawnCommand {

    private final EventCore plugin;

    public SpawnCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("spawn")
                .withPermission("event.command.spawn")
                .executesPlayer(this::onCommand);
    }

    private void onCommand(Player player, CommandArguments args) {
        if (plugin.getMapManager().getSpawnLocation() == null) {
            player.sendMessage(MessageUtil.getPrefix() + "§cThere isn’t a spawn location yet. Set one using the command /event setspawn");
            return;
        }

        if (plugin.getGameManager().isRunning() && !player.hasPermission("event.bypass")) {
            player.sendMessage(MessageUtil.getPrefix() + "§cYou cannot teleport to the spawn while the event is running");
            return;
        }

        player.teleportAsync(plugin.getMapManager().getSpawnLocation());
    }
}
