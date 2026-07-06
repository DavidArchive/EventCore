package me.david.util;

import lombok.experimental.UtilityClass;
import me.david.EventCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

@UtilityClass
public class PlayerUtil {

    public int getAlive() {
        return Bukkit.getOnlinePlayers().stream().filter(player2 -> player2.getGameMode() == GameMode.SURVIVAL).toList().size();
    }

    public int getTotal() {
        return Bukkit.getOnlinePlayers().size();
    }

    public void cleanPlayer(@NotNull Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH) != null ? Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue() : 20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        new ArrayList<>(player.getActivePotionEffects()).forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().clear();
        EventCore.getInstance().getKitManager().give(player);
    }

}
