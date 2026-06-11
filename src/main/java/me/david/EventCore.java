package me.david;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.david.api.EventCoreAPI;
import me.david.command.impl.*;
import me.david.listener.*;
import me.david.manager.GameManager;
import me.david.manager.KitManager;
import me.david.manager.MapManager;
import me.david.util.*;
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

        if (FoliaUtil.isFOLIA() && !FoliaUtil.isCANVAS()) {
            getLogger().info("EventCore is not compatible on Folia due to a lack of Events");
            getLogger().info("Consider Switching To CanvasMC or forks that support it");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new UpdateChecker(instance, "DavidArchive", "EventCore").check();


        mapManager = new MapManager();
        gameManager = new GameManager();
        kitManager = new KitManager();

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
        Bukkit.getPluginManager().registerEvents(FoliaUtil.getPlatformRespawnListener(), instance);
        Bukkit.getPluginManager().registerEvents(FoliaUtil.getPlatformTeleportListener(), instance);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook().register();
        }

        Scheduler.repeatedAsync(new BorderUtil(), 20, 10);
        Scheduler.repeatedAsync(new AutoBroadcast(), 20, 20 * getConfig().getLong("AutoBroadcast.Interval", 60));
        Scheduler.runLaterSync(() -> {
            World world = mapManager.getSpawnLocation().getWorld();

            world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.getWorldBorder().setSize(BorderUtil.borderDefault);
            world.getWorldBorder().setDamageBuffer(BorderUtil.borderDamageBuffer);
            world.getWorldBorder().setDamageAmount(BorderUtil.borderDamageAmount);
        }, 2);

        if (getConfig().getBoolean("Messages.Actionbar.Enabled")) {
            Scheduler.repeatedAsync(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String raw = getConfig().getString("Messages.Actionbar.Message", "&aYou are playing the best Event!");
                    String parsed = PlaceholderAPI.setPlaceholders(player, raw);

                    player.sendActionBar(MessageUtil.translateColorCodes(parsed));
                }
            }, 0, 20);
        }

        new Metrics(this, 28277);
        EventCoreAPI.initialize(this);
    }

    @Override
    public void onDisable() {
        if (gameManager.isRunning()) {
            gameManager.stop(null);
        }
    }

}
