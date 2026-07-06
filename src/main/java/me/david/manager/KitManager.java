package me.david.manager;

import lombok.Getter;
import me.david.EventCore;
import me.david.api.events.kit.KitDeleteEvent;
import me.david.api.events.kit.KitEnableEvent;
import me.david.api.events.kit.KitGiveEvent;
import me.david.api.events.kit.KitSaveEvent;
import me.david.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class KitManager implements me.david.api.manager.KitManager {

    private final Logger LOGGER = Logger.getLogger("KitManager");

    private volatile String enabledKit = "default";
    private final Map<String, Map<Integer, ItemStack>> kits = new ConcurrentHashMap<>();

    public KitManager() {
        loadAllKits();
    }

    public void give(@NotNull final Player player) {
        Map<Integer, ItemStack> kitItems = kits.get(enabledKit);
        if (kitItems == null || kitItems.isEmpty()) return;

        KitGiveEvent kitGiveEvent = new KitGiveEvent(player, enabledKit);
        Bukkit.getPluginManager().callEvent(kitGiveEvent);
        if (kitGiveEvent.isCancelled()) return;

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        for (Map.Entry<Integer, ItemStack> entry : kitItems.entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }
    }

    public void loadAllKits() {
        kits.clear();
        final FileConfiguration config = EventCore.getInstance().getConfig();
        enabledKit = config.getString("Kits.EnabledKit", "default");

        final ConfigurationSection section = config.getConfigurationSection("Kits.Kits");
        if (section == null) return;

        for (String kitName : section.getKeys(false)) {
            final ConfigurationSection kitSection = section.getConfigurationSection(kitName);
            if (kitSection == null) continue;

            final Map<Integer, ItemStack> map = new ConcurrentHashMap<>();
            for (String key : kitSection.getKeys(false)) {
                final int slot;
                try {
                    slot = Integer.parseInt(key);
                } catch (NumberFormatException ignored) {
                    continue;
                }

                try {
                    final ItemStack item = readItem(kitSection, key);
                    if (item != null) map.put(slot, item.clone());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "[KitManager] Failed to load slot " + key + " of kit " + kitName, e);
                }
            }
            kits.put(kitName, map);
        }
        LOGGER.info("Loaded " + kits.size() + " kits into memory.");
    }

    public void save(@NotNull final String kit, @NotNull final Player player) {
        final KitSaveEvent kitSaveEvent = new KitSaveEvent(kit, player);
        Bukkit.getPluginManager().callEvent(kitSaveEvent);
        if (kitSaveEvent.isCancelled()) return;

        try {
            final FileConfiguration config = EventCore.getInstance().getConfig();
            final ConfigurationSection kitSection = config.createSection("Kits.Kits." + kit);

            final Map<Integer, ItemStack> cacheMap = new ConcurrentHashMap<>();
            for (int i = 0; i < 41; i++) {
                final ItemStack item = player.getInventory().getItem(i);
                if (item == null) continue;

                kitSection.set(String.valueOf(i), Base64.getEncoder().encodeToString(item.serializeAsBytes()));
                cacheMap.put(i, item.clone());
            }

            kits.put(kit, cacheMap);
            EventCore.getInstance().saveConfig();

            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§aKit saved successfully!")));
        } catch (Exception e) {
            player.sendMessage(MessageUtil.getPrefix().append(MessageUtil.translateColorCodes("§cFailed to save kit!")));
            throw new RuntimeException(e);
        }
    }

    public void enable(@NotNull final String kit) {
        if (!kits.containsKey(kit)) {
            LOGGER.warning("Tried to enable unknown kit: " + kit);
            return;
        }

        final String previousKit = enabledKit;
        final KitEnableEvent event = new KitEnableEvent(kit, previousKit);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        enabledKit = kit;
        EventCore.getInstance().getConfig().set("Kits.EnabledKit", kit);
        EventCore.getInstance().saveConfig();

        Bukkit.getOnlinePlayers().forEach(this::give);
    }

    public void delete(@NotNull final String kit) {
        final KitDeleteEvent event = new KitDeleteEvent(kit);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        kits.remove(kit);
        EventCore.getInstance().getConfig().set("Kits.Kits." + kit, null);
        EventCore.getInstance().saveConfig();
    }

    private @Nullable ItemStack readItem(@NotNull final ConfigurationSection kitSection, @NotNull final String key) {
        if (kitSection.isString(key)) {
            final byte[] bytes = Base64.getDecoder().decode(kitSection.getString(key));
            return ItemStack.deserializeBytes(bytes);
        }
        return kitSection.getItemStack(key);
    }
}
