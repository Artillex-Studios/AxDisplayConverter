package com.artillexstudios.axdisplayconverter;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.packet.PacketEvents;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axdisplayconverter.entityid.EntityManager;
import com.artillexstudios.axdisplayconverter.packets.PacketListeners;
import com.artillexstudios.axdisplayconverter.version.VersionUtils;

public final class AxDisplayConverter extends AxPlugin {
    private static AxDisplayConverter instance;

    public static AxDisplayConverter getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        EntityManager.init();
        VersionUtils.init();

        PacketListeners packetListeners = new PacketListeners();
        PacketEvents.INSTANCE.addListener(packetListeners);
        getServer().getPluginManager().registerEvents(packetListeners, this);
    }

    @Override
    public void updateFlags() {
        FeatureFlags.ENABLE_PACKET_LISTENERS.set(true);
        FeatureFlags.USE_LEGACY_HEX_FORMATTER.set(true);
    }
}
