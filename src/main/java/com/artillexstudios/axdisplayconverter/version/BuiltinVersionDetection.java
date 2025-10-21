package com.artillexstudios.axdisplayconverter.version;

import com.artillexstudios.axapi.utils.Version;
import org.bukkit.entity.Player;

public class BuiltinVersionDetection implements PlayerVersion {

    @Override
    public int getProtocolId(Player player) {
        return Version.getPlayerVersion(player).getProtocolId();
    }
}
