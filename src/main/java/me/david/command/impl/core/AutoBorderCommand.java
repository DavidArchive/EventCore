package me.david.command.impl.core;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.BorderUtil;
import me.david.util.MessageUtil;
import org.bukkit.entity.Player;

public class AutoBorderCommand {
    private final EventCore plugin;

    public AutoBorderCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("autoborder")
                .withPermission("eventcore.command.event.autoborder")
                .withArguments(new BooleanArgument("state")) // true = on, false = off
                .executesPlayer(this::onCommand);
    }

    private void onCommand(Player player, CommandArguments args) {
        boolean state = Boolean.TRUE.equals(args.get("state"));

        BorderUtil.setAutoBorder(state);

        if (!state) {
            BorderUtil.lastOptimal = BorderUtil.borderDefault;
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("AutoBorder has been §cdeactivated!")));
        } else {
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("AutoBorder has been §aactivated!")));
        }
    }

}