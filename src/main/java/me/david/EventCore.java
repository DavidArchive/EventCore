package me.david;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPILogger;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.clip.placeholderapi.PlaceholderAPI;
import me.david.command.*;
import me.david.listener.*;
import me.david.manager.GameManager;
import me.david.manager.KitManager;
import me.david.manager.MapManager;
import me.david.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Getter
public class EventCore extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger("EventCore");

    @Getter
    private static EventCore instance;
    private MapManager mapManager;
    private GameManager gameManager;
    private KitManager kitManager;

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onLoad() {
        CommandAPI.setLogger(CommandAPILogger.fromJavaLogger(getLogger()));
        CommandAPIPaperConfig config = new CommandAPIPaperConfig(this);
        config.setNamespace("eventcore");
        CommandAPI.onLoad(config);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        new UpdateChecker(instance, "DavidArchive", "EventCore").check();

        mapManager = new MapManager();
        gameManager = new GameManager();
        kitManager = new KitManager();

        CommandAPI.onEnable();

        new AnnouncementCommand(instance).init().register();
        new EventCoreCommand(instance);
        new KitCommand(instance);
        new ReviveCommand(instance).init().register();
        new SpawnCommand(instance).init().register();

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
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportListener(), instance);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook().register();
        }

        Scheduler.timerAsync(new BorderUtil(), 20, 10);
        Scheduler.timerAsync(new AutoBroadcast(), 20, 20 * getConfig().getLong("AutoBroadcast.Interval", 60));
        Scheduler.wait(() -> {
            World world = mapManager.getSpawnLocation().getWorld();
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.getWorldBorder().setSize(BorderUtil.borderDefault);
            world.getWorldBorder().setDamageBuffer(BorderUtil.borderDamageBuffer);
            world.getWorldBorder().setDamageAmount(BorderUtil.borderDamageAmount);
        }, 2);

        if (getConfig().getBoolean("Messages.Actionbar.Enabled")) {
            Scheduler.timerAsync(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String message = MessageUtil.get("Messages.Actionbar.Message");
                    message = PlaceholderAPI.setPlaceholders(player, message);
                    player.sendActionBar(message);
                }
            }, 0, 20);
        }

        int id = 28277;
        new Metrics(this, id);
    }

    @Override
    public void onDisable() {
        if (gameManager.isRunning()) {
            gameManager.stop(null);
        }

        CommandAPI.onDisable();
    }

}