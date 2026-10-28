package com.artillexstudios.axdisplayconverter.listeners;

import com.artillexstudios.axdisplayconverter.version.VersionUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        VersionUtils.getPlayerCache().remove(event.getPlayer());
    }
}
