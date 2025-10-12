package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StopCommand {
    private final EventCore plugin;

    public StopCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("stop")
                .withPermission("eventcore.command.event.stop")
                .withArguments(new EntitySelectorArgument.OnePlayer("winner"))
                .executes(this::onCommand);
    }

    private void onCommand(CommandSender sender, CommandArguments args) {
        final @NotNull Player player = (Player) Objects.requireNonNull(args.get("winner"));

        plugin.getGameManager().stop(player.getName());
    }
}