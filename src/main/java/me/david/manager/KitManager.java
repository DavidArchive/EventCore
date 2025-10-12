package me.david.manager;

import lombok.Getter;
import me.david.EventCore;
import me.david.api.events.kit.KitDeleteEvent;
import me.david.api.events.kit.KitEnableEvent;
import me.david.api.events.kit.KitGiveEvent;
import me.david.api.events.kit.KitSaveEvent;
import me.david.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class KitManager {

    private String enabledKit = "";
    private final Map<String, Map<Integer, ItemStack>> kits = new ConcurrentHashMap<>();

    public KitManager() {
        load();
    }

    public void give(@NotNull final Player player) {
        KitGiveEvent kitGiveEvent = new KitGiveEvent(player, enabledKit);
        Bukkit.getPluginManager().callEvent(kitGiveEvent);

        if (kitGiveEvent.isCancelled()) {
            return;
        }

        player.getInventory().setArmorContents(null);
        player.getInventory().clear();

        Map<Integer, ItemStack> map = kits.getOrDefault(enabledKit, new ConcurrentHashMap<>());
        for (int i = 0; i < 41; i++) {
            player.getInventory().setItem(i, map.getOrDefault(i, null));
        }
    }

    private void load() {
        enabledKit = EventCore.getInstance().getConfig().getString("Kits.EnabledKit", "default");

        if (EventCore.getInstance().getConfig().getConfigurationSection("Kits") == null) return;
        if (EventCore.getInstance().getConfig().getConfigurationSection("Kits.Kits") == null) return;

        Objects.requireNonNull(EventCore.getInstance().getConfig().getConfigurationSection("Kits.Kits")).getKeys(false).forEach(kit -> {
            Map<Integer, ItemStack> map = new ConcurrentHashMap<>();
            String base64 = EventCore.getInstance().getConfig().getString("Kits.Kits." + kit, "-");
            if (!(base64.equalsIgnoreCase("-"))) {
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
                    BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

                    for (int i = 0; i < 41; i++) {
                        map.put(i, (ItemStack) bukkitObjectInputStream.readObject());
                    }
                } catch (Exception ignored) { }
            }
            kits.put(kit, map);
        });
    }

    public void enable(@NotNull final String kit) {
        String previousKit = enabledKit;
        KitEnableEvent kitEnableEvent = new KitEnableEvent(kit, previousKit);
        Bukkit.getPluginManager().callEvent(kitEnableEvent);

        if (kitEnableEvent.isCancelled()) {
            return;
        }

        enabledKit = kit;
        EventCore.getInstance().getConfig().set("Kits.EnabledKit", kit);
        EventCore.getInstance().saveConfig();
        Bukkit.getOnlinePlayers().forEach(this::give);
    }

    public void save(@NotNull final String kit, Player player) {
        KitSaveEvent kitSaveEvent = new KitSaveEvent(kit, player);
        Bukkit.getPluginManager().callEvent(kitSaveEvent);

        if (kitSaveEvent.isCancelled()) {
            player.sendMessage(MessageUtil.getPrefix() + "§cKit save was cancelled!");
            return;
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

            for (int i = 0; i < 41; i++) {
                bukkitObjectOutputStream.writeObject(player.getInventory().getItem(i));
            }

            bukkitObjectOutputStream.close();
            String base64 = Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());
            EventCore.getInstance().getConfig().set("Kits.Kits." + kit, base64);
            EventCore.getInstance().saveConfig();

            load();

            player.sendMessage(MessageUtil.getPrefix() + "§aKit has been saved successfully!");
        } catch (Exception exception) {
            player.sendMessage(MessageUtil.getPrefix() + "§cFailed to save!");
        }
    }

    public void delete(@NotNull final String kit) {
        KitDeleteEvent kitDeleteEvent = new KitDeleteEvent(kit);
        Bukkit.getPluginManager().callEvent(kitDeleteEvent);

        if (kitDeleteEvent.isCancelled()) {
            return;
        }

        kits.remove(kit);
        EventCore.getInstance().getConfig().set("Kits.Kits." + kit, null);
        EventCore.getInstance().saveConfig();
    }

}
