package me.david.listener;

import me.david.EventCore;
import me.david.util.MessageUtil;
import me.david.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    //todo - refactor death messages to use components

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.deathMessage(Component.empty());
            return;
        }

        if (EventCore.getInstance().getConfig().getBoolean("Messages.PlayerDeath.Enabled")) {
            if (player.getKiller() != null) {
                event.setDeathMessage(MessageUtil.get("Messages.PlayerDeath.Message1").replaceAll("%player%", player.getName()).replaceAll("%killer%", player.getKiller().getName()));
            } else {
                event.setDeathMessage(MessageUtil.get("Messages.PlayerDeath.Message2").replaceAll("%player%", player.getName()));
            }
        } else {
            event.deathMessage(Component.empty());
        }

        event.setKeepLevel(true);
        event.setDroppedExp(0);

        PlayerUtil.cleanPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

}
