package me.david.util;

import lombok.experimental.UtilityClass;
import me.david.EventCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class MessageUtil {

    public Component get(@NotNull String key) {
        return translateColorCodes(EventCore.getInstance().getConfig().getString(key, ""));
    }

    public Component getPrefix() {
        return translateColorCodes(EventCore.getInstance().getConfig().getString("Messages.Prefix", ""));
    }

    public @NotNull Component format(@NotNull String key, @NotNull Map<String, ? extends ComponentLike> replacements) {
        Component component = get(key);
        for (var entry : replacements.entrySet()) {
            component = component.replaceText(builder -> builder
                    .matchLiteral(entry.getKey())
                    .replacement(entry.getValue())
            );
        }
        return component;
    }

    @NotNull
    public Component translateColorCodes(@NotNull String message) {
        message = message.replaceAll("&([0-9a-fk-or])", "§$1");
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, "§x"
                    + "§" + matcher.group(1).charAt(0)
                    + "§" + matcher.group(1).charAt(1)
                    + "§" + matcher.group(1).charAt(2)
                    + "§" + matcher.group(1).charAt(3)
                    + "§" + matcher.group(1).charAt(4)
                    + "§" + matcher.group(1).charAt(5));
        }
        matcher.appendTail(buffer);
        return LegacyComponentSerializer.legacySection().deserialize(buffer.toString());
    }

}
