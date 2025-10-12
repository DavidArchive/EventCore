package me.david.command;

import dev.jorel.commandapi.CommandAPICommand;
import me.david.EventCore;
import me.david.command.impl.core.*;
import me.david.util.MessageUtil;
import me.david.util.Scheduler;

public class EventCoreCommand {

    private final EventCore plugin;

    public EventCoreCommand(EventCore plugin) {
        this.plugin = plugin;
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        new CommandAPICommand("event")
                .withAliases("eventcore", "e")
                .withSubcommands(
                        new ReloadCommand(plugin).init(),
                        new StartCommand(plugin).init(),
                        new StopCommand(plugin).init(),
                        new DropCommand(plugin).init(),
                        new ResetCommand(plugin).init(),
                        new SetSpawnCommand(plugin).init(),
                        new KickSpecCommand(plugin).init(),
                        new KickAllCommand(plugin).init(),
                        new ClearAllCommand(plugin).init(),
                        new AutoBorderCommand(plugin).init()
                )
                .executesPlayer((player, args) -> {
                    player.sendMessage(" ");
                    player.sendMessage(MessageUtil.getPrefix() + "§7Running §aEventCore §7v" + EventCore.getInstance().getDescription().getVersion() + " §7on §a" + getSoftware());
                    player.sendMessage(MessageUtil.getPrefix() + "§7Download at §ahttps://github.com/VertrauterDavid");
                    player.sendMessage(" ");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event start");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event stop <winner>");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event drop");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event reset");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event autoBorder <on / off>");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event setSpawn");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event reload");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event kickspec");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event kickall");
                    player.sendMessage(MessageUtil.getPrefix() + "Usage: §c/event clearall");
                    player.sendMessage(" ");
                })
                .register();
    }

    private String getSoftware() {
        return Scheduler.isFOLIA() ? "Folia" : "PaperMC";
    }
}