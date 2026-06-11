package me.david.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.david.EventCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class PlaceholderHook extends PlaceholderExpansion {

    private static final DecimalFormat KD_FORMAT = new DecimalFormat("#0.00");
    private static final int MAX_CACHED_TOTEM_COUNTS = 10_000;
    private static final Map<UUID, Integer> TOTEM_COUNTS = Collections.synchronizedMap(new LinkedHashMap<>(
            MAX_CACHED_TOTEM_COUNTS,
            0.75F,
            true
    ) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<UUID, Integer> eldest) {
            return size() > MAX_CACHED_TOTEM_COUNTS;
        }
    });
    private static final Set<UUID> PENDING_TOTEM_REFRESHES = ConcurrentHashMap.newKeySet();

    @Override
    public @NotNull String getIdentifier() {
        return "eventcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "VertrauterDavid & JavaMio";
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull String getVersion() {
        return EventCore.getInstance().getDescription().getVersion();
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
            case "totems" -> String.valueOf(getCachedTotemCount(player));
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

    private static int getCachedTotemCount(final @NotNull Player player) {
        final UUID playerId = player.getUniqueId();
        final int cached = TOTEM_COUNTS.getOrDefault(playerId, 0);
        requestTotemCountRefresh(player, playerId);
        return TOTEM_COUNTS.getOrDefault(playerId, cached);
    }

    private static void requestTotemCountRefresh(final @NotNull Player player, final @NotNull UUID playerId) {
        if (!PENDING_TOTEM_REFRESHES.add(playerId)) return;

        FoliaUtil.scheduleToOrRun(player, () -> {
            try {
                refreshTotemCount(player, playerId);
            } finally {
                PENDING_TOTEM_REFRESHES.remove(playerId);
            }
        });
    }

    public static void removeCachedTotemCount(final @NotNull Player player) {
        final UUID playerId = player.getUniqueId();
        TOTEM_COUNTS.remove(playerId);
        PENDING_TOTEM_REFRESHES.remove(playerId);
    }

    private static int refreshTotemCount(final @NotNull Player player, final @NotNull UUID playerId) {
        final int count = countTotems(player);
        TOTEM_COUNTS.put(playerId, count);
        return count;
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
