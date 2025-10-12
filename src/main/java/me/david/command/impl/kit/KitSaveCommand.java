package me.david.command.impl.kit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.david.EventCore;
import me.david.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Objects;

public class KitSaveCommand {

    private final EventCore plugin;

    public KitSaveCommand(EventCore plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand init() {
        return new CommandAPICommand("save")
                .withPermission("event.command.save")
                .withArguments(new StringArgument("kit"))
                .executesPlayer(this::onCommand);
    }

    private void onCommand(Player sender, CommandArguments args) {
        final String kit = ((String) Objects.requireNonNull(args.get("kit"))).toLowerCase();

        plugin.getKitManager().save(kit, sender);
        sender.sendMessage(MessageUtil.getPrefix() + "§a" + kit + " §7has been saved!");
    }
}
