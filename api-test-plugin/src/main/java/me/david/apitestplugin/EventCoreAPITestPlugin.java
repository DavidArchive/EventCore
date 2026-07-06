package me.david.apitestplugin;

import lombok.Getter;
import me.david.api.EventCoreAPI;
import me.david.apitestplugin.listener.GameStartListener;
import me.david.apitestplugin.listener.GameStopListener;
import me.david.apitestplugin.listener.KitGiveListener;
import me.david.apitestplugin.listener.MapDropListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventCoreAPITestPlugin extends JavaPlugin {

    @Getter
    public static EventCoreAPITestPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("EventCore")) {
            getLogger().severe("EventCore is not enabled!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final EventCoreAPI api = EventCoreAPI.get();

        Bukkit.getServer().getPluginManager().registerEvents(new GameStartListener(api), instance);
        Bukkit.getServer().getPluginManager().registerEvents(new GameStopListener(), instance);
        Bukkit.getServer().getPluginManager().registerEvents(new KitGiveListener(), instance);
        Bukkit.getServer().getPluginManager().registerEvents(new MapDropListener(), instance);

        getLogger().info("Current kit: " + api.getKitManager().getEnabledKit());
        getLogger().info("Game running: " + api.getGameManager().isRunning());
    }

}
