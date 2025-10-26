package me.david.util;

import lombok.experimental.UtilityClass;
import me.david.EventCore;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class MessageUtil {

    public String get(@NotNull String key) {
        return translateColorCodes(EventCore.getInstance().getConfig().getString(key, ""));
    }

    public String getPrefix() {
        return translateColorCodes(EventCore.getInstance().getConfig().getString("Messages.Prefix", ""));
    }

    @SuppressWarnings("deprecation")
    public String translateColorCodes(@NotNull String message) {
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

}
