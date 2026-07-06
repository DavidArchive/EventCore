package me.david.listener.canvas;

import io.canvasmc.canvas.event.PlayerPostRespawnAsyncEvent;
import io.canvasmc.canvas.event.PlayerRespawnAsyncEvent;
import me.david.EventCore;
import me.david.util.PlayerUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CanvasPlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnAsyncEvent event) {
        event.setRespawnLocation(EventCore.getInstance().getMapManager().getSpawnLocation());
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnAsyncEvent event) {
        final Player player = event.getPlayer();
        PlayerUtil.cleanPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

}
