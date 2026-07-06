package me.david.util;

import me.david.EventCore;
import me.david.util.folia.FoliaScheduler;

public class BorderUtil implements Runnable {

    public static volatile int borderDefault = 200;
    public static volatile double borderDamageBuffer = 0.0;
    public static volatile double borderDamageAmount = 0.2;
    public static volatile int lastOptimal = borderDefault;
    public static volatile boolean autoBorder;

    public BorderUtil() {
        borderDefault = EventCore.getInstance().getConfig().getInt("Settings.WorldBorder.DefaultSize", borderDefault);
        borderDamageBuffer = EventCore.getInstance().getConfig().getDouble("Settings.WorldBorder.Damage.Buffer", borderDamageBuffer);
        borderDamageAmount = EventCore.getInstance().getConfig().getDouble("Settings.WorldBorder.Damage.Amount", borderDamageAmount);
        autoBorder = EventCore.getInstance().getConfig().getBoolean("Settings.WorldBorder.AutoBorder", false);
        lastOptimal = borderDefault;
    }

    public static void setAutoBorder(boolean value) {
        autoBorder = value;
        EventCore.getInstance().getConfig().set("Settings.WorldBorder.AutoBorder", value);
        EventCore.getInstance().saveConfig();
    }


    @Override
    public void run() {
        if (EventCore.getInstance().getGameManager().isRunning() && autoBorder) {
            double current = EventCore.getInstance().getMapManager().getSpawnLocation().getWorld().getWorldBorder().getSize();
            int optimal = getOptimalSize();
            if (lastOptimal > optimal) {
                lastOptimal = optimal;
                FoliaScheduler.getGlobalRegionScheduler().execute(EventCore.getInstance(), () -> EventCore.getInstance().getMapManager().getSpawnLocation().getWorld().getWorldBorder().changeSize(optimal, (long) (current - optimal) * 20));
            }
        }
    }

    private int getOptimalSize() {
        int optimal = (int) (((Math.pow(PlayerUtil.getAlive(), 2)) / 60 + 4 + 0.6 * PlayerUtil.getAlive()) * 2);
        return Math.min(200, optimal);
    }

}
