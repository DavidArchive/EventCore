package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import org.bukkit.command.CommandSender;

public class ResetCommand {
    private final EventCore plugin;

    public ResetCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("reset")
                .withPermission("eventcore.command.event.reset")
                .executes(this::onCommand);
    }

    // Although this method currently contains only one line,
    // we keep it separate so that the command logic can be easily extended in the future.
    private void onCommand(CommandSender sender, CommandArguments args) {
        plugin.getMapManager().reset();
    }
}