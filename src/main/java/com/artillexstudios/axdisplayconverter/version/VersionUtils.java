package com.artillexstudios.axdisplayconverter.version;

import com.artillexstudios.axapi.reflection.ClassUtils;
import org.bukkit.entity.Player;

public class VersionUtils {
    private static PlayerVersion playerVersion;

    public static void init() {
        if (ClassUtils.INSTANCE.classExists("com.viaversion.viaversion.api.Via")) {
            playerVersion = new ViaVersionHook();
        } else {
            playerVersion = new BuiltinVersionDetection();
        }
    }

    public static boolean isOutdated(Player player) {
        return playerVersion.getProtocolId(player) < 762;
    }
}
