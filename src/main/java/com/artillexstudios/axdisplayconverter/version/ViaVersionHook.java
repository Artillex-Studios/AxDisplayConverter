package com.artillexstudios.axdisplayconverter.version;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public class ViaVersionHook implements PlayerVersion {

    @Override
    public int getProtocolId(Player player) {
        return Via.getAPI().getPlayerVersion(player);
    }
}
