package me.david.api.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface KitManager {

    void give(@NotNull Player player);

    void loadAllKits();

    void save(@NotNull String kit, @NotNull Player player);

    void enable(@NotNull String kit);

    void delete(@NotNull String kit);

    String getEnabledKit();

    Map<String, Map<Integer, ItemStack>> getKits();
}
