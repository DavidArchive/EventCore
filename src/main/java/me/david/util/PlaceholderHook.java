package me.david.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class PlaceholderHook extends PlaceholderExpansion {

    private static final DecimalFormat KD_FORMAT = new DecimalFormat("#0.00");

    @Override
    public @NotNull String getIdentifier() {
        return "eventcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "VertrauterDavid & JavaMio";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of(
                "total",
                "alive",
                "kills",
                "deaths",
                "kd",
                "totems",
                "border",
                "ping",
                "tps"
        );
    }

    @Override
    public @NotNull String onPlaceholderRequest(final Player player, final @NotNull String params) {
        if (player == null) return "";

        // I'm personally not a huge fan of switch & case, but in this case, it looks way better </3
        return switch (params) {
            case "total" -> String.valueOf(PlayerUtil.getTotal());
            case "alive" -> String.valueOf(PlayerUtil.getAlive());
            case "kills" -> String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
            case "deaths" -> String.valueOf(player.getStatistic(Statistic.DEATHS));
            case "kd" -> formatKD(player);
            case "totems" -> String.valueOf(countTotems(player));
            case "border" -> String.valueOf((int) (player.getWorld().getWorldBorder().getSize() / 2));
            case "ping" -> String.valueOf(player.isOnline() ? (int) (player.getPing() * 0.8) : 0);
            case "tps" -> formatTPS();
            default -> "";
        };
    }

    @Contract(pure = true)
    private static @NotNull String formatKD(final @NotNull Player player) {
        final double kills = player.getStatistic(Statistic.PLAYER_KILLS);
        final double deaths = player.getStatistic(Statistic.DEATHS);
        final double ratio = deaths == 0 ? kills : kills / deaths;
        return KD_FORMAT.format(Math.max(0, ratio));
    }

    private static int countTotems(final @NotNull Player player) {
        return (int) Stream.of(player.getInventory().getContents()).filter(Objects::nonNull)
                .filter(item -> item.getType() == Material.TOTEM_OF_UNDYING)
                .count();
    }

    @SuppressWarnings("deprecation")
    private static @NotNull String formatTPS() {
        final String raw = PlaceholderAPI.setPlaceholders(null, "%spark_tps_5m%");
        return ChatColor.stripColor(raw.replace("*", "").split("\\.")[0]);
    }
}
