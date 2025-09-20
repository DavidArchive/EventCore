package me.david.listener;

import me.david.EventCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (player.hasPermission("event.bypass")) {
            event.setCancelled(false);
            return;
        }

        if (event.getBlock().getLocation().getBlockY() > EventCore.getInstance().getConfig().getLong("Settings.MaxBuildHeight", 0L)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(!(EventCore.getInstance().getGameManager().isRunning()));
    }

}
