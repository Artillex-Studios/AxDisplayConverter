package com.artillexstudios.axdisplayconverter.version;

import com.artillexstudios.axapi.reflection.ClassUtils;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VersionUtils {
    private static final Map<Player, Boolean> playerCache = new ConcurrentHashMap<>();
    private static PlayerVersion playerVersion;

    public static void init() {
        if (ClassUtils.INSTANCE.classExists("com.viaversion.viaversion.api.Via")) {
            playerVersion = new ViaVersionHook();
        } else {
            playerVersion = new BuiltinVersionDetection();
        }
    }

    public static boolean isOutdated(Player player) {
        Boolean value = playerCache.get(player);
        if (value != null) return value;
        return playerVersion.getProtocolId(player) < 762;
    }

    public static Map<Player, Boolean> getPlayerCache() {
        return playerCache;
    }
}
