package me.david.command;

import me.david.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class BukkitCommand extends Command {

    private final String permission;

    public BukkitCommand(String name, String permission, String... aliases) {
        super(name, name + " command", "/" + name, aliases[0].isEmpty() ? Collections.singletonList(name) : Stream.concat(Stream.of(name), Arrays.stream(aliases)).toList());
        this.permission = permission;
        registerCommand(this);
    }

    public BukkitCommand(String name, String permission) {
        this(name, permission, "");
    }

    public abstract void onCommand(CommandSender sender, String label, String[] args);

    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return super.tabComplete(sender, label, args);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (permission == null || sender.hasPermission(permission)) {
            onCommand(sender, label, args);
        } else {
            sender.sendMessage(MessageUtil.translateColorCodes("&cYou don't have permission to use this command!"));
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        return onTabComplete(sender, alias, args);
    }


    private void registerCommand(Command command) {
        Bukkit.getCommandMap().register("eventcore", command);
    }
}
