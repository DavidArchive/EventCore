package me.david.command;

import dev.jorel.commandapi.CommandAPICommand;
import me.david.EventCore;
import me.david.command.impl.kit.*;
import me.david.util.MessageUtil;

public class KitCommand {

    private final EventCore plugin;

    public KitCommand(EventCore plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        new CommandAPICommand("kit")
                .withPermission("event.command")
                .withSubcommands(
                        new KitGiveCommand(plugin).init(),
                        new KitEnableCommand(plugin).init(),
                        new KitSaveCommand(plugin).init(),
                        new KitDeleteCommand(plugin).init()
                )
                .executesPlayer((player, args) -> {
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/kit <player>");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/kit enable <kit>");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/kit save <kit>");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/kit delete <kit>");
                })
                .register();
    }
}
