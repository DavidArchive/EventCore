package me.david.manager;

import lombok.Getter;
import me.david.EventCore;
import me.david.api.events.game.GameStartEvent;
import me.david.api.events.game.GameStopEvent;
import me.david.api.events.game.GameTimerTickEvent;
import me.david.api.events.game.InGameTimerTickEvent;
import me.david.util.BorderUtil;
import me.david.util.MessageUtil;
import me.david.util.PlayerUtil;
import me.david.util.Scheduler;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GameManager {

    private boolean running = false;
    private volatile boolean timerRunning = false;

    private Scheduler.TaskWrapper startTask;
    private Scheduler.TaskWrapper autoStopTask;
    private Scheduler.TaskWrapper autoDropTask;
    private Scheduler.TaskWrapper timerTask;

    private AtomicInteger timer;
    private long inGameTimer;
    private boolean autoDropped = false;

    @SuppressWarnings("deprecation")
    public void start() {
        stopAllTimers();
        if (timerRunning) return;

        running = false;
        autoDropped = false;
        timerRunning = true;

        timer = new AtomicInteger(EventCore.getInstance().getConfig().getInt("Messages.StartTimer.Timer", 5));

        GameStartEvent gameStartEvent = new GameStartEvent(timer.get());
        Bukkit.getPluginManager().callEvent(gameStartEvent);

        if (gameStartEvent.isCancelled()) {
            timerRunning = false;
            return;
        }

        startTask = Scheduler.timer(() -> {
            if (!timerRunning || running) return;

            int current = timer.get();

            Bukkit.getPluginManager().callEvent(new GameTimerTickEvent(current));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (current > 0) {
                    String color = MessageUtil.get("Messages.StartTimer.Colors." + current + "sec");
                    String timerText = color + current + "§7";

                    player.sendMessage(MessageUtil.getPrefix()
                            + MessageUtil.get("Messages.StartTimer.Message").replaceAll("%timer%", timerText));
                    player.sendTitle(
                            MessageUtil.get("Messages.StartTimer.Title").replaceAll("%timer%", timerText),
                            MessageUtil.get("Messages.StartTimer.SubTitle").replaceAll("%timer%", timerText)
                    );
                    player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 5, 5);
                } else {
                    player.sendMessage(MessageUtil.getPrefix() + MessageUtil.get("Messages.Start.Message"));
                    player.sendTitle(
                            MessageUtil.get("Messages.Start.Title"),
                            MessageUtil.get("Messages.Start.SubTitle")
                    );
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 5);
                }
            }

            if (current <= 0) {
                for (World world : Bukkit.getWorlds()) {
                    world.setDifficulty(Difficulty.HARD);
                }

                if (EventCore.getInstance().getConfig().getBoolean("Settings.IngameTimer.Enabled")
                        && !EventCore.getInstance().getConfig().getBoolean("Messages.Actionbar.Enabled")) {
                    startInGameTimer();
                }

                EventCore.getInstance().getConfig().getStringList("Settings.Start.CustomCommands")
                        .forEach(command -> Scheduler.dispatchCommand(
                                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(1)))
                        );

                running = true;
                timerRunning = false;

                if (startTask != null) {
                    startTask.cancel();
                    startTask = null;
                }
            } else {
                timer.decrementAndGet();
            }
        }, 0, 20);

        if (EventCore.getInstance().getConfig().getBoolean("Settings.AutoStop1Player")) {
            autoStopTask = Scheduler.timer(() -> {
                if (running && PlayerUtil.getAlive() == 1) {
                    running = false;
                    Scheduler.runSync(() -> stop(
                            Bukkit.getOnlinePlayers().stream()
                                    .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                                    .findFirst()
                                    .map(Player::getName)
                                    .orElse("Unknown")
                    ));
                }
            }, 0, 20);
        }

        if (EventCore.getInstance().getConfig().getBoolean("Settings.DropOnPlayerCount.Enabled")) {
            autoDropTask = Scheduler.timer(() -> {
                if (running && PlayerUtil.getAlive() <= EventCore.getInstance().getConfig().getLong("Settings.DropOnPlayerCount.Count") && !autoDropped) {
                    autoDropped = true;
                    EventCore.getInstance().getMapManager().drop();
                }
            }, 0, 20);
        }
    }

    @SuppressWarnings("deprecation")
    public void stop(final String winner) {
        GameStopEvent gameStopEvent = new GameStopEvent(winner);
        Bukkit.getPluginManager().callEvent(gameStopEvent);

        if (gameStopEvent.isCancelled()) {
            return;
        }

        running = false;
        timerRunning = false;
        BorderUtil.lastOptimal = 200;

        stopInGameTimer();
        stopAllTimers();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MessageUtil.getPrefix()
                    + MessageUtil.get("Messages.Stop.Message").replaceAll("%winner%", winner));
            player.sendTitle(
                    MessageUtil.get("Messages.Stop.Title").replaceAll("%winner%", winner),
                    MessageUtil.get("Messages.Stop.SubTitle").replaceAll("%winner%", winner)
            );
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 5);
            PlayerUtil.cleanPlayer(player);
        }

        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.PEACEFUL);
            world.getWorldBorder().setSize(BorderUtil.borderDefault);
        }

        EventCore.getInstance().getConfig().getStringList("Settings.Stop.CustomCommands")
                .forEach(cmd -> Scheduler.dispatchCommand(
                        () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(1)))
                );

        if (EventCore.getInstance().getConfig().getBoolean("Settings.MapReset.AutoReset")) {
            EventCore.getInstance().getMapManager().reset();
        }
    }

    public void startInGameTimer() {
        inGameTimer = 0;

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        timerTask = Scheduler.timer(() -> {
            inGameTimer++;

            Bukkit.getPluginManager().callEvent(new InGameTimerTickEvent(inGameTimer));

            String message = MessageUtil.get("Settings.IngameTimer.Format")
                    .replaceAll("hh", String.format("%02d", (inGameTimer / 3600)))
                    .replaceAll("mm", String.format("%02d", ((inGameTimer % 3600) / 60)))
                    .replaceAll("ss", String.format("%02d", (inGameTimer % 60)));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message);
            }
        }, 0, 20);
    }

    public void stopInGameTimer() {
        inGameTimer = 0;
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private void stopAllTimers() {
        timerRunning = false;

        if (startTask != null) { startTask.cancel(); startTask = null; }
        if (autoStopTask != null) { autoStopTask.cancel(); autoStopTask = null; }
        if (autoDropTask != null) { autoDropTask.cancel(); autoDropTask = null; }
        if (timerTask != null) { timerTask.cancel(); timerTask = null; }
    }
}
