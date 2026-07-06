package me.david;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.david.api.EventCoreAPI;
import me.david.command.impl.*;
import me.david.listener.*;
import me.david.listener.canvas.CanvasPlayerRespawnListener;
import me.david.listener.canvas.CanvasPlayerTeleportListener;
import me.david.manager.GameManager;
import me.david.manager.KitManager;
import me.david.manager.MapManager;
import me.david.util.*;
import me.david.util.folia.FoliaScheduler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class EventCore extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger("EventCore");

    @Getter
    private static EventCore instance;
    private MapManager mapManager;
    private GameManager gameManager;
    private KitManager kitManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        new UpdateChecker(instance, "DavidArchive", "EventCore").check();

        mapManager = new MapManager();
        gameManager = new GameManager();
        kitManager = new KitManager();
        EventCoreAPI.initialize(instance, gameManager, kitManager, mapManager);

        new AnnouncementCommand(instance);
        new EventCommand(instance);
        new KitCommand(instance);
        new ReviveCommand();
        new SpawnCommand(instance);

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), instance);
        Bukkit.getPluginManager().registerEvents(new BlockExplodeListener(), instance);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), instance);
        Bukkit.getPluginManager().registerEvents(new CreatureSpawnListener(), instance);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), instance);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), instance);
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerPickupItemListener(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), instance);

        if (FoliaScheduler.isCanvas()) {
            Bukkit.getPluginManager().registerEvents(new CanvasPlayerRespawnListener(), instance);
            Bukkit.getPluginManager().registerEvents(new CanvasPlayerTeleportListener(), instance);
        } else {
            Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), instance);
            Bukkit.getPluginManager().registerEvents(new PlayerTeleportListener(), instance);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook().register();
        }

        FoliaScheduler.getAsyncScheduler().runAtFixedRate(instance, o -> new BorderUtil().run(), 20, 10);
        FoliaScheduler.getAsyncScheduler().runAtFixedRate(instance, o -> new AutoBroadcast().run(), 20, 20 * getConfig().getLong("AutoBroadcast.Interval", 60));
        FoliaScheduler.getGlobalRegionScheduler().runDelayed(instance, o -> {
            World world = mapManager.getSpawnLocation().getWorld();
            world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.getWorldBorder().setSize(BorderUtil.borderDefault);
            world.getWorldBorder().setDamageBuffer(BorderUtil.borderDamageBuffer);
            world.getWorldBorder().setDamageAmount(BorderUtil.borderDamageAmount);
        }, 40L);

        if (getConfig().getBoolean("Messages.Actionbar.Enabled")) {
            FoliaScheduler.getAsyncScheduler().runAtFixedRate(instance, o -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String raw = getConfig().getString("Messages.Actionbar.Message", "&aYou are playing the best Event!");
                    String parsed = PlaceholderAPI.setPlaceholders(player, raw);

                    player.sendActionBar(MessageUtil.translateColorCodes(parsed));
                }
            }, 0, 20);
        }

        if (getConfig().getBoolean("Settings.Metrics")) {
            new Metrics(instance, 28277);
        }
    }

    @Override
    public void onDisable() {
        if (gameManager.isRunning()) {
            gameManager.stop(null);
        }
        EventCoreAPI.shutdown();
    }

}
