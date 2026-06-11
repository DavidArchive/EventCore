package me.david.listener.folia;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.canvasmc.canvas.event.PlayerRespawnAsyncEvent;
import me.david.EventCore;
import me.david.util.FoliaUtil;
import me.david.util.PlayerUtil;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CanvasPlayerRespawnListener implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnAsyncEvent event) {
        event.setRespawnLocation(EventCore.getInstance().getMapManager().getSpawnLocation());
    }


    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        var player = event.getPlayer();

        FoliaUtil.scheduleToOrRun(player, () -> {
            PlayerUtil.cleanPlayer(player);
            player.setGameMode(GameMode.SPECTATOR);
        });
    }
}
