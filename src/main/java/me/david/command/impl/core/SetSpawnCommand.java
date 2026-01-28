package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SetSpawnCommand {
    private final EventCore plugin;

    public SetSpawnCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("setspawn")
                .withPermission("eventcore.command.event.setspawn")
                .executesPlayer(this::onCommand);
    }

    private void onCommand(Player sender, CommandArguments args) {
        plugin.getMapManager().saveSpawnLocation(sender);

        sender.sendMessage(MessageUtil.getPrefix().append(Component.text("Location saved!").color(NamedTextColor.GREEN)));
        sender.playSound(sender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 5);
    }
}